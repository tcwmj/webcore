package org.yiwan.webcore.util;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final static String CONTENT_DISPOSITION = "Content-Disposition";

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

	public static void newHAR(TestBase testcase) {
		if (testcase.isEnableHAR())
			proxy.newHar();
	}

	public static void newHAR(TestBase testcase, String initialPageRef) {
		if (testcase.isEnableHAR())
			proxy.newHar(initialPageRef);
	}

	public static void newHAR(TestBase testcase, String initialPageRef, String initialPageTitle) {
		if (testcase.isEnableHAR())
			proxy.newHar(initialPageRef, initialPageTitle);
	}

	/**
	 * write har to a file
	 * 
	 * @param testcase
	 * @param filename
	 *            file name without extension
	 */
	public static void writeHAR(TestBase testcase, final String filename) {
		if (testcase.isEnableHAR()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.startsWith(filename))
						return true;
					return false;
				}
			};
			File[] files = new File(testcase.getHARFolder()).listFiles(filter);
			String filePath = testcase.getHARFolder() + filename + "_" + (files == null ? 0 : files.length) + ".har";
			new File(testcase.getHARFolder()).mkdirs();
			try {
				proxy.getHar().writeTo(new FileWriter(filePath));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void supportRecordTime(final TestBase testcase) {
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
	public static void supportFileDownload(final TestBase testcase) {
		logger.info("setup proxy to support file download mechianism");
		// set response filter rule for downloading files
		addResponseFilter(new ResponseFilter() {
			@Override
			public void filterResponse(HttpResponse response, HttpMessageContents contents,
					HttpMessageInfo messageInfo) {
				if (testcase.isPrepareToDownload()) {
					String fileName = testcase.getTargetDataFolder() + testcase.getTestCaseId() + "_"
							+ Helper.randomize() + ".";
					if (contents.getContentType() != null && contents.getContentType().contains("text/csv")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getTextContents() != null) {
							if (testcase.getDefaultDownloadFileName() != null) {
								testcase.setDownloadFile(
										fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							} else {
								testcase.setDownloadFile(fileName + "csv");
							}
							logger.info("saving csv file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeStringToFile(file, contents.getTextContents());
							} catch (UnsupportedCharsetException | IOException e) {
								logger.error("chartset was unsupported", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
					} else if (contents.getContentType() != null && contents.getContentType().contains("text/xml")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getTextContents() != null) {
							if (testcase.getDefaultDownloadFileName() != null) {
								testcase.setDownloadFile(
										fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							} else {
								testcase.setDownloadFile(fileName + "xml");
							}
							logger.info("saving xml file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeStringToFile(file, contents.getTextContents());
							} catch (UnsupportedCharsetException | IOException e) {
								logger.error("charset was unsupported", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
					} else if (contents.getContentType() != null
							&& contents.getContentType().contains("application/vnd.ms-excel")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getBinaryContents() != null) {
							if (testcase.getDefaultDownloadFileName() != null) {
								testcase.setDownloadFile(
										fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							} else {
								testcase.setDownloadFile(fileName + "xls");
							}
							logger.info("saving xls file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
							} catch (IOException e) {
								logger.error("IO exception occurred", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
					} else if (contents.getContentType() != null
							&& contents.getContentType().contains("application/pdf")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getBinaryContents() != null) {
							if (testcase.getDefaultDownloadFileName() != null) {
								testcase.setDownloadFile(
										fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							} else {
								testcase.setDownloadFile(fileName + "pdf");
							}
							logger.info("saving pdf file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
							} catch (IOException e) {
								logger.error("IO exception occurred", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
					} else if (contents.getContentType() != null
							&& contents.getContentType().contains("application/zip")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getBinaryContents() != null) {
							if (testcase.getDefaultDownloadFileName() != null) {
								testcase.setDownloadFile(
										fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							} else {
								testcase.setDownloadFile(fileName + "zip");
							}
							logger.info("saving zip file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
							} catch (IOException e) {
								logger.error("IO exception occurred", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
					} else if (contents.getContentType() != null
							&& contents.getContentType().contains("application/octet-stream")
							&& response.headers().get(CONTENT_DISPOSITION) != null
							&& response.headers().get(CONTENT_DISPOSITION).contains("attachment;filename=")) {
						testcase.setDefaultDownloadFileName(getAttachmentFileName(response));
						if (contents.getBinaryContents() != null) {
							testcase.setDownloadFile(
									fileName + Helper.getFileExtension(testcase.getDefaultDownloadFileName()));
							logger.info("saving file to " + testcase.getDownloadFile());
							File file = new File(testcase.getDownloadFile());
							try {
								FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
							} catch (IOException e) {
								logger.error("IO exception occurred", e);
							}
							response.setStatus(HttpResponseStatus.NO_CONTENT);
							testcase.setPrepareToDownload(false);
						}
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
}
