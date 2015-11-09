/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.basic;


import p2p.simulator.AbstractPeer;
import p2p.simulator.P2PNetwork;

public class BasicGnutella {
    private static int NMESSAGES;

    public static void test(int npeers, int neighbourdLimit, int networkDelay) {
        P2PNetwork overlay = new P2PNetwork();
        overlay.setNetworkDelay(networkDelay);

        for (int i = 0; i < npeers; i++) {
            AbstractPeer newPeer = new BasicPeer();
            newPeer.setNeighbourdLimit(neighbourdLimit);
            newPeer.joinNetwork(overlay);
        }

        NMESSAGES = overlay.getMessageQueue().getNumberOfMessages();
    }

    public static int getNumberOfMessages() {
        return NMESSAGES;
    }
}
