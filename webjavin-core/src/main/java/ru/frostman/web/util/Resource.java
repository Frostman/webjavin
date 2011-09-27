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

import com.google.common.collect.Lists;
import ru.frostman.web.Javin;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Main class of Javin framework
 *
 * @author slukjanov aka Frostman
 */
public class Resource {
    private static final ClassLoader MAIN_CLASS_LOADER = Javin.class.getClassLoader();

    /**
     * Returns stream of the specified resource using ClassLoader that loads Javin class.
     *
     * @param name of the resource
     *
     * @return stream of the resource
     */
    public static InputStream getAsStream(String name) {
        URL url = get(name);

        if (url != null) {
            try {
                // avoid caching in getResourceAsStream(...)
                return url.openStream();
            } catch (IOException e) {
                return null;
            }
        }

        return null;
    }

    public static Reader getAsReader(String name) {
        InputStream inputStream = getAsStream(name);

        if (inputStream != null) {
            try {
                return new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            } catch (IOException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Returns file of the specified resource using ClassLoader that loads Javin class.
     *
     * @param name of the resource
     *
     * @return file of the resource
     */
    public static File getAsFile(String name) {
        URL url = get(name);
        if (url == null) {
            return null;
        }

        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Returns url of the specified resource using ClassLoader that loads Javin class.
     *
     * @param name of the resource
     *
     * @return url of the resource
     */
    public static URL get(String name) {
        return MAIN_CLASS_LOADER.getResource(name);
    }

    public static List<InputStream> getAllAsStreams(String name) {
        List<InputStream> result = Lists.newLinkedList();
        for (URL url : getAll(name)) {
            try {
                result.add(url.openStream());
            } catch (IOException e) {
                //no operations
            }
        }

        return result;
    }

    public static List<URL> getAll(String name) {
        try {
            return Collections.list(MAIN_CLASS_LOADER.getResources(name));
        } catch (IOException e) {
            return Lists.newLinkedList();
        }
    }

}
