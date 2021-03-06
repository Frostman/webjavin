/******************************************************************************
 * WebJavin - Java Web Framework.                                             *
 *                                                                            *
 * Copyright (c) 2011 - Sergey "Frosman" Lukjanov, me@frostman.ru             *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package ru.frostman.web.annotation;

import ru.frostman.web.util.HttpMethod;

import java.lang.annotation.*;

import static ru.frostman.web.util.HttpMethod.GET;
import static ru.frostman.web.util.HttpMethod.POST;

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
    HttpMethod[] method() default {GET, POST};

    /**
     * @return use or not async api if it available
     */
    boolean async() default false;

}
