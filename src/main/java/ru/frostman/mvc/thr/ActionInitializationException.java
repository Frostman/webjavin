package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class ActionInitializationException extends FrostyRuntimeException {
    public ActionInitializationException() {
    }

    public ActionInitializationException(String message) {
        super(message);
    }

    public ActionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionInitializationException(Throwable cause) {
        super(cause);
    }
}
