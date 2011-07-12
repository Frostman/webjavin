package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrapper {
    /**
     * @return pattern that specifies methods to be wrapped with
     */
    String value();
}
