package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.Frosty;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public abstract class ActionInvoker implements Runnable {
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected AsyncContext asyncContext;
    protected boolean async = isAsync();

    public ActionInvoker(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    //todo add callback
    public void invoke() {
        //todo если очередь не переполнилась и это long_action то в фоне иначе прям тут

        //todo remove hard code
        if (async && Frosty.isAsyncApiSupported() && Frosty.getInvoker().getQueueSize() < 100) {
            asyncContext = request.startAsync(request, response);
            //todo think about async listener
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
            } catch (Throwable actionThrowable) {
                //todo move this try into the action method
                //todo may be throw specific exception from action that needed to catched by catchError
                catchError(actionThrowable);
            }

            after();
        } catch (Throwable th) {
            //todo impl http error send
            th.printStackTrace();
        }

        if (async) {
            asyncContext.complete();
        }
    }

    protected abstract void before();

    protected abstract void action();

    protected abstract void after();

    protected abstract void catchError(Throwable throwable);

    protected abstract boolean isAsync();

}
