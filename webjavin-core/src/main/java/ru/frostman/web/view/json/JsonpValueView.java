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

package ru.frostman.web.view.json;

import ru.frostman.web.controller.Model;
import ru.frostman.web.controller.View;
import ru.frostman.web.util.Json;
import ru.frostman.web.view.CharacterEncodings;
import ru.frostman.web.view.ContentTypes;

import java.io.PrintWriter;

/**
 * @author slukjanov aka Frostman
 */
public class JsonpValueView<T> extends View {
    private final T value;
    private final String callback;

    public JsonpValueView(T value, String callback) {
        this.value = value;
        this.callback = callback;
        this.contentType = ContentTypes.APPLICATION_JS;
        this.characterEncoding = CharacterEncodings.UTF8;
    }

    public T getValue() {
        return value;
    }

    public String getCallback() {
        return callback;
    }

    @Override
    public void process(Model model, PrintWriter writer) {
        writer.write(callback);
        writer.write('(');
        writer.write(Json.renderValueToJson(value));
        writer.write(')');
    }
}
