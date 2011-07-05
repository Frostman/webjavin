package ru.frostman.mvc.secure;

import com.google.common.collect.Maps;
import ru.frostman.mvc.thr.FrostyIllegalAccessException;
import ru.frostman.mvc.thr.SecureCheckException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author slukjanov aka Frostman
 */
public class FrostySecurityManager {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<User>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<String>();

    private AtomicInteger counter = new AtomicInteger();
    private Map<Integer, SecureExpression> expressions = Maps.newHashMap();

    public int register(String expressionStr, List<String> paramClasses) {
        int id = counter.getAndIncrement();
        expressions.put(id, new SecureExpression(expressionStr, paramClasses));

        return id;
    }

    public synchronized void compileAll() {
        final int length = counter.get();
        for (int i = 0; i < length; i++) {
            expressions.get(i).compile();
        }
    }

    public static boolean isAuthenticated() {
        return currentUser.get() != null;
    }

    public static boolean hasRole(String role) {
        return currentRole.get().equals(role);
    }

    public <T> void check(Class<T> clazz, String method, int expressionId, Object[] args) {
        try {
            SecureExpression expression = expressions.get(expressionId);

            //todo impl it
            User user = new User();
            String role = "role";

            currentUser.set(user);
            currentRole.set(role);

            if (!expression.execute(user, role, args)) {
                throw new FrostyIllegalAccessException("Secure expression returns false");
            }
        } catch (FrostyIllegalAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new SecureCheckException("Can't check secure expression for method: " + clazz.getName() + "#" + method, e);
        } finally {
            currentUser.remove();
            currentRole.remove();
        }
    }
}
