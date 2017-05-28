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

/**
 * Class description.
 *
 * @author Sascha Theves
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
    VIRTUAL("VIRTUAL");

    private String mode;

    SurroundMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
