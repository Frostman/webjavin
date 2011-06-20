package ru.frostman.mvc;

import ru.frostman.mvc.dispatch.ActionInvoker;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        //response.setHeader("Transfer-Encoding", "chunked")

        //dispatch static content on /static for example :)

        //think about not modified (http cache)

        //todo iff dev mode
        Frosty.getClasses().update();

        ActionInvoker actionInvoker = Frosty.getClasses().getDispatcher().dispatch(request, response);
        actionInvoker.invoke();

        //we can load the hole page from cache, i think it's cool

    }
}
