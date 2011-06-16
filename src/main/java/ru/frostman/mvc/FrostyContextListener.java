package ru.frostman.mvc;

import ru.frostman.mvc.classloading.FrostyClasses;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Frosty.setServletApiMajorVersion(sce.getServletContext().getMajorVersion());
        Frosty.setServletApiMinorVersion(sce.getServletContext().getMinorVersion());

        Frosty.setApplicationPath(sce.getServletContext().getRealPath("/"));
        Frosty.setBasePackages(Arrays.asList(sce.getServletContext().getInitParameter("basePackages").split(":")));
        Frosty.setClasses(new FrostyClasses());

        //todo remove it
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        Frosty.getClasses().update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        System.out.println("Inited");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //todo impl
        System.out.println("Destroyed");
    }
}
