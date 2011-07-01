package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class EnhancerException extends FrostyRuntimeException{
    public EnhancerException() {
    }

    public EnhancerException(String message) {
        super(message);
    }

    public EnhancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnhancerException(Throwable cause) {
        super(cause);
    }
}
