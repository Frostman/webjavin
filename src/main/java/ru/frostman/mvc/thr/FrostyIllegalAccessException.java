package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyIllegalAccessException extends FrostyRuntimeException{
    public FrostyIllegalAccessException() {
    }

    public FrostyIllegalAccessException(String message) {
        super(message);
    }

    public FrostyIllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrostyIllegalAccessException(Throwable cause) {
        super(cause);
    }
}
