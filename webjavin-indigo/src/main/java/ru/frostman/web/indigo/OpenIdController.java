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

package ru.frostman.web.indigo;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.annotation.Action;
import ru.frostman.web.annotation.Controller;
import ru.frostman.web.annotation.Param;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.controller.Controllers;
import ru.frostman.web.controller.View;
import ru.frostman.web.session.JavinSession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.frostman.web.controller.Controllers.redirect;
import static ru.frostman.web.controller.Controllers.redirectAbs;

/**
 * @author slukjanov aka Frostman
 */
@Controller
public class OpenIdController {
    private static final Logger log = LoggerFactory.getLogger(OpenIdController.class);

    private final static String YAHOO_ENDPOINT = "https://me.yahoo.com";
    private final static String GOOGLE_ENDPOINT = "https://www.google.com/accounts/o8/id";

    private static final String OPENID_DISCOVERY = "openid-discovery";
    private static final String CALLBACK_URL = "/javin/indigo/openid/callback";

    //todo think about thread safety
    private static final ConsumerManager manager = new ConsumerManager();

    //todo remove test url
    // http://localhost:8080/test/javin/indigo/openid/sendAuth?identifier=https://www.google.com/accounts/o8/id&targetUrl=/test

    @Action("/javin/indigo/openid/sendAuth")
    public View sendAuthRequest(@Param("identifier") String userSuppliedString, JavinSession session,
                                @Param("targetUrl") String targetUrl, HttpServletRequest request) throws OpenIDException {
        // perform discovery on the user-supplied identifier
        List discoveries = manager.discover(userSuppliedString);

        // attempt to associate with the OpenID provider
        // and retrieve one service endpoint for authentication
        DiscoveryInformation discovered = manager.associate(discoveries);

        // store the discovery information in the user's session
        session.setAttribute(OPENID_DISCOVERY, discovered);

        // obtain a AuthRequest message to be sent to the OpenID provider
        String callbackUrl = JavinConfig.get().getAddress() + Controllers.url(CALLBACK_URL) + "?targetUrl=" + targetUrl;
        AuthRequest authReq = manager.authenticate(discovered, callbackUrl);

        FetchRequest fetch = FetchRequest.createFetchRequest();
        if (userSuppliedString.startsWith(GOOGLE_ENDPOINT)) {
            fetch.addAttribute("email", "http://axschema.org/contact/email", true);
//            fetch.addAttribute("firstName", "http://axschema.org/namePerson/first", true);
//            fetch.addAttribute("lastName", "http://axschema.org/namePerson/last", true);
        } else if (userSuppliedString.startsWith(YAHOO_ENDPOINT)) {
            fetch.addAttribute("email", "http://axschema.org/contact/email", true);
//            fetch.addAttribute("fullname", "http://axschema.org/namePerson", true);
        } else { // works for myOpenID
            fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
//            fetch.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
        }

        // attach the extension to the authentication request
        authReq.addExtension(fetch);

        return redirectAbs(authReq.getDestinationUrl(true));
    }

    @Action(CALLBACK_URL)
    public View callbackVerify(HttpServletRequest request, JavinSession session,
                               @Param("targetUrl") String targetUrl) throws OpenIDException {
        // extract the parameters from the authentication response
        // (which comes in as a HTTP request from the OpenID provider)
        ParameterList response = new ParameterList(request.getParameterMap());

        // retrieve the previously stored discovery information
        DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute(OPENID_DISCOVERY);

        // extract the receiving URL from the HTTP request
        StringBuffer receivingURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(request.getQueryString());

        // verify the response; ConsumerManager needs to be the same
        // (static) instance used to place the authentication request
        VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);

        // examine the verification result and extract the verified
        // identifier
        Identifier verified = verification.getVerifiedId();
        if (verified != null) {
            AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

            if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);

                List emails = fetchResp.getAttributeValues("email");
                String email = (String) emails.get(0);

                log.info("OpenId login done with email: " + email);
            }

            //todo store credentials in session
        }

        return redirect(targetUrl + "?verified=" + (verified != null));
    }
}
