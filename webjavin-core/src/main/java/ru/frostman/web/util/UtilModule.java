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

package ru.frostman.web.util;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.annotation.InjectLogger;
import ru.frostman.web.thr.FastRuntimeException;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class UtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new Slf4JTypeListener());

        //todo scan for all app classes and requestStaticInjection
        //todo bind request, response, etc

        //todo take list of classes that required to static inject
        List<Class> classes = Lists.newLinkedList();
        requestStaticInjection(classes.toArray(new Class[classes.size()]));
    }

    static class Slf4JTypeListener implements TypeListener {
        public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
            for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
                    typeEncounter.register(new Slf4JMembersInjector<T>(field));
                }
            }
        }
    }

    static class Slf4JMembersInjector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        Slf4JMembersInjector(Field field) {
            this.field = field;
            logger = LoggerFactory.getLogger(field.getDeclaringClass());
            field.setAccessible(true);
        }

        public void injectMembers(T t) {
            try {
                field.set(t, logger);
            } catch (IllegalAccessException e) {
                throw new FastRuntimeException("Error while injecting slf4j logger into the member of class: '"
                        + t.getClass() + "'", e);
            }
        }
    }
}
