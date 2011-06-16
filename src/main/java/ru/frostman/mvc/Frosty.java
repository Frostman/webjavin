package ru.frostman.mvc;

import ru.frostman.mvc.classloading.FrostyClasses;
import ru.frostman.mvc.dispatch.Dispatcher;

import java.util.Collections;
import java.util.List;

/**
 * Main class of Frosty framework, that encapsulates most of all
 * application's properties, such as, servlet api version, loaded
 * classes of the application and so on.
 *
 * @author slukjanov aka Frostman
 */
public class Frosty {
    /**
     * Major version of the Servlet API that current servlet container supports
     */
    private static int servletApiMajorVersion;

    /**
     * Minor version of the Servlet API that current servlet container supports
     */
    private static int servletApiMinorVersion;

    /**
     * Real path of the "/" resource
     */
    private static String applicationPath;

    /**
     * Base packages, where application classes will find
     */
    private static List<String> basePackages;

    /**
     * Application classes and tools to update and use them, such as class loader
     */
    private static FrostyClasses classes;

    /**
     * Request dispatcher
     */
    private static Dispatcher dispatcher;

    /**
      * @return true iff current servlet container supports async api
     */
    public static boolean isAsyncApiSupported() {
        return servletApiMajorVersion >= 3;
    }

    /**
     * @return base packages, where application classes will find
     */
    public static List<String> getBasePackages() {
        return basePackages;
    }

    /**
     * @return application classes and tools to update and use them
     */
    public static FrostyClasses getClasses() {
        return classes;
    }

    /**
     * @return real path of the "/" resource
     */
    public static String getApplicationPath() {
        return applicationPath;
    }

    /**
     * @return request dispatcher
     */
    public static Dispatcher getDispatcher() {
        return dispatcher;
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
