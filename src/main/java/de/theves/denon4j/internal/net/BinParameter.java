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

package de.theves.denon4j.internal.net;

import de.theves.denon4j.controls.InvalidSignatureException;
import de.theves.denon4j.controls.Signature;
import de.theves.denon4j.net.Parameter;

/**
 * Class description.
 *
 * @author stheves
 */
public class BinParameter implements Parameter<byte[]> {

    private BinValue binValue;

    public BinParameter(BinValue binValue) {

        this.binValue = binValue;
    }

    @Override
    public byte[] getValue() {
        return new byte[0];
    }

    @Override
    public Signature build() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void validate() throws InvalidSignatureException {

    }
}
