/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example.optimized;

import p2p.example.cached.CachedPeer;
import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.MessagePayload;
import p2p.simulator.core.MessageType;

import java.util.HashMap;

public class OptimizedCachedPeer extends CachedPeer {
    protected HashMap<String, Long> timestamps = new HashMap<>();

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

        if (cachedPongs.size() < MINIMUM_CACHE_SIZE) {
            this.forwardPing(message);
        } else {
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

    }

    protected void receivePong(Message message) {
        String finalDestination = message.getPayload().getDestination();
        String originalSource = message.getPayload().getSource();

        if (finalDestination.equals(this.peerAddress)) { /* I am the destination of the PONG message */
            if (this.neighbours.size() < NEIGHBOURS_LIMIT) { /*we can accept another neighbour */
                this.neighbours.add(originalSource);
            }

        } else { // Received PONG, but I am not the final destination (need to forward it on the backward path
            String backwardPathAddress = this.messageSources.get(finalDestination);
            Message replyMessage = new Message(MessageType.PONG, backwardPathAddress, this.peerAddress, message.getTTL());
            replyMessage.setPayload(message.getPayload());
            this.sendMessage(replyMessage);
        }

        /* Add the PONG message to the cache */
        this.cachedPongs.add(originalSource);
        this.timestamps.put(originalSource, System.currentTimeMillis());
    }

    private void refreshCache() {
        String toDelete = null;
        long oldestTimestamp = Long.MAX_VALUE;

        for (String cachedPongSource: this.cachedPongs) {
            long timestamp = this.timestamps.get(cachedPongSource);

            if (timestamp < oldestTimestamp) {
                toDelete = cachedPongSource;
                oldestTimestamp = timestamp;
            }
        }

        if (toDelete != null) {
            this.cachedPongs.remove(toDelete);
            this.timestamps.remove(toDelete);
        }

        this.lastRefresh = System.currentTimeMillis();
    }

    public void run() {
        this.joinNetwork();

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
