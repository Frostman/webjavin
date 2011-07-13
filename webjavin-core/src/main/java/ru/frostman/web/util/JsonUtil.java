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

package ru.frostman.web.util;

import org.codehaus.jackson.map.ObjectMapper;
import ru.frostman.web.controller.Model;
import ru.frostman.web.thr.JsonManipulationException;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class JsonUtil {
    private static final String[] generatedProperties = {Model.REQUEST, Model.RESPONSE};
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String renderJson(Map<String, Object> map) {
        StringWriter writer = new StringWriter();
        renderJson(map, writer);

        return writer.toString();
    }

    private static void renderJson(Map<String, Object> map, Writer writer) {
        try {
            for (String key : generatedProperties) {
                map.remove(key);
            }

            mapper.writeValue(writer, map);
        } catch (Exception e) {
            throw new JsonManipulationException("Exception while rendering JSON", e);
        }
    }

    public static String renderJson(Model model) {
        return renderJson(model.toMap());
    }

    public static void renderJson(Model model, Writer writer) {
        renderJson(model.toMap(), writer);
    }
}
