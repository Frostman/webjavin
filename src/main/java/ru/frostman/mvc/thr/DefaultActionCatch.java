package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class DefaultActionCatch extends FrostyRuntimeException{
    public DefaultActionCatch() {
    }

    public DefaultActionCatch(String message) {
        super(message);
    }

    public DefaultActionCatch(String message, Throwable cause) {
        super(message, cause);
    }

    public DefaultActionCatch(Throwable cause) {
        super(cause);
    }
}
