package org.yiwan.webcore.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.TestCaseBase;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

public class ProxyHelper {
	private final static Logger logger = LoggerFactory.getLogger(ProxyHelper.class);
	private final static BrowserMobProxy proxy = new BrowserMobProxyServer();

	static {
		proxy.start(0);
	}

	public static BrowserMobProxy getProxy() {
		return proxy;
	}

	public static void startProxy() {
		if (proxy.isStarted()) {
			logger.info("proxy is already started at port " + proxy.getPort());
		} else {
			logger.info("starting proxy");
			proxy.start(0);
		}
	}

	/**
	 * support download file mechanism through the proxy
	 * 
	 * @param testcase
	 */
	public static void supportFileDownload(final TestCaseBase testcase) {
		logger.info("setup proxy to support file download mechianism");
		// set response filter rule for downloading files
		proxy.addResponseFilter(new ResponseFilter() {
			@Override
			public void filterResponse(HttpResponse response, HttpMessageContents contents,
					HttpMessageInfo messageInfo) {
				String fileName = testcase.getTargetDataFolder() + testcase.getTestCaseId() + "_" + Helper.randomize();
				if (contents.getContentType() != null && contents.getContentType().contains("text/csv")) {
					if (contents.getTextContents() != null) {
						testcase.setDownloadFile(fileName + ".csv");
						logger.info("saving csv file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeStringToFile(file, contents.getTextContents());
						} catch (UnsupportedCharsetException | IOException e) {
							logger.error("chartset was unsupported", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				} else if (contents.getContentType() != null && contents.getContentType().contains("text/xml")) {
					if (contents.getTextContents() != null) {
						testcase.setDownloadFile(fileName + ".xml");
						logger.info("saving xml file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeStringToFile(file, contents.getTextContents());
						} catch (UnsupportedCharsetException | IOException e) {
							logger.error("charset was unsupported", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				} else if (contents.getContentType() != null
						&& contents.getContentType().contains("application/vnd.ms-excel")) {
					if (contents.getBinaryContents() != null) {
						testcase.setDownloadFile(fileName + ".xls");
						logger.info("saving xls file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
						} catch (IOException e) {
							logger.error("IO exception occurred", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				} else if (contents.getContentType() != null && contents.getContentType().contains("application/pdf")) {
					if (contents.getBinaryContents() != null) {
						testcase.setDownloadFile(fileName + ".pdf");
						logger.info("saving pdf file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
						} catch (IOException e) {
							logger.error("IO exception occurred", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				} else if (contents.getContentType() != null && contents.getContentType().contains("application/zip")) {
					if (contents.getBinaryContents() != null) {
						testcase.setDownloadFile(fileName + ".zip");
						logger.info("saving zip file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
						} catch (IOException e) {
							logger.error("IO exception occurred", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				} else if (contents.getContentType() != null
						&& contents.getContentType().contains("application/octet-stream")
						&& response.headers().get("Content-Disposition") != null
						&& response.headers().get("Content-Disposition").contains("attachment;filename=")) {
					if (contents.getBinaryContents() != null) {
						testcase.setDownloadFile(
								fileName + "."
										+ response.headers().get("Content-Disposition")
												.replace("attachment;filename=", "").replace(";", "").replace("\"", "")
												.replace("'", "").split("\\.")[1].trim());
						logger.info("saving file to " + testcase.getDownloadFile());
						File file = new File(testcase.getDownloadFile());
						try {
							FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
						} catch (IOException e) {
							logger.error("IO exception occurred", e);
						}
						response.setStatus(HttpResponseStatus.NO_CONTENT);
					}
				}
			}
		});
	}

	public static void stopProxy() {
		if (proxy.isStarted()) {
			logger.info("stopping proxy");
			proxy.stop();
		} else {
			logger.info("proxy isn't started");
		}
	}
}
