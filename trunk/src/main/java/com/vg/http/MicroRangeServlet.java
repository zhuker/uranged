package com.vg.http;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FilenameUtils.isExtension;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.lang.SystemUtils.getUserHome;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vg.MicroRangeServer.Config;
import com.vg.util.ContentRange;
import com.vg.util.LimitedBandwidthInputStream;
import com.vg.util.RandomAccessFileBufferedInputStream;
import com.vg.util.SeekableInputStream;

public class MicroRangeServlet extends HttpServlet {
    private static final String FrontPage = "<html><body><h2>uranged</h2><p>Simple http server with Content-Range support.</p>"
            + "<p>Streams files in your home folder</p>" + "<p>Bandwidth limited to 512KB/sec</p>" + "</body></html>";
    private static final long serialVersionUID = 1L;
    private final Config config;

    public MicroRangeServlet(Config config) {
        this.config = config;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
        System.out.println(req);

        if (req.getParameter("html5video") != null) {
            resp.getOutputStream().write(
                    ("<html><body><video src='" + pathInfo + "' controls></video></body></html>").getBytes());
            return;
        }
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.getOutputStream().write(FrontPage.getBytes());
            return;
        }
        File file = new File(getUserHome(), pathInfo);
        if (!file.exists() || !file.canRead() || !file.isFile()) {
            resp.sendError(404);
            return;
        }
        long len = file.length();

        if (isExtension(file.getName(), asList("mp4", "m4v", "mov"))) {
            resp.setContentType("video/quicktime");
        }

        resp.setDateHeader("Expires", 0);
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-store,private,no-cache");
        resp.setHeader("Accept-Ranges", "bytes");

        String range = req.getHeader("Range");
        SeekableInputStream baseIn = new RandomAccessFileBufferedInputStream(file);
        InputStream is = baseIn;
        try {
            if (range == null) {
                resp.setHeader("Content-Length", String.valueOf(len));
            } else {
                resp.setStatus(206);
                ContentRange r = ContentRange.parseRange(range, len);
                resp.setHeader("Content-Range", r.toHeaderValue());
                resp.setHeader("Content-Length", String.valueOf(r.getContentLength()));
                is = r.limitedInputStream(baseIn);
            }
            copy(limitBandwidth(is), resp.getOutputStream());
        } finally {
            closeQuietly(is);
        }
    }

    private InputStream limitBandwidth(InputStream in) {
        long bandwidthLimit = config.getBandwidthLimit();
        if (bandwidthLimit > 0) {
            return new LimitedBandwidthInputStream(in, bandwidthLimit);
        }
        return in;
    }

}
