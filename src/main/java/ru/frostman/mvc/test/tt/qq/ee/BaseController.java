package ru.frostman.mvc.test.tt.qq.ee;

import ru.frostman.mvc.annotation.After;
import ru.frostman.mvc.annotation.Before;

/**
 * @author slukjanov aka Frostman
 */
public class BaseController {
    @Before
    public void baseBefore3() {
        System.out.println("before test base3");
    }

    @After
    public void baseAfter12() {
        System.out.println("after test base12");
    }
}
