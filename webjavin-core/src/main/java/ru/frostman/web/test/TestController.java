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

import ru.frostman.web.Javin;
import ru.frostman.web.annotation.*;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.view.JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class TestController {

    @Before
    public void before(HttpServletRequest request) {
        System.out.println("BEFORE: " + request.getMethod());
    }

    @After
    public void after(HttpServletResponse response) {
        System.out.println("AFTER: " + response.getBufferSize());
    }

    @Secure("user != null && isAuth() && hasRole('role') && param$1 != null")
    @Action("/test/test")
    public View test(Model model, @Param(value = "b", required = false) String param) throws IOException {
        model.put("testParam", param);

        if ("f".equals(param)) {
            return new JsonView();
        }

        return Javin.getViews().getViewByName("test.ftl");
    }

    @Action("/test/qwe")
    public String qwe(Model model) throws IOException {
        model.put("testParam", "BLA-BLA!!");

        return "test.ftl";
    }
}
