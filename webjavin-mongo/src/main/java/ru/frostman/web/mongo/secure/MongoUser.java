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

package ru.frostman.web.mongo.secure;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;
import ru.frostman.web.mongo.BaseEntity;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.Role;
import ru.frostman.web.secure.userdetails.UserDetails;

import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
@Indexes({
        @Index(value = "username", unique = true),
        @Index("enabled")
})
@Entity
public class MongoUser extends BaseEntity implements UserDetails {
    private String username;

    @Reference(lazy = true)
    private Set<Credentials> credentials;

    private boolean nonExpired;

    private boolean nonLocked;

    private boolean enabled;

    private Set<Role> roles;

    private Set<String> permissions;

    @Override
    public boolean checkCredentials(Credentials credentials) {
        return this.credentials.contains(credentials);
    }

    @Override
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    @Override
    public int compareTo(ru.frostman.web.secure.userdetails.UserDetails userDetails) {
        return username.compareTo(userDetails.getUsername());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Credentials> getCredentials() {
        return credentials;
    }

    public void setCredentials(Set<Credentials> credentials) {
        this.credentials = credentials;
    }

    public boolean isNonExpired() {
        return nonExpired;
    }

    public void setNonExpired(boolean nonExpired) {
        this.nonExpired = nonExpired;
    }

    public boolean isNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("username", username)
                .add("enabled", enabled)
                .add("expired", !nonExpired)
                .add("locked", !nonLocked)
                .toString();
    }
}
