package com.vg;

import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.lang.math.NumberUtils;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.vg.http.MicroRangeServlet;

public class MicroRangeServer {
    static {
        setDefaultUncaughtExceptionHandler();
    }

    public static class Config {
        long bandwidthLimit = 2 * 1024 * 1024L;
        long firstBurst = 5000000L;
        int port = 8085;

        public long getFirstBurst() {
            return firstBurst;
        }

        public long getBandwidthLimit() {
            return bandwidthLimit;
        }

        public void setBandwidthLimit(long bandwidthLimit) {
            this.bandwidthLimit = bandwidthLimit;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }

    private Context context;
    private Server jetty;
    private Config config = new Config();

    public MicroRangeServer() {
    }

    void init() {
        Connector connector = new SelectChannelConnector();
        connector.setPort(8085);
        jetty = new Server();
        jetty.addConnector(connector);

        context = new Context(jetty, "/", Context.SESSIONS);
        context.addServlet(new ServletHolder(new MicroRangeServlet(config)), "/*");
    }

    public static void main(String[] args) throws Exception {
        MicroRangeServer s = new MicroRangeServer();
        if (args.length >= 1) {
            s.config.setPort(toInt(args[0], s.config.getPort()));
        }
        s.init();
        s.jetty.start();
        s.jetty.join();
    }

    public static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Uncaught exception in: " + t);
                System.out.println(e);
                e.printStackTrace();
                System.exit(123);
            }
        });
    }

}
