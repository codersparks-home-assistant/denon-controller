/*
 * Copyright 2017 Sascha Theves
 *
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

package io.theves.denon4j;

import io.theves.denon4j.controls.*;
import io.theves.denon4j.logging.LoggingSystem;
import io.theves.denon4j.net.*;
import io.theves.denon4j.net.EventListener;

import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class DenonReceiver implements AutoCloseable, EventDispatcher {
    private static final long RECV_TIMEOUT = 10 * 1000L;

    private final Logger log = Logger.getLogger(DenonReceiver.class.getName());
    private final Object sendReceiveLock = new Object();
    private final List<EventListener> eventListeners;
    private final Protocol protocol;

    private Collection<AbstractControl> controls;
    private Toggle powerToggle;
    private Volume masterVolume;
    private Toggle mainZoneToggle;
    private Toggle muteToggle;
    private Setting selectInput;
    private Setting selectVideo;
    private NetUsbIPodControl netUsb;
    private Menu menu;
    private Setting selectSurround;
    private Session session;
    private RecvContext currentContext;
    private SleepTimer sleepTimer;
    private Volume subwooferVolume;
    private Volume centerVolume;
    private Volume frontLeftVolume;
    private Volume frontRightVolume;
    private Volume surroundLeftVolume;
    private Volume surroundRightVolume;
    private Volume surroundBackRightVolume;
    private Volume surroundBackLeftVolume;
    private Volume surroundBackVolume;
    private Volume frontHeightLeftVolume;
    private Volume frontHeightRightVolume;
    private Slider tunerFrequency;
    private Slider tunerPreset;
    private Setting tunerMode;

    /**
     * Starts auto discovery and chooses first receiver found.
     * Takes some time from time to time, so pls be patient.
     */
    public DenonReceiver(String subnet) {
        this(autoDiscover(subnet).getHostAddress(), 23);
    }

    public DenonReceiver(String host, int port) {
        this(Protocol.tcp(host, port));
    }

    private static InetAddress autoDiscover(String subnet) {
        AutoDiscovery autoDiscovery = new AutoDiscovery();
        autoDiscovery.setSubnet(subnet);
        Collection<InetAddress> discovered = autoDiscovery.discover(1);
        if (discovered.isEmpty()) {
            throw new ConnectionException("No receivers found");
        }
        return discovered.iterator().next();
    }

    public DenonReceiver(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol);
        this.eventListeners = Collections.synchronizedList(new ArrayList<>());
        this.controls = new ArrayList<>();
        this.protocol.setDispatcher(this);

        createControls(this.controls);
        addToDispatcher(this.controls);
        initLogging();
    }

    private void createControls(Collection<AbstractControl> controls) {
        // power control
        powerToggle = new Toggle(this, "PW", "ON", "STANDBY");
        powerToggle.setName("Power Switch");
        controls.add(powerToggle);

        // vol. control
        masterVolume = new Volume(this, "MV", "UP", "DOWN");
        masterVolume.setName("Master Volume");
        controls.add(masterVolume);

        subwooferVolume = new Volume(this, "CV", "SW UP", "SW DOWN") {
            @Override
            public void set(String value) {
                super.set("SW " + value);
            }
        };
        subwooferVolume.setName("Subwoofer Volume");
        controls.add(subwooferVolume);

        centerVolume = new Volume(this, "CV", "C UP", "C DOWN") {
            @Override
            public void set(String value) {
                super.set("C " + value);
            }
        };
        centerVolume.setName("Center Volume");
        controls.add(centerVolume);

        frontLeftVolume = new Volume(this, "CV", "FL UP", "FL DOWN") {
            @Override
            public void set(String value) {
                super.set("FL " + value);
            }
        };
        frontLeftVolume.setName("Front Left Volume");
        controls.add(frontLeftVolume);

        frontRightVolume = new Volume(this, "CV", "FR UP", "FR DOWN") {
            @Override
            public void set(String value) {
                super.set("FR " + value);
            }
        };
        frontRightVolume.setName("Front Right Volume");
        controls.add(frontRightVolume);

        surroundLeftVolume = new Volume(this, "CV", "SL UP", "SL DOWN") {
            @Override
            public void set(String value) {
                super.set("SL " + value);
            }
        };
        surroundLeftVolume.setName("Surround Left Volume");
        controls.add(surroundLeftVolume);

        surroundRightVolume = new Volume(this, "CV", "SR UP", "SR DOWN") {
            @Override
            public void set(String value) {
                super.set("SR " + value);
            }
        };
        surroundRightVolume.setName("Surround Right Volume");
        controls.add(surroundRightVolume);

        surroundBackRightVolume = new Volume(this, "CV", "SBR UP", "SBR DOWN") {
            @Override
            public void set(String value) {
                super.set("SBR " + value);
            }
        };
        surroundBackRightVolume.setName("Surround Back Right Volume");
        controls.add(surroundBackRightVolume);

        surroundBackLeftVolume = new Volume(this, "CV", "SBL UP", "SBL DOWN") {
            @Override
            public void set(String value) {
                super.set("SBL " + value);
            }
        };
        surroundBackLeftVolume.setName("Surround Back Left Volume");
        controls.add(surroundBackLeftVolume);

        surroundBackVolume = new Volume(this, "CV", "SB UP", "SB DOWN") {
            @Override
            public void set(String value) {
                super.set("SB " + value);
            }
        };
        surroundBackVolume.setName("Surround Back Volume");
        controls.add(surroundBackVolume);

        frontHeightLeftVolume = new Volume(this, "CV", "FHL UP", "FHL DOWN") {
            @Override
            public void set(String value) {
                super.set("FHL " + value);
            }
        };
        frontHeightLeftVolume.setName("Front Height Left Volume");
        controls.add(frontHeightLeftVolume);

        frontHeightRightVolume = new Volume(this, "CV", "FHR UP", "FHR DOWN") {
            @Override
            public void set(String value) {
                super.set("FHR " + value);
            }
        };
        frontHeightRightVolume.setName("Front Height Right Volume");
        controls.add(frontHeightRightVolume);

        // mute control
        muteToggle = new Toggle(this, "MU", "ON", "OFF");
        muteToggle.setName("Mute Toggle");
        controls.add(muteToggle);

        // select input
        selectInput = new Setting(this, "SI");
        selectInput.setName("Select INPUT Source");
        controls.add(selectInput);

        // select video
        selectVideo = new Setting(this, "SV");
        selectVideo.setName("Select VIDEO Source");
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new Toggle(this, "ZM", "ON", "OFF");
        mainZoneToggle.setName("Main Zone Toggle");
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        netUsb = new NetUsbIPodControl(this, true);
        controls.add(netUsb);

        // main menu
        menu = new Menu(this);
        controls.add(menu);

        // surround mode settings
        selectSurround = new Setting(this, "MS");
        selectSurround.setName("Select Surround Mode");
        controls.add(selectSurround);

        // sleep timer
        sleepTimer = new SleepTimer(this);
        sleepTimer.setName("Main Zone Sleep Timer setting");
        controls.add(sleepTimer);

        // analog tuner control
        tunerFrequency = new Slider(this, "TFAN", "UP", "DOWN");
        tunerFrequency.setName("Tuner Frequency");
        controls.add(tunerFrequency);

        tunerPreset = new Slider(this, "TPAN", "UP", "DOWN");
        tunerPreset.setName("Tuner Preset");
        controls.add(tunerPreset);
        // TODO tuner preset memory

        tunerMode = new Setting(this, "TMAN");
        tunerMode.setName("Tuner Band/Mode");
        controls.add(tunerMode);
    }

    public Setting tunerMode() {
        return tunerMode;
    }

    public Slider tunerFrequency() {
        return tunerFrequency;
    }

    public Slider tunerPreset() {
        return tunerPreset;
    }

    private void addToDispatcher(Collection<AbstractControl> controls) {
        controls.forEach(this::addListener);
    }

    private void initLogging() {
        new LoggingSystem().initialize();
    }

    public Volume frontLeftVolume() {
        return frontLeftVolume;
    }

    public Volume frontRightVolume() {
        return frontRightVolume;
    }

    public Volume surroundLeftVolume() {
        return surroundLeftVolume;
    }

    public Volume surroundRightVolume() {
        return surroundRightVolume;
    }

    public Volume surroundBackRightVolume() {
        return surroundBackRightVolume;
    }

    public Volume surroundBackLeftVolume() {
        return surroundBackLeftVolume;
    }

    public Volume surroundBackVolume() {
        return surroundBackVolume;
    }

    public Volume frontHeightLeftVolume() {
        return frontHeightLeftVolume;
    }

    public Volume frontHeightRightVolume() {
        return frontHeightRightVolume;
    }

    public Volume centerVolume() {
        return centerVolume;
    }

    public Volume masterVolume() {
        return masterVolume;
    }

    public Volume subwooferVolume() {
        return subwooferVolume;
    }

    public SleepTimer sleepTimer() {
        return sleepTimer;
    }

    public void addListener(EventListener listener) {
        if (null != listener) {
            eventListeners.add(listener);
        }
    }

    public void removeListener(EventListener eventListener) {
        if (null != eventListener) {
            eventListeners.remove(eventListener);
        }
    }

    public Setting surroundMode() {
        return selectSurround;
    }

    public Toggle power() {
        return powerToggle;
    }

    public Toggle mainZone() {
        return mainZoneToggle;
    }

    public Toggle mute() {
        return muteToggle;
    }

    public Setting input() {
        return selectInput;
    }

    public NetUsbIPodControl netUsb() {
        return netUsb;
    }

    public Setting video() {
        return selectVideo;
    }

    public Menu menu() {
        return menu;
    }

    @Override
    public final void dispatch(Event event) {
        recv(event);
        notifyEventListeners(event);
    }

    private void recv(Event event) {
        synchronized (sendReceiveLock) {
            if (isReceiving()) {
                currentContext.received().add(event);
                if (currentContext.fulfilled()) {
                    sendReceiveLock.notify();
                }
            }
        }
    }

    private void notifyEventListeners(Event event) {
        synchronized (eventListeners) {
            eventListeners.forEach(listener -> {
                try {
                    listener.handle(event);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Caught exception from listener: " + listener, e);
                }
            });
        }
    }

    private boolean isReceiving() {
        return currentContext != null && currentContext.isReceiving();
    }

    @Override
    public void close() {
        disconnect();
    }

    public void disconnect() {
        getControls().forEach(this::removeListener);
        protocol.disconnect();
        session.finish();
    }

    public Collection<AbstractControl> getControls() {
        return controls;
    }

    public void connect(int timeout) {
        session = new Session(this);
        protocol.establishConnection(timeout);
    }

    public Session getSession() {
        return session;
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }

    public Event sendRequest(String command, String regex) {
        return sendAndReceive(command, Condition.regex(regex)).stream().findFirst().orElseThrow(() -> new TimeoutException(
            format("No response received after %s milliseconds. Receiver may be too busy to respond.", RECV_TIMEOUT)
        ));
    }

    /**
     * Send the command to the receiver and waits for the response until the <code>condition</code> is fulfilled.
     *
     * @param command the command to send.
     * @param c       the condition.
     * @return the received response.
     */
    public final List<Event> sendAndReceive(String command, Condition c) {
        if (command == null || c == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        synchronized (sendReceiveLock) {
            try {
                currentContext = new RecvContext(c);
                currentContext.beginReceive();
                protocol.send(Command.createCommand(command));
                // check if we can return immediately
                if (currentContext.fulfilled()) {
                    return currentContext.received();
                }
                // wait for response
                try {
                    sendReceiveLock.wait(RECV_TIMEOUT);
                } catch (InterruptedException e) {
                    log.log(Level.FINEST, "Interrupted while waiting for response", e);
                }

                // copy result
                return new ArrayList<>(currentContext.received());
            } finally {
                currentContext.endReceive();
                log.log(Level.FINE, "Send/Recv took: " + currentContext.duration().toString());
            }
        }
    }

    public void send(String command) {
        sendAndReceive(command, Condition.bool(true));
    }

    List<EventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }
}
