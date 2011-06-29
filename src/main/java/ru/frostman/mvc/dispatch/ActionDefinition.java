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
    private final String invokerClassName;
    private Class<? extends ActionInvoker> invokerClass;
    private Constructor<? extends ActionInvoker> invokerClassConstructor;

    public ActionDefinition(UrlPattern urlPattern, String invokerClassName) {
        this.urlPattern = urlPattern;
        this.invokerClassName = invokerClassName;
    }

    @SuppressWarnings({"unchecked"})
    public void init(ClassLoader classLoader) {
        try {
            invokerClass = (Class<? extends ActionInvoker>) classLoader.loadClass(invokerClassName);
            invokerClassConstructor = invokerClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class);
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }

    public boolean matches(String url) {
        //todo impl http methods checking
        return urlPattern.matches(url);
    }

    public ActionInvoker initInvoker(HttpServletRequest request, HttpServletResponse response) {
        try {
            return invokerClassConstructor.newInstance(request, response);
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public String getInvokerClassName() {
        return invokerClassName;
    }

    public Class<? extends ActionInvoker> getInvokerClass() {
        return invokerClass;
    }

    public Constructor<? extends ActionInvoker> getInvokerClassConstructor() {
        return invokerClassConstructor;
    }
}
