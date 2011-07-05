package ru.frostman.mvc.el;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.mvc.secure.User;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author slukjanov aka Frostman
 */
public class SecureExpressions {
    private static AtomicInteger counter = new AtomicInteger();
    private static Map<Integer, ExecutableStatement> expressions = Maps.newHashMap();
    //todo think about sync and updating

    private static final ThreadLocal<User> currentUser = new ThreadLocal<User>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<String>();

    public static int compile(String expressionStr, Class... paramTypes) {
        try {
            ParserContext context = new ParserContext();
            context.setStrongTyping(true);

            context.addInput("user", User.class);
            context.addInput("role", String.class);

            int idx = 1;
            for (Class paramType : paramTypes) {
                context.addInput("param$" + idx, paramType);

                idx++;
            }

            context.addImport("isAuth", SecureExpressions.class.getMethod("isAuthenticated"));

            ExecutableStatement expression = (ExecutableStatement) MVEL.compileExpression(expressionStr, context);
            if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
                throw new FrostyRuntimeException("Expression should return boolean");
            }

            int id = counter.getAndIncrement();
            expressions.put(id, expression);

            return id;
        } catch (Exception e) {
            throw new FrostyRuntimeException("Can't compile secure expression: " + expressionStr, e);
        }
    }

    public static boolean execute(int expressionId, User user, String role, Object... params) {
        try {
            ExecutableStatement expression = expressions.get(expressionId);
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
        } catch (Exception e) {
            throw new FrostyRuntimeException("Exception while executing secure expression with id: " + expressionId, e);
        }
    }

    public static boolean isAuthenticated() {
        return false;
    }
}
