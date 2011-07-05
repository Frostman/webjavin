package ru.frostman.mvc;

/**
 * @author slukjanov aka Frostman
 */
public enum FrostyMode {
    DEV, PROD;

    public boolean isDevelopmentMode() {
        return this == DEV;
    }

    public boolean isProductionMode() {
        return this == PROD;
    }
}
