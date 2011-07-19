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

package ru.frostman.web.session;

import java.util.Enumeration;

/**
 * Provides a way to store user-unique information on server side.
 *
 * In case of implementation session information can be scoped only to
 * the current web application or disturbed available.
 *
 * @author slukjanov aka Frostman
 */
public interface JavinSession {
    /**
     * @return the creation time as number of milliseconds since 1/1/1970 GMT
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public long getCreationTime();


    /**
     * @return the unique identifier assigned to this session
     */
    public String getId();


    /**
     * Returns the last access time as number of milliseconds since
     * 1/1/1970.
     *
     * Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return the last access time as number of milliseconds since 1/1/1970 GMT
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public long getLastAccessedTime();

    /**
     * @param name of the object
     *
     * @return the object with the specified name
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public Object getAttribute(String name);

    /**
     * @return an Enumeration of objects names bound to this session
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public Enumeration<String> getAttributeNames();

    /**
     * Binds an object to this session, using the name specified.
     * Objects of the same name already bounded to the session will be replaced.
     *
     * If the value passed in is null, this has the same effect as calling
     * removeAttribute().
     *
     * @param name  to which the object is bound; cannot be null
     * @param value to be bound
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public void setAttribute(String name, Object value);

    /**
     * @param name of the object to remove from this session
     *
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    public void removeAttribute(String name);

    /**
     * Invalidates this session and removes objects bound to it.
     *
     * @throws IllegalStateException if this method is called on an
     *                               already invalidated session
     */
    public void invalidate();


    /**
     * @return true iff server has created a session,
     *         but the client has not yet joined
     *
     * @throws IllegalStateException if this method is called on an
     *                               already invalidated session
     */
    public boolean isNew();

}
