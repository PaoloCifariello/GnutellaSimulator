/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package edu.p2p.gnutella.core;


import edu.p2p.gnutella.core.log.LogLevel;
import edu.p2p.gnutella.core.log.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractPeer implements Runnable {
    protected String peerAddress;
    protected HashSet<String> neighbours;

    public static int NEIGHBOURS_LIMIT = 5;
    public static long REFRESH_CACHE_TIME = 10000;
    public static long N_CACHED_PONG_SENT = NEIGHBOURS_LIMIT;
    public static long MINIMUM_CACHE_SIZE = NEIGHBOURS_LIMIT;

    private P2PNetwork overlay;
    private Thread thread;
    protected final ConcurrentLinkedQueue<Message> inMessageQueue;

    protected NetworkSimulator network;
    protected HashMap<String, String> messageSources;

    public AbstractPeer() {
        this.peerAddress = UUID.randomUUID().toString();
        this.neighbours = new HashSet<>();
        this.messageSources = new HashMap<>();
        this.inMessageQueue = new ConcurrentLinkedQueue<>();
    }

    public String getPeerAddress() {
        return this.peerAddress;
    }

    public void joinNetwork() {
        // get a random peer address from the network
        HashSet<String> randomPeerAddresses = this.overlay.getRandomPeerAddress();
        // register on the network (just for simulation purposes)
        this.network = overlay.registerPeer(this, this.inMessageQueue);

        // add the new peer to the list of neighbours
        for (String randomPeerAddress: randomPeerAddresses) {
            this.neighbours.add(randomPeerAddress);
            Message message = new Message(MessageType.PING, randomPeerAddress, this.peerAddress);
            message.setPayload(new MessagePayload(this.peerAddress));
            this.sendMessage(message);
        }
    }

    protected void sendMessage(Message message) {
        this.network.sendMessage(message);
    }

    protected void receiveMessage(Message message) {
        // I have to check if the message has expired its TTL
        if (message.isExpired()) { // TTL = 0, ignore the request
            Logger.log("Message received by peer: " + this.peerAddress + " has been dropped, expired TTL", LogLevel.OPTIONAL);
            return;
        }

        switch (message.getType()) {
            case PING:
                this.receivePing(message);
                break;
            case PONG:
                this.receivePong(message);
                break;
            default:
                break;
        }
    }

    public void start(P2PNetwork overlay) {
        this.overlay = overlay;
        this.thread = new Thread(this);
        this.thread.start();
    }

    abstract protected void receivePing(Message message);
    abstract protected void receivePong(Message message);
}
