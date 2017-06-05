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

package de.theves.denon4j.internal.controls;

import de.theves.denon4j.controls.CommandRegistry;
import de.theves.denon4j.controls.CommandStack;
import de.theves.denon4j.internal.net.SetCommandImpl;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class description.
 *
 * @author stheves
 */
public class CommandStackImpl implements CommandStack {
    private final Logger logger = LoggerFactory.getLogger(CommandStackImpl.class);

    private final LinkedList<Command> commandList;

    private final CommandRegistry commandRegistry;

    CommandStackImpl(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
        commandList = new LinkedList<>();
    }

    @Override
    public Command execute(CommandId commandId, String value) {
        synchronized (commandList) {
            // prepare command
            Command cmd = commandRegistry.getCommand(commandId);
            prepareSetCommand(value, cmd);

            // execute
            cmd.execute();

            // save command
            commandList.add(cmd);
            logger.debug("CommandImpl executed: " + cmd);
            return cmd;
        }
    }

    private void prepareSetCommand(String value, Command cmd) {
        if (cmd instanceof SetCommandImpl) {
            ((SetCommandImpl) cmd).set(value);
        }
    }

    @Override
    public List<Command> history() {
        synchronized (commandList) {
            return Collections.unmodifiableList(commandList);
        }
    }

    @Override
    public void redo() {
        synchronized (commandList) {
            commandList.getLast().execute();
        }
    }

    @Override
    public boolean canRedo() {
        synchronized (commandList) {
            return commandList.getLast() != null;
        }
    }
}
