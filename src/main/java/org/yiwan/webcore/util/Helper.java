package org.yiwan.webcore.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.utils.DefaultErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kenny Wang
 * 
 */
public class Helper {
	private final static Logger logger = LoggerFactory.getLogger(Helper.class);

	/**
	 * convert a string date from a format to a format
	 * 
	 * @param date
	 * @param fromFormat
	 * @param toFormat
	 * @return string
	 */
	public static String toDate(String date, String fromFormat, String toFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
		try {
			Date d = dateFormat.parse(date);
			return (new SimpleDateFormat(toFormat)).format(d);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return date;
	}

	/**
	 * whether a data string is by a date format
	 * 
	 * @param date
	 * @param format
	 * @return boolean
	 */
	public static boolean isDate(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * whether a data string is by US date format
	 * 
	 * @param date
	 * @return boolean
	 */
	public static boolean isDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				I18N.DATE_PATTERN_EN_US);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * assert file exists in a time range of Property.FILE_ACCESSABLE
	 * 
	 * @param path
	 */
	public static void assertFileExists(String path) {
		File file = new File(path);
		Assert.assertTrue(file.exists(), path);
	}

	/**
	 * delete a file
	 * 
	 * @param filepath
	 */
	public static void deleteFile(String filepath) {
		File file = new File(filepath);
		file.delete();
	}

	/**
	 * use current time in milliseconds to generate a string
	 * 
	 * @return random string
	 */
	public static String randomize() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * validate xml file by a specified xsd file
	 * 
	 * @param xmlfile
	 * @param xsdfile
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void validateXml(File xmlfile, File xsdfile) throws Exception {
		final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);

		SAXParser parser = factory.newSAXParser();
		parser.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
		parser.setProperty(SCHEMA_SOURCE, xsdfile);

		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(new DefaultHandler());
		xmlReader.setErrorHandler(new DefaultErrorHandler());
		xmlReader.parse(xmlfile.getAbsolutePath());
	}

	/**
	 * get a string by reporting style
	 * 
	 * @param source
	 * @param text
	 * @return report string
	 */
	public static String getTestReportStyle(String source, String text) {
		return "<a href = 'javascript:void(0)' onclick=\"window.open ('"
				+ source
				+ "','newwindow','height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')\">"
				+ text + "</a>";
	}

	/**
	 * read xml and xls feed mapping rule from a file
	 * 
	 * @param stream
	 * @return Map
	 */
	public static Map<String, Map<String, String>> getFeedMapping(
			InputStream stream) {
		Reader r = null;
		try {
			r = new InputStreamReader(stream, "utf-8");
			Properties props = new Properties();
			props.load(r);
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Set<Entry<Object, Object>> entrySet = props.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				if (!entry.getKey().toString().startsWith("#")) {
					Map<String, String> m = new HashMap<String, String>();
					String s = ((String) entry.getValue()).trim();
					s = s.substring(1, s.length() - 1);
					String[] kvs = s.split(",");
					for (String kv : kvs) {
						m.put(kv.substring(0, kv.indexOf('=')).trim(), kv
								.substring(kv.indexOf('=') + 1).trim());
						map.put(((String) entry.getKey()).trim(), m);
					}
				}
			}
			return map;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	/**
	 * convert a word's first letter to lower case
	 * 
	 * @param s
	 * @return word with first letter in lower case
	 */
	public static String firstLetterToLowerCase(String s) {
		return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0)))
				.append(s.substring(1)).toString();
	}
}
