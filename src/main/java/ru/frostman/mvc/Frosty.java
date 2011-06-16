package ru.frostman.mvc;

import ru.frostman.mvc.classloading.FrostyClasses;

import java.util.Collections;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class Frosty {
    private static int servletApiMajorVersion;
    private static int servletApiMinorVersion;
    private static String applicationPath;
    private static List<String> basePackages;

    private static FrostyClasses classes;

    public static boolean isAsyncApiSupported() {
        return servletApiMajorVersion >= 3;
    }

    public static List<String> getBasePackages() {
        return basePackages;
    }

    public static FrostyClasses getClasses() {
        return classes;
    }

    public static String getApplicationPath() {
        return applicationPath;
    }

    static void setServletApiMajorVersion(int servletApiMajorVersion) {
        Frosty.servletApiMajorVersion = servletApiMajorVersion;
    }

    static void setServletApiMinorVersion(int servletApiMinorVersion) {
        Frosty.servletApiMinorVersion = servletApiMinorVersion;
    }

    static void setBasePackages(List<String> basePackages) {
        Frosty.basePackages = Collections.unmodifiableList(basePackages);
    }

    static void setClasses(FrostyClasses classes) {
        Frosty.classes = classes;
    }

    static void setApplicationPath(String applicationPath) {
        Frosty.applicationPath = applicationPath;
    }
}
