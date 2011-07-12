package ru.frostman.mvc.dispatch;

import ru.frostman.mvc.dispatch.url.UrlPattern;
import ru.frostman.mvc.thr.ActionInitializationException;
import ru.frostman.mvc.util.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
public class ActionDefinition {
    private final List<UrlPattern> urlPatterns;
    private final Set<HttpMethod> methods;
    private final String invokerClassName;
    private Class<? extends ActionInvoker> invokerClass;
    private Constructor<? extends ActionInvoker> invokerClassConstructor;

    public ActionDefinition(List<UrlPattern> urlPatterns, Set<HttpMethod> methods, String invokerClassName) {
        this.urlPatterns = urlPatterns;
        this.methods = methods;
        this.invokerClassName = invokerClassName;
    }

    @SuppressWarnings({"unchecked"})
    public void init(ClassLoader classLoader) {
        try {
            invokerClass = (Class<? extends ActionInvoker>) classLoader.loadClass(invokerClassName);
            invokerClassConstructor = invokerClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class);
        } catch (Exception e) {
            throw new ActionInitializationException("Can't initialize ActionDefinition for ActionInvoker: "
                    + invokerClass.getName());
        }
    }

    public boolean matches(String url, HttpMethod method) {
        if (!methods.contains(method)) {
            return false;
        }

        for (UrlPattern urlPattern : urlPatterns) {
            if (urlPattern.matches(url)) {
                return true;
            }
        }

        return false;
    }

    public ActionInvoker initInvoker(HttpServletRequest request, HttpServletResponse response) {
        try {
            return invokerClassConstructor.newInstance(request, response);
        } catch (Exception e) {
            throw new ActionInitializationException("Can't initialize ActionInvoker: " + invokerClass.getName(), e);
        }
    }

    public List<UrlPattern> getUrlPatterns() {
        return urlPatterns;
    }

    public Set<HttpMethod> getMethods() {
        return methods;
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
