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
import ru.frostman.web.view.ForwardView;
import ru.frostman.web.view.RedirectView;
import ru.frostman.web.view.json.JsonModelView;
import ru.frostman.web.view.json.JsonValueView;

/**
 * @author slukjanov aka Frostman
 */
public class Controllers {
    private static final String SLASH = "/";

    public static String forward(String targetUrl) {
        return "forward:" + url(targetUrl);
    }

    public static View forwardView(String targetUrl) {
        return new ForwardView(url(targetUrl));
    }

    public static String redirect(String targetUrl) {
        return "redirect:" + url(targetUrl);
    }

    public static View redirectView(String targetUrl) {
        return new RedirectView(url(targetUrl));
    }

    public static View view(String viewName) {
        return Javin.getViews().getViewByName(viewName);
    }

    public static <T> View jsonValue(T value) {
        return new JsonValueView<T>(value);
    }

    public static View jsonModel() {
        return new JsonModelView();
    }

    public static String url(String relativeUrl) {
        String context = JavinConfig.getCurrentConfig().getContext();

        if (relativeUrl.length() != 0 && relativeUrl.charAt(0) == '/') {
            return context + relativeUrl;
        }

        return context + "/" + relativeUrl;
    }
}
