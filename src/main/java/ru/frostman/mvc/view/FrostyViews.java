package ru.frostman.mvc.view;

import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.controller.View;

import java.io.File;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyViews {
    private static final String FORWARD = "forward:";
    private final Configuration freemarker;

    public FrostyViews() {
        freemarker = new Configuration();

        //todo in production mode set to no update
        freemarker.setTemplateUpdateDelay(0);
        freemarker.setDefaultEncoding("utf-8");
        freemarker.setOutputEncoding("utf-8");

        //todo remove hard code
        freemarker.setCacheStorage(new MruCacheStorage(25, 250));

        try {
            freemarker.setDirectoryForTemplateLoading(new File(Frosty.getApplicationPath() + FrostyConfig.getCurrentConfig().getTemplates().getPath()));
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }

    public View getViewByName(String name) {
        if (name.startsWith(FORWARD)) {
            return new ForwardView(name.substring(FORWARD.length()).trim());
        }

        try {
            //todo cache freemarker view instances in production
            return new FreemarkerView(freemarker.getTemplate(name));
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }
}
