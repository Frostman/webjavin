package ru.frostman.mvc.aop;

import com.google.common.collect.Lists;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.mvc.annotation.Wrapper;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.thr.AopException;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.util.List;
import java.util.Map;

import static ru.frostman.mvc.classloading.enhance.EnhancerUtil.getDeclaredMethodsAnnotatedWith;
import static ru.frostman.mvc.classloading.enhance.EnhancerUtil.isPublicAndStatic;

/**
 * @author slukjanov aka Frostman
 */
public class MethodWrappersUtil {
    public static List<MethodWrapper> findWrappers(Map<String, FrostyClass> classes) {
        try {
            List<MethodWrapper> methodWrappers = Lists.newLinkedList();

            for (Map.Entry<String, FrostyClass> entry : classes.entrySet()) {
                CtClass ctClass = entry.getValue().getCtClass();

                for (CtMethod method : getDeclaredMethodsAnnotatedWith(Wrapper.class, ctClass)) {
                    if (!isPublicAndStatic(method)) {
                        //todo think about static
                        throw new AopException("Wrapper method should be public and static");
                    }

                    if (!method.getReturnType().getName().equals("java.lang.Object")) {
                        throw new AopException("Wrapper method should return Object: " + method.getLongName());
                    }

                    CtClass[] parameterTypes = method.getParameterTypes();
                    if (!parameterTypes[0].getName().equals("java.lang.String")
                            || !parameterTypes[1].getName().equals("java.lang.Object")
                            || !parameterTypes[2].getName().equals("java.lang.reflect.Method")
                            || !parameterTypes[3].isArray() || parameterTypes[3].getName().equals("java.lang.Object")) {
                        throw new AopException("Wrapper method should have correct signature: " + method.getLongName());
                    }

                    //todo check body

                    //todo add to list
                    //todo method signature: public static Object wrap(String className, Object instance, Method method, Object[] params);

                    Wrapper wrapperAnn = (Wrapper) method.getAnnotation(Wrapper.class);
                    methodWrappers.add(new MethodWrapper(ctClass.getName(), method.getName(), wrapperAnn.value()));
                }
            }

            return methodWrappers;
        } catch (Exception e) {
            throw new FrostyRuntimeException("Exception while searching method wrappers", e);
        }
    }
}
