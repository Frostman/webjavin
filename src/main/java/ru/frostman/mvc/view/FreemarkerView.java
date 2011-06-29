package ru.frostman.mvc.view;

import com.google.common.base.Preconditions;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ru.frostman.mvc.Model;
import ru.frostman.mvc.View;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class FreemarkerView extends View {
    private Template template;

    public FreemarkerView(Template template) {
        Preconditions.checkNotNull(template);

        this.template = template;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        try {
            template.process(model, writer);
        } catch (TemplateException e) {
            //todo impl
            throw new RuntimeException(e);
        } catch (IOException e) {
            //todo impl
            throw new RuntimeException(e);
        }

    }
}
