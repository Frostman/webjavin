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

package ru.frostman.web.controller;

import java.io.PrintWriter;

/**
 * View part of MVC.
 *
 * Provides process method to render view with specified model and target.
 *
 * @author slukjanov aka Frostman
 */
public abstract class View {
    protected boolean processed;
    protected String contentType;
    protected String characterEncoding;

    /**
     * @param model to pass values to view
     * @param writer to write to
     */
    public abstract void process(Model model, PrintWriter writer);

    /**
     * @return true if view is already processed
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Change processed state.
     *
     * @param processed new processed state
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return character encoding
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * @param characterEncoding to set
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }
}
