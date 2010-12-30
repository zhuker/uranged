package com.vg.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SeekableInputStream extends InputStream {

    public abstract long seek(long position) throws IOException;

    public abstract long length() throws IOException;

    public abstract long position() throws IOException;

    @Override
    public synchronized void reset() throws IOException {
        seek(0);
    }

    protected void checkIdx(long position) throws IOException {
        long len = length();
        if (position < 0 || position >= len) {
            throw new IndexOutOfBoundsException("trying to seek to " + position + " while max len is " + len);
        }
    }

    protected final int checkRead(int read) throws IOException {
        if (read != -1) {
            return read;
        }
        long pos = position();
        long len = length();
        if (pos != len) {
            throw new IllegalStateException("position " + pos + " at end of stream != length " + len);
        }
        return read;
    }

}
