package ru.frostman.mvc.test;

import ru.frostman.mvc.Model;
import ru.frostman.mvc.annotation.Action;
import ru.frostman.mvc.annotation.Param;

import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class TestController {
    @Action("/*")
    public String test2(Model model, @Param("a") String param) throws IOException {
        model.getAndPut("testParam", param);

        return "test.ftl";
    }
}
