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

package ru.frostman.web;

import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.InitializationException;
import ru.frostman.web.util.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author slukjanov aka Frostman
 */
public class Javin {
    private static final String VERSION_FILENAME = "version";
    private static final String VERSION = calculateVersion();

    private static boolean started = false;
    private static boolean asyncSupported = false;
    private static JavinConfig config = new JavinConfig(false);

    static synchronized void init(boolean asyncSupported) {
        Javin.asyncSupported = asyncSupported;

        try {
            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);

            if (config.getMode().isProdMode()) {
                refresh();
            }
        } catch (Throwable th) {
            //todo change it
            System.err.println("BABABABABABAHHHH!!!!!!");
            th.printStackTrace();
        }
    }

    static synchronized void destroy() {
        stop();

        //todo clean some other parts
    }

    static synchronized void refresh() {
        if (started && config.getMode().isProdMode()) {
            return;
        }

        if (detectChanges()) {
            start();
        }
    }

    private static synchronized void start() {
        if (started) {
            stop();
        }

    }

    private static synchronized void stop() {
        if (!started) {
            return;
        }

    }

    private static synchronized boolean detectChanges() {
        if (JavinConfig.changed(config)) {
            config = JavinConfig.load();

            return true;
        }

        //todo impl
        // detect changes in classes
        return false;
    }

    private static String calculateVersion() {
        try {
            return CharStreams.readFirstLine(new InputSupplier<Reader>() {
                @Override
                public Reader getInput() throws IOException {
                    return new InputStreamReader(Resource.getAsStream(VERSION_FILENAME), "UTF-8");
                }
            });
        } catch (Exception e) {
            //todo replace with UserFriendly message
            throw new InitializationException("Can't load WebJavin's version", e);
        }
    }

    public static String getVersion() {
        return VERSION;
    }

    public static boolean isStarted() {
        return started;
    }

    public static boolean isAsyncSupported() {
        return asyncSupported;
    }

    public static JavinConfig getConfig() {
        return config;
    }
}
