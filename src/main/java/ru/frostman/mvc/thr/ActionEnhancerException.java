package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class ActionEnhancerException extends BytecodeManipulationException {
    public ActionEnhancerException() {
    }

    public ActionEnhancerException(String message) {
        super(message);
    }

    public ActionEnhancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionEnhancerException(Throwable cause) {
        super(cause);
    }
}
