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
import java.util.UUID;

public abstract class AbstractPeer {
    private String peerAddress;
    private ArrayList<String> neighbours;

    public static int NEIGHBOURS_LIMIT = 5; // default value
    private NetworkSimulator network;

    public AbstractPeer() {
        this.peerAddress = UUID.randomUUID().toString();
        this.neighbours = new ArrayList<String>();
    }

    public String getPeerAddress() {
        return this.peerAddress;
    }

    public void joinNetwork(P2PNetwork overlay) {
        // get a random peer address from the network
        String randomPeerAddress = overlay.getRandomPeerAddress();
        // register on the network (just for simulation purposes)
        this.network = overlay.registerPeer(this);

        // add the new peer to the list of neighbours
        if (randomPeerAddress != null) {
            this.neighbours.add(randomPeerAddress);
            this.sendPing(randomPeerAddress);
        }
    }

    protected void sendPing(String destinationPeerAddress) {
        Message message = new Message(MessageType.PING, destinationPeerAddress, this.peerAddress);
        this.sendMessage(message);
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

        switch (message.getType())
        {
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

    private void receivePing(Message message) {
        String pingingPeerAddress = message.getSource();

        // If I can add more neighbours then I add the pinging peer and I send him back a PONG
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT && !this.neighbours.contains(pingingPeerAddress)) {
            this.neighbours.add(message.getSource());
            Message response = new Message(MessageType.PONG, pingingPeerAddress, this.peerAddress);
            this.network.sendMessage(response);
        }

        // Then I forward the PING request to all my neighbours
        this.neighbours
                .stream()
                .filter(neighbourAddress -> !neighbourAddress.equals(pingingPeerAddress))
                .forEach(neighbourAddress -> { // if it's not the currently pinging peer, send him a Ping
                    Message pingMessage = new Message(MessageType.PING, neighbourAddress, pingingPeerAddress, message.getTTL());
                    this.network.sendMessage(pingMessage);
                });
    }

    private void receivePong(Message message) {
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT) { //we can accept another neighbour
            this.neighbours.add(message.getSource());
        }
    }
}
