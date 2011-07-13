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

package ru.frostman.web;

import ru.frostman.web.classloading.AppClasses;
import ru.frostman.web.util.Invoker;
import ru.frostman.web.view.AppViews;

/**
 * Main class of Javin framework, that encapsulates most of all
 * application's properties, such as, servlet api version, loaded
 * classes of the application and so on.
 *
 * @author slukjanov aka Frostman
 */
public final class Javin {

    /**
     * Current application mode
     */
    private static JavinMode mode;

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
    private static AppClasses classes;

    /**
     * Application views, like templates and codec
     */
    private static AppViews views;

    /**
     * Executor
     */
    private static Invoker invoker;

    /**
     * @return current application mode
     */
    public static JavinMode getMode() {
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
    public static AppClasses getClasses() {
        return classes;
    }

    /**
     * @return application views
     */
    public static AppViews getViews() {
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
    public static Invoker getInvoker() {
        return invoker;
    }

    static void setMode(JavinMode mode) {
        Javin.mode = mode;
    }

    static void setServletApiMajorVersion(int servletApiMajorVersion) {
        Javin.servletApiMajorVersion = servletApiMajorVersion;
    }

    static void setServletApiMinorVersion(int servletApiMinorVersion) {
        Javin.servletApiMinorVersion = servletApiMinorVersion;
    }

    static void setClasses(AppClasses classes) {
        Javin.classes = classes;
    }

    static void setApplicationPath(String applicationPath) {
        Javin.applicationPath = applicationPath;
    }

    static void setViews(AppViews views) {
        Javin.views = views;
    }

    public static void setInvoker(Invoker invoker) {
        Javin.invoker = invoker;
    }
}