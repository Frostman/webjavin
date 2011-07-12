package ru.frostman.mvc.aop;

/**
 * @author slukjanov aka Frostman
 */
public class MethodWrapper {
    private final String wrapperClassName;
    private final String wrapperMethodName;
    private final String methodPattern;

    public MethodWrapper(String wrapperClassName, String wrapperMethodName, String methodPattern) {
        this.wrapperClassName = wrapperClassName;
        this.wrapperMethodName = wrapperMethodName;
        this.methodPattern = methodPattern;
    }

    public String getWrapperClassName() {
        return wrapperClassName;
    }

    public String getWrapperMethodName() {
        return wrapperMethodName;
    }

    public String getMethodPattern() {
        return methodPattern;
    }
}
