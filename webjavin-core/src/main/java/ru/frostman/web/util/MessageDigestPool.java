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

import com.google.common.collect.Maps;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MessageDigestPool {
    private static final ThreadLocal<Map<String, MessageDigest>> pool = new ThreadLocal<Map<String, MessageDigest>>();

    public static MessageDigest get(String algorithm) throws NoSuchAlgorithmException {
        if (pool.get() == null) {
            pool.set(Maps.<String, MessageDigest>newHashMap());
        }

        MessageDigest messageDigest = pool.get().get(algorithm);

        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance(algorithm);
            pool.get().put(algorithm, messageDigest);
        }

        return messageDigest;
    }
}
