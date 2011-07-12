package ru.frostman.mvc.view;

import ru.frostman.mvc.controller.Model;
import ru.frostman.mvc.controller.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class RedirectView extends View {
    private final String targetUrl;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public RedirectView(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        //todo think about context and async (if response already begin)
        try {
            response.sendRedirect(targetUrl);
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }
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
