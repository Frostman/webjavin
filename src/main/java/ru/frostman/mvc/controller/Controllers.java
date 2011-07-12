package ru.frostman.mvc.controller;

import ru.frostman.mvc.view.ForwardView;

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

}
