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

import de.theves.denon4j.model.*;
import de.theves.denon4j.net.NetClient;
import de.theves.denon4j.net.TimeoutException;

import java.util.Optional;

/**
 * This class implements the control protocol for the Denon AVR 1912.
 *
 * @author Sascha Theves
 */
public class Avr1912 extends GenericDenonReceiver {

    public Avr1912(NetClient client) {
        super(client);
    }

    public Avr1912(String host, int port) {
        super(host, port);
    }

    public Optional<Response> powerOn() {
        Optional<Response> res = send(new Command("PWON"));
        // as specification - K) 1 seconds later, please
        // transmit the next COMMAND after transmitting a
        // power on COMMAND （ PWON ）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        return res;
    }

    public Optional<Response> powerOff() {
        return send(new Command("PWSTANDBY"));
    }

    public boolean isPowerOn() {
        return send(new Command("PW?")).get().getEvents().get(0).equals("PWON");
    }

    public Optional<Response> mute() {
        return send(new Command("MUTEON"));
    }

    public Optional<Response> unmute() {
        return send(new Command("MUTEOFF"));
    }

    public boolean isMuted() {
        return send(new Command("MUTE?")).get().getEvents().get(0).equals("MUTE");
    }

    public Response getVolume() {
        return send(new Command("MV?")).orElseThrow(() -> new TimeoutException("No response within received."));
    }

    public Optional<Response> volumeUp() {
        return send(new Command("MVUP"));
    }

    public Optional<Response> volumeDown() {
        return send(new Command("MVDOWN"));
    }

    public Optional<Response> changeVolume(String value) {
        return send(new Command("MV", Optional.of(value)));
    }

    public Response getInputSource() {
        return send(new Command("SI?")).orElseThrow(() -> new TimeoutException("No response within received."));
    }

    public Optional<Response> selectInputSource(Sources source) {
        return send(new Command("SI", Optional.of(source.name())));
    }

    public Optional<Response> selectVideoSource(Sources source) {
        return send(new Command("SV", Optional.of(source.name())));
    }

    public Optional<Response> mainZoneOff() {
        return send(new Command("ZMOFF"));
    }

    public Optional<Response> mainZoneOn() {
        return send(new Command("ZMON"));
    }

    public boolean mainZoneEnabled() {
        Optional<Response> response = send(new Command("ZM?"));
        if (response.isPresent()) {
            return response.get().getEvents().get(0).getMessage().equals("ZMON");
        } else {
            throw new TimeoutException();
        }
    }

    public Optional<Response> play(Playback playback) {
        return send(new Command("SI", Optional.of(playback.name())));
    }

    public DigitalInputMode getDigitalInputMode() {
        // TODO handle exceptions in an other way
        Response response = send(new Command(("DC?"))).orElseThrow(() -> new TimeoutException());
        return responseParser.pDC(response).orElseThrow(() -> new IllegalStateException());
    }

    public Optional<Response> digitalInputModeAuto() {
        return send(new Command("DC", Optional.of("AUTO")));
    }

    public Optional<Response> dolbyModePCM() {
        return send(new Command("DC", Optional.of("PCM")));
    }

    public Optional<Response> dolbyModeDts() {
        return send(new Command("DC", Optional.of("DTS")));
    }

    public SurroundMode surroundMode() {
        Response response = send(new Command("MS?")).orElseThrow(() -> new TimeoutException());
        return responseParser.pMS(response).orElseThrow(() ->
                new IllegalStateException("Receiver responded unexpectaly."));
    }

    public OSD createOSD() {
        return new OSD(this);
    }

    public Response isSleepTimerSet() {
        return send(new Command("SLP?")).orElseThrow(() -> new TimeoutException("No response within received."));
    }

    public Optional<Response> sleepTimer(String value) {
        return send(new Command("SLP", Optional.of(value)));
    }

    public Optional<Response> sleepTimerOff() {
        return send(new Command("SLPOFF"));
    }
}
