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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class LoginServlet extends javax.servlet.http.HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    final static String YAHOO_ENDPOINT = "https://me.yahoo.com";
    final static String GOOGLE_ENDPOINT = "https://www.google.com/accounts/o8/id";

    private ServletContext context;
    private ConsumerManager manager;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
        this.manager = new ConsumerManager();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("------------------------");
        log.debug("context: " + context);
        Identifier identifier = this.verifyResponse(req);
        log.debug("identifier: " + identifier);
        // if openid login succeded redirect to home page using our demo account
        //if your site is open to anyone without login you can do the redirect directly
        if (identifier != null) {
            // ALL GOOD
            resp.sendRedirect("/index.jsp");
        } else {
            System.out.println("login with openid failed");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String identifier = req.getParameter("identifier");
        this.authRequest(identifier, req, resp);
    }

    // --- placing the authentication request ---
    public String authRequest(String userSuppliedString,
                              HttpServletRequest httpReq, HttpServletResponse httpResp)
            throws IOException {
        try {
            // configure the return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            String returnToUrl = httpReq.getRequestURL().toString();

            // --- Forward proxy setup (only if needed) ---
            // ProxyProperties proxyProps = new ProxyProperties();
            // proxyProps.setProxyName("proxy.example.com");
            // proxyProps.setProxyPort(8080);
            // HttpClientFactory.setProxyProperties(proxyProps);

            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(userSuppliedString);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            httpReq.getSession().setAttribute("openid-disc", discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            FetchRequest fetch = FetchRequest.createFetchRequest();
            if (userSuppliedString.startsWith(GOOGLE_ENDPOINT)) {
                fetch.addAttribute("email",
                        "http://axschema.org/contact/email", true);
                fetch.addAttribute("firstName",
                        "http://axschema.org/namePerson/first", true);
                fetch.addAttribute("lastName",
                        "http://axschema.org/namePerson/last", true);
            } else if (userSuppliedString.startsWith(YAHOO_ENDPOINT)) {
                fetch.addAttribute("email",
                        "http://axschema.org/contact/email", true);
                fetch.addAttribute("fullname",
                        "http://axschema.org/namePerson", true);
            } else { // works for myOpenID
                fetch.addAttribute("fullname",
                        "http://schema.openid.net/namePerson", true);
                fetch.addAttribute("email",
                        "http://schema.openid.net/contact/email", true);
            }

            // attach the extension to the authentication request
            authReq.addExtension(fetch);

            httpResp.sendRedirect(authReq.getDestinationUrl(true));

        } catch (OpenIDException e) {
            // present error to the user
        }

        return null;
    }

    // --- processing the authentication response ---
    public Identifier verifyResponse(HttpServletRequest httpReq) {
        try {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response = new ParameterList(
                    httpReq.getParameterMap());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = (DiscoveryInformation) httpReq
                    .getSession().getAttribute("openid-disc");

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(httpReq.getQueryString());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(
                    receivingURL.toString(), response, discovered);

            // examine the verification result and extract the verified
            // identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification
                        .getAuthResponse();

                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse) authSuccess
                            .getExtension(AxMessage.OPENID_NS_AX);

                    List emails = fetchResp.getAttributeValues("email");
                    String email = (String) emails.get(0);
                    log.info("OpenIdlogin done with email: " + email);
                }

                return verified; // success
            }
        } catch (OpenIDException e) {
            // present error to the user
        }

        return null;
    }

}
