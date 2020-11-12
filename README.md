# Denon Controller

## Modifications from forked repo

This is a modified fork of the [denon4j](https://github.com/stheves/denon4j) repo

* New readme create (original archived to [denon4j-README](./denon4j-README.md))
* Moved to use SLF4j instead of custom logger using java.util.logging
* Changed to use spring boot dependencies to standardise versions (as planning on using in spring boot project)
* Moved to use gradle as build system
* removed release and travis files
* Deleted the cli so that this can be just a library
* Deleted AutoDiscovery as it uses ip address stepping
