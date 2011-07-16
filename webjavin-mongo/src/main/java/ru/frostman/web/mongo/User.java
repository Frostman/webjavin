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

package ru.frostman.web.mongo;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.Role;
import ru.frostman.web.secure.userdetails.UserDetails;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
@Entity
public class User extends BaseEntity implements UserDetails {
    private String username;

    @Embedded
    private List<Credentials> credentials;

    private boolean nonExpired;

    private boolean nonLocked;

    private boolean enabled;

    //todo think about lazy
    @Reference(lazy = true)
    private List<Role> roles;

    private List<String> permissions;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Credentials> getCredentials() {
        return credentials;
    }

    @Override
    public boolean checkCredentials(Credentials credentials) {
        return this.credentials.contains(credentials);
    }

    public void setCredentials(List<Credentials> credentials) {
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

    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int compareTo(UserDetails userDetails) {
        return username.compareTo(userDetails.getUsername());
    }
}
