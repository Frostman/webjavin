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

package ru.frostman.web.controller;

import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.AsyncSuspendEvent;
import ru.frostman.web.view.CompleteRequestView;
import ru.frostman.web.view.ForwardView;
import ru.frostman.web.view.RedirectView;
import ru.frostman.web.view.json.JsonModelView;
import ru.frostman.web.view.json.JsonValueView;
import ru.frostman.web.view.json.JsonpValueView;

import java.util.concurrent.TimeUnit;

/**
 * Helper class that provides some methods to work with Views, Models,
 * redirects, forwards, json, etc.
 *
 * You can inherit your controller from it, static import methods or
 * just use it's methods.
 *
 * @author slukjanov aka Frostman
 */
public class Controllers {
    private static final String SLASH = "/";

    /**
     * @param targetUrl to forward
     *
     * @return view name that forward to specified targetUrl
     */
    public static String forwardStr(String targetUrl) {
        return "forward:" + url(targetUrl);
    }

    /**
     * @param targetUrl to forward
     *
     * @return view that forward to specified targetUrl
     */
    public static View forward(String targetUrl) {
        return new ForwardView(url(targetUrl));
    }

    /**
     * @param targetUrl to redirect
     *
     * @return view name that redirect to specified targetUrl
     */
    public static String redirectStr(String targetUrl) {
        return "redirect:" + url(targetUrl);
    }

    /**
     * @param targetUrl to redirect
     *
     * @return view that redirect to specified targetUrl
     */
    public static View redirect(String targetUrl) {
        return new RedirectView(url(targetUrl));
    }

    /**
     * @param targetUrl absolute path to redirect
     *
     * @return view that redirect to specified targetUrl
     */
    public static View redirectAbs(String targetUrl) {
        return new RedirectView(targetUrl);
    }

    /**
     * @param viewName to find view
     *
     * @return view with specified name
     */
    public static View view(String viewName) {
        return Javin.getViews().getViewByName(viewName);
    }

    /**
     * @param value to convert to json
     * @param <T>   value type
     *
     * @return view that convert specified value to json
     */
    public static <T> View json(T value) {
        return new JsonValueView<T>(value);
    }

    /**
     * @param value    to convert to jsonp
     * @param callback to use in jsonp
     * @param <T>      value type
     *
     * @return view that convert specified value to jsonp
     */
    public static <T> View jsonp(T value, String callback) {
        return new JsonpValueView<T>(value, callback);
    }

    /**
     * @return view that convert model to json
     */
    public static View json() {
        return new JsonModelView();
    }

    /**
     * @param relativeUrl to normalize
     *
     * @return absolute url of specified relativeUrl
     */
    public static String url(String relativeUrl) {
        String context = JavinConfig.get().getContext();

        if (relativeUrl.length() != 0 && relativeUrl.charAt(0) == '/') {
            return context + relativeUrl;
        }

        return context + "/" + relativeUrl;
    }

    /**
     * @param relativeUrl to normalize
     *
     * @return full url of specified relativeUrl (http://server.addr/context/relativeUrl)
     */
    public static String urlFull(String relativeUrl) {
        return JavinConfig.get().getAddress() + url(relativeUrl);
    }

    /**
     * Suspends processing of the current request and associated ServletResponse.
     * It is an equivalent of moving request to the end of processing queue.
     */
    public static void suspend() {
        suspend(0, TimeUnit.NANOSECONDS);
    }

    /**
     * Suspends processing of the current request and associated ServletResponse
     * for specified delay in milliseconds.
     *
     * @param delay to suspend
     */
    public static void suspend(long delay) {
        suspend(delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Suspends processing of the current request and associated ServletResponse
     * for specified delay in specified TimeUnit.
     *
     * @param delay    to suspend
     * @param timeUnit of the delay
     */
    public static void suspend(long delay, TimeUnit timeUnit) {
        if (delay == 0) {
            throw AsyncSuspendEvent.SUSPEND_WITHOUT_DELAY;
        }

        throw new AsyncSuspendEvent(delay, timeUnit);
    }

    /**
     * @return view that will complete current request and response processing
     */
    public static View complete() {
        return new CompleteRequestView();
    }
}
