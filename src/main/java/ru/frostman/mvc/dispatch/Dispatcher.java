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

package ru.frostman.mvc.dispatch;

import com.google.common.collect.Lists;
import ru.frostman.mvc.util.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class Dispatcher {
    private final List<ActionDefinition> actions;

    public Dispatcher(List<ActionDefinition> actions) {
        this.actions = Lists.newLinkedList(actions);
    }

    public ActionInvoker dispatch(String requestUrl, HttpMethod requestMethod, HttpServletRequest request
            , HttpServletResponse response) {
        //todo think about context
        for (ActionDefinition definition : actions) {
            if (definition.matches(requestUrl, requestMethod)) {
                return definition.initInvoker(request, response);
            }
        }

        //todo replace it with default handlers may be or 404 page/error handler
        return null;
    }
}
