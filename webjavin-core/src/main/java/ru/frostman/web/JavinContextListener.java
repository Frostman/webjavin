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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.classloading.AppClasses;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.FastRuntimeException;
import ru.frostman.web.view.AppViews;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author slukjanov aka Frostman
 */
public class JavinContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(JavinContextListener.class);
    private static final String JAVIN_CONFIG = "javin-config";

    private static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            servletContext = sce.getServletContext();

            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);

            String configName = sce.getServletContext().getInitParameter(JAVIN_CONFIG);
            if (configName != null) {
                JavinConfig.setConfigFileName(configName);
            }

            JavinConfig.update();

            Javin.setMode(JavinConfig.get().getMode());
            Javin.setServletApiMajorVersion(sce.getServletContext().getMajorVersion());
            Javin.setServletApiMinorVersion(sce.getServletContext().getMinorVersion());

            Javin.setApplicationPath(sce.getServletContext().getRealPath("/"));
            Javin.setClasses(new AppClasses());
            Javin.setViews(new AppViews());

            log.info("Javin context initialized successfully");
        } catch (Throwable th) {
            log.error("Initialization failed with: ", th);

            throw new FastRuntimeException("Initialization failed");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Javin context destroyed successfully");
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }
}
