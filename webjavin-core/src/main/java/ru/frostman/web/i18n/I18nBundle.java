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

package ru.frostman.web.i18n;

import com.google.common.collect.Maps;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * @author slukjanov aka Frostman
 */
public class I18nBundle {
    private final Map<String, Properties> messages = Maps.newHashMap();

    public I18nBundle(String location) {
        if (location.contains("..")) {
            throw new JavinRuntimeException("I18nConfig error: bundle location should not contain '..': " + location);
        }

        String bundleLocation = JavinConfig.get().getI18n().getPath() + "/" + location + "_";
        for (String locale : JavinConfig.get().getI18n().getLocales()) {
            Properties props = new Properties();
            InputStream bundle = Resources.getResourceAsStream(bundleLocation + locale + ".properties");
            if (bundle != null) {
                try {
                    props.load(new InputStreamReader(bundle, "UTF-8"));
                } catch (IOException e) {
                    throw new JavinRuntimeException("Can't load i18n bundle: " + bundleLocation + " with locale: " + locale, e);
                }
            }
            messages.put(locale, props);
        }
    }

    public String get(String key) {
        return get(JavinConfig.get().getI18n().getDefaultLocale(), key);
    }

    public String get(String locale, String key) {
        if (!JavinConfig.get().getI18n().getLocales().contains(locale)) {
            return get(key);
        }

        Properties props = messages.get(locale);
        if (props != null) {
            return (String) props.get(key);
        }
        //todo if null then try to check default locale

        return null;
    }
}
