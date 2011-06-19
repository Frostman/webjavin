package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.dispatch.url.UrlPattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;

/**
 * @author slukjanov aka Frostman
 */
public class ActionDefinition {
    private final UrlPattern urlPattern;
    private final Class<? extends ActionInvoker> invokerClass;
    private final Constructor<? extends ActionInvoker> invokerClassConstructor;

    public ActionDefinition(UrlPattern urlPattern, Class<? extends ActionInvoker> invokerClass) {
        this.urlPattern = urlPattern;
        this.invokerClass = invokerClass;

        try {
            invokerClassConstructor = invokerClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class);
        } catch (NoSuchMethodException e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }

    public boolean matches(String url){
        return urlPattern.matches(url);
    }

    public ActionInvoker initInvoker(HttpServletRequest request, HttpServletResponse response) {
        //todo may be generate one big class that contains ActionInvoker and ActionDefinition
        try {
            return invokerClassConstructor.newInstance(request, response);
        } catch (Exception e){
            //todo impl
            throw new RuntimeException(e);
        }
    }
}
