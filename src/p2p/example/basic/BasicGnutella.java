/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.basic;


import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.P2PNetwork;

public class BasicGnutella {
    public static int NPEERS;
    private static P2PNetwork overlay;

    public static void test() {
        overlay = new P2PNetwork();

        for (int i = 0; i < NPEERS; i++) {
            AbstractPeer newPeer = new BasicPeer();
            newPeer.joinNetwork(overlay);
        }
    }

    public static int getNumberOfMessages() {
        return overlay.getNumberOfMessages();
    }
}
