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

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * @author slukjanov aka Frostman
 */
public class ResourcesTest {

    private static final String TEST_FILE = "test.file";

    private void checkResource(InputStream inputStream) throws IOException {
        Assert.assertNotNull(inputStream);

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        Assert.assertEquals("test", br.readLine().trim());
    }


    @Test
    public void testGetResourceAsStream() throws IOException {
        checkResource(Resources.getResourceAsStream(TEST_FILE));
    }

    @Test
    public void testGetResourceAsFile() throws IOException {
        File file = Resources.getResourceAsFile(TEST_FILE);

        Assert.assertNotNull(file);
        Assert.assertTrue(file.isFile());

        checkResource(new FileInputStream(file));
    }
}
