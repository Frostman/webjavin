package ru.frostman.mvc.controller;

import ru.frostman.mvc.view.ForwardView;
import ru.frostman.mvc.view.RedirectView;

/**
 * @author slukjanov aka Frostman
 */
public class Controllers {

    public static String forward(String targetUrl) {
        return "forward:" + targetUrl;
    }

    public static View forwardView(String targetUrl) {
        return new ForwardView(targetUrl);
    }

    public static String redirect(String targetUrl) {
        return "redirect:" + targetUrl;
    }

    public static View redirectView(String targetUrl) {
        return new RedirectView(targetUrl);
    }

}
