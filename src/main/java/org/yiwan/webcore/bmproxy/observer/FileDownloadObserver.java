package org.yiwan.webcore.bmproxy.observer;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.filters.ResponseFilterAdapter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.bmproxy.ProxyWrapper;
import org.yiwan.webcore.test.ITestBase;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class FileDownloadObserver extends SampleObserver {
    private final static Logger logger = LoggerFactory.getLogger(FileDownloadObserver.class);
    private ProxyWrapper proxyWrapper;
    private ITestBase testCase;

    public FileDownloadObserver(ITestBase testCase) {
        this.testCase = testCase;
        this.proxyWrapper = testCase.getProxyWrapper();
        supportDownloadFile(testCase);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (testCase.isPrepareToDownload()) {
            testCase.setPrepareToDownload(false);
        }
    }


    /**
     * support download file mechanism through the proxy
     */
    private void supportDownloadFile(final ITestBase testCase) {
        logger.debug("setup proxy to support file download mechianism");
        // set response filter rule for downloading files
        proxyWrapper.addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(new ResponseFilter() {
            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
                if (testCase.isPrepareToDownload() && contents.getContentType() != null) {
                    String filename = PropHelper.DOWNLOAD_FOLDER + Helper.randomize() + ".";
                    if (contents.getContentType().contains("text/csv")) {
                        setDownloadFile(response, filename, "csv");
                        downloadTextFile(contents.getTextContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("text/plain")) {
                        setDownloadFile(response, filename, "unknown");
                        downloadTextFile(contents.getTextContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("text/xml")) {
                        setDownloadFile(response, filename, "xml");
                        downloadTextFile(contents.getTextContents());
                        completeDownload(response);
                    } else if (contents.getContentType().contains("application/xml")) {
                        setDownloadFile(response, filename, "xml");
                        downloadBinaryFile(contents.getBinaryContents());
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
                    } else if (contents.getContentType().contains("application/octet-stream") && response.headers().get(ProxyWrapper.CONTENT_DISPOSITION) != null && response.headers().get(ProxyWrapper.CONTENT_DISPOSITION).contains("attachment;filename=")) {
                        setDownloadFile(response, filename, "unknown");
                        downloadBinaryFile(contents.getBinaryContents());
                        completeDownload(response);
                    }
                }
            }
        }, PropHelper.MAXIMUM_RESPONSE_BUFFER_SIZE));
    }

    /**
     * get attachment file name from response header Content-Disposition
     *
     * @return string
     */
    private String getAttachmentFileName(HttpResponse response) {
        String patternString = "( )*attachment( )*;( )*filename( )*=";
        if (response.headers().get(ProxyWrapper.CONTENT_DISPOSITION) != null && response.headers().get(ProxyWrapper.CONTENT_DISPOSITION).matches(patternString + ".*")) {
            return response.headers().get(ProxyWrapper.CONTENT_DISPOSITION).replaceFirst(patternString, "").replaceAll("\"|'|;", "").trim();
        }
        return "";
    }

    private void setDownloadFile(HttpResponse response, String filename, String extension) {
        testCase.setDefaultDownloadFileName(getAttachmentFileName(response));
        if (testCase.getDefaultDownloadFileName() != null) {
            testCase.setDownloadFile(filename + Helper.getFileExtension(testCase.getDefaultDownloadFileName()));
        } else {
            testCase.setDownloadFile(filename + extension);
        }
    }

    private void downloadTextFile(String text) {
        logger.debug("saving text file to " + testCase.getDownloadFile());
        try {
            FileUtils.writeStringToFile(new File(testCase.getDownloadFile()), text);
        } catch (UnsupportedCharsetException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void downloadBinaryFile(byte[] bytes) {
        logger.debug("saving binary file to " + testCase.getDownloadFile());
        try {
            FileUtils.writeByteArrayToFile(new File(testCase.getDownloadFile()), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void completeDownload(HttpResponse response) {
//        prevent to get download prompt
        if (PropHelper.HTTP_STATUS_TO_204) {
            response.setStatus(HttpResponseStatus.NO_CONTENT);
        }
        if (testCase.isPrepareToDownload()) {
            testCase.setPrepareToDownload(false);
        }
    }

}
