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

package ru.frostman.web.config;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import static ru.frostman.web.i18n.I18n.DEFAULT_BUNDLE;
import static ru.frostman.web.i18n.Locales.EN;
import static ru.frostman.web.i18n.Locales.RU;

/**
 * @author slukjanov aka Frostman
 */
public class I18nConfig {
    private String path = "/i18n";
    private String defaultLocale = EN;
    private List<String> bundles = Lists.newArrayList(DEFAULT_BUNDLE);
    private Set<String> locales = Sets.newHashSet(EN, RU);

    public I18nConfig() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<String> getBundles() {
        return bundles;
    }

    public void setBundles(List<String> bundles) {
        this.bundles = bundles;
    }

    public Set<String> getLocales() {
        return locales;
    }

    public void setLocales(Set<String> locales) {
        this.locales = locales;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof I18nConfig) {
            I18nConfig config = (I18nConfig) obj;

            return Objects.equal(path, config.path)
                    && Objects.equal(defaultLocale, config.defaultLocale)
                    && Objects.equal(bundles, config.bundles)
                    && Objects.equal(locales, config.locales);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (defaultLocale != null ? defaultLocale.hashCode() : 0);
        result = 31 * result + (bundles != null ? bundles.hashCode() : 0);

        return result;
    }
}
