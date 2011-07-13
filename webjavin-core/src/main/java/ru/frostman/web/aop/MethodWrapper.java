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

package ru.frostman.web.aop;

/**
 * @author slukjanov aka Frostman
 */
public class MethodWrapper {
    private final String wrapperClassName;
    private final String wrapperMethodName;
    private final String methodPattern;

    public MethodWrapper(String wrapperClassName, String wrapperMethodName, String methodPattern) {
        this.wrapperClassName = wrapperClassName;
        this.wrapperMethodName = wrapperMethodName;
        this.methodPattern = methodPattern;
    }

    public String getWrapperClassName() {
        return wrapperClassName;
    }

    public String getWrapperMethodName() {
        return wrapperMethodName;
    }

    public String getMethodPattern() {
        return methodPattern;
    }
}
