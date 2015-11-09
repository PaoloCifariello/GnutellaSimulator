/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator;

import java.util.ArrayList;

public class P2PNetwork {
    private ArrayList<AbstractPeer> connectedAbstractPeers;
    private MessageQueue messageQueue;

    public P2PNetwork() {
        this.connectedAbstractPeers = new ArrayList<AbstractPeer>();
        this.messageQueue = new MessageQueue();
    }

    public void setNetworkDelay(int networkDelay) {
        this.messageQueue.setNetworkDelay(networkDelay);
    }

    public MessageQueue getMessageQueue() {
        return this.messageQueue;
    }

    public void registerPeer(AbstractPeer abstractPeer) {
        this.connectedAbstractPeers.add(abstractPeer);
        this.messageQueue.registerPeer(abstractPeer.getPeerAddress(), abstractPeer);
        System.out.println("Peer " + abstractPeer.getPeerAddress() + " has joined the network");
    }

    public String getRandomPeerAddress() {
        String ret = null;

        if (this.connectedAbstractPeers.size() > 0) {
            int randomIndex = (int) (Math.random() * this.connectedAbstractPeers.size());
            AbstractPeer randomAbstractPeer = this.connectedAbstractPeers.get(randomIndex);
            ret = randomAbstractPeer.getPeerAddress();
        }

        return ret;
    }
}
