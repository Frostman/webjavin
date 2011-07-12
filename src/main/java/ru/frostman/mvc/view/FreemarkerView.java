package ru.frostman.mvc.view;

import com.google.common.base.Preconditions;
import freemarker.template.Template;
import ru.frostman.mvc.controller.Model;
import ru.frostman.mvc.controller.View;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class FreemarkerView extends View {
    private final Template template;

    public FreemarkerView(Template template) {
        Preconditions.checkNotNull(template);

        this.template = template;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        try {
            template.process(model, writer);
        } catch (Exception e) {
            throw new FrostyRuntimeException("Exception while processing template", e);
        }
    }
}
