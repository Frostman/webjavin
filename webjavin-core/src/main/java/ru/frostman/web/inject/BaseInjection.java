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

package ru.frostman.web.inject;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class BaseInjection implements InjectionRule {
    private final List<String> annotations;
    private final List<String> classNames;
    private final String name;
    private final String initCode;
    private final String bodyCode;

    public BaseInjection(List<String> annotations, List<String> classNames, String name, String initCode, String bodyCode) {
        this.annotations = annotations;
        this.classNames = classNames;
        this.name = name;
        this.initCode = initCode;
        this.bodyCode = bodyCode;
    }

    @Override
    public List<String> classNames() {
        return classNames;
    }

    @Override
    public List<String> annotations() {
        return annotations;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String initCode() {
        return initCode;
    }

    public String prependCode() {
        return bodyCode;
    }
}
