/**
 * P2P Mid Term,
 *
 * An Analysis of the Ping-Pong Gnutella Protocol
 * Deadline 30-11-2015
 *
 * Paolo Cifariello
 */

package edu.p2p.gnutella.simulation;


import edu.p2p.gnutella.simulation.basic.BasicGnutella;
import edu.p2p.gnutella.simulation.cached.CachedGnutella;
import edu.p2p.gnutella.simulation.optimized.OptimizedCachedGnutella;
import edu.p2p.gnutella.core.AbstractPeer;
import edu.p2p.gnutella.core.Message;
import edu.p2p.gnutella.core.NetworkSimulator;
import edu.p2p.gnutella.core.P2PNetwork;
import edu.p2p.gnutella.core.log.LogLevel;
import edu.p2p.gnutella.core.log.Logger;

import java.io.*;
import java.util.Properties;


public class Application {
    /* # of total peers in the network */
    static int N_PEERS;
    /* Test to do (basic, cached or optimized) */
    static String TEST_TYPE;
    /* prints every n seconds how many messages have been forwarded */
    static long DEBUG_OUTPUT_TIME;
    /* n milliseconds between peers join the network  */
    static long JOIN_DELAY;
    /* log level (ESSENTIAL for only important logs, OPTIONAL for all)  */
    static final LogLevel LEVEL_BARRIER = LogLevel.ESSENTIAL;

    public static void main(String[] args) {
        Properties config = null;

        try {
            config = getProperties(args[0]);
        } catch (IOException | IndexOutOfBoundsException e) {
            System.err.println("Please provide a valid configuration file");
            System.exit(1);
        }

        setProperties(config);
        test();
    }

    private static void setProperties(Properties config) {
        Logger.LEVEL_BARRIER = LEVEL_BARRIER;

        /* configuration variables */
        Application.N_PEERS = Integer.parseInt(config.getProperty("N_PEERS"));
        Application.TEST_TYPE = config.getProperty("TEST_TYPE");
        NetworkSimulator.NETWORK_DELAY = Long.parseLong(config.getProperty("NETWORK_DELAY"));
        AbstractPeer.NEIGHBOURS_LIMIT = Integer.parseInt(config.getProperty("NEIGHBOURS_LIMIT"));
        AbstractPeer.MINIMUM_CACHE_SIZE = Integer.parseInt(config.getProperty("MINIMUM_CACHE_SIZE"));
        Message.DEFAULT_TTL = Long.parseLong(config.getProperty("DEFAULT_TTL"));
        Application.DEBUG_OUTPUT_TIME = Long.parseLong(config.getProperty("DEBUG_OUTPUT_TIME"));
        AbstractPeer.REFRESH_CACHE_TIME = Long.parseLong(config.getProperty("REFRESH_CACHE_TIME"));
        AbstractPeer.N_CACHED_PONG_SENT = Long.parseLong(config.getProperty("N_CACHED_PONG_SENT"));
        Application.JOIN_DELAY = Long.parseLong(config.getProperty("JOIN_DELAY"));
        P2PNetwork.BOOTSTRAP_MAX_PEERS_KNOWN = Long.parseLong(config.getProperty("BOOTSTRAP_MAX_PEERS_KNOWN"));

    }

    private static void test() {
        long time_elapsed = 0;

        switch (TEST_TYPE){
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

    private static Properties getProperties(String configFilename) throws IOException {
        File configFile = new File(configFilename);
        FileReader reader = new FileReader(configFile);
        Properties props = new Properties();

        // load properties file:
        props.load(reader);
        return props;
    }
}
