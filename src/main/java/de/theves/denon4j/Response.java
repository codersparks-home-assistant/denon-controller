/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.theves.denon4j;

import java.util.Collections;
import java.util.List;

public class Response {
    private List<String> responseLines;
    private byte[] rawBytes;

    public Response(byte[] raw, List<String> response) {
        this.responseLines = response;
        this.rawBytes = raw;
    }

    public List<String> getResponseLines() {
        return Collections.unmodifiableList(responseLines);
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    @Override
    public String toString() {
        return responseLines.toString();
    }
}
