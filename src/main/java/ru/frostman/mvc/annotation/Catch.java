package ru.frostman.mvc.annotation;

import java.lang.annotation.*;

/**
 * This annotation is needed to mark methods that should be
 * invoked iff action throws some Throwable. This method should
 * take only one parameter - Throwable.
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Catch {
    //todo check that @catch method is only one
}
