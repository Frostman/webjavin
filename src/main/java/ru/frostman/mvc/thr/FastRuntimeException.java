package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class FastRuntimeException extends RuntimeException {
    public FastRuntimeException() {
    }

    public FastRuntimeException(String message) {
        super(message);
    }

    public FastRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastRuntimeException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
