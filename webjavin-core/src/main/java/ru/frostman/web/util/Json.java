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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import ru.frostman.web.Javin;
import ru.frostman.web.controller.Model;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.thr.JsonManipulationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Json {
    private static final String[] generatedProperties = {Model.REQUEST, Model.RESPONSE};
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String renderMapToJson(Map<String, Object> map) {
        StringWriter writer = new StringWriter();
        renderMapToJson(map, writer);

        return writer.toString();
    }

    public static void renderMapToJson(Map<String, Object> map, Writer writer) {
        try {
            for (String key : generatedProperties) {
                map.remove(key);
            }

            mapper.writeValue(writer, map);
        } catch (Exception e) {
            throw new JsonManipulationException("Exception while rendering JSON", e);
        }
    }

    public static String renderModelToJson(Model model) {
        return renderMapToJson(model.toMap());
    }

    public static void renderModelToJson(Model model, Writer writer) {
        renderMapToJson(model.toMap(), writer);
    }

    public static String renderValueToJson(Object value) {
        StringWriter writer = new StringWriter();
        renderValueToJson(value, writer);

        return writer.toString();
    }

    public static void renderValueToJson(Object value, Writer writer) {
        try {
            mapper.writeValue(writer, value);
        } catch (Exception e) {
            throw new JsonManipulationException("Exception while rendering JSON", e);
        }
    }

    public static JsonNode parseJsonBody(HttpServletRequest request) {
        try {
            return mapper.readTree(request.getReader());
        } catch (IOException e) {
            throw new JavinRuntimeException("Exception while parsing request body as tree: ", e);
        }
    }

    /**
     * Return specified parameter from Json tree.
     * Nested elements supported, to specify nested element
     * you should pass all names of parent elements in array.
     *
     * @param node     json tree
     * @param typeName name of type to convert to
     * @param path     full name of element
     *
     * @return specified parameter converted to specified type
     */
    public static Object getParam(JsonNode node, String typeName, String... path) {
        try {
            //todo impl recursive
            Class<?> type = Javin.getClasses().getClassLoader().loadClass(typeName);
            return mapper.readValue((path == null || path.length == 0) ? node : node.get(path[0]), type);
        } catch (IOException e) {
            throw new JavinRuntimeException("Exception while finding element with specified path: " + Arrays.toString(path), e);
        } catch (ClassNotFoundException e) {
            throw new JavinRuntimeException("Can't load class to read json from request body:" + typeName, e);
        }
    }
}
