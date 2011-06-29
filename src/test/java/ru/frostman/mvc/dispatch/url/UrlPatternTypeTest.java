package ru.frostman.mvc.dispatch.url;

import org.junit.Assert;
import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;
import static ru.frostman.mvc.dispatch.url.UrlPatternType.SERVLET;
import static ru.frostman.mvc.dispatch.url.UrlPatternType.get;
import static ru.frostman.mvc.util.HttpMethod.GET;

/**
 * @author slukjanov aka Frostman
 */
public class UrlPatternTypeTest {

    @Test
    public void simpleTest() {
        UrlPattern url = get("/*", SERVLET, newHashSet(GET));

        Assert.assertTrue(url.matches("/asdasda.asdsadad?asd", GET));

    }

}
