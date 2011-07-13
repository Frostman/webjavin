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

import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.controller.ModelAndView;
import ru.frostman.web.controller.View;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.view.ForwardView;
import ru.frostman.web.view.RedirectView;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public abstract class ActionInvoker implements Runnable {
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected AsyncContext asyncContext;
    protected ModelAndView mav;
    protected boolean async = isAsync();

    public ActionInvoker(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        this.mav = new ModelAndView(request, response);
    }

    public void invoke() {
        if (async && Javin.isAsyncApiSupported() && Javin.getInvoker().getQueueSize() < JavinConfig.getCurrentConfig().getApp().getAsyncQueueLength()) {
            asyncContext = request.startAsync(request, response);
            Javin.getInvoker().execute(this);
        } else {
            async = false;
            run();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        try {
            before();

            try {
                action();
            } catch (ActionException e) {
                catchError(e.getCause());
            }

            after();

            process();
        } catch (Throwable th) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, th.getMessage());
            } catch (IOException e) {
                //todo impl
                e.printStackTrace();
            }

            // todo impl
            th.printStackTrace();
        }

        if (async) {
            asyncContext.complete();
        }
    }

    private void process() throws IOException {
        if (mav == null || mav.getModel() == null || mav.getView() == null) {
            throw new JavinRuntimeException("ModelAndView or Model or View are undefined after action invoked");
        }

        View view = mav.getView();

        if (view instanceof ForwardView) {
            ForwardView forwardView = (ForwardView) view;

            forwardView.setRequest(request);
            forwardView.setResponse(response);
        } else if (view instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) view;

            redirectView.setRequest(request);
            redirectView.setResponse(response);
        }

        mav.process(response.getWriter());
    }

    protected abstract void before();

    protected abstract void action() throws ActionException;

    protected abstract void after();

    protected abstract void catchError(Throwable throwable) throws Throwable;

    protected abstract boolean isAsync();

}
