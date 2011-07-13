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

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.classloading.FrostyClasses;
import ru.frostman.web.config.FrostyConfig;
import ru.frostman.web.thr.FastRuntimeException;
import ru.frostman.web.view.FrostyViews;

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
            MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

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
