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

import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author slukjanov aka Frostman
 */
public class NodeEvaluator {
    private final Node node;
    private final TemplateRuntime runtime;
    private final TemplateOutputStream appender;
    private final Object ctx;
    private final VariableResolverFactory factory;

    public NodeEvaluator(Node node, TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        this.node = checkNotNull(node);
        this.runtime = checkNotNull(runtime);
        this.appender = checkNotNull(appender);
        this.ctx = ctx;
        this.factory = factory;
    }

    public void eval() {
        node.eval(runtime, appender, ctx, factory);
    }
}
