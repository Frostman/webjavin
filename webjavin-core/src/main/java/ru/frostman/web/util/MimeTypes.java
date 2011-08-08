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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author slukjanov aka Frostman
 */
public class MimeTypes {
    private static final BiMap<String, String> TYPES = HashBiMap.create();
    private static final BiMap<String, String> TYPES_INVERSE = TYPES.inverse();
    private static final String DEFAULT_TYPE = "text/plain";

    static {
        // Default javax.activation mime types

        TYPES.put("text/html", "htm");
        TYPES.put("text/html", "html");

        TYPES.put("text/plain", "txt");
        TYPES.put("text/plain", "text");

        TYPES.put("image/gif", "gif");

        TYPES.put("image/ief", "ief");

        TYPES.put("image/jpeg", "jpg");
        TYPES.put("image/jpeg", "jpeg");

        TYPES.put("image/tiff", "tif");
        TYPES.put("image/tiff", "tiff");

        TYPES.put("image/png", "png");

        TYPES.put("image/x-xwindowdump", "xwd");

        TYPES.put("application/postscript", "ai");
        TYPES.put("application/postscript", "eps");
        TYPES.put("application/postscript", "ps");

        TYPES.put("application/rtf", "rtf");

        TYPES.put("application/x-tex", "tex");

        TYPES.put("application/x-texinfo", "texi");
        TYPES.put("application/x-texinfo", "texinfo");

        TYPES.put("application/x-troff", "t");
        TYPES.put("application/x-troff", "tr");
        TYPES.put("application/x-troff", "roff");

        TYPES.put("audio/basic", "au");

        TYPES.put("audio/midi", "mid");
        TYPES.put("audio/midi", "midi");

        TYPES.put("audio/x-aifc", "aifc");

        TYPES.put("audio/x-aiff", "aif");
        TYPES.put("audio/x-aiff", "aiff");

        TYPES.put("audio/x-mpeg", "mpg");
        TYPES.put("audio/x-mpeg", "mpeg");

        TYPES.put("audio/x-wav", "wav");

        TYPES.put("video/mpeg", "mpg");
        TYPES.put("video/mpeg", "mpeg");
        TYPES.put("video/mpeg", "mpe");

        TYPES.put("video/quicktime", "qt");
        TYPES.put("video/quicktime", "mov");

        TYPES.put("video/x-msvideo", "avi");

        // additional mime types

        TYPES.put("text/css", "css");
        TYPES.put("text/csv", "csv");

        TYPES.put("image/x-png", "png");

        TYPES.put("image/x-ms-bmp", "bmp");

        TYPES.put("application/rtf", "rtf");
        TYPES.put("application/pdf", "pdf");

        TYPES.put("application/x-latex", "latex");

        TYPES.put("application/x-tar", "tar");
        TYPES.put("application/x-gtar", "gtar");
        TYPES.put("application/x-ustar", "ustar");

        TYPES.put("application/zip", "zip");

        TYPES.put("application/octet-stream", "uu");
        TYPES.put("application/octet-stream", "bin");
        TYPES.put("application/octet-stream", "com");
        TYPES.put("application/octet-stream", "exe");

        TYPES.put("application/javascript", "js");
    }

    public static String addContentType(String type, String extension) {
        return TYPES.put(type, extension);
    }

    public static String removeContentType(String type) {
        return TYPES.remove(type);
    }

    public static String getContentType(String resourceName) {
        int dotPos = resourceName.lastIndexOf('.');
        String extension = resourceName.substring(dotPos + 1);
        String type = TYPES_INVERSE.get(extension.toLowerCase());

        return type != null ? type : DEFAULT_TYPE;
    }

}
