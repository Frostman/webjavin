package ru.frostman.mvc.annotation;

import ru.frostman.mvc.util.HttpMethod;

import java.lang.annotation.*;

import static ru.frostman.mvc.util.HttpMethod.GET;

/**
 * This annotation is needed to mark action methods in controller
 * classes. It supports async processing.
 *
 * @author slukjanov aka Frostman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    /**
     * @return urls that can be used to access action
     */
    String[] value();

    /**
     * @return supported http methods
     */
    HttpMethod[] method() default GET;

    /**
     * @return use or not async api if it available
     */
    boolean async() default false;

}
