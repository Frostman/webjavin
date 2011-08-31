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

package ru.frostman.web.classloading.enhance;

/**
 * @author slukjanov aka Frostman
 */
public class ClassConstants {
    public static final String HTTP_SERVLET_REQUEST = "javax.servlet.http.HttpServletRequest";
    public static final String SERVLET_REQUEST = "javax.servlet.ServletRequest";
    public static final String HTTP_SERVLET_RESPONSE = "javax.servlet.http.HttpServletResponse";
    public static final String SERVLET_RESPONSE = "javax.servlet.ServletResponse";
    public static final String ASYNC_CONTEXT = "javax.servlet.AsyncContext";
    public static final String VIEW = "ru.frostman.web.controller.View";
    public static final String MODEL = "ru.frostman.web.controller.Model";
    public static final String MODEL_AND_VIEW = "ru.frostman.web.controller.ModelAndView";
    public static final String JAVA_LANG_STRING = "java.lang.String";
    public static final String THROWABLE = "java.lang.Throwable";
    public static final String DEFAULT_ACTION_CATCH = "ru.frostman.web.thr.DefaultActionCatch";
    public static final String ACTION_EXCEPTION = "ru.frostman.web.dispatch.ActionException";
    public static final String JSON_UTIL = "ru.frostman.web.util.Json";
    public static final String JSON_NODE = "org.codehaus.jackson.JsonNode";
    public static final String REQUEST_BODY_JSON = "requestBodyJson";
    public static final String PARAMETER_REQUIRED_EXCEPTION = "ru.frostman.web.thr.ParameterRequiredException";
    public static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
    public static final String JAVIN_SESSION = "ru.frostman.web.session.JavinSession";
    public static final String JAVIN_SESSIONS = "ru.frostman.web.session.JavinSessions";
    public static final String CSRF_PROTECTOR = "ru.frostman.web.secure.CsrfProtector";
    public static final String CSRF_TOKEN_EXCEPTION = "ru.frostman.web.thr.CsrfTokenNotValidException";
}
