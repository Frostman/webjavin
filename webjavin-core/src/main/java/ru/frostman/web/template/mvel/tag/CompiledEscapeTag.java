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
public class CompiledEscapeTag extends Node {

    private ExecutableStatement compiledExpr;

    public CompiledEscapeTag() {
    }

    public CompiledEscapeTag(int begin, String name, char[] template, int start, int end) {
        super(begin, name, template, start, end);

        init();
    }

    public CompiledEscapeTag(int begin, String name, char[] template, int start, int end, Node next) {
        super(begin, name, template, start, end);

        init();
    }

    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        String result = String.valueOf(MVEL.executeExpression(compiledExpr, ctx, factory));
        //todo escape
        appender.append(result);

        return next != null ? next.eval(runtime, appender, ctx, factory) : null;
    }

    public boolean demarcate(Node terminatingNode, char[] template) {
        return false;
    }

    private void init() {
        compiledExpr = (ExecutableStatement) MVEL.compileExpression(this.contents);
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
