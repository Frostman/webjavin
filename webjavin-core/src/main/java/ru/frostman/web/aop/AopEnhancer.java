/******************************************************************************
 * WebJavin - Java Web Framework.                                             *
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

package ru.frostman.web.aop;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ru.frostman.web.thr.FrostyRuntimeException;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class AopEnhancer {
    public static void enhance(ClassPool classPool, CtClass ctClass, List<MethodWrapper> methodWrappers) {
        try {
            //todo make AOP like filters in servlet-api or see AOP libs like guice

            ctClass.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    //todo check package, className, methodName, param types == SIGNATURE

                    if (!methodCall.getMethodName().equals("test")) {
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("");

                    methodCall.replace(sb.toString());
                }
            });
        } catch (Exception e) {
            //todo impl
            throw new FrostyRuntimeException(e);
        }
    }
}
