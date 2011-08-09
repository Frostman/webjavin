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

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MimeTypes {
    private static final Map<String, String> TYPES = Maps.newLinkedHashMap();
    private static final String DEFAULT_TYPE = "text/plain";

    static {
        TYPES.put("htm", "text/html");
        TYPES.put("html", "text/html");

        TYPES.put("txt", "text/plain");
        TYPES.put("text", "text/plain");

        TYPES.put("gif", "image/gif");

        TYPES.put("ief", "image/ief");

        TYPES.put("jpg", "image/jpeg");
        TYPES.put("jpeg", "image/jpeg");

        TYPES.put("tif", "image/tiff");
        TYPES.put("tiff", "image/tiff");

        TYPES.put("png", "image/png");

        TYPES.put("ai", "application/postscript");
        TYPES.put("eps", "application/postscript");
        TYPES.put("ps", "application/postscript");

        TYPES.put("tex", "application/x-tex");

        TYPES.put("texi", "application/x-texinfo");
        TYPES.put("texinfo", "application/x-texinfo");

        TYPES.put("au", "audio/basic");

        TYPES.put("mid", "audio/midi");
        TYPES.put("midi", "audio/midi");

        TYPES.put("aifc", "audio/x-aifc");

        TYPES.put("aif", "audio/x-aiff");
        TYPES.put("aiff", "audio/x-aiff");

        TYPES.put("wav", "audio/x-wav");

        TYPES.put("mpe", "video/mpeg");
        TYPES.put("mpg", "video/mpeg");
        TYPES.put("mpeg", "video/mpeg");

        TYPES.put("qt", "video/quicktime");
        TYPES.put("mov", "video/quicktime");

        TYPES.put("avi", "video/x-msvideo");

        TYPES.put("css", "text/css");

        TYPES.put("csv", "text/csv");

        TYPES.put("bmp", "image/x-ms-bmp");

        TYPES.put("rtf", "application/rtf");

        TYPES.put("pdf", "application/pdf");

        TYPES.put("latex", "application/x-latex");

        TYPES.put("tar", "application/x-tar");
        TYPES.put("gtar", "application/x-gtar");
        TYPES.put("ustar", "application/x-ustar");

        TYPES.put("zip", "application/zip");

        TYPES.put("uu", "application/octet-stream");
        TYPES.put("bin", "application/octet-stream");
        TYPES.put("com", "application/octet-stream");
        TYPES.put("exe", "application/octet-stream");

        TYPES.put("js", "application/javascript");
    }

    public static String addContentType(String type, String extension) {
        return TYPES.put(extension, type);
    }

    public static String removeContentTypeByExtension(String extension) {
        return TYPES.remove(extension);
    }

    public static String getContentType(String resourceName) {
        int dotPos = resourceName.lastIndexOf('.');
        String extension = resourceName.substring(dotPos + 1);
        String type = TYPES.get(extension.toLowerCase());

        return type != null ? type : DEFAULT_TYPE;
    }

    public static String getDefaultType() {
        return DEFAULT_TYPE;
    }
}
