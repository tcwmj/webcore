package org.yiwan.webcore.util;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

public class ProxyHelper {
    private final static Logger logger = LoggerFactory.getLogger(ProxyHelper.class);

    private final static BrowserMobProxy proxy = new BrowserMobProxyServer();

    public static BrowserMobProxy getProxy() {
        return proxy;
    }

    private final static String CONTENT_DISPOSITION = "Content-Disposition";

    @Before
    public void setUp() {
        if (PropHelper.ENABLE_PROXY) {
            logger.info("start proxy");
            proxy.setHarCaptureTypes(CaptureType.getRequestCaptureTypes());
            proxy.start(0);
            if (PropHelper.ENABLE_DOWNLOAD)
                supportFileDownload();
        }
    }

    @After
    public void tearDown() {
        if (proxy.isStarted()) {
            logger.info("stop proxy");
            proxy.stop();
        }
    }

    /**
     * Adds a new ResponseFilter that can be used to examine and manipulate the
     * response before sending it to the client.
     *
     * @param filter filter instance
     */
    private static void addResponseFilter(ResponseFilter filter) {
        logger.info("add a new response filter to the proxy");
        proxy.addResponseFilter(filter);
    }

    /**
     * Adds a new RequestFilter that can be used to examine and manipulate the
     * request before sending it to the server.
     *
     * @param filter filter instance
     */
    private static void addReqeustFilter(RequestFilter filter) {
        logger.info("add a new request filter to the proxy");
        proxy.addRequestFilter(filter);
    }

    public static void newHar() {
        if (enableHar)
            proxy.newHar();
    }

    public static void newHar(String initialPageRef) {
        if (enableHar)
            proxy.newHar(initialPageRef);
    }

    public static void newHar(String initialPageRef, String initialPageTitle) {
        if (enableHar)
            proxy.newHar(initialPageRef, initialPageTitle);
    }

    /**
     * write har to a file
     *
     * @param testcase
     * @param filename file name without extension
     */
    public static void writeHar(final String filename) {
        if (enableHar) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith(filename))
                        return true;
                    return false;
                }
            };
            File[] files = new File(PropHelper.HAR_FOLDER).listFiles(filter);
            String filePath = PropHelper.HAR_FOLDER + filename + "_" + (files == null ? 0 : files.length) + ".har";
            new File(PropHelper.HAR_FOLDER).mkdirs();
            try {
                proxy.getHar().writeTo(new FileWriter(filePath));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void supportRecordTime() {
        addReqeustFilter(new RequestFilter() {
            @Override
            public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents,
                                              HttpMessageInfo messageInfo) {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    /**
     * support download file mechanism through the proxy
     *
     * @param testcase
     */
    public static void supportFileDownload() {
        logger.info("setup proxy to support file download mechianism");
        // set response filter rule for downloading files
        addResponseFilter(new ResponseFilter() {
            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents,
                                       HttpMessageInfo messageInfo) {
                if (isPrepareToDownload() && contents.getContentType() != null) {
                    String filename = PropHelper.TARGET_DATA_FOLDER + Helper.randomize() + ".";
                    if (contents.getContentType().contains("text/csv")) {
                        setDownloadFile(response, filename, "csv");
                        downloadTextFile(contents.getTextContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("text/xml")) {
                        setDownloadFile(response, filename, "xml");
                        downloadTextFile(contents.getTextContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("application/vnd.ms-excel")) {
                        setDownloadFile(response, filename, "xls");
                        downloadBinaryFile(contents.getBinaryContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("application/pdf")) {
                        setDownloadFile(response, filename, "pdf");
                        downloadBinaryFile(contents.getBinaryContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("application/zip")) {
                        setDownloadFile(response, filename, "zip");
                        downloadBinaryFile(contents.getBinaryContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("application/octet-stream")
                            && response.headers().get(CONTENT_DISPOSITION) != null
                            && response.headers().get(CONTENT_DISPOSITION).contains("attachment;filename=")) {
                        setDownloadFile(response, filename, "unknown");
                        downloadBinaryFile(contents.getBinaryContents());
                        completeDownload(response);
                    }
                }
            }
        });
    }

    /**
     * get attachment file name from response header Content-Disposition
     *
     * @return string or null
     */
    private static String getAttachmentFileName(HttpResponse response) {
        if (response.headers().get(CONTENT_DISPOSITION) != null
                && response.headers().get(CONTENT_DISPOSITION).contains("attachment;filename="))
            return response.headers().get(CONTENT_DISPOSITION).replace("attachment;filename=", "").replace(";", "")
                    .replace("\"", "").replace("'", "").trim();
        return null;
    }

    private static void setDownloadFile(HttpResponse response, String filename, String extension) {
        defaultDownloadFileName = getAttachmentFileName(response);
        if (defaultDownloadFileName != null) {
            downloadFile = filename + Helper.getFileExtension(defaultDownloadFileName);
        } else {
            downloadFile = filename + extension;
        }
    }

    private static void downloadTextFile(String text) {
        logger.info("saving text file to " + getDownloadFile());
        try {
            FileUtils.writeStringToFile(new File(getDownloadFile()), text);
        } catch (UnsupportedCharsetException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void downloadBinaryFile(byte[] bytes) {
        logger.info("saving binary file to " + getDownloadFile());
        try {
            FileUtils.writeByteArrayToFile(new File(getDownloadFile()), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void completeDownload(HttpResponse response) {
        response.setStatus(HttpResponseStatus.NO_CONTENT);
        prepareToDownload = false;
    }

    private static boolean prepareToDownload = false;

    public static boolean isPrepareToDownload() {
        return prepareToDownload;
    }

    public static void setPrepareToDownload(boolean prepareToDownload) {
        ProxyHelper.prepareToDownload = prepareToDownload;
    }

    private static String downloadFile;

    /**
     * get last download file name with relative path
     *
     * @return download file name
     */
    public static String getDownloadFile() {
        return downloadFile;
    }

    private static String defaultDownloadFileName;

    /**
     * get default name of download file
     *
     * @return
     */
    public static String getDefaultDownloadFileName() {
        return defaultDownloadFileName;
    }

    /**
     * enable http request archive, default value should be false, true for
     * performance use
     */
    private static boolean enableHar = PropHelper.ENABLE_HAR;

    /**
     * is enable http request archive
     *
     * @return enabled or disabled
     */
    public static boolean isEnableHar() {
        return enableHar;
    }
}
