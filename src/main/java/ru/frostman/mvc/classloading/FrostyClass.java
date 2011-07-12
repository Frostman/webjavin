package ru.frostman.mvc.classloading;

import javassist.CtClass;

/**
 * This class contains information about application classes.
 *
 * @author slukjanov aka Frostman
 */
public class FrostyClass {
    private String name;
    private byte[] bytecode;
    private byte[] enhancedBytecode;

    private long lastLoaded;
    private String hashCode;

    private CtClass ctClass;
    private Class javaClass;

    private boolean generated;

    public FrostyClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public void setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    public byte[] getEnhancedBytecode() {
        return enhancedBytecode;
    }

    public void setEnhancedBytecode(byte[] enhancedBytecode) {
        this.enhancedBytecode = enhancedBytecode;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public CtClass getCtClass() {
        return ctClass;
    }

    public void setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class javaClass) {
        this.javaClass = javaClass;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
