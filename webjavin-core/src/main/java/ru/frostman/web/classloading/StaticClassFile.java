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

package ru.frostman.web.classloading;

import com.google.common.io.ByteStreams;
import ru.frostman.web.thr.JavinRuntimeException;

import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class StaticClassFile extends ClassFile {

    public StaticClassFile(String className) {
        super(className, null);

        String realPath = className.replace('.', '/') + ".class";
        try {
            bytes = ByteStreams.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream(realPath));
        } catch (IOException e) {
            throw new JavinRuntimeException("Can't read bytecode of class: " + className);
        }
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    public String getHashCode() {
        return className;
    }
}
