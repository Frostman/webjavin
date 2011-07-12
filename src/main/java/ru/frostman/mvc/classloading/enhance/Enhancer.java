package ru.frostman.mvc.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import ru.frostman.mvc.aop.AopEnhancer;
import ru.frostman.mvc.aop.MethodWrapper;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.classloading.FrostyClasses;
import ru.frostman.mvc.dispatch.ActionDefinition;
import ru.frostman.mvc.thr.EnhancerException;
import ru.frostman.mvc.thr.FrostyRuntimeException;

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

    public static void prepareClasses(Map<String, FrostyClass> classes) {
        for (FrostyClass frostyClass : Lists.newLinkedList(classes.values())) {
            frostyClass.setEnhancedBytecode(null);

            if (frostyClass.isGenerated()) {
                classes.remove(frostyClass.getName());
                continue;
            }

            CtClass ctClass;
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(frostyClass.getBytecode()));
            } catch (Exception e) {
                throw new EnhancerException(e);
            }

            frostyClass.setCtClass(ctClass);
        }

    }

    public static void enhance(Map<String, FrostyClass> classes, FrostyClass frostyClass,
                               List<ActionDefinition> actionDefinitions, List<MethodWrapper> methodWrappers) {
        if (frostyClass.isGenerated() || frostyClass.getEnhancedBytecode() != null) {
            return;
        }

        CtClass ctClass = frostyClass.getCtClass();

        try {
            CtClass superclass = ctClass.getSuperclass();
            if (classes.containsKey(superclass.getName())) {
                Enhancer.enhance(classes, classes.get(superclass.getName()), actionDefinitions, methodWrappers);
            }
        } catch (NotFoundException e) {
            throw new FrostyRuntimeException(e);
        }

        ActionsEnhancer.enhance(classes, classPool, ctClass, actionDefinitions);
        AopEnhancer.enhance(classPool, ctClass, methodWrappers);
        SecurityEnhancer.enhance(classPool, ctClass);

        try {
            frostyClass.setEnhancedBytecode(ctClass.toBytecode());
        } catch (Exception e) {
            throw new EnhancerException(e);
        }

        ctClass.defrost();
        ctClass.detach();
    }

}
