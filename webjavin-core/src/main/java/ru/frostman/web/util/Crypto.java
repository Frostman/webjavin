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

import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.JavinRuntimeException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides cryptography features such as hmac.
 *
 * @author slukjanov aka Frostman
 */
public class Crypto {
    public static final String HMAC_ALGORITHM = "HmacSHA384";

    public static String sign(String message) {
        return sign(message, JavinConfig.get().getApp().getSecretBytes());
    }

    public static boolean checkSign(String message, String signature) {
        return checkSign(message, signature, JavinConfig.get().getApp().getSecretBytes());
    }

    public static String sign(String message, byte[] key) {
        if (key.length == 0) {
            return message;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA384");
            mac.init(signingKey);
            byte[] signature = mac.doFinal(message.getBytes("utf-8"));

            return Codec.encodeBase64(signature);
        } catch (Exception e) {
            throw new JavinRuntimeException(e);
        }
    }

    public static boolean checkSign(String message, String signature, byte[] key) {
        return signature.equals(sign(message, key));
    }
}
