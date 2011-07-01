package ru.frostman.mvc.thr;

/**
 * @author slukjanov aka Frostman
 */
public class ParameterRequiredException extends RuntimeException{
    public ParameterRequiredException() {
    }

    public ParameterRequiredException(String message) {
        super(message);
    }

    public ParameterRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterRequiredException(Throwable cause) {
        super(cause);
    }
}
