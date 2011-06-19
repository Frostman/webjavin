package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * This annotation is needed to mark methods, invocation of that
 * will be checked by security manager.
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
    /**
     * @return security expression to check
     */
    String value();
}
