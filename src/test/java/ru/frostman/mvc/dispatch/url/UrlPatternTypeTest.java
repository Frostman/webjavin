package ru.frostman.mvc.dispatch.url;

import org.junit.Assert;
import org.junit.Test;

import static ru.frostman.mvc.dispatch.url.UrlPatternType.SERVLET;
import static ru.frostman.mvc.dispatch.url.UrlPatternType.get;

/**
 * @author slukjanov aka Frostman
 */
public class UrlPatternTypeTest {

    @Test
    public void simpleTest() {
        UrlPattern url = get("/*", SERVLET);

        Assert.assertTrue(url.matches("/asdasda.asdsadad?asd"));

    }

}
