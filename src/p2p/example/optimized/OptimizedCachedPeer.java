/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.optimized;

import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.MessagePayload;
import p2p.simulator.core.MessageType;

public class OptimizedCachedPeer extends AbstractPeer {

    protected void receivePing(Message message) {
        String pingingPeerAddress = message.getSource();
        String originalSource = message.getPayload().getSource();

        // If I can add more neighbours then I add the pinging peer and I send him back a PONG
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT && !this.neighbours.contains(originalSource)) {
            this.neighbours.add(originalSource);
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
}
