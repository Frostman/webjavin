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
import ru.frostman.web.dispatch.Dispatcher;

/**
 * @author slukjanov aka Frostman
 */
public class StaticResource {
    private String secure;
    private String target;
    private long expire = Dispatcher.DEFAULT_EXPIRE_TIME;

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StaticResource) {
            StaticResource resource = (StaticResource) obj;

            return Objects.equal(secure, resource.secure)
                    && Objects.equal(target, resource.target)
                    && expire == resource.expire;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = secure != null ? secure.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (int) (expire ^ (expire >>> 32));
        return result;
    }
}
