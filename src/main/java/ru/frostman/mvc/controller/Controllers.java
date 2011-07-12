/******************************************************************************
 * Frosty - MVC framework.                                                    *
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

package ru.frostman.mvc.controller;

import ru.frostman.mvc.view.ForwardView;
import ru.frostman.mvc.view.RedirectView;

/**
 * @author slukjanov aka Frostman
 */
public class Controllers {

    public static String forward(String targetUrl) {
        return "forward:" + targetUrl;
    }

    public static View forwardView(String targetUrl) {
        return new ForwardView(targetUrl);
    }

    public static String redirect(String targetUrl) {
        return "redirect:" + targetUrl;
    }

    public static View redirectView(String targetUrl) {
        return new RedirectView(targetUrl);
    }

}
