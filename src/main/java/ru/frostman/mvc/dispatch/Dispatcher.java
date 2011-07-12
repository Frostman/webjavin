package ru.frostman.mvc.dispatch;

import com.google.common.collect.Lists;
import ru.frostman.mvc.util.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class Dispatcher {
    private final List<ActionDefinition> actions;

    public Dispatcher(List<ActionDefinition> actions) {
        this.actions = Lists.newLinkedList(actions);
    }

    public ActionInvoker dispatch(String requestUrl, HttpMethod requestMethod, HttpServletRequest request
            , HttpServletResponse response) {
        for (ActionDefinition definition : actions) {
            if (definition.matches(requestUrl, requestMethod)) {
                return definition.initInvoker(request, response);
            }
        }

        //todo replace it with default handlers may be
        return null;
    }
}
