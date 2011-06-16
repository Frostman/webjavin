package ru.frostman.mvc.classloading;

import com.google.common.io.Files;
import ru.frostman.mvc.util.Hex;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author slukjanov aka Frostman
 */
public class ClassFile {
    private String className;
    private File file;
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
        //todo need to cache MessageDigest

        try {
            return Hex.encode(Files.getDigest(file, MessageDigest.getInstance("sha-1")));
        } catch (IOException e) {
            throw new RuntimeException("Can't read file: ", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
