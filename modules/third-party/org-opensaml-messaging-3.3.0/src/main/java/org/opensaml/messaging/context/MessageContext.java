/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.messaging.context;

import java.io.Serializable;

/**
 * A context component which holds the state related to the processing of a single message.
 * 
 * <p>
 * Additional information associated with the message represented by the context may be held by the context
 * as subordinate subcontext instances. Subcontext instances may simply hold state information related to the message, 
 * in which case they may be seen as a type-safe variant of the ubiquitous properties map pattern.  They may 
 * also be more functional or operational in nature, for example providing "views" onto the message 
 * and/or message context data.
 * </p>
 *
 * @param <MessageType> the message type of the message context 
 */
public class MessageContext<MessageType> extends BaseContext implements Serializable {

    /** The message represented. */
    private MessageType msg;

    /**
     * Get the message represented by the message context.
     * 
     * @return the message
     */
    public MessageType getMessage() {
        return msg;
    }

    /**
     * Set the message represented by the message context.
     * 
     * @param message the message
     */
    public void setMessage(MessageType message) {
        msg = message;
    }

}
/* @generated */