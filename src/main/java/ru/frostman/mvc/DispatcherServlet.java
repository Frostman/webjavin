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
        try {
            response.setContentType("text/plain");
            response.setHeader("Transfer-Encoding", "chunked");

            if (Frosty.getMode().isDevelopmentMode()) {
                Frosty.getClasses().update();
            }

            ActionInvoker actionInvoker = Frosty.getClasses().getDispatcher().dispatch(request, response);
            actionInvoker.invoke();
        } catch (Throwable th) {
            //todo handle exceptions
            th.printStackTrace();
        }
    }
}
