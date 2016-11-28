/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmote.rongo;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class ObjectIdDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    public ObjectIdDeserializer() {
        this(false);
    }

    public ObjectIdDeserializer(boolean fieldIsObjectId) {
        this.fieldIsObjectId = fieldIsObjectId;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        return new ObjectIdDeserializer(ObjectId.class.isAssignableFrom(property.getType().getRawClass()));
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode treeNode = jp.readValueAsTree();
        JsonNode oid = ((JsonNode) treeNode).get("$oid");
        Object result;
        if (fieldIsObjectId) {
            if (oid != null) {
                result = new ObjectId(oid.asText());
            } else {
                result = new ObjectId(((JsonNode) treeNode).asText());
            }
        } else {
            if (oid != null) {
                result = oid.asText();
            } else {
                result = ((JsonNode) treeNode).asText();
            }
        }
        return result;
    }

    private final boolean fieldIsObjectId;
}
