package ru.frostman.mvc.controller;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class ModelAndView {
    private Model model;
    private View view;

    public ModelAndView(HttpServletRequest request, HttpServletResponse response) {
        model = new Model(request, response);
    }

    public ModelAndView(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void process(PrintWriter writer) {
        Preconditions.checkNotNull(view, "View can't be null");

        view.process(model, writer);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
