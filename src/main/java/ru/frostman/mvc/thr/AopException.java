package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class AopException extends FrostyRuntimeException {
    public AopException() {
    }

    public AopException(String message) {
        super(message);
    }

    public AopException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopException(Throwable cause) {
        super(cause);
    }
}
