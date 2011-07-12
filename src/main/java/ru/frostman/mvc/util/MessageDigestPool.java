package ru.frostman.mvc.util;

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
