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

package ru.frostman.web.secure.impl;

import com.google.common.base.Objects;
import ru.frostman.web.secure.userdetails.Credentials;

/**
 * @author slukjanov aka Frostman
 */
public class SimpleLoginPasswordCredentials implements Credentials {
    private String login;
    private String password;
    private boolean nonExpired;

    public SimpleLoginPasswordCredentials() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isNonExpired() {
        return nonExpired;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleLoginPasswordCredentials) {
            SimpleLoginPasswordCredentials credentials = (SimpleLoginPasswordCredentials) obj;

            return Objects.equal(login, credentials.login)
                    && Objects.equal(password, credentials.password);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (nonExpired ? 1 : 0);
        return result;
    }
}
