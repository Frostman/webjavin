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
import ru.frostman.web.thr.JavinPluginException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class I18n {
    public static final String DEFAULT_BUNDLE = "messages";
    private static final Map<String, I18nBundle> bundles = Maps.newHashMap();
    public static String defaultLocale;

    public static void update() {
        if (!JavinConfig.get().getI18n().getLocales().contains(JavinConfig.get().getI18n().getDefaultLocale())) {
            throw new JavinPluginException("I18nConfig error: defaultLocale should be part of locales");
        }
        defaultLocale = JavinConfig.get().getI18n().getDefaultLocale();

        List<String> bundleNames = JavinConfig.get().getI18n().getBundles();

        for (String bundleName : bundleNames) {
            bundles.put(bundleName, new I18nBundle(bundleName));
        }
    }

    public static String get(String bundleName, String locale, String key) {
        I18nBundle bundle = bundles.get(bundleName);
        if (bundle != null) {
            return bundle.get(locale, key);
        }

        return null;
    }

    public static String get(String locale, String key) {
        return get(DEFAULT_BUNDLE, locale, key);
    }

    public static String get(String key) {
        return get(DEFAULT_BUNDLE, defaultLocale, key);
    }
}
