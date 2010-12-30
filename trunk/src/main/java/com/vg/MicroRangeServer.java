package com.vg;

import java.lang.Thread.UncaughtExceptionHandler;

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
        long bandwidthLimit = 512 * 1024L;

        public long getBandwidthLimit() {
            return bandwidthLimit;
        }

        public void setBandwidthLimit(long bandwidthLimit) {
            this.bandwidthLimit = bandwidthLimit;
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
