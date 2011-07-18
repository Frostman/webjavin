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

package ru.frostman.web.test;

import ru.frostman.web.annotation.Action;
import ru.frostman.web.annotation.Controller;
import ru.frostman.web.annotation.JsonParam;
import ru.frostman.web.annotation.JsonResponse;
import ru.frostman.web.mongo.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author slukjanov aka Frostman
 */
@Controller
public class TestController {

    @Action("/test/*")
    @JsonResponse
    public User test(HttpServletRequest request, @JsonParam(name = {"test"}) User user) {
//        User user = new User();
//        user.setUsername("test");

        return user;
    }

}
