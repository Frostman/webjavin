package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.Frosty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public class ActionInvoker implements Runnable {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public ActionInvoker(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    //todo add callback
    public void invoke() {
        //todo если очередь не переполнилась и это long_action то в фоне иначе прям тут

        if (Frosty.isAsyncApiSupported()) {
            // add to queue
        } else {
            // run here
            run();
        }
    }

    //todo generate invokers for each Action

    @Override
    public void run() {

    }
}
