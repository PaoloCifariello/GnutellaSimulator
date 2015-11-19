/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package p2p.example;


import p2p.example.basic.BasicGnutella;
import p2p.example.cached.CachedGnutella;
import p2p.example.optimized.OptimizedCachedGnutella;
import p2p.simulator.core.AbstractPeer;
import p2p.simulator.core.Message;
import p2p.simulator.core.NetworkSimulator;
import p2p.simulator.log.LogLevel;
import p2p.simulator.log.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class Application {
    /* # of total peers in the network */
    static int N_PEERS = 100;
    /* prints every n seconds how many messages have been forwarded */
    static long DEBUG_OUTPUT_TIME = 1000;
    /* n milliseconds between peers join the network  */
    static long JOIN_DELAY = 0;
    /* log level (ESSENTIAL for only important logs, OPTIONAL for all)  */
    static final LogLevel LEVEL_BARRIER = LogLevel.ESSENTIAL;

    public static void main(String[] args) {

        setProperties();
        String testName = "cached"; /* or cached or optimized */
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
            Message.DEFAULT_TTL = Long.parseLong(prop.getProperty("DEFAULT_TTL"));
            Application.DEBUG_OUTPUT_TIME = Long.parseLong(prop.getProperty("DEBUG_OUTPUT_TIME"));
            AbstractPeer.REFRESH_CACHE_TIME = Long.parseLong(prop.getProperty("REFRESH_CACHE_TIME"));
            AbstractPeer.N_CACHED_PONG_SENT = Long.parseLong(prop.getProperty("N_CACHED_PONG_SENT"));
            Application.JOIN_DELAY = Long.parseLong(prop.getProperty("JOIN_DELAY"));

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
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
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
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
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
                    Logger.log("Using basic PING-PONG there have been " + nmessages + " messages exchanged between the " + N_PEERS + " peers", LogLevel.ESSENTIAL);
                }
            }

        }
    }
}
