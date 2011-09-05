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

package ru.frostman.web.secure.inject;

import com.google.common.collect.Lists;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.inject.BaseInjection;
import ru.frostman.web.inject.InjectionRule;
import ru.frostman.web.secure.JavinSecurityManager;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserServiceProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class SecureSupport {
    public static final String SECURITY_MANAGER_GET = "ru.frostman.web.secure.JavinSecurityManager.get()";

    public static void buildSecureInjectionRules(Map<String, AppClass> classes, List<InjectionRule> injectionRules) {
        UserServiceProvider provider = JavinSecurityManager.get().getUserServiceProvider();

        LinkedList<String> classNames = Lists.<String>newLinkedList();
        classNames.add(UserDetails.class.getName());
        classNames.add(provider.getUserDetailsClass().getName());

        InjectionRule rule = new BaseInjection(
                Lists.<String>newLinkedList(),
                classNames,
                "",
                SECURITY_MANAGER_GET + ".getUserService().extract(request, response)",
                ""
        );

        injectionRules.add(rule);
    }
}
