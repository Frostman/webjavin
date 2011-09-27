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

import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

/**
 * @author slukjanov aka Frostman
 */
public class CompiledNestedTag extends Node {
    public static final String NESTED_NODE_VAR = "$$_nested_node";

    private ExecutableStatement compiledExpr;

    public CompiledNestedTag() {
    }

    public CompiledNestedTag(int begin, String name, char[] template, int start, int end) {
        super(begin, name, template, start, end);

        init();
    }

    public CompiledNestedTag(int begin, String name, char[] template, int start, int end, Node next) {
        super(begin, name, template, start, end);

        init();
    }

    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        MVEL.executeExpression(compiledExpr, ctx, factory);

        return next != null ? next.eval(runtime, appender, ctx, factory) : null;
    }

    public boolean demarcate(Node terminatingNode, char[] template) {
        return false;
    }

    private void init() {
        compiledExpr = (ExecutableStatement) MVEL.compileExpression(NESTED_NODE_VAR + ".eval()");
    }

    @Override
    public void setContents(char[] contents) {
        super.setContents(contents);
        init();
    }

    public String toString() {
        //todo replace with toStringHelper
        return "EscapeNode:" + name + "{"
                + (contents == null ? "" : new String(contents))
                + "}" +
                " (start=" + begin + ";end=" + end + ")";
    }
}
