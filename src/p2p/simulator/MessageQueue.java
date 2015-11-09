/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator;

import java.util.HashMap;

public class MessageQueue {
    public HashMap<String, AbstractPeer> peerAddressMap;
    private long networkDelay = 100; // Default value: 100ms
    private int nMessages = 0;

    public MessageQueue() {
        this.peerAddressMap = new HashMap<String, AbstractPeer>();
    }


    public int getNumberOfMessages() {
        return this.nMessages;
    }

    public void setNetworkDelay(int networkDelay) {
        this.networkDelay = networkDelay;
    }

    public void registerPeer(String peerAddress, AbstractPeer abstractPeer) {
        this.peerAddressMap.put(peerAddress, abstractPeer);
    }

    public void sendMessage(Message message) {
        // trying to simulate network delay
        try {
            Thread.sleep(this.networkDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Message " + message.getType() + " sent from " + message.getSource() + " to " + message.getDestination());

        message.decreaseTTL();

        AbstractPeer destination = this.peerAddressMap.get(message.getDestination());

        if (destination != null) {
            destination.receiveMessage(message);
        }

        // increase total number of exchanged messages
        this.nMessages++;
    }
}
