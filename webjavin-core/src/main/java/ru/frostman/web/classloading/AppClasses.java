/******************************************************************************
 * WebJavin - Java Web Framework.                                             *
 *                                                                            *
 * Copyright (c) 2011 - Sergey "Frosman" Lukjanov, me@frostman.ru             *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package ru.frostman.web.classloading;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.Javin;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.aop.MethodInterceptors;
import ru.frostman.web.cache.JavinCacheManager;
import ru.frostman.web.classloading.enhance.Enhancer;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.dispatch.ActionDefinition;
import ru.frostman.web.dispatch.Dispatcher;
import ru.frostman.web.plugin.JavinPlugins;
import ru.frostman.web.secure.JavinSecurityManager;
import ru.frostman.web.session.JavinSession;
import ru.frostman.web.session.JavinSessions;
import ru.frostman.web.thr.JavinRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author slukjanov aka Frostman
 */
public class AppClasses {
    private static final Logger log = LoggerFactory.getLogger(AppClasses.class);

    private static final String ATTR_CLASSES_UUID = "javin.classes.uuid";

    /**
     * Lock that used to synchronize all invokes of method update()
     */
    private static final ReentrantLock UPDATE_LOCK = new ReentrantLock();

    /**
     * Last update time
     */
    private static long lastUpdate = 0;

    /**
     * Instance uuid
     */
    private String uuid;

    /**
     * All application classes stored by name
     */
    private final Map<String, AppClass> classes = Maps.newLinkedHashMap();

    /**
     * Current class loader instance
     */
    private AppClassLoader classLoader;

    /**
     * Request dispatcher
     */
    private Dispatcher dispatcher;

    /**
     * Security manager
     */
    private JavinSecurityManager securityManager;

    /**
     * Ready status
     */
    private boolean ready;

    private boolean forceReload;

    public AppClasses() {
        uuid = UUID.randomUUID().toString();

        if (Javin.getMode().isProductionMode()) {
            update();
        }
    }

    /**
     * Scan class path for classes in base packages and iff class added,
     * changed or removed than new ClassLoader will be created.
     *
     * @return true iff class loader changed
     */
    public boolean update() {
        if (ready && System.currentTimeMillis() - lastUpdate < JavinConfig.get().getClasses().getUpdateInterval()) {
            return false;
        }

        UPDATE_LOCK.lock();
        try {
            long start = 0;
            if (log.isDebugEnabled()) {
                log.debug("Searching for new, changed or removed classes");
                start = System.currentTimeMillis();
            }

            boolean needReload = JavinConfig.update() | JavinSessions.update()
                    | JavinCacheManager.update() | forceReload;

            if (forceReload) {
                forceReload = false;
            }

            // load plugins
            //todo about not reloading plugins if nothing changes
            JavinPlugins.reload();

            List<String> packageNames = Lists.newLinkedList();
            packageNames.addAll(JavinConfig.get().getClasses().getPackages());
            packageNames.addAll(JavinPlugins.get().getAppClassesPackages());

            List<ClassFile> classFiles = ClassPathUtil.findClassFiles(packageNames);

            Set<String> existedClassNames = Sets.newHashSet(classes.keySet());
            Set<String> foundClassNames = Sets.newHashSet();
            for (ClassFile classFile : classFiles) {
                final String className = classFile.getClassName();
                foundClassNames.add(className);

                if (!classes.containsKey(className)) {
                    // new class added

                    needReload = true;

                    long lastModified = classFile.getLastModified();
                    AppClass newClass = new AppClass();
                    newClass.setName(className);
                    newClass.setLastLoaded(lastModified);
                    newClass.setHashCode(classFile.getHashCode());
                    newClass.setBytecode(classFile.getBytes());

                    classes.put(className, newClass);

                    log.debug("Application class added: {}", className);
                } else {
                    // existed class
                    final AppClass existedClass = classes.get(className);

                    final String hashCode = classFile.getHashCode();
                    if (existedClass.getLastLoaded() < classFile.getLastModified()
                            && (!existedClass.getHashCode().equals(hashCode))) {
                        //class changed

                        needReload = true;

                        long lastModified = classFile.getLastModified();
                        AppClass changedClass = new AppClass();
                        changedClass.setName(className);
                        changedClass.setLastLoaded(lastModified);
                        changedClass.setHashCode(hashCode);
                        changedClass.setBytecode(classFile.getBytes());

                        classes.put(className, changedClass);

                        log.debug("Application class changed: {}", className);
                    }
                }
            }

            existedClassNames.removeAll(foundClassNames);

            for (String removedClassName : existedClassNames) {
                if (removedClassName.contains("$action$")) {
                    continue;
                }

                classes.remove(removedClassName);

                needReload = true;

                log.debug("Application class removed: {}", removedClassName);
            }

            if (log.isDebugEnabled()) {
                log.debug("Application classes scan completed ({}ms)", System.currentTimeMillis() - start);
            }

            if (needReload) {
                log.debug("Application classes is need to reload");
                if (log.isInfoEnabled()) {
                    start = System.currentTimeMillis();
                }

                // prepare classes (creates CtClasses)
                Enhancer.prepareClasses(classes);

                // init security manager
                this.securityManager = new JavinSecurityManager();

                JavinPlugins.get().beforeClassesEnhance(classes);

                // find actions
                List<ActionDefinition> actionDefinitions = Lists.newLinkedList();

                // find method interceptors
                List<MethodInterceptor> methodInterceptors = MethodInterceptors.findInterceptors(classes);

                for (String className : Lists.newLinkedList(classes.keySet())) {
                    Enhancer.enhance(classes, classes.get(className), actionDefinitions, methodInterceptors);
                }

                // create new class loader and load all app classes
                AppClassLoader newClassLoader = new AppClassLoader(ImmutableMap.copyOf(classes));
                newClassLoader.loadAllClasses();

                // init action definitions with new class loader
                for (ActionDefinition definition : actionDefinitions) {
                    definition.init(newClassLoader);
                }

                this.classLoader = newClassLoader;
                this.dispatcher = new Dispatcher(actionDefinitions);

                // compile all secure expressions
                this.securityManager.compileAllExpressions();

                JavinPlugins.get().afterClassesEnhance(classes);

                if (log.isInfoEnabled()) {
                    log.info("Application classes successfully reloaded ({}ms)", System.currentTimeMillis() - start);
                }
            } else {
                log.debug("Application classes is up to date");
            }

            ready = true;

            return needReload;
        } catch (Throwable th) {
            forceReload = true;
            throw new JavinRuntimeException(th);

            //todo think about unlocking UPDATE_LOCK here
        } finally {
            lastUpdate = System.currentTimeMillis();
            UPDATE_LOCK.unlock();
        }
    }

    public AppClassLoader getClassLoader() {
        return classLoader;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public JavinSecurityManager getSecurityManager() {
        return securityManager;
    }

    public String getUuid() {
        return uuid;
    }

    public void checkSession(HttpServletRequest request, HttpServletResponse response) {
        JavinSession session = JavinSessions.getSession(request, response, false);

        if (session == null) {
            return;
        }

        String classesUuid = (String) session.getAttribute(ATTR_CLASSES_UUID);

        if (classesUuid != null && (!uuid.equals(classesUuid))) {
            session.invalidate();
        }
    }

    public void attachUuid(HttpServletRequest request, HttpServletResponse response) {
        JavinSession session = JavinSessions.getSession(request, response, false);

        if (session == null) {
            return;
        }

        if (!Objects.equal(uuid, session.getAttribute(ATTR_CLASSES_UUID))) {
            session.setAttribute(ATTR_CLASSES_UUID, uuid);
        }
    }
}
