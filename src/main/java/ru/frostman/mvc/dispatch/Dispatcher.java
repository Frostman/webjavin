package ru.frostman.mvc.dispatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public class Dispatcher {

    public ActionInvoker dispatch(HttpServletRequest request, HttpServletResponse response) {
        return new ActionInvoker(request, response);
    }

}
