package ru.frostman.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.mvc.classloading.FrostyClasses;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(FrostyContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Frosty.setServletApiMajorVersion(sce.getServletContext().getMajorVersion());
        Frosty.setServletApiMinorVersion(sce.getServletContext().getMinorVersion());

        Frosty.setApplicationPath(sce.getServletContext().getRealPath("/"));
        Frosty.setBasePackages(Arrays.asList(sce.getServletContext().getInitParameter("basePackages").split(":")));
        Frosty.setClasses(new FrostyClasses());

        log.info("Frosty context initialized successfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //todo impl

        log.info("Frosty context destroyed successfully");
    }
}
