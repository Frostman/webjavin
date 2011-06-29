package ru.frostman.mvc.util;

import com.google.common.collect.ImmutableList;
import ru.frostman.mvc.FrostyMode;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyConfig {
    private static FrostyMode mode;
    private static List<String> applicationPackages;
    private static String templatesPath;
    private static long updateInterval;

    static {
        update();
    }

    public static boolean update() {

        //todo impl return true iff changed
        try {
            Properties properties = new Properties();
            final InputStream propertiesStream = FrostyConfig.class.getResourceAsStream("/frosty.conf");
            if (propertiesStream == null) {
                //todo impl
                throw new RuntimeException("Can't find properties file");
            }
            properties.load(propertiesStream);

            mode = FrostyMode.parseMode(properties.getProperty("frosty.mode"));
            applicationPackages = Arrays.asList(properties.getProperty("base.packages", "").split(":"));
            templatesPath = properties.getProperty("templates.path", "templates");

            updateInterval = Long.parseLong(properties.getProperty("frosty.updateInterval", "1000"));
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }

        return false;
    }

    public static FrostyMode getMode() {
        return mode;
    }

    public static List<String> getApplicationPackages() {
        return ImmutableList.copyOf(applicationPackages);
    }

    public static String getTemplatesPath() {
        return templatesPath;
    }

    public static long getUpdateInterval() {
        return updateInterval;
    }
}
