/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.simulation;


import p2p.simulation.basic.BasicGnutella;
import p2p.simulation.cached.CachedGnutella;
import p2p.simulation.optimized.OptimizedCachedGnutella;
import p2p.core.AbstractPeer;
import p2p.core.Message;
import p2p.core.NetworkSimulator;
import p2p.core.P2PNetwork;
import p2p.core.log.LogLevel;
import p2p.core.log.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class Application {
    /* # of total peers in the network */
    static int N_PEERS;
    /* prints every n seconds how many messages have been forwarded */
    static long DEBUG_OUTPUT_TIME;
    /* n milliseconds between peers join the network  */
    static long JOIN_DELAY;
    /* log level (ESSENTIAL for only important logs, OPTIONAL for all)  */
    static final LogLevel LEVEL_BARRIER = LogLevel.ESSENTIAL;

    public static void main(String[] args) {

        setProperties();
        String testName = "optimized"; /* or cached or optimized */
        test(testName);
    }

    private static void setProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);

            Logger.LEVEL_BARRIER = LEVEL_BARRIER;

            /* configuration variables */
            Application.N_PEERS = Integer.parseInt(prop.getProperty("N_PEERS"));
            NetworkSimulator.NETWORK_DELAY = Long.parseLong(prop.getProperty("NETWORK_DELAY"));
            AbstractPeer.NEIGHBOURS_LIMIT = Integer.parseInt(prop.getProperty("NEIGHBOURS_LIMIT"));
            AbstractPeer.MINIMUM_CACHE_SIZE = Integer.parseInt(prop.getProperty("MINIMUM_CACHE_SIZE"));
            Message.DEFAULT_TTL = Long.parseLong(prop.getProperty("DEFAULT_TTL"));
            Application.DEBUG_OUTPUT_TIME = Long.parseLong(prop.getProperty("DEBUG_OUTPUT_TIME"));
            AbstractPeer.REFRESH_CACHE_TIME = Long.parseLong(prop.getProperty("REFRESH_CACHE_TIME"));
            AbstractPeer.N_CACHED_PONG_SENT = Long.parseLong(prop.getProperty("N_CACHED_PONG_SENT"));
            Application.JOIN_DELAY = Long.parseLong(prop.getProperty("JOIN_DELAY"));
            P2PNetwork.BOOTSTRAP_MAX_PEERS_KNOWN = Long.parseLong(prop.getProperty("BOOTSTRAP_MAX_PEERS_KNOWN"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void test(String testName) {
        long time_elapsed = 0;

        switch (testName){
            case "basic":
            {
                /* Start up test */
                BasicGnutella.test(N_PEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = BasicGnutella.getNumberOfMessages();
                    time_elapsed += DEBUG_OUTPUT_TIME;
                    Logger.log(time_elapsed/1000 + ": Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
                }
            }
            case "cached":
            {
                /* Start up test */
                CachedGnutella.test(N_PEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = CachedGnutella.getNumberOfMessages();
                    time_elapsed += DEBUG_OUTPUT_TIME;
                    Logger.log(time_elapsed/1000 + ": Using cached PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
                }
            }
            case "optimized":
            {
                /* Start up test */
                OptimizedCachedGnutella.test(N_PEERS, JOIN_DELAY);

                while (true) {
                    try {
                        Thread.sleep(DEBUG_OUTPUT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int nmessages = OptimizedCachedGnutella.getNumberOfMessages();
                    time_elapsed += DEBUG_OUTPUT_TIME;
                    Logger.log(time_elapsed/1000 + ": Using optimized cached PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
                }
            }

        }
    }
}
