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

public abstract class AbstractPeer {
    private String peerAddress;
    private ArrayList<String> neighbours;

    public static int NEIGHBOURS_LIMIT = 5; // default value
    private NetworkSimulator network;
    private HashMap<String, String> messageSources;

    public AbstractPeer() {
        this.peerAddress = UUID.randomUUID().toString();
        this.neighbours = new ArrayList<>();
        this.messageSources = new HashMap<>();
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
        String originalSource = message.getPayload().getSource();

        // If I can add more neighbours then I add the pinging peer and I send him back a PONG
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT && !this.neighbours.contains(pingingPeerAddress)) {
            this.neighbours.add(message.getSource());
            Message response = new Message(MessageType.PONG, pingingPeerAddress, this.peerAddress);
            response.setPayload(new MessagePayload(this.peerAddress, originalSource));
            this.sendMessage(response);
        }

        // Store source of the message used to send back PONG messages
        this.messageSources.put(originalSource, message.getSource());

        // Then I forward the PING request to all my neighbours
        this.neighbours
                .stream()
                .filter(neighbourAddress -> !neighbourAddress.equals(pingingPeerAddress))
                .forEach(neighbourAddress -> { // if it's not the currently pinging peer, send him a Ping
                    Message pingMessage = new Message(MessageType.PING, neighbourAddress, this.peerAddress, message.getTTL());
                    pingMessage.setPayload(message.getPayload());
                    this.sendMessage(pingMessage);
                });
    }

    private void receivePong(Message message) {
        String finalDestination = message.getPayload().getDestination();
        String originalSource = message.getPayload().getSource();

        if (finalDestination.equals(this.peerAddress)) { // I am the destination of the PONG message
            if (this.neighbours.size() < NEIGHBOURS_LIMIT) { //we can accept another neighbour
                this.neighbours.add(originalSource);
            }
        } else { // Received PONG, but I am not the final destination (need to forward it on the backward path
            String backwardPathAddress = this.messageSources.get(finalDestination);
            Message replyMessage = new Message(MessageType.PONG, backwardPathAddress, this.peerAddress, message.getTTL());
            replyMessage.setPayload(message.getPayload());
            this.sendMessage(replyMessage);

        }


    }
}
