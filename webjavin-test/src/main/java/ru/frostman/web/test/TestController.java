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
import ru.frostman.web.annotation.Param;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.mongo.User;
import ru.frostman.web.view.JsonValueView;

import static ru.frostman.web.controller.Controllers.view;

/**
 * @author slukjanov aka Frostman
 */
@Controller
public class TestController {

    @Action("/test/*")
    public View test(Model model, @Param("a") String testParam, @Param(value = "b", required = false) String testParam2) {

        model.put("testParam", testParam);
        model.put("testParam2", testParam2);

        if("page".equals(testParam2)) {
            return view("test.ftl");
        }

        User user = new User();
        user.setUsername(testParam);

        return new JsonValueView<User>(user);
    }

}
