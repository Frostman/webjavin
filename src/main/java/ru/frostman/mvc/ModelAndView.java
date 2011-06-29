package ru.frostman.mvc;

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
        if (view == null) {
            //todo impl
            throw new RuntimeException();
        }

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
