package ru.frostman.mvc.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.annotation.Secure;
import ru.frostman.mvc.thr.BytecodeManipulationException;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class SecurityEnhancer {

    public static void enhance(ClassPool classPool, CtClass ctClass) {
        for (CtMethod method : EnhancerUtil.getDeclaredMethodsAnnotatedWith(Secure.class, ctClass)) {
            try {
                Secure secureAnnotation = (Secure) method.getAnnotation(Secure.class);

                List<String> paramClasses = Lists.newLinkedList();
                for (CtClass paramCtClass : method.getParameterTypes()) {
                    paramClasses.add(paramCtClass.getName());
                }

                int expressionId = Frosty.getClasses().getSecurityManager()
                        .register(secureAnnotation.value(), paramClasses);

                method.insertBefore("{" + Frosty.class.getName() + ".getClasses().getSecurityManager().check("
                        + ctClass.getName() + ".class, " +
                        "\"" + method.getName() + "\", "
                        + expressionId
                        + ", $args);}");
            } catch (Exception e) {
                throw new BytecodeManipulationException("Error in enhancing @Secure method", e);
            }
        }
    }
}
