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

/**
 * @author slukjanov aka Frostman
 */
public class MimeTypesTest {
    private void checkContentType(String extension, String expected) {
        Assert.assertEquals(expected, MimeTypes.getContentType("smth." + extension));
    }

    @Test
    public void testDefaultContentType() {
        checkContentType("unknown_extension", "text/plain");
    }

    @Test
    public void testPlainTexts() {
        checkContentType("txt", "text/plain");
        checkContentType("text", "text/plain");
    }

    @Test
    public void testHtmlTexts() {
        checkContentType("htm", "text/html");
        checkContentType("html", "text/html");
    }

    @Test
    public void testCssType() {
        checkContentType("css", "text/css");
    }

    @Test
    public void testOtherTextTypes() {
        checkContentType("csv", "text/csv");
    }

    @Test
    public void testJsType() {
        checkContentType("js", "application/javascript");
    }

    @Test
    public void testImages() {
        checkContentType("gif", "image/gif");

        checkContentType("ief", "image/ief");

        checkContentType("jpg", "image/jpeg");
        checkContentType("jpeg", "image/jpeg");

        checkContentType("tif", "image/tiff");
        checkContentType("tiff", "image/tiff");

        checkContentType("png", "image/png");

        checkContentType("bmp", "image/x-ms-bmp");
    }

    @Test
    public void testAudioTypes() {
        checkContentType("au", "audio/basic");

        checkContentType("mid", "audio/midi");
        checkContentType("midi", "audio/midi");

        checkContentType("aifc", "audio/x-aifc");

        checkContentType("aif", "audio/x-aiff");
        checkContentType("aiff", "audio/x-aiff");

        checkContentType("wav", "audio/x-wav");
    }

    @Test
    public void testVideoTypes() {
        checkContentType("mpe", "video/mpeg");
        checkContentType("mpg", "video/mpeg");
        checkContentType("mpeg", "video/mpeg");

        checkContentType("qt", "video/quicktime");
        checkContentType("mov", "video/quicktime");

        checkContentType("avi", "video/x-msvideo");
    }

    @Test
    public void testTexTypes() {
        checkContentType("tex", "application/x-tex");

        checkContentType("texi", "application/x-texinfo");
        checkContentType("texinfo", "application/x-texinfo");

        checkContentType("latex", "application/x-latex");
    }

    @Test
    public void testArchiveTypes() {
        checkContentType("zip", "application/zip");

        checkContentType("tar", "application/x-tar");
        checkContentType("gtar", "application/x-gtar");
        checkContentType("ustar", "application/x-ustar");
    }

    @Test
    public void testDocumentsTypes() {
        checkContentType("rtf", "application/rtf");

        checkContentType("pdf", "application/pdf");
    }

    @Test
    public void testOctetStreamTypes() {
        checkContentType("uu", "application/octet-stream");
        checkContentType("bin", "application/octet-stream");
        checkContentType("com", "application/octet-stream");
        checkContentType("exe", "application/octet-stream");
    }

    @Test
    public void testPostscriptTypes() {
        checkContentType("ai", "application/postscript");
        checkContentType("eps", "application/postscript");
        checkContentType("ps", "application/postscript");
    }

    @Test
    public void testAddRemoveContentTypes() {
        String extension = "test";
        String type = "text/test";

        checkContentType(extension, MimeTypes.getDefaultType());

        MimeTypes.addContentType(type, extension);

        checkContentType(extension, type);

        MimeTypes.removeContentTypeByExtension(extension);

        checkContentType(extension, MimeTypes.getDefaultType());
    }
}
