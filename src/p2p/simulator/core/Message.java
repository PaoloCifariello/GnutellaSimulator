/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator.core;

public class Message {
    private final String destination;
    private final String source;
    private final MessageType type;
    private long TTL;
    private MessagePayload payload;

    public static long DEFAULT_TTL = 2; // default TTL


    public Message(MessageType type, String destination, String source) {
        this.type = type;
        this.destination = destination;
        this.source = source;
        this.TTL = DEFAULT_TTL;
    }

    public Message(MessageType type, String destination, String source, long TTL) {
        this(type, destination, source);
        this.TTL = TTL;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }

    public MessageType getType() {
        return this.type;
    }

    public void decreaseTTL() {
        this.TTL = this.TTL - 1;
    }

    public boolean isExpired() {
        return this.TTL == 0;
    }

    public long getTTL() {
        return this.TTL;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }
}

enum MessageType {
    PING, PONG
}