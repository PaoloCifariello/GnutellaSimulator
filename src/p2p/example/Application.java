/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example;


import p2p.example.basic.BasicGnutella;
import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.NetworkSimulator;
import p2p.simulator.log.LogLevel;
import p2p.simulator.log.Logger;

public class Application {
    static final int NPEERS = 100; // # of peers
    static final int NETWORK_DELAY = 10; // 50ms
    static final int NEIGHBOURS_LIMIT = 4; // max # of neighbours for each peer
    static final int DEFAULT_TTL = 3; // starting TTL of sended messages
    static final long DEBUG_OUTPUT_TIME = 1000; // prints every 1s how many messages have been forwarded

    public static void main(String[] args) {
        // Set up configuration variables
        NetworkSimulator.NETWORK_DELAY = NETWORK_DELAY;
        AbstractPeer.NEIGHBOURS_LIMIT = NEIGHBOURS_LIMIT;
        Message.DEFAULT_TTL = DEFAULT_TTL;
        BasicGnutella.NPEERS = NPEERS;
        Logger.LEVEL_BARRIER = LogLevel.ESSENTIAL;

        // Start up test
        BasicGnutella.test();

        while (true) {
            try {
                Thread.sleep(DEBUG_OUTPUT_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int nmessages = BasicGnutella.getNumberOfMessages();
            Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + NPEERS + " peers", LogLevel.ESSENTIAL);
        }


        //CachedGnutella.test();
        //OptimizedGnutella.test();
    }
}
