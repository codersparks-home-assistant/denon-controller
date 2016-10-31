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

package de.theves.denon4j.model;

public enum Sources {
    CD("CD"), TUNER("TUNER"), DVD("DVD"), BD("BD"), TV("TV"), SAT_CBL("SAT/CBL"), GAME(
            "GAME"), GAME2("GAME2"), AUX("V.AUX"), DOCK("DOCK"), SOURCE(
            "SOURCE"), IPOD("IPOD"), NET_UBS("NET/USB"), RHAPSODY("RHAPSODY"), NAPSTER(
            "NAPSTER"), PANDORA("PANDORA"), LASTFM("LASTFM"), FLICKR("FLICKR"), FAVORITES(
            "FAVORITES"), IRADIO("IRADIO"), UPNP_SERVER("SERVER"), USB_IPOD(
            "USB/IPOD");

    private String inputSource;

    Sources(String source) {
        this.inputSource = source;
    }

    public String getInputSource() {
        return inputSource;
    }

    @Override
    public String toString() {
        return inputSource;
    }
}
