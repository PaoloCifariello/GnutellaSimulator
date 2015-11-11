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

public class BasicGnutella implements Runnable {
    public static int NPEERS;
    public static long JOIN_DELAY;

    private static P2PNetwork overlay;

    public static void test() {
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
