package ru.frostman.mvc.test;

import ru.frostman.mvc.annotation.Wrapper;

import java.lang.reflect.Method;

/**
 * @author slukjanov aka Frostman
 */
public class TestWrappers {

    @Wrapper("test")
    public static Object wrap(String className, Object instance, Method method, Object[] params) throws Exception {
        return method.invoke(instance, params);
    }

}
