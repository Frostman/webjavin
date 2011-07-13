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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.Javin;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author slukjanov aka Frostman
 */
public class AppClassLoader extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(AppClassLoader.class);

    /**
     * AppClassLoader instances counter
     */
    private static final AtomicLong LOADERS_COUNT = new AtomicLong();

    /**
     * Current instance id
     */
    private final long id = LOADERS_COUNT.getAndIncrement();

    /**
     * All application classes stored by name
     */
    private final Map<String, AppClass> classes;

    /**
     * Protection domain will be applied to all loaded classes
     */
    private ProtectionDomain protectionDomain;

    public AppClassLoader(Map<String, AppClass> classes) {
        super(Javin.class.getClassLoader());

        this.classes = classes;

        try {
            CodeSource codeSource = new CodeSource(new URL("file:" + Javin.getApplicationPath()), (Certificate[]) null);
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            protectionDomain = new ProtectionDomain(codeSource, permissions);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadAllClasses() {
        long start = 0;
        if (log.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        for (String name : classes.keySet()) {
            try {
                loadClass(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("All application classes loaded successfully ({}ms)", System.currentTimeMillis() - start);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Iff class is already loaded, return it
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }

        // Try to load application class
        clazz = loadApplicationClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }

            return clazz;
        }

        // Load class by parent classloader
        return super.loadClass(name, resolve);
    }

    private Class<?> loadApplicationClass(String name) {
        long start = 0;
        if (log.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        AppClass appClass = classes.get(name);

        if (appClass == null) {
            return null;
        }

        final byte[] enhancedBytecode = appClass.getEnhancedBytecode();
        Class<?> clazz = defineClass(name, enhancedBytecode, 0, enhancedBytecode.length, protectionDomain);
        resolveClass(clazz);

        appClass.setJavaClass(clazz);

        if (log.isDebugEnabled()) {
            log.debug("Application class defined and resolved: {} ({}ms)", name, System.currentTimeMillis() - start);
        }

        return clazz;
    }

    public List<Class> getClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        List<Class> result = Lists.newLinkedList();
        for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
            final Class<?> javaClass = entry.getValue().getJavaClass();
            if (javaClass != null && javaClass.getAnnotation(annotation) != null) {
                result.add(javaClass);
            }
        }

        return result;
    }

    public long getId() {
        return id;
    }

    public static long getLoadersCount() {
        return LOADERS_COUNT.get();
    }
}
