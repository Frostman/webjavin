package ru.frostman.mvc.classloading;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
public class ClassPathUtil {
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
                    String fullPath = resource.getFile();

                    File dir = new File(fullPath);
                    if (dir.exists()) {
                        scanDirectory(packageName, dir, classes, classNames);
                    }
                }
            } catch (IOException e) {
                log.warn("Error while scanning for class files in package: {}", e);
            }
        }

        return classes;
    }

    private static void scanDirectory(String packageName, File dir, List<ClassFile> classes, Set<String> classNames) {
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
}
