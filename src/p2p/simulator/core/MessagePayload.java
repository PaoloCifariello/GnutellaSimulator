/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator.core;

public class MessagePayload {
    private String source;
    private String destination;

    public MessagePayload(String source) {
        this.source = source;
    }

    public MessagePayload(String source, String destination) {
        this(source);
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }
}
