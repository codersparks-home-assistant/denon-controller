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

package de.theves.denon4j.net;

import de.theves.denon4j.model.Command;
import de.theves.denon4j.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Sends the actual bytes of a command to the receiver.
 *
 * @author Sascha Theves
 */
public final class TcpClient implements Protocol {
    private final static char CR = 0x0d; // \r character

    private final Logger logger = LoggerFactory.getLogger(TcpClient.class);
    private final Integer port;
    private final String host;
    private final Collection<EventConsumer> eventConsumers;

    private Socket socket;
    private PollingEventReceiver eventReceiver;
    private Writer writer;

    public TcpClient(String host, Integer port) {
        this.host = Optional.ofNullable(host).orElse("127.0.0.1");
        this.port = Optional.ofNullable(port).orElse(23);
        this.eventConsumers = Collections.synchronizedList(new ArrayList<>(10));
    }

    @Override
    public synchronized void connect(int timeout) throws ConnectException {
        if (isConnected()) {
            throw new ConnectException("Already connected.");
        }
        try {
            socket = new Socket();
            socket.setSoTimeout(0);
            socket.connect(new InetSocketAddress(host, port), timeout);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            eventReceiver = new PollingEventReceiver(this, socket);
            eventReceiver.startListening();
        } catch (SocketTimeoutException ste) {
            throw new TimeoutException("Could not establish connection within timeout of " + timeout + " ms.", ste);
        } catch (IOException e) {
            throw new ConnectException("Cannot connect to host/ip " + host + " on port " + port, e);
        }
    }

    private void checkConnection() {
        if (!isConnected()) {
            throw new ConnectionException("Not connected.");
        }
    }

    @Override
    public boolean isConnected() {
        return null != socket && socket.isConnected();
    }

    @Override
    public synchronized void disconnect() {
        if (!isConnected()) {
            return;
        }
        try {
            eventReceiver.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
        } finally {
            socket = null;
            eventReceiver = null;
            writer = null;
        }
    }


    @Override
    public void send(Command command) {
        checkConnection();
        doSend(command);
    }

    private void doSend(Command command) {
        try {
            writer.write(command.build() + CR);
            writer.flush();
        } catch (Exception e) {
            throw new ConnectionException("Communication failure.", e);
        }
    }

    @Override
    public void addEventConsumer(EventConsumer participant) {
        eventConsumers.add(participant);
    }

    @Override
    public void removeEventConsumer(EventConsumer participant) {
        eventConsumers.remove(participant);
    }

    @Override
    public Collection<EventConsumer> getEventConsumers() {
        return Collections.unmodifiableCollection(eventConsumers);
    }

    private void notify(Event event) {
        synchronized (eventConsumers) {
            for (EventConsumer eventConsumer : eventConsumers) {
                try {
                    eventConsumer.onEvent(event);
                } catch (Exception e) {
                    logger.warn("Error invoking consumer", e);
                }
            }
        }
    }

    public void received(String event) {
        notify(new Event(event));
    }
}
