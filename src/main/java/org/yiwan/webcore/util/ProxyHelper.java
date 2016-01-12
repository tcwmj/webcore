package org.yiwan.webcore.util;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.TestCaseBase;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
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

	/**
	 * Adds a new ResponseFilter that can be used to examine and manipulate the
	 * response before sending it to the client.
	 * 
	 * @param filter
	 *            filter instance
	 */
	private static void addResponseFilter(ResponseFilter filter) {
		logger.info("add a new response filter to the proxy");
		proxy.addResponseFilter(filter);
	}

	/**
	 * Adds a new RequestFilter that can be used to examine and manipulate the
	 * request before sending it to the server.
	 * 
	 * @param filter
	 *            filter instance
	 */
	private static void addReqeustFilter(RequestFilter filter) {
		logger.info("add a new request filter to the proxy");
		proxy.addRequestFilter(filter);
	}

	public static void newHAR(TestCaseBase testcase) {
		if (testcase.isEnableHAR())
			proxy.newHar();
	}

	public static void newHAR(TestCaseBase testcase, String initialPageRef) {
		if (testcase.isEnableHAR())
			proxy.newHar(initialPageRef);
	}

	public static void newHAR(TestCaseBase testcase, String initialPageRef, String initialPageTitle) {
		if (testcase.isEnableHAR())
			proxy.newHar(initialPageRef, initialPageTitle);
	}

	public static void writeHAR(TestCaseBase testcase, final String filename) {
		FilenameFilter filter = new FilenameFilter() {
			private String name = Helper.getFileNameWithoutExtension(filename);

			public boolean accept(File dir, String name) {
				if (name.startsWith(this.name))
					return true;
				return false;
			}
		};
		File[] files = new File(testcase.getHARFolder()).listFiles(filter);
		String filePath = testcase.getHARFolder() + Helper.getFileNameWithoutExtension(filename) + "_"
				+ (files == null ? 0 : files.length) + "." + Helper.getFileExtension(filename);
		new File(testcase.getHARFolder()).mkdirs();
		try {
			proxy.getHar().writeTo(new FileWriter(filePath));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void supportRecordTime(final TestCaseBase testcase) {
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
	public static void supportFileDownload(final TestCaseBase testcase) {
		logger.info("setup proxy to support file download mechianism");
		// set response filter rule for downloading files
		addResponseFilter(new ResponseFilter() {
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

}
