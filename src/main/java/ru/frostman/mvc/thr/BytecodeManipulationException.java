package ru.frostman.mvc.thr;

public class BytecodeManipulationException extends FrostyRuntimeException {
    public BytecodeManipulationException() {
    }

    public BytecodeManipulationException(String message) {
        super(message);
    }

    public BytecodeManipulationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BytecodeManipulationException(Throwable cause) {
        super(cause);
    }
}
