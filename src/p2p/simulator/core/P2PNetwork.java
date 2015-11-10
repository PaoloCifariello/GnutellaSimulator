/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator.core;

import p2p.simulator.log.LogLevel;
import p2p.simulator.log.Logger;

import java.util.ArrayList;

public class P2PNetwork {
    private ArrayList<AbstractPeer> connectedAbstractPeers;
    private NetworkSimulator networkSimulator;

    public P2PNetwork() {
        this.connectedAbstractPeers = new ArrayList<>();
        this.networkSimulator = new NetworkSimulator();
        new Thread(this.networkSimulator).start();
    }


    public int getNumberOfMessages() {
        return this.networkSimulator.getNumberOfMessages();
    }

    public NetworkSimulator registerPeer(AbstractPeer abstractPeer) {
        this.connectedAbstractPeers.add(abstractPeer);
        this.networkSimulator.registerPeer(abstractPeer.getPeerAddress(), abstractPeer);
        Logger.log("Peer " + abstractPeer.getPeerAddress() + " has joined the network", LogLevel.OPTIONAL);

        return this.networkSimulator;
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
