package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyRuntimeException extends RuntimeException{
    public FrostyRuntimeException() {
    }

    public FrostyRuntimeException(String message) {
        super(message);
    }

    public FrostyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrostyRuntimeException(Throwable cause) {
        super(cause);
    }
}
