package ru.frostman.mvc.test;

import ru.frostman.mvc.annotation.*;
import ru.frostman.mvc.controller.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.frostman.mvc.controller.Controllers.redirect;

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

    @Secure("user != null && isAuth() && hasRole('role') && param$1 != null")
    @Action("/test/test")
    public String test(Model model, @Param(value = "b", required = false) String param) throws IOException {
        model.put("testParam", param);

        if ("f".equals(param)) {
            return redirect("/test/qweqw");
        }

        return "test.ftl";
    }

    @Action("/test/qwe")
    public String qwe(Model model) throws IOException {
        model.put("testParam", "BLA-BLA!!");

        return "test.ftl";
    }
}
