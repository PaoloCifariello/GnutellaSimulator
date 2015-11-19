/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.basic;

import p2p.simulator.core.*;

public class BasicPeer extends AbstractPeer {

    protected void forwardPing(Message message) {
        String pingingPeerAddress = message.getSource();
        String originalPeerAddress = message.getPayload().getSource();

        /* Then I forward the PING request to all my neighbours */
        this.neighbours
                .stream()
                .filter(neighbourAddress -> !neighbourAddress.equals(pingingPeerAddress) && !!neighbourAddress.equals(originalPeerAddress))
                .forEach(neighbourAddress -> { // if it's not the currently pinging peer, send him a Ping
                    Message pingMessage = new Message(MessageType.PING, neighbourAddress, this.peerAddress, message.getTTL());
                    pingMessage.setPayload(message.getPayload());
                    this.sendMessage(pingMessage);
                });
    }

    protected void receivePing(Message message) {
        String pingingPeerAddress = message.getSource();
        String originalSource = message.getPayload().getSource();

        // If I can add more neighbours then I add the pinging peer and I send him back a PONG
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT) {
            this.neighbours.add(originalSource);
        }

        Message response = new Message(MessageType.PONG, pingingPeerAddress, this.peerAddress);
        response.setPayload(new MessagePayload(this.peerAddress, originalSource));
        this.sendMessage(response);

        /* Store source of the message used to send back PONG messages on the same path */
        this.messageSources.put(originalSource, message.getSource());
        this.forwardPing(message);
    }

    protected void receivePong(Message message) {
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

    public void run() {
        this.joinNetwork();

        while (true) {

            if (this.inMessageQueue.size() == 0) {
                try {
                    synchronized (this.inMessageQueue) {
                        this.inMessageQueue.wait();
                    }
                } catch (InterruptedException ignored) { }    // simply exits from wait state
            }

            Message message;
            while ((message = this.inMessageQueue.poll()) != null) {
                this.receiveMessage(message);
            }
        }
    }
}
