/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulation.basic;


import p2p.core.AbstractPeer;
import p2p.core.P2PNetwork;

public class BasicGnutella implements Runnable {
    public static int NPEERS;
    public static long JOIN_DELAY;

    protected static P2PNetwork overlay;

    public static void test(int NPEERS, long JOIN_DELAY) {
        BasicGnutella.NPEERS = NPEERS;
        BasicGnutella.JOIN_DELAY = JOIN_DELAY;
        new Thread(new BasicGnutella()).start();
    }

    public static int getNumberOfMessages() {
        return overlay.getNumberOfMessages();
    }

    @Override
    public void run() {
        overlay = new P2PNetwork();

        for (int i = 0; i < NPEERS; i++) {
            AbstractPeer newPeer = new BasicPeer();
            newPeer.start(overlay);
            try {
                Thread.sleep(JOIN_DELAY);
            } catch (InterruptedException e) { }
        }
    }
}
