/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.core.log;

public class Logger {
    public static LogLevel LEVEL_BARRIER = LogLevel.ESSENTIAL; // by default it shows only ESSENTIAL logs

    public static void log(String str, LogLevel level) {
        if (level.ordinal() >= LEVEL_BARRIER.ordinal()) {
            System.out.println(str);
        }
    }
}

