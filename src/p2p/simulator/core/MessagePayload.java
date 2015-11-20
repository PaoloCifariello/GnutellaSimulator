/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulator.core;

import java.util.HashSet;

public class MessagePayload {
    private String source;
    private String destination;
    private HashSet<String> otherSources = new HashSet<>();

    public MessagePayload(String source) {
        this.source = source;
    }

    public MessagePayload(String source, String destination) {
        this(source);
        this.destination = destination;
    }

    public void addOtherSource(String source) {
        if (!source.equals(this.source)) {
            this.otherSources.add(source);
        }
    }

    public HashSet<String> getOtherSources() {
        return this.otherSources;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }
}
