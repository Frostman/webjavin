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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.mvc.classloading.FrostyClasses;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.thr.FastRuntimeException;
import ru.frostman.mvc.view.FrostyViews;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(FrostyContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);

            Frosty.setMode(FrostyConfig.getCurrentConfig().getMode());
            Frosty.setServletApiMajorVersion(sce.getServletContext().getMajorVersion());
            Frosty.setServletApiMinorVersion(sce.getServletContext().getMinorVersion());

            Frosty.setApplicationPath(sce.getServletContext().getRealPath("/"));
            Frosty.setClasses(new FrostyClasses());
            Frosty.setViews(new FrostyViews());

            log.info("Frosty context initialized successfully");
        } catch (Throwable th) {
            log.error("Initialization failed with: ", th);

            throw new FastRuntimeException("Initialization failed");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Frosty context destroyed successfully");
    }
}
