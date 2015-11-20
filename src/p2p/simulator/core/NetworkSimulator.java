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

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkSimulator implements Runnable {
    public static long NETWORK_DELAY = 100; // Default value: 100ms

    public HashMap<String, ConcurrentLinkedQueue<Message>> peerAddressMap;
    private ConcurrentLinkedQueue<Message> outMessageQueue;
    private int nMessages = 0;

    public NetworkSimulator() {
        this.outMessageQueue = new ConcurrentLinkedQueue<>();;
        this.peerAddressMap = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            // trying to simulate network delay
            try {
                Thread.sleep(NETWORK_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Message nextMessage = this.outMessageQueue.poll();

            if (nextMessage != null) {
                this.forwardMessage(nextMessage);
            }
        }
    }

    public void registerPeer(String peerAddress, ConcurrentLinkedQueue<Message> peerInMessageQueue) {
        this.peerAddressMap.put(peerAddress, peerInMessageQueue);
    }

    public int getNumberOfMessages() {
        return this.nMessages;
    }

    public void sendMessage(Message message) {
        // Adding the new message to the queue
        this.outMessageQueue.add(message);
    }

    private void forwardMessage(Message message) {
        Logger.log("Message " + message.getType() + " sent from " + message.getSource() + " to " + message.getDestination() + "\n"
                + "Originated by " + message.getPayload().getSource() + " and directed to " + message.getPayload().getDestination(), LogLevel.OPTIONAL);
        message.decreaseTTL();
        ConcurrentLinkedQueue<Message> destinationQueue = this.peerAddressMap.get(message.getDestination());

        if (destinationQueue != null) {
            destinationQueue.add(message);
            synchronized (destinationQueue) {
                destinationQueue.notify();
            }
        }

        // increase total number of exchanged messages
        this.nMessages++;
    }
}
