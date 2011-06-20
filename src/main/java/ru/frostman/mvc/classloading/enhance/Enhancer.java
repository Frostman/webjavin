package ru.frostman.mvc.classloading.enhance;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.classloading.FrostyClasses;
import ru.frostman.mvc.dispatch.ActionDefinition;

import java.io.ByteArrayInputStream;
import java.util.List;
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

    public static void enhance(Map<String, FrostyClass> classes, FrostyClass frostyClass,
                               List<ActionDefinition> actionDefinitions) {
        if (frostyClass.isGenerated() || frostyClass.getEnhancedBytecode() != null) {
            //todo think about generated classes
            return;
        }

        CtClass ctClass;
        try {
            ctClass = classPool.makeClass(new ByteArrayInputStream(frostyClass.getBytecode()));
        } catch (Exception e) {
            //todo impl
            throw new RuntimeException(e);
        }

        try {
            CtClass superclass = ctClass.getSuperclass();
            if (classes.containsKey(superclass.getName())) {
                //todo think about recursion (stack overflow exception)
                Enhancer.enhance(classes, classes.get(superclass.getName()), actionDefinitions);
            }
        } catch (NotFoundException e) {
            //todo impl
            throw new RuntimeException(e);
        }

        ActionsEnhancer.enhance(classes, classPool, ctClass, actionDefinitions);

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
