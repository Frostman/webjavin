/******************************************************************************
 * Frosty - MVC framework.                                                    *
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
