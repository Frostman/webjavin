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
import ru.frostman.web.thr.*;
import ru.frostman.web.util.Invoker;
import ru.frostman.web.view.ForwardView;
import ru.frostman.web.view.RedirectView;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author slukjanov aka Frostman
 */
public abstract class ActionInvoker implements Runnable, AsyncActionInvoker {
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected AsyncContext asyncContext;
    protected ModelAndView mav;

    // async part
    protected boolean async = isAsync();
    protected boolean firstRun = true;

    public ActionInvoker(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        this.mav = new ModelAndView(request, response);
    }

    public void invoke() {
        doInvoke(0, TimeUnit.NANOSECONDS);
    }

    private void doInvoke(long delay, TimeUnit timeUnit) {
        Invoker invoker = Javin.getInvoker();
        int asyncQueueLength = JavinConfig.get().getApp().getAsyncQueueLength();

        if (async && Javin.isAsyncApiSupported() && invoker.getQueueSize() < asyncQueueLength) {
            if (asyncContext == null) {
                asyncContext = request.startAsync(request, response);
            }

            invoker.schedule(this, delay, timeUnit);
        } else {
            //todo think about delay
            async = false;
            run();
        }
    }

    @Override
    public void resume() {
        Javin.getInvoker().remove(this);
        invoke();
        //todo think about synchronization of run method
    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        try {
            if (firstRun) {
                before();
                firstRun = false;
            }

            //todo think about actions resuming, not only suspending, see http://docs.codehaus.org/display/JETTY/Continuations
            try {
                action();
            } catch (AsyncSuspendEvent e) {
                //todo think about this
                doInvoke(e.getDelay(), e.getTimeUnit());
                return;
            } catch (ActionException e) {
                //todo think about more info in response about exception, use Throwables.
                catchError(e.getCause());
            }

            after();

            process();
        } catch (ParameterRequiredException e) {
            throw new NotFoundException(e);
        } catch (CsrfTokenNotValidException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Throwable th) {
            throw new JavinRuntimeException("Exception while executing action", th);
        }
        //todo we need to handle ALL exceptions at this level

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

        response.setContentType(view.getContentType());
        response.setCharacterEncoding(view.getCharacterEncoding());

        mav.process(response.getWriter());
    }

    protected abstract void before();

    protected abstract void action() throws ActionException;

    protected abstract void after();

    protected abstract void catchError(Throwable throwable) throws Throwable;

    protected abstract boolean isAsync();

    public boolean isFirstRun() {
        return firstRun;
    }
}
