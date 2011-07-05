package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.controller.ModelAndView;
import ru.frostman.mvc.thr.FrostyRuntimeException;

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
        if (async && Frosty.isAsyncApiSupported() && Frosty.getInvoker().getQueueSize() < FrostyConfig.getCurrentConfig().getApp().getAsyncQueueLength()) {
            asyncContext = request.startAsync(request, response);
            Frosty.getInvoker().execute(this);
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

            if (mav == null || mav.getModel() == null || mav.getView() == null) {
                throw new FrostyRuntimeException("ModelAndView or Model or View are undefined after action invoked");
            }
            mav.process(response.getWriter());
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

    protected abstract void before();

    protected abstract void action() throws ActionException;

    protected abstract void after();

    protected abstract void catchError(Throwable throwable) throws Throwable;

    protected abstract boolean isAsync();

}
