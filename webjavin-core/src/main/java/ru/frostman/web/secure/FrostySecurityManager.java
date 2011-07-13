/******************************************************************************
 * Frosty - MVC framework.                                                    *
 *                                                                            *
 * Copyright (c) 2011 - Sergey "Frosman" Lukjanov, me@frostman.ru             *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package ru.frostman.web.secure;

import com.google.common.collect.Maps;
import ru.frostman.web.thr.FrostyIllegalAccessException;
import ru.frostman.web.thr.SecureCheckException;

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
            User user = new User(){
                @Override
                public String getLogin() {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void getRole() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void hasPermission() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void hasRole() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            };
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
