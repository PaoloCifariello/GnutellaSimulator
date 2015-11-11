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
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractPeer implements Runnable {
    protected String peerAddress;
    protected ArrayList<String> neighbours;

    public static int NEIGHBOURS_LIMIT = 5; // default value

    private P2PNetwork overlay;
    private Thread thread;
    private ConcurrentLinkedQueue<Message> inMessageQueue;

    protected NetworkSimulator network;
    protected HashMap<String, String> messageSources;



    public AbstractPeer() {
        this.peerAddress = UUID.randomUUID().toString();
        this.neighbours = new ArrayList<>();
        this.messageSources = new HashMap<>();
        this.inMessageQueue = new ConcurrentLinkedQueue<>();
    }

    public String getPeerAddress() {
        return this.peerAddress;
    }

    public void joinNetwork() {
        // get a random peer address from the network
        String randomPeerAddress = this.overlay.getRandomPeerAddress();
        // register on the network (just for simulation purposes)
        this.network = overlay.registerPeer(this, this.inMessageQueue);

        // add the new peer to the list of neighbours
        if (randomPeerAddress != null) {
            this.neighbours.add(randomPeerAddress);
            Message message = new Message(MessageType.PING, randomPeerAddress, this.peerAddress);
            message.setPayload(new MessagePayload(this.peerAddress));
            this.sendMessage(message);
        }
    }

    protected void sendMessage(Message message) {
        this.network.sendMessage(message);
    }

    public void receiveMessage(Message message) {
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

    public void run() {
        this.joinNetwork();

        while (true) {

            if (this.inMessageQueue.size() == 0) {
                try {
                    synchronized (this.inMessageQueue) {
                        this.inMessageQueue.wait();
                    }
                } catch (InterruptedException e) { }    // simply exits from wait state
            }

            Message message;
            while ((message = this.inMessageQueue.poll()) != null) {
                this.receiveMessage(message);
            }
        }
    }

    abstract protected void receivePing(Message message);
    abstract protected void receivePong(Message message);
}
