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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import ru.frostman.web.config.Config;
import ru.frostman.web.inject.CoreModule;
import ru.frostman.web.plugin.JavinPlugin;
import ru.frostman.web.plugin.Plugins;
import ru.frostman.web.template.TemplatesManager;
import ru.frostman.web.template.mvel.MvelTemplatesManager;
import ru.frostman.web.thr.InitializationException;
import ru.frostman.web.util.Resource;
import ru.frostman.web.util.UserFriendly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;

/**
 * @author slukjanov aka Frostman
 */
public class Javin {
    private static final String VERSION_FILENAME = "version";
    private static final String VERSION = calculateVersion();

    private static boolean initialized = false;
    private static boolean started = false;
    private static boolean asyncSupported = false;
    private static boolean needRefresh = false;

    // update in detect changes
    private static Config config = new Config(false);
    private static Injector injector;
    private static JavinPlugin plugins;

    // updated on start
    private static TemplatesManager templatesManager;

    static synchronized void init(boolean asyncSupported) {
        if (initialized) {
            return;
        }

        Javin.asyncSupported = asyncSupported;

        //todo
//        try {
//            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);
//        } catch (ClassNotFoundException e) {
//            handleError("Error while setting logger for freemarker", e);
//            return;
//        }

        detectChanges();
        if (config.getMode().isProdMode()) {
            start();
        } else {
            needRefresh = true;
        }

        initialized = true;
    }

    static synchronized void destroy() {
        if (!initialized) {
            return;
        }

        stop();

        //todo clean some other parts
    }

    static synchronized void refresh() {
        if (!initialized || started && config.getMode().isProdMode()) {
            return;
        }

        try {
            if (detectChanges() || needRefresh) {
                needRefresh = false;
                start();
            }
        } catch (Throwable th) {
            handleError("Error while refreshing application", th);
        }
    }

    private static synchronized void start() {
        if (!initialized) {
            return;
        }
        if (started) {
            stop();
        }

        //todo start all plugins, load all classes, start all services
        injector = Guice.createInjector(resolveGuiceStage(), new CoreModule());
        //todo resolve by guice
        templatesManager = new MvelTemplatesManager();


        //todo request static injections

        started = true;
    }

    private static synchronized void stop() {
        if (!initialized || !started) {
            return;
        }

        //todo make some cleanings, etc

        //todo need to write in docs to not store reference to injector in your code
        injector = null;
        templatesManager = null;


        started = false;
    }

    private static synchronized boolean detectChanges() {
        boolean changed = false;
        if (Config.changed(config)) {
            config = Config.load();
            changed = true;
        }

        if (changed || plugins == null || plugins.changed()) {
            plugins = Plugins.load();
            changed = true;
        }

        //if(changed || Classes.changed()) {
        //todo it should only load updated bytecode
        //}

        return changed;
    }

    public static void handleError(Throwable th) {
        handleError(null, th);
    }

    public static void handleError(@Nullable String message, Throwable th) {
        if (th == null) {
            return;
        }

        //todo write to log
        System.err.println("-------------------------------\n"
                + new Date(System.currentTimeMillis()).toString() + " JAVIN :: "
                + UserFriendly.convert(message, th)
                + "\n-------------------------------"
        );
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

    private static Stage resolveGuiceStage() {
        return config.getMode().isProdMode() ? Stage.PRODUCTION : Stage.DEVELOPMENT;
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

    public static Config getConfig() {
        return config;
    }

    @Nullable
    public static Injector getInjector() {
        return injector;
    }

    public static JavinPlugin getPlugins() {
        return plugins;
    }

    public static TemplatesManager getTemplatesManager() {
        return templatesManager;
    }
}
