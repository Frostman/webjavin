package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.controller.ModelAndView;
import ru.frostman.mvc.controller.View;
import ru.frostman.mvc.thr.FrostyRuntimeException;
import ru.frostman.mvc.view.ForwardView;
import ru.frostman.mvc.view.RedirectView;

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
            throw new FrostyRuntimeException("ModelAndView or Model or View are undefined after action invoked");
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
