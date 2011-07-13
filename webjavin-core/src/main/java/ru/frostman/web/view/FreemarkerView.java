/******************************************************************************
 * WebJavin - Java Web Framework.                                             *
 *                                                                            *
 * Copyright (c) 2011 - Sergey "Frosman" Lukjanov, me@frostman.ru             *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package ru.frostman.web.view;

import com.google.common.base.Preconditions;
import freemarker.template.Template;
import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.thr.JavinRuntimeException;

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
            throw new JavinRuntimeException("Exception while processing template", e);
        }
    }
}
