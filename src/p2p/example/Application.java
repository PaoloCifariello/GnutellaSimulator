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
import p2p.example.optimized.OptimizedGnutella;

public class Application {
    static final int NPEERS = 100; // # of peers
    static final int NETWORK_DELAY = 50; // 50ms
    static final int NEIGHBOURD_LIMIT = 10; // max # of neighbours for each peer

    public static void main(String[] args) {
        BasicGnutella.test(NPEERS, NEIGHBOURD_LIMIT, NETWORK_DELAY);
        int nmessages = BasicGnutella.getNumberOfMessages();
        System.out.println("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + NPEERS + " peers");
        //CachedGnutella.test();
        //OptimizedGnutella.test();
    }
}
