package ru.frostman.mvc.dispatch;

/**
 * @author slukjanov aka Frostman
 */
public class ActionException extends Throwable {
    public ActionException(String message) {
        super(message);
    }

    public ActionException(Throwable cause) {
        super(cause);
    }
}
