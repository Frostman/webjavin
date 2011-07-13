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

import com.google.common.io.Files;
import ru.frostman.web.util.Hex;
import ru.frostman.web.util.MessageDigestPool;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author slukjanov aka Frostman
 */
class ClassFile {
    private final String className;
    private final File file;
    private byte[] bytes;

    public ClassFile(String className, File file) {
        this.className = className;
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public synchronized byte[] getBytes() {
        if (bytes == null) {
            try {
                bytes = Files.toByteArray(file);
            } catch (IOException e) {
                throw new RuntimeException("Can't read class file: ", e);
            }
        }

        return bytes;
    }

    public long getLastModified() {
        return file.lastModified();
    }

    public String getHashCode() {
        try {
            return Hex.encode(Files.getDigest(file, MessageDigestPool.get("sha-1")));
        } catch (IOException e) {
            throw new RuntimeException("Can't read file: ", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
