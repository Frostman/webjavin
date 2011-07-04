package ru.frostman.mvc.el;

import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.mvc.secure.User;

/**
 * @author slukjanov aka Frostman
 */
public class Expressions {
    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        ExecutableStatement expression = SecureExpressions.compile("isAuth(user) && 'asde'.equals(role)");
        boolean result = SecureExpressions.execute(expression, new User(), "asd");
        System.out.println("Result: " + result);
    }
}
