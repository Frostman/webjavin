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

package ru.frostman.web;

import ru.frostman.web.dispatch.ActionInvoker;
import ru.frostman.web.util.HttpMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class DispatcherServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //response.setContentType("text/plain");
            //response.setHeader("Transfer-Encoding", "chunked");

            if (Frosty.getMode().isDevelopmentMode()) {
                Frosty.getClasses().update();
            }

            ActionInvoker actionInvoker = Frosty.getClasses().getDispatcher()
                    .dispatch(request.getRequestURI(), HttpMethod.valueOf(request.getMethod()), request, response);
            if (actionInvoker == null) {
                //todo handle NotFound
            }
            actionInvoker.invoke();
        } catch (Throwable th) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, th.getMessage());
            } catch (IOException e) {
                //todo impl
                e.printStackTrace();
            }

            //todo handle exceptions
            th.printStackTrace();
        }
    }
}
