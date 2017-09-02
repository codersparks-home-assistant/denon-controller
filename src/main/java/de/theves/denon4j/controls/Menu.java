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

package de.theves.denon4j.controls;

import de.theves.denon4j.net.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class description.
 *
 * @author stheves
 */
public class Menu extends AbstractControl {
    private List<String> paramList;

    public Menu(CommandRegistry registry) {
        super("MN", registry);
        setName("Main Menu");
    }

    @Override
    protected void doHandle(Event event) {
        // not needed
    }

    @Override
    protected void doInit() {
        MenuControls[] params = MenuControls.values();
        paramList = new ArrayList<>(params.length);
        paramList.addAll(Stream.of(params).map(Enum::toString).collect(Collectors.toList()));
        register(paramList.toArray(new String[paramList.size()]));
    }

    public void control(MenuControls controls) {
        executeCommand(getCommands().get(paramList.indexOf(controls.toString())).getId());
    }
}
