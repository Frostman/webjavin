package ru.frostman.mvc.classloading;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.classloading.enhance.Enhancer;
import ru.frostman.mvc.dispatch.ActionDefinition;
import ru.frostman.mvc.dispatch.Dispatcher;
import ru.frostman.mvc.secure.FrostySecurityManager;
import ru.frostman.mvc.util.FrostyConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyClasses {
    private static final Logger log = LoggerFactory.getLogger(FrostyClasses.class);

    /**
     * Lock that used to synchronize all invokes of method update()
     */
    private static final ReentrantLock UPDATE_LOCK = new ReentrantLock();

    /**
     * Last update time
     */
    private static long lastUpdate = 0;

    /**
     * All application classes stored by name
     */
    private final Map<String, FrostyClass> classes = Maps.newLinkedHashMap();

    /**
     * Current class loader instance
     */
    private FrostyClassLoader classLoader;

    /**
     * Request dispatcher
     */
    private Dispatcher dispatcher;

    /**
     * Security manager
     */
    private FrostySecurityManager securityManager;

    public FrostyClasses() {
        if (Frosty.getMode().isProductionMode()) {
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
        if (System.currentTimeMillis() - lastUpdate < FrostyConfig.getUpdateInterval()) {
            return false;
        }

        UPDATE_LOCK.lock();
        try {
            long start = 0;
            if (log.isDebugEnabled()) {
                log.debug("Searching for new, changed or removed classes");
                start = System.currentTimeMillis();
            }

            boolean needReload = FrostyConfig.update();

            List<ClassFile> foundClasses = ClassPathUtil.findClassFiles(FrostyConfig.getApplicationPackages());

            Set<String> existedClassNames = Sets.newHashSet(classes.keySet());
            Set<String> foundClassNames = Sets.newHashSet();
            for (ClassFile classFile : foundClasses) {
                final String className = classFile.getClassName();
                foundClassNames.add(className);

                if (!classes.containsKey(className)) {
                    // new class added

                    needReload = true;

                    long lastModified = classFile.getLastModified();
                    FrostyClass newClass = new FrostyClass();
                    newClass.setName(className);
                    newClass.setLastLoaded(lastModified);
                    newClass.setHashCode(classFile.getHashCode());
                    newClass.setBytecode(classFile.getBytes());
                    classes.put(className, newClass);

                    log.debug("Application class added: {}", className);
                } else {
                    // existed class
                    final FrostyClass existedClass = classes.get(className);

                    final String hashCode = classFile.getHashCode();
                    if (existedClass.getLastLoaded() < classFile.getLastModified()
                            && (!existedClass.getHashCode().equals(hashCode))) {
                        //class changed

                        needReload = true;

                        long lastModified = classFile.getLastModified();
                        FrostyClass changedClass = new FrostyClass();
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

                for (FrostyClass frostyClass : Lists.newLinkedList(classes.values())) {
                    frostyClass.setEnhancedBytecode(null);

                    if (frostyClass.isGenerated()) {
                        classes.remove(frostyClass.getName());
                    }
                }

                List<ActionDefinition> actionDefinitions = Lists.newLinkedList();
                for (String className : Lists.newLinkedList(classes.keySet())) {
                    Enhancer.enhance(classes, classes.get(className), actionDefinitions);
                }

                FrostyClassLoader newClassLoader = new FrostyClassLoader(ImmutableMap.copyOf(classes));
                newClassLoader.loadAllClasses();

                for (ActionDefinition definition : actionDefinitions) {
                    definition.init(newClassLoader);
                }

                this.classLoader = newClassLoader;
                this.dispatcher = new Dispatcher(actionDefinitions);
                this.securityManager = new FrostySecurityManager();

                if (log.isInfoEnabled()) {
                    log.info("Application classes successfully reloaded ({}ms)", System.currentTimeMillis() - start);
                }
            } else {
                log.debug("Application classes is up to date");
            }

            return needReload;

        } finally {
            lastUpdate = System.currentTimeMillis();
            UPDATE_LOCK.unlock();
        }
    }

    public FrostyClassLoader getClassLoader() {
        return classLoader;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public FrostySecurityManager getSecurityManager() {
        return securityManager;
    }
}
