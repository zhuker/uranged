package com.vg.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedBandwidthInputStream extends FilterInputStream {

    private final long limit;
    private long count;
    private final long startTime = System.currentTimeMillis();
    private long check;
    private final long firstBurst;

    /**
     * 
     * @param in
     * @param limit
     *            bandwith limit in bytes per second
     * @param firstBurst
     *            TODO
     */
    public LimitedBandwidthInputStream(InputStream in, long limit, long firstBurst) {
        super(in);
        this.limit = limit;
        this.firstBurst = firstBurst;
    }

    @Override
    public int read() throws IOException {
        int found = super.read();
        this.count += (found >= 0) ? 1 : 0;
        this.check += (found >= 0) ? 1 : 0;
        tryLimit();
        return found;
    }

    private void tryLimit() {
        if (count > firstBurst && check >= 4096) {
            long time = System.currentTimeMillis() - startTime + 1;
            long bps = count * 1000L / time;
            if (bps > limit) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            check = 0;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int found = super.read(b, off, len);
        this.count += (found >= 0) ? found : 0;
        this.check += (found >= 0) ? found : 0;
        tryLimit();
        return found;
    }

}
