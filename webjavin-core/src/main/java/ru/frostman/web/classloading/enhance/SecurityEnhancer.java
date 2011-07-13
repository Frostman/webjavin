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

package ru.frostman.web.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.web.Frosty;
import ru.frostman.web.annotation.Secure;
import ru.frostman.web.thr.BytecodeManipulationException;

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
