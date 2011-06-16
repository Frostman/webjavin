package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * This annotation is needed to mark action method params to auto
 * inject values from request parameters
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * @return parameter name
     */
    String name();

    /**
     * @return true if param required
     */
    boolean required() default true;
}
