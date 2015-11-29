/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.core;

import p2p.core.log.LogLevel;
import p2p.core.log.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class P2PNetwork {
    public static long BOOTSTRAP_MAX_PEERS_KNOWN = 1;

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

    public NetworkSimulator registerPeer(AbstractPeer abstractPeer, ConcurrentLinkedQueue<Message> peerInMessageQueue) {
        this.connectedAbstractPeers.add(abstractPeer);
        this.networkSimulator.registerPeer(abstractPeer.getPeerAddress(), peerInMessageQueue);
        Logger.log("Peer " + abstractPeer.getPeerAddress() + " has joined the network", LogLevel.OPTIONAL);

        return this.networkSimulator;
    }

    public HashSet<String> getRandomPeerAddress() {
        HashSet<String> ret = new HashSet<>();

        if (this.connectedAbstractPeers.size() > 0) {
            Random randomGenerator = new Random();

            for (int i = 0; i < BOOTSTRAP_MAX_PEERS_KNOWN; i++) {
                int index = randomGenerator.nextInt(this.connectedAbstractPeers.size());
                ret.add(this.connectedAbstractPeers.get(index).getPeerAddress());
            }
        }

        return ret;
    }

}
