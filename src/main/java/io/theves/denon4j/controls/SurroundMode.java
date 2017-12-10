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

package io.theves.denon4j.controls;

/**
 * Class description.
 *
 * @author stheves
 */
public enum SurroundMode {
    MOVIE("MOVIE"),
    MUSIC("MUSIC"),
    GAME("GAME"),
    DIRECT("DIRECT"),
    PURE_DIRECT("PURE DIRECT"),
    STEREO("STEREO"),
    STANDARD("STANDARD"),
    DOLBY_DIGITAL("DOLBY DIGITAL"),
    DTS("DTS SURROUND"),
    MULTI_CHANNEL_STEREO("MCH STEREO"),
    ROCK_ARENA("ROCK ARENA"),
    JAZZ_CLUB("JAZZ CLUB"),
    MONO_MOVIE("MONO MOVIE"),
    MATRIX("MATRIX"),
    VIDEO_GAME("VIDEO GAME"),
    VIRTUAL("VIRTUAL"),
    QUICK_SELECT1("QUICK1"),
    QUICK_SELECT2("QUICK2"),
    QUICK_SELECT3("QUICK3"),
    QUICK_SELECT4("QUICK4"),
    QUICK_SELECT1_MEMORY("QUICK1 MEMORY"),
    QUICK_SELECT2_MEMORY("QUICK2 MEMORY"),
    QUICK_SELECT3_MEMORY("QUICK3 MEMORY"),
    QUICK_SELECT4_MEMORY("QUICK4 MEMORY"),
    QUICK_SELECT5_MEMORY("QUICK5 MEMORY"),
    QUICK_SELECT_STATUS("QUICK ?");

    private String mode;

    SurroundMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
