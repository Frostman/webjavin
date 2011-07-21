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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author slukjanov aka Frostman
 */
class ClassPathUtil {
    private static final Logger log = LoggerFactory.getLogger(ClassPathUtil.class);
    private static final String CLASS = ".class";

    public static List<ClassFile> findClassFiles(List<String> packageNames) {
        final List<ClassFile> classes = Lists.newLinkedList();
        final Set<String> classNames = Sets.newHashSet();

        for (String packageName : packageNames) {
            try {
                String realPath = packageName.replace('.', '/');
                Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(realPath);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    Preconditions.checkNotNull(resource, "no resource for: {}", packageName);
                    String filePath = resource.getFile();

                    File dir = new File(filePath);
                    if (dir.exists()) {
                        scanDirectory(packageName, dir, classes, classNames);
                    } else if ((filePath.indexOf("!") > 0) && (filePath.indexOf(".jar") > 0)) {
                        String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(filePath.indexOf(":") + 1);
                        // WINDOWS HACK
                        if (jarPath.contains(":")) {
                            jarPath = jarPath.substring(1);
                        }

                        scanJarFile(packageName, new File(jarPath), classes, classNames);
                    }
                }
            } catch (IOException e) {
                log.warn("Error while scanning for class files in package: {}", e);
            }
        }

        return classes;
    }

    private static void scanDirectory(String packageName, File dir, List<ClassFile> classes, Set<String> classNames) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                scanDirectory(packageName + "." + file.getName(), file, classes, classNames);
            } else if (fileName.endsWith(CLASS)) {
                String className = packageName + "." + fileName.substring(0, fileName.length() - CLASS.length());
                if (classNames.contains(className)) {
                    continue;
                }

                classes.add(new ClassFile(className, file));
                classNames.add(className);
            }
        }
    }

    private static void scanJarFile(String packageName, File jar, List<ClassFile> classes, Set<String> classNames) throws IOException {
        JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
        JarEntry jarEntry;
        do {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry != null) {
                String fileName = jarEntry.getName();

                if (!fileName.endsWith(".class")) {
                    continue;
                }

                String className = fileName.replace("/", ".").substring(0, fileName.length() - CLASS.length());

                if (classNames.contains(className)) {
                    continue;
                }

                String currPackage = className.substring(0, className.lastIndexOf("."));
                if (!packageName.equals(currPackage)) {
                    continue;
                }

                classes.add(new StaticClassFile(className));
                classNames.add(className);
            }
        } while (jarEntry != null);
    }
}
