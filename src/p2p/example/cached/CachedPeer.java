/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.cached;

import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.MessagePayload;
import p2p.simulator.core.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CachedPeer extends AbstractPeer {
    HashSet<String> cachedPongs = new HashSet<>();
    private long lastRefresh;

    protected void receivePing(Message message) {
        String pingingPeerAddress = message.getSource();
        String originalSource = message.getPayload().getSource();

        /* If I can add more neighbours then I add the pinging peer and I send him back a PONG */
        if (this.neighbours.size() < this.NEIGHBOURS_LIMIT && !this.neighbours.contains(originalSource)) {
            this.neighbours.add(originalSource);
        }

        /* send the PONG message back */
        Message response = new Message(MessageType.PONG, pingingPeerAddress, this.peerAddress);
        response.setPayload(new MessagePayload(this.peerAddress, originalSource));
        this.sendMessage(response);

        // Store source of the message used to send back PONG messages
        this.messageSources.put(originalSource, message.getSource());

        int sentPong = 0;

        for (String cachedPongSource: cachedPongs) {
            if (!cachedPongSource.equals(originalSource)) {
                Message cachedResponse = new Message(MessageType.PONG, pingingPeerAddress, this.peerAddress);
                cachedResponse.setPayload(new MessagePayload(cachedPongSource, originalSource));
                this.sendMessage(cachedResponse);
                sentPong++;

                /* we have already sent the maximum number of PONG messages */
                if (sentPong >= N_CACHED_PONG_SENT) {
                    break;
                }
            }
        }
    }

    protected void receivePong(Message message) {
        String finalDestination = message.getPayload().getDestination();
        String originalSource = message.getPayload().getSource();

        if (finalDestination.equals(this.peerAddress)) { // I am the destination of the PONG message
            /* Add the new neighbour only if it was not already present in my neighbours list
             * and if I have enough space to collect him */
            if (this.neighbours.size() < NEIGHBOURS_LIMIT && !this.neighbours.contains(finalDestination)) { //we can accept another neighbour
                this.neighbours.add(originalSource);
            }

            /* Then I store in the peer cache the IP of the PONG message source */
            this.cachedPongs.add(originalSource);
        } else { // Received PONG, but I am not the final destination (need to forward it on the backward path
            String backwardPathAddress = this.messageSources.get(finalDestination);
            Message replyMessage = new Message(MessageType.PONG, backwardPathAddress, this.peerAddress, message.getTTL());
            replyMessage.setPayload(message.getPayload());
            this.sendMessage(replyMessage);
        }
    }

    private void refreshCache() {
        /* Send a PING message to all the neighbours in order to refresh the cache */
        this.neighbours
                .stream()
                .forEach(neighbourAddress -> { // if it's not the currently pinging peer, send him a Ping
                    Message pingMessage = new Message(MessageType.PING, neighbourAddress, this.peerAddress);
                    pingMessage.setPayload(new MessagePayload(this.peerAddress));
                    this.sendMessage(pingMessage);
                });
        this.lastRefresh = System.currentTimeMillis();
    }

    public void run() {
        this.joinNetwork();
        this.refreshCache();


        while (true) {
            if (this.inMessageQueue.size() == 0) {
                try {
                    synchronized (this.inMessageQueue) {
                        this.inMessageQueue.wait(500);
                    }
                } catch (InterruptedException ignored) { }    // simply exits from wait state
            }

            Message message;
            while ((message = this.inMessageQueue.poll()) != null) {
                this.receiveMessage(message);
            }


            if (System.currentTimeMillis() - this.lastRefresh >=  REFRESH_CACHE_TIME) {
                /* Refresh cached PONG messages */
                this.refreshCache();
            }
        }
    }
}
