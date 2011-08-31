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
import ru.frostman.web.annotation.Action;
import ru.frostman.web.annotation.Controller;
import ru.frostman.web.annotation.CsrfProtected;
import ru.frostman.web.annotation.Param;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.i18n.I18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.frostman.web.controller.Controllers.view;

/**
 * @author slukjanov aka Frostman
 */
@Controller
public class TestController {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public TestController(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Action("/test")
    public View test(Model model, @Param(value = "verified", required = false) boolean verified, TestComponent comp) {
        model.put("page", "test" + comp.g() + "<br>"
                + I18n.get("ru", "test")
        ).put("verified", verified).put("version", Javin.getVersion());

        return view("test.ftl");
    }

    @CsrfProtected
    @Action("/qwe")
    public View qwe(Model model) {
        model.put("page", "qwe");

        return view("test.ftl");
    }

    @Action("/indigo")
    public View indigo(Model model) {
        model.put("page", "qwe");
        return view("/indigo/auth.ftl");
    }

}
