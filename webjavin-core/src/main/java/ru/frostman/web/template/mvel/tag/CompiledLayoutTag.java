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

package ru.frostman.web.template.mvel.tag;

import com.google.common.collect.Maps;
import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.EndNode;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.res.TerminalNode;
import org.mvel2.templates.util.TemplateOutputStream;
import ru.frostman.web.template.Template;
import ru.frostman.web.template.mvel.MvelTemplate;

import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class CompiledLayoutTag extends Node {
    private Node nestedNode;

    public CompiledLayoutTag() {
        init();
    }

    public CompiledLayoutTag(int begin, String name, char[] template, int start, int end) {
        super(begin, name, template, start, end);

        init();
    }

    public CompiledLayoutTag(int begin, String name, char[] template, int start, int end, Node next) {
        super(begin, name, template, start, end, next);

        init();
    }

    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        if (nestedNode != null) {
            Map<String, Object> map = Maps.newHashMap();
            map.put(CompiledNestedTag.NESTED_NODE_VAR, new NodeEvaluator(nestedNode, runtime, appender, ctx, factory));
            //todo ask javin for TemplatesManager, cache template from demarcate method
            Template template = new MvelTemplate(new String(contents));
            if (template instanceof MvelTemplate) {
                ((MvelTemplate) template).render(map, appender);
            } else {
                throw new CompileException("Layout should be an mvel template, but found non mvel");
            }
        }

        return next != null ? next.eval(runtime, appender, ctx, factory) : null;
    }

    public boolean demarcate(Node terminatingNode, char[] template) {
        Node n = nestedNode = next;

        while (n.getNext() != null) {
            n = n.next;
        }

        n.next = new EndNode();
        next = terminus;

        return false;
    }

    private void init() {
        setTerminus(new TerminalNode());
    }

    @Override
    public boolean isOpenNode() {
        return true;
    }

    public String toString() {
        //todo replace with toStringHelper
        return "LayoutNode:" + name + "{"
                + (contents == null ? "" : new String(contents))
                + "}" +
                " (start=" + begin + ";end=" + end + ")";
    }
}
