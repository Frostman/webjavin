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

package ru.frostman.web.view;

import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class ForwardView extends View {
    private static final String FORWARDS_COUNT = "ru.frostman.web.view.ForwardView.forwardsCount";

    private final String targetUrl;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public ForwardView(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        Integer count = (Integer) request.getAttribute(FORWARDS_COUNT);
        if (count == null) {
            count = 0;
        }

        final int maxForwardsCount = JavinConfig.getCurrentConfig().getApp().getMaxForwardsCount();
        if (count > maxForwardsCount) {
            throw new JavinRuntimeException("Forwards count more than specified value (" + maxForwardsCount + ")");
        }
        request.setAttribute(FORWARDS_COUNT, count + 1);

        Javin.getClasses().getDispatcher()
                .dispatch(targetUrl, HttpMethod.valueOf(request.getMethod()), request, response);
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
