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
import p2p.example.cached.CachedGnutella;
import p2p.example.optimized.OptimizedCachedGnutella;
import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.NetworkSimulator;
import p2p.simulator.log.LogLevel;
import p2p.simulator.log.Logger;


public class Application {
    /* # of total peers in the network */
    static final int NPEERS = 100;
    /* delay between messages sent on the network */
    static final int NETWORK_DELAY = 1;
    /* max # of neighbours for each peer */
    static final int NEIGHBOURS_LIMIT = 5;
    /* default TTL of sent messages */
    static final int DEFAULT_TTL = 4;
    /* prints every n seconds how many messages have been forwarded */
    static final long DEBUG_OUTPUT_TIME = 1000;
    /* Every n seconds of inactivity a peer refresh its own cache */
    static final long REFRESH_CACHE_TIME = 10000;
    /* how many PONG messages are sent back on PING message (only for cached peers) */
    static final long N_CACHED_PONG_SENT = NEIGHBOURS_LIMIT;
    /* n milliseconds between peers join the network  */
    static final long JOIN_DELAY = 0;
    /* log level (ESSENTIAL for only important logs, OPTIONAL for all)  */
    static final LogLevel LEVEL_BARRIER = LogLevel.ESSENTIAL;

    public static void main(String[] args) {
        /* configuration variables */
        NetworkSimulator.NETWORK_DELAY = NETWORK_DELAY;
        AbstractPeer.NEIGHBOURS_LIMIT = NEIGHBOURS_LIMIT;
        AbstractPeer.REFRESH_CACHE_TIME = REFRESH_CACHE_TIME;
        AbstractPeer.N_CACHED_PONG_SENT = N_CACHED_PONG_SENT;
        Message.DEFAULT_TTL = DEFAULT_TTL;
        Logger.LEVEL_BARRIER = LEVEL_BARRIER;

        String testName = "cached"; /* or cached or optimized */

        test(testName);
    }

    private static void test(String testName) {
        switch (testName){
            case "basic":
            {
                /* Start up test */
                BasicGnutella.test(NPEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = BasicGnutella.getNumberOfMessages();
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + NPEERS + " peers", LogLevel.ESSENTIAL);
                }
            }
            case "cached":
            {
                /* Start up test */
                CachedGnutella.test(NPEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = CachedGnutella.getNumberOfMessages();
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + NPEERS + " peers", LogLevel.ESSENTIAL);
                }
            }
            case "optimized":
            {
                /* Start up test */
                OptimizedCachedGnutella.test(NPEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = OptimizedCachedGnutella.getNumberOfMessages();
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + NPEERS + " peers", LogLevel.ESSENTIAL);
                }
            }

        }
    }
}
