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

public class Avr1912Demo {

    public void demo(String host, int port) throws Exception {
        try (Avr1912 avr = new Avr1912(host, port)) {
            // show all available commands
            avr.printHelp();
            avr.connect(1000);
            System.out.println("PWON: " + avr.isPowerOn());
            if (!avr.isPowerOn()) {
                // powering on
                avr.togglePower();
            }

            System.out.println("MASTER VOL: " + avr.getMasterVol());
            avr.masterVolDown();
            System.out.println("MASTER VOL: " + avr.getMasterVol());
            avr.setMasterVol(new Value("50"));
            System.out.println("MASTER VOL: " + avr.getMasterVol());
            avr.masterVolUp();
        }
    }

    public static void main(String[] args) throws Exception {
        if (null == args || args.length != 2) {
            System.err.println("Try java -jar $/path/to/jar $host $port");
            System.exit(1);
        }
        System.out.println(String.format("Starting demo... Avr1912: %s:%s",
                args[0], args[1]));
        Avr1912Demo test = new Avr1912Demo();
        test.demo(args[0], Integer.parseInt(args[1]));
        System.out.println("Demo done.");
    }
}
