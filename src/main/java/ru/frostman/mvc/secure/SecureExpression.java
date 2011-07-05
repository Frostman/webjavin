package ru.frostman.mvc.secure;

import com.google.common.collect.Maps;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
class SecureExpression {
    private String expressionStr;
    private List<String> paramTypes;
    private ExecutableStatement expression;

    SecureExpression(String expressionStr, List<String> paramTypes) {
        this.expressionStr = expressionStr;
        this.paramTypes = paramTypes;
    }

    void compile() {
        try {
            ParserContext context = new ParserContext();
            context.setStrongTyping(true);

            context.addInput("user", User.class);
            context.addInput("role", String.class);

            int idx = 1;
            for (String paramType : paramTypes) {
                context.addInput("param$" + idx, Frosty.getClasses().getClassLoader().loadClass(paramType));

                idx++;
            }

            context.addImport("isAuth", FrostySecurityManager.class.getMethod("isAuthenticated"));
            context.addImport("hasRole", FrostySecurityManager.class.getMethod("hasRole", String.class));

            expression = (ExecutableStatement) MVEL.compileExpression(expressionStr, context);

            if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
                throw new FrostyRuntimeException("Expression should return boolean");
            }
        } catch (Exception e) {
            throw new FrostyRuntimeException("Can't compile secure expression: " + expressionStr, e);
        }
    }

    boolean execute(User user, String role, Object... params) {
        try {
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
            throw new FrostyRuntimeException("Exception while executing secure expression: " + expressionStr, e);
        }
    }

    public String getExpressionStr() {
        return expressionStr;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public ExecutableStatement getExpression() {
        return expression;
    }
}
