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
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.IOContext;

import de.undercouch.bson4jackson.BsonParser;
import de.undercouch.bson4jackson.types.ObjectId;

class JongoBsonParser extends BsonParser {

    public JongoBsonParser(IOContext ctxt, int jsonFeatures, int bsonFeatures, InputStream in) {
        super(ctxt, jsonFeatures, bsonFeatures, in);
    }

    @Override
    public Object getEmbeddedObject() throws IOException, JsonParseException {
        Object object = super.getEmbeddedObject();
        if (object instanceof ObjectId) {
            return convertToNativeObjectId((ObjectId) object);
        }
        return object;
    }

    private org.bson.types.ObjectId convertToNativeObjectId(ObjectId id) {
        return org.bson.types.ObjectId.createFromLegacyFormat(id.getTime(), id.getMachine(), id.getInc());
    }
}
