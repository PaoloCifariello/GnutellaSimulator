# Gnutella Simulator
Gnutella protocol simulator

### Description

An attempt to build a simulator of the Gnutella P2P protocol. An implementation of three main strategies has been developed and tested:

- Basic Gnutella with on-demand PING
- Cached Gnutella with periodic PING
- Optimized Gnutella

### Usage
You can provide options in a configuration file (an example is provided in [config.properties](config.properties)) and then you can use Maven to compile:

```
mvn clean install
java -jar target/Gnutella-1.0.jar config.properties
```
