package ru.frostman.mvc.view;

import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.controller.Model;
import ru.frostman.mvc.controller.View;
import ru.frostman.mvc.dispatch.ActionInvoker;
import ru.frostman.mvc.thr.FrostyRuntimeException;
import ru.frostman.mvc.util.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class ForwardView extends View {
    private static final String FORWARDS_COUNT = "ru.frostman.mvc.view.ForwardView.forwardsCount";

    private final String targetUrl;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public ForwardView(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        Integer count = (Integer) request.getAttribute(FORWARDS_COUNT);
        if (count == null) {
            count = 0;
        }

        final int maxForwardsCount = FrostyConfig.getCurrentConfig().getApp().getMaxForwardsCount();
        if (count > maxForwardsCount) {
            throw new FrostyRuntimeException("Forwards count more than specified value (" + maxForwardsCount + ")");
        }
        request.setAttribute(FORWARDS_COUNT, count + 1);

        ActionInvoker actionInvoker = Frosty.getClasses().getDispatcher()
                .dispatch(targetUrl, HttpMethod.valueOf(request.getMethod()), request, response);
        if (actionInvoker == null) {
            //todo handle NotFound
        }
        actionInvoker.invoke();
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
