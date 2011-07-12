package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class SecureCheckException extends FrostyRuntimeException {
    public SecureCheckException() {
    }

    public SecureCheckException(String message) {
        super(message);
    }

    public SecureCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecureCheckException(Throwable cause) {
        super(cause);
    }
}
