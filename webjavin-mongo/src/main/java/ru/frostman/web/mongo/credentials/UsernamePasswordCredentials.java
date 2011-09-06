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

package ru.frostman.web.mongo.credentials;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import ru.frostman.web.mongo.BaseEntity;
import ru.frostman.web.secure.userdetails.Credentials;

/**
 * @author slukjanov aka Frostman
 */
@Indexes({
        @Index("username"),
        @Index("password"),
        @Index("username, password")
})
@Entity
public class UsernamePasswordCredentials extends BaseEntity implements Credentials {
    private boolean nonExpired;

    private String username;

    private String password;

    public UsernamePasswordCredentials() {
    }

    public UsernamePasswordCredentials(String username, String password) {
        nonExpired = true;
        this.username = username;
        setPassword(password);
    }

    @Override
    public boolean isNonExpired() {
        return nonExpired;
    }

    public void setNonExpired(boolean nonExpired) {
        this.nonExpired = nonExpired;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = encodePassword(password);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(encodePassword(password));
    }

    public static String encodePassword(String password) {
        return Base64.encodeBase64URLSafeString(DigestUtils.sha384(password));
    }
}
