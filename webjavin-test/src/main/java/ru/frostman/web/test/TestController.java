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
import ru.frostman.web.annotation.Param;
import ru.frostman.web.annotation.Secure;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.i18n.I18n;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static ru.frostman.web.controller.Controllers.*;

/**
 * @author slukjanov aka Frostman
 */
@Controller
public class TestController {
    private int idx = 0;

    @Secure("true")
    @Action("/test")
    public View test(Model model, @Param(value = "verified", required = false) boolean verified, TestComponent comp
            , UserService userService, UserDetails userDetails) {
        model.put("page", "test" + comp.g() + "<br>"
                + I18n.get("ru", "test")
        ).put("verified", verified).put("version", Javin.getVersion());

        return view("test.ftl");
    }

    private boolean firstTime = true;

    @Action(value = "/async", async = true)
    public View async(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        if (firstTime) {
            printWriter.print("<html><head></head><body>");
        }
        printWriter.print("<script type='text/javascript'>console.log(");
        printWriter.print("'" + (idx++) + ": " + new Date().toString() + "'");
        printWriter.println(");</script>");
        if (firstTime) {
            printWriter.println("</body></html>");
        }
        firstTime = false;
        printWriter.flush();

        if (idx < 10) {
            //todo test it!!!
            suspend(1000);
        }

        return complete();
    }

}
