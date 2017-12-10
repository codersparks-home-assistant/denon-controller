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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;

/**
 * Switch control like on/off.
 *
 * @author stheves
 */
public abstract class SwitchImpl extends AbstractControl implements Switch {
    protected final SwitchState onValue;
    protected final SwitchState offValue;

    public SwitchImpl(DenonReceiver receiver, String prefix, SwitchState onValue, SwitchState offValue) {
        super(receiver, prefix);
        this.onValue = onValue;
        this.offValue = offValue;
    }

    @Override
    public void switchOff() {
        executeCommand(offValue.get());
    }

    @Override
    public void switchOn() {
        executeCommand(onValue.get());
    }

    @Override
    public SwitchState state() {
        return SwitchState.valueOf(sendRequest().asciiValue().substring(2));
    }

    private void executeCommand(String param) {
        send(param);
    }

    @Override
    protected void doHandle(Event event) {
    }

}
