package ru.frostman.mvc.secure;

import java.util.Arrays;

/**
 * @author slukjanov aka Frostman
 */
public class FrostySecurityManager {
    public <T> void check(Class<T> clazz, String method, String expression, Object[] args) {
        System.out.println("SECURE - CHECK: " + clazz.getName() + "#" + method + " :: " + expression + "\nargs:" + Arrays.toString(args));
    }
}
