package ru.frostman.mvc;

/**
 * @author slukjanov aka Frostman
 */
public enum FrostyMode {
    DEVELOPMENT, PRODUCTION;

    public boolean isDevelopmentMode() {
        return this == DEVELOPMENT;
    }

    public boolean isProductionMode() {
        return this == PRODUCTION;
    }

    public static FrostyMode parseMode(String str) {
        str = str.toLowerCase();

        if ("dev".equals(str)) {
            return DEVELOPMENT;
        } else if ("prod".equals(str)) {
            return PRODUCTION;
        }

        return FrostyMode.valueOf(str);
    }
}
