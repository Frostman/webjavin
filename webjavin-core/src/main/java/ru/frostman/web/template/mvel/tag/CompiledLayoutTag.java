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

import org.apache.commons.lang3.StringEscapeUtils;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

import static org.mvel2.util.ParseTools.subset;

/**
 * @author slukjanov aka Frostman
 */
public class CompiledLayoutTag extends Node {

    private ExecutableStatement compiledExpression;

    public CompiledLayoutTag() {
    }

    public CompiledLayoutTag(int begin, String name, char[] template, int start, int end) {
        this.begin = begin;
        this.name = name;
        this.contents = subset(template, this.cStart = start, (this.end = this.cEnd = end) - start - 1);

        compileExpression();
    }

    public CompiledLayoutTag(int begin, String name, char[] template, int start, int end, Node next) {
        this.name = name;
        this.begin = begin;
        this.next = next;
        this.contents = subset(template, this.cStart = start, (this.end = this.cEnd = end) - start - 1);

        compileExpression();
    }

    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        String result = String.valueOf(MVEL.executeExpression(compiledExpression, ctx, factory));
        appender.append(StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeEcmaScript(result)));

        return next != null ? next.eval(runtime, appender, ctx, factory) : null;
    }

    public boolean demarcate(Node terminatingNode, char[] template) {
        return false;
    }

    private void compileExpression() {
        compiledExpression = (ExecutableStatement) MVEL.compileExpression(this.contents);
    }

    @Override
    public void setContents(char[] contents) {
        super.setContents(contents);
        compileExpression();
    }

    public String toString() {
        //todo replace with toStringHelper
        return "LayoutNode:" + name + "{"
                + (contents == null ? "" : new String(contents))
                + "}" +
                " (start=" + begin + ";end=" + end + ")";
    }
}
