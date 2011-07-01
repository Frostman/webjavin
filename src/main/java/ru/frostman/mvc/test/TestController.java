package ru.frostman.mvc.test;

import ru.frostman.mvc.Model;
import ru.frostman.mvc.annotation.Action;
import ru.frostman.mvc.annotation.After;
import ru.frostman.mvc.annotation.Before;
import ru.frostman.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class TestController {

    @Before
    public void before(HttpServletRequest request) {
        System.out.println("BEFORE: " + request.getMethod());
    }

    @After
    public void after(HttpServletResponse response) {
        System.out.println("AFTER: " + response.getBufferSize());
    }

    @Action("/*")
    public String test2(Model model, @Param(value = "b", required = false) String param) throws IOException {
        model.put("testParam", param);
        System.out.println("ACTION");
        return "test.ftl";
    }
}
