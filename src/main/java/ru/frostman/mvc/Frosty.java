/******************************************************************************
 * Frosty - MVC framework.                                                    *
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

package ru.frostman.mvc;

import ru.frostman.mvc.classloading.FrostyClasses;
import ru.frostman.mvc.util.FrostyInvoker;
import ru.frostman.mvc.view.FrostyViews;

/**
 * Main class of Frosty framework, that encapsulates most of all
 * application's properties, such as, servlet api version, loaded
 * classes of the application and so on.
 *
 * @author slukjanov aka Frostman
 */
public final class Frosty {

    /**
     * Current application mode
     */
    private static FrostyMode mode;

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
     * Application classes and tools to update and use them, such as class loader
     */
    private static FrostyClasses classes;

    /**
     * Application views, like templates and codec
     */
    private static FrostyViews views;

    /**
     * Executor
     */
    private static FrostyInvoker invoker;

    /**
     * @return current application mode
     */
    public static FrostyMode getMode() {
        return mode;
    }

    /**
     * @return true iff current servlet container supports async api
     */
    public static boolean isAsyncApiSupported() {
        return servletApiMajorVersion >= 3;
    }

    /**
     * @return application classes and tools to update and use them
     */
    public static FrostyClasses getClasses() {
        return classes;
    }

    /**
     * @return application views
     */
    public static FrostyViews getViews() {
        return views;
    }

    /**
     * @return real path of the "/" resource
     */
    public static String getApplicationPath() {
        return applicationPath;
    }

    /**
     * @return executor
     */
    public static FrostyInvoker getInvoker() {
        return invoker;
    }

    static void setMode(FrostyMode mode) {
        Frosty.mode = mode;
    }

    static void setServletApiMajorVersion(int servletApiMajorVersion) {
        Frosty.servletApiMajorVersion = servletApiMajorVersion;
    }

    static void setServletApiMinorVersion(int servletApiMinorVersion) {
        Frosty.servletApiMinorVersion = servletApiMinorVersion;
    }

    static void setClasses(FrostyClasses classes) {
        Frosty.classes = classes;
    }

    static void setApplicationPath(String applicationPath) {
        Frosty.applicationPath = applicationPath;
    }

    static void setViews(FrostyViews views) {
        Frosty.views = views;
    }

    public static void setInvoker(FrostyInvoker invoker) {
        Frosty.invoker = invoker;
    }
}
