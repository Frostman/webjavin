package ru.frostman.mvc;

import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public abstract class View {
    private boolean processed;

    public boolean isProcessed() {
        return processed;
    }

    protected void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public abstract void process(Model model, PrintWriter writer);

}
