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

import ru.frostman.web.Javin;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author slukjanov aka Frostman
 */
public class Resources {
    private static final ClassLoader MAIN_CLASS_LOADER = Javin.class.getClassLoader();

    /**
     * Returns stream of the specified resource using ClassLoader that loads Javin class.
     *
     * @param name of the resource
     *
     * @return stream of the resource
     */
    public static InputStream getResourceAsStream(String name) {
        return MAIN_CLASS_LOADER.getResourceAsStream(name);
    }

    /**
     * Returns file of the specified resource using ClassLoader that loads Javin class.
     *
     * @param name of the resource
     *
     * @return file of the resource
     */
    public static File getResourceAsFile(String name) {
        URL url = MAIN_CLASS_LOADER.getResource(name);
        if (url == null) {
            return null;
        }

        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
