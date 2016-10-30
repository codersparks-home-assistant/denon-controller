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

import de.theves.denon4j.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Sends the actual bytes of a command to the receiver.
 *
 * @author Sascha Theves
 */
public final class TcpClient implements NetClient {
    private final Integer port;
    private final String host;
    private Socket socket;
    private EventBus bus;

    public TcpClient(String host, Integer port) {
        this.host = Objects.requireNonNull(host);
        this.port = Objects.requireNonNull(port);
    }

    @Override
    public void connect(int timeout) throws ConnectException {
        if (isConnected()) {
            throw new ConnectException("Already connected.");
        }
        try {
            socket = new Socket();
            socket.setSoTimeout(0);
            socket.connect(new InetSocketAddress(host, port), timeout);
            bus = new EventBus(socket);
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
    public void disconnect() {
        try {
            bus.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
        }
        socket = null;
        bus = null;
    }


    @Override
    public Response sendAndReceive(String command, Optional<String> value) {
        checkConnection();
        try {
            //send
            sendCommand(command, value, socket.getOutputStream());
            // receive the response
            return receiveResponse();
        } catch (Exception e) {
            throw new ConnectionException("Communication failure.", e);
        }
    }

    private void sendCommand(String command, Optional<String> value, OutputStream out) throws IOException {
        String request = buildRequest(command, value);
        out.write(request.getBytes(ENCODING));
        out.flush();
    }

    private Response receiveResponse() throws IOException {
        return new Response(Collections.singletonList(bus.get().get()));
    }

    private String buildRequest(String command, Optional<String> value) {
        String request = command;
        if (value.isPresent()) {
            request += value.get();
        }
        return request + END;
    }
}
