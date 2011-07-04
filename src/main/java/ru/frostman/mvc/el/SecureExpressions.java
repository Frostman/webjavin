package ru.frostman.mvc.el;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.mvc.annotation.Secure;
import ru.frostman.mvc.secure.User;

import javax.management.RuntimeErrorException;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class SecureExpressions {

    public static ExecutableStatement compile(String expressionStr) {
        ParserContext context = new ParserContext();
        context.setStrongTyping(true);

        context.addInput("user", User.class);
        context.addInput("role", String.class);

        try {
            context.addImport("isAuth", SecureExpressions.class.getMethod("isAuthenticated", User.class));
        } catch (NoSuchMethodException e) {
            //todo impl
            throw new RuntimeException(e);
        }

        //todo inject all method args as param$1, param$2, etc
        //todo подсовывать пользователя, его роль и тд в ThreadLocal чтобы упростить вызовы

        ExecutableStatement expression = (ExecutableStatement) MVEL.compileExpression(expressionStr, context);

        if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
            //todo impl
            throw new RuntimeException("Expression should return boolean");
        }

        return expression;
    }

    public static boolean execute(ExecutableStatement expression, User user, String role, Object... params) {
        Preconditions.checkNotNull(expression);

        Map<String, Object> vars = Maps.newHashMap();
        vars.put("user", user);
        vars.put("role", role);

        int idx = 1;
        for (Object param : params) {
            vars.put("param$" + idx, param);

            idx++;
        }

        return (Boolean) MVEL.executeExpression(expression, vars);
    }

    //todo think about this
    public static boolean isAuthenticated(User user) {
        return user != null;
    }
}
