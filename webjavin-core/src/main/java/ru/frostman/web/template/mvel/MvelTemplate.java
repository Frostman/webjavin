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

package ru.frostman.web.template.mvel;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import org.apache.commons.io.output.StringBuilderWriter;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;
import ru.frostman.web.template.Template;
import ru.frostman.web.template.mvel.tag.CompiledEscapeTag;
import ru.frostman.web.template.mvel.tag.CompiledLayoutTag;
import ru.frostman.web.template.mvel.tag.CompiledNestedTag;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.thr.TemplateNotFoundException;
import ru.frostman.web.util.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MvelTemplate implements Template {

    private final String name;
    private final String template;

    private CompiledTemplate compiledTemplate;

    public MvelTemplate(String name) {
        this.name = name;

        try {
            this.template = Joiner.on("\n").join(CharStreams.readLines(Resource.getAsReader(name)));
        } catch (Exception e) {
            throw new TemplateNotFoundException(name);
        }
    }

    @Override
    public Template compile() {
        //todo cache this map
        Map<String, Class<? extends Node>> nodes = Maps.newHashMap();
        nodes.put("escape", CompiledEscapeTag.class);
        nodes.put("layout", CompiledLayoutTag.class);
        nodes.put("nested", CompiledNestedTag.class);

        compiledTemplate = new TemplateCompiler(template, nodes, true).compile();

        return this;
    }

    @Override
    public String render(Map<String, Object> args) {
        return render(args, new StringBuilderWriter()).toString();
    }

    @Override
    public OutputStream render(Map<String, Object> args, OutputStream outputStream) {
        render(args, new PrintWriter(outputStream, true));

        return outputStream;
    }

    public TemplateOutputStream render(Map<String, Object> args, TemplateOutputStream outputStream) {
        if (compiledTemplate == null) {
            compile();
        }

        //todo think about context and null args
        TemplateRuntime.execute(compiledTemplate, args, null, null, outputStream);

        return outputStream;
    }

    @Override
    public Writer render(Map<String, Object> args, Writer writer) {
        if (compiledTemplate == null) {
            compile();
        }

        MvelTemplateOutputStream stream = new MvelTemplateOutputStream(writer);

        //todo think about context and null args
        TemplateRuntime.execute(compiledTemplate, args, null, null, stream);

        stream.flush();

        return writer;
    }

    private static final class MvelTemplateOutputStream implements TemplateOutputStream {

        private final Writer writer;

        private MvelTemplateOutputStream(Writer writer) {
            Preconditions.checkNotNull(writer, "Writer should be not null");
            this.writer = writer;
        }

        @Override
        public TemplateOutputStream append(CharSequence c) {
            try {
                writer.append(c, 0, c.length());
            } catch (IOException e) {
                throw new JavinRuntimeException("Can't write to writer", e);
            }

            return this;
        }

        @Override
        public TemplateOutputStream append(char[] c) {
            try {
                writer.write(c);
            } catch (IOException e) {
                throw new JavinRuntimeException("Can't write to writer", e);
            }

            return this;
        }

        public void flush() {
            try {
                writer.flush();
            } catch (IOException e) {
                throw new JavinRuntimeException("Can't flush writer", e);
            }
        }
    }
}
