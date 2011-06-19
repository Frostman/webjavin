package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * This annotation is needed to mark methods that should be
 * invoked after each action method
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
    //todo impl priority
}
