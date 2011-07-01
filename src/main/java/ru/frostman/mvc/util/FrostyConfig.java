package ru.frostman.mvc.util;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import ru.frostman.mvc.FrostyMode;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyConfig {
    private static String prevHashCode = "";

    private static FrostyMode mode;
    private static List<String> applicationPackages;
    private static String templatesPath;
    private static long updateInterval;
    private static int asyncQueueLength;

    static {
        update();
    }

    public synchronized static boolean update() {
        try {

            Properties properties = new Properties();
            final InputStream propertiesStream = getConfigStream();
            if (propertiesStream == null) {
                throw new FrostyRuntimeException("Can't find properties file");
            }
            properties.load(propertiesStream);
            String currHashCode = getConfigHashCode();

            mode = FrostyMode.parseMode(properties.getProperty("frosty.mode"));
            applicationPackages = Arrays.asList(properties.getProperty("base.packages", "").split(":"));
            templatesPath = properties.getProperty("templates.path", "templates");

            updateInterval = Long.parseLong(properties.getProperty("frosty.updateInterval", "1000"));
            asyncQueueLength = Integer.parseInt(properties.getProperty("frosty.async.queueLength", "100"));

            if (!currHashCode.equals(prevHashCode)) {
                prevHashCode = currHashCode;
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new FrostyRuntimeException("Can't load framework configuration");
        }
    }

    private static InputStream getConfigStream() {
        return FrostyConfig.class.getResourceAsStream("/frosty.conf");
    }

    private static String getConfigHashCode() {
        //todo need to cache MessageDigest

        try {
            return Hex.encode(ByteStreams.getDigest(new InputSupplier<InputStream>() {
                @Override
                public InputStream getInput() throws IOException {
                    return getConfigStream();
                }
            }, MessageDigest.getInstance("sha-1")));
        } catch (IOException e) {
            throw new RuntimeException("Can't read file: ", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

    public static int getAsyncQueueLength() {
        return asyncQueueLength;
    }
}
