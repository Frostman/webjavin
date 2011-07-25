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

package ru.frostman.web.dispatch;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.config.StaticResource;
import ru.frostman.web.controller.Controllers;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.thr.NotFoundException;
import ru.frostman.web.util.HttpMethod;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Dispatcher {
    private static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();

    static {
        MIME_MAP.addMimeTypes("application/javascript js");
        MIME_MAP.addMimeTypes("text/css css");
        MIME_MAP.addMimeTypes("image/png png");
    }

    private final List<ActionDefinition> actions;

    public Dispatcher(List<ActionDefinition> actions) {
        this.actions = Lists.newLinkedList(actions);
    }

    public void dispatch(String requestUrl, HttpMethod requestMethod, HttpServletRequest request
            , HttpServletResponse response) {

        if (JavinConfig.get().getContext().equals(requestUrl)) {
            requestUrl += "/";
        }

        if (dispatchStatic(requestUrl, requestMethod, request, response)) {
            return;
        }

        ActionInvoker invoker = null;

        if (invoker == null) {
            for (ActionDefinition definition : actions) {
                if (definition.matches(requestUrl, requestMethod)) {
                    invoker = definition.initInvoker(request, response);

                    break;
                }
            }
        }

        if (invoker == null) {
            sendNotFound(request, response);
        } else {
            try {
                invoker.invoke();
            } catch (NotFoundException e) {
                sendNotFound(request, response);
            }
        }
    }

    private boolean dispatchStatic(String url, HttpMethod requestMethod, HttpServletRequest request,
                                   HttpServletResponse response) {

        //todo think about this
        if (url.contains("..")) {
            return false;
        }

        if (requestMethod.equals(HttpMethod.GET))
            //todo impl caching, think about regexp in statics
            //todo compile expressions on AppClasses#update

            for (Map.Entry<String, StaticResource> entry : JavinConfig.get().getStatics().entrySet()) {
                String fullUrl = Controllers.url(entry.getKey());
                if (url.startsWith(fullUrl)) {
                    String resource = Javin.getApplicationPath() + entry.getValue().getTarget() + "/" + url.substring(fullUrl.length() - 1);

                    try {
                        File resourceFile = new File(resource);
                        FileInputStream resourceStream = new FileInputStream(resourceFile);

                        String contentType = MIME_MAP.getContentType(resourceFile);
                        response.setContentType(contentType);

                        IOUtils.copy(resourceStream, response.getWriter());
                    } catch (IOException e) {
                        throw new JavinRuntimeException("Exception while streaming resource: " + resource, e);
                    }

                    return true;
                }
            }

        return false;
    }

    private void sendNotFound(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
        } catch (IOException e) {
            throw new JavinRuntimeException("Exception while sending 404:Not found", e);
        }
    }
}
