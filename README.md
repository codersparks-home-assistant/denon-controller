#denon4j Java API

[![Build Status](https://travis-ci.org/sath1982/denon4j.svg?branch=master)](https://travis-ci.org/sath1982/denon4j)

The denon4j project provides a java API for communicating with a Denon AVR (currently only AVR1912 is supported and tested)
via TCP/IP. The project is intended to be easily extended for those who want to write an API for their own AVR model.

Contributions are welcome!

# Prerequisites

- Java 8
- Maven 3.2+ (only for building)
- You must be connected to the same network as your AV receiver

# Basic Usage

```
Avr1912 avr = new Avr1912(host, port);
avr.connect(1000);
avr.volumeUp();
avr.disconnect();
```

# Building

This is a maven style project so `mvn` needs to be on your `$PATH`. To build
from scratch just type:

> mvn clean package

# License

Apache License Version 2.0, January 2004 (see LICENSE file)

# Contributing

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create a new [Issue](https://github.com/sath1982/denon4j/issues/new) with a link to your branch, or just make a Pull Request.
6. Enjoy a refreshing Diet Coke and wait