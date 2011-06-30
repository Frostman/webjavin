package ru.frostman.mvc.test;

import ru.frostman.mvc.Model;
import ru.frostman.mvc.annotation.Action;
import ru.frostman.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class TestController {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Model model;

    public TestController(HttpServletRequest request, HttpServletResponse response, Model model) {
        this.request = request;
        this.response = response;
        this.model = model;
    }

    @Action("/*")
    public String test2(Model model, @Param(value = "b", required = false) String param) throws IOException {
        model.put("testParam", param);
        System.out.println();
        return "test.ftl";
    }
}
