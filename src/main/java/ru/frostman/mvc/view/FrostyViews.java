package ru.frostman.mvc.view;

import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.View;
import ru.frostman.mvc.util.FrostyConfig;

import java.io.File;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyViews {
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
            freemarker.setDirectoryForTemplateLoading(new File(Frosty.getApplicationPath() + FrostyConfig.getTemplatesPath()));
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }

    public View getViewByName(String name) {
        try {
            //todo cache freemarker view instances in production
            return new FreemarkerView(freemarker.getTemplate(name));
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }
}
