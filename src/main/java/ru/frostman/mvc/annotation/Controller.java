package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * This annotation is needed to mark classes that contains actions.
 * It supports url prefix to specify, that will be prepended to
 * all action's urls.
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    /**
     * @return Url prefix
     */
    String value() default WITHOUT_PREFIX;

    public static final String WITHOUT_PREFIX = "";
}
