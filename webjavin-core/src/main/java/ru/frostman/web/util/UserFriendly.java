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

import com.google.common.base.Throwables;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import ru.frostman.web.thr.FastRuntimeException;

import javax.annotation.Nullable;

/**
 * @author slukjanov aka Frostman
 */
public class UserFriendly {

    // todo impl all and move to util.Log
    public static String convert(Throwable th) {
        return convert(null, th);
    }

    public static String convert(@Nullable String message, Throwable th) {
        StringBuilder sb = new StringBuilder();

        convertTo(sb, message, th);

        return sb.toString();
    }

    public static void convertTo(StringBuilder sb, @Nullable String message, Throwable th) {
        if (th == null) {
            return;
        }
        if (message != null) {
            sb.append(message).append('\n');
        }

        if (th instanceof FastRuntimeException) {
            if (hasCause(th)) {
                convertTo(sb, th.getMessage(), th.getCause());
            } else {
                sb.append(th.getMessage()).append('\n');
            }
        } else if (th instanceof MarkedYAMLException) {
            convertMarkedYamlException(sb, (MarkedYAMLException) th);
        } else if (hasCause(th)) {
            convertTo(sb, null, th.getCause());
        } else {
            sb.append(Throwables.getStackTraceAsString(th));
        }
    }

    private static boolean hasCause(Throwable th) {
        return th.getCause() != null && th.getCause() != th;
    }

    private static void convertMarkedYamlException(StringBuilder sb, MarkedYAMLException e) {
        sb.append("Error ").append(e);
    }
}
