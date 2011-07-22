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

package ru.frostman.web.indigo.openid;

import ru.frostman.web.controller.Controllers;

/**
 * @author slukjanov aka Frostman
 */
public class OpenId {

    public static String getAuthUrl(String openIdProvider, String targetUrl) {
        return Controllers.url(OpenIdController.AUTH_REDIRECT_URL) + "?"
                + OpenIdController.PARAM_PROVIDER + "=" + openIdProvider + "&"
                + OpenIdController.PARAM_TARGET + "=" + targetUrl;
    }

    public static String getGoogleAuthUrl(String targetUrl) {
        return getAuthUrl(OpenIdController.GOOGLE_ENDPOINT, targetUrl);
    }

}
