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

/**
 * @author slukjanov aka Frostman
 */
public class Log {
    public static String minifyName(Class<?> declaringClass) {
        return minifyName(declaringClass.getName());
    }

    //todo think about this
    public static String minifyName(String loggerName) {
        StringBuilder sb = new StringBuilder();

        String[] parts = loggerName.split("\\.");
        int length = parts.length;
        for (int i = 0; i < length; i++) {
            String part = parts[i];
            if (i + 2 == length) {
                sb.append(part).append('.');
            } else if (i + 1 == length) {
                sb.append(part);
            } else {
                sb.append(part.charAt(0)).append('.');
            }
        }

        return sb.toString();
    }
}
