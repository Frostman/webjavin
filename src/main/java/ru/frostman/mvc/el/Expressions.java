package ru.frostman.mvc.el;

import ru.frostman.mvc.secure.User;

/**
 * @author slukjanov aka Frostman
 */
public class Expressions {
    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        int id = SecureExpressions.compile("isAuth() || 'asd'.equals(param$1)", String.class);
        boolean result = SecureExpressions.execute(id, new User(), "asd", "asd");
        System.out.println("Result: " + result);
    }
}
