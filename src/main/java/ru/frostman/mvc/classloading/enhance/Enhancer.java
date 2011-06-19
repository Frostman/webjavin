package ru.frostman.mvc.classloading.enhance;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.classloading.FrostyClasses;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Enhancer {
    private static final ClassPool classPool = new ClassPool();

    static {
        classPool.appendClassPath(new ClassClassPath(FrostyClasses.class));
        classPool.appendSystemPath();
    }

    public static void enhance(Map<String, FrostyClass> classes, FrostyClass frostyClass) {
        CtClass ctClass;
        try {
            ctClass = classPool.makeClass(new ByteArrayInputStream(frostyClass.getBytecode()));
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }

        //...
        ActionsEnhancer.enhance(classes, classPool, ctClass);

        SecurityEnhancer.enhance(classPool, ctClass);

        try {
            frostyClass.setEnhancedBytecode(ctClass.toBytecode());
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }

        ctClass.defrost();
    }

}
