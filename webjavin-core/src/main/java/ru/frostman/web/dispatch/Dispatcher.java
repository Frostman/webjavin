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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.config.StaticResource;
import ru.frostman.web.controller.Controllers;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.thr.NotFoundException;
import ru.frostman.web.util.Crypto;
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

    private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    private static final String HEADER_E_TAG = "ETag";
    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String HEADER_EXPIRES = "Expires";

    public static final long DEFAULT_EXPIRE_TIME = 604800000L;

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

                        long length = resourceFile.length();
                        long lastModified = resourceFile.lastModified();
                        String eTag = Crypto.hash(resourceFile.getName() + "_" + length + "_" + lastModified);

                        // Check headers for caching

                        // If-None-Match header should contain "*" or ETag. If so, then return 304.
                        String ifNoneMatch = request.getHeader(HEADER_IF_NONE_MATCH);
                        if (ifNoneMatch != null && (Objects.equal(ifNoneMatch, eTag) || Objects.equal(ifNoneMatch, "*"))) {
                            response.setHeader(HEADER_E_TAG, eTag); // Required in 304.

                            //todo extract into method
                            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                            return true;
                        }

                        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
                        // This header is ignored if any If-None-Match header is specified.
                        long ifModifiedSince = request.getDateHeader(HEADER_IF_MODIFIED_SINCE);
                        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
                            response.setHeader(HEADER_E_TAG, eTag); // Required in 304.

                            //todo extract into method
                            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                            return true;
                        }

                        //todo think about random access impl
                        FileInputStream resourceStream = new FileInputStream(resourceFile);

                        String contentType = MIME_MAP.getContentType(resourceFile);
                        response.setContentType(contentType);
                        //todo think about setting encoding for text
                        response.setCharacterEncoding(null);

                        response.setHeader(HEADER_E_TAG, eTag);
                        response.setDateHeader(HEADER_LAST_MODIFIED, lastModified);

                        //todo add expire time into configurations
                        response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

                        IOUtils.copy(resourceStream, response.getOutputStream());
                    } catch (IOException e) {
                        //todo think about this
                        // no operations
                        continue;
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
