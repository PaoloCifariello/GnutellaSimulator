/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.cached;


import p2p.example.basic.BasicGnutella;
import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.P2PNetwork;

public class CachedGnutella extends BasicGnutella {

    public static void test(int NPEERS, long JOIN_DELAY) {
        CachedGnutella.NPEERS = NPEERS;
        CachedGnutella.JOIN_DELAY = JOIN_DELAY;
        new Thread(new CachedGnutella()).start();
    }

    public static int getNumberOfMessages() {
        return overlay.getNumberOfMessages();
    }

    @Override
    public void run() {
        overlay = new P2PNetwork();

        for (int i = 0; i < NPEERS; i++) {
            AbstractPeer newPeer = new CachedPeer();
            newPeer.start(overlay);
            try {
                Thread.sleep(JOIN_DELAY);
            } catch (InterruptedException e) { }
        }
    }
}
