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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.session.JavinSessions;
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
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            //response.setHeader("Transfer-Encoding", "chunked");
            response.setHeader("Server", JavinConfig.get().getApp().getServerHeader());

            if (Javin.getMode().isDevelopmentMode()) {
                Javin.getClasses().update();
                Javin.getClasses().checkSession(request, response);
            }

            JavinSessions.checkSession(request, response);

            Javin.getClasses().getDispatcher()
                    .dispatch(request.getRequestURI(), HttpMethod.valueOf(request.getMethod()), request, response);

            if (Javin.getMode().isDevelopmentMode()) {
                Javin.getClasses().attachUuid(request, response);
            }
        } catch (Throwable th) {
            try {
                log.debug("Sending error: ", th);
                if (Javin.getMode().isDevelopmentMode()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, th.getMessage());
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (IOException e) {
                log.warn("Exception while sending error (" + th.getMessage() + "): ", e);
            }
        }
    }
}
