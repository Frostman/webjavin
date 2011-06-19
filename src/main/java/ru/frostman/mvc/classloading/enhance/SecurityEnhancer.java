package ru.frostman.mvc.classloading.enhance;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.annotation.Secure;

/**
 * @author slukjanov aka Frostman
 */
public class SecurityEnhancer {

    public static void enhance(ClassPool classPool, CtClass ctClass) {
        for (CtMethod method : EnhancerUtil.getDeclaredMethodsAnnotatedWith(Secure.class, ctClass)) {
            try {
                method.insertBefore("{" + Frosty.class.getName() + ".getSecurityManager().check("
                        + ctClass.getName() + ".class, " +
                        "\"" + method.getName() + "\", \""
                        + ((Secure) method.getAnnotation(Secure.class)).value()
                        + "\", $args);}");
            } catch (Exception e) {
                throw new EnhancerUtil.BytecodeManipulationException("Error in enhancing @Secure method", e);
            }
        }
    }
}
