package ru.frostman.mvc.config;

import com.google.common.base.Objects;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.mvc.FrostyMode;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.io.InputStream;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyConfig {
    private static FrostyConfig currentConfig;

    private FrostyMode mode = FrostyMode.DEV;
    private ClassesConfig classes = new ClassesConfig();
    private TemplatesConfig templates = new TemplatesConfig();
    private AppConfig app = new AppConfig();

    static {
        update();
    }

    public synchronized static boolean update() {
        try {
            Yaml yaml = new Yaml(new Constructor(FrostyConfig.class));
            FrostyConfig config = (FrostyConfig) yaml.load(getConfigStream());

            boolean changed = false;
            if(!config.equals(currentConfig)) {
                changed = true;

                currentConfig = config;
            }

            return changed;
        } catch (Exception e) {
            throw new FrostyRuntimeException("Can't load framework configuration", e);
        }
    }

    public static FrostyConfig getCurrentConfig() {
        return currentConfig;
    }

    private static InputStream getConfigStream() {
        return FrostyConfig.class.getResourceAsStream("/frosty.yaml");
    }

    public FrostyMode getMode() {
        return mode;
    }

    public void setMode(FrostyMode mode) {
        this.mode = mode;
    }

    public ClassesConfig getClasses() {
        return classes;
    }

    public void setClasses(ClassesConfig classes) {
        this.classes = classes;
    }

    public TemplatesConfig getTemplates() {
        return templates;
    }

    public void setTemplates(TemplatesConfig templates) {
        this.templates = templates;
    }

    public AppConfig getApp() {
        return app;
    }

    public void setApp(AppConfig app) {
        this.app = app;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FrostyConfig) {
            FrostyConfig config = (FrostyConfig) obj;

            return mode == config.mode
                    && Objects.equal(classes, config.classes)
                    && Objects.equal(templates, config.templates)
                    &&Objects.equal(app, config.app);
        }

        return false;
    }
}
