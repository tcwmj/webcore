package org.yiwan.webcore.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Kenny Wang
 * 
 */
public class I18N {
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(I18N.class);

	private final String langPath = "config/lang";

	private final String lang;

	private HashMap<String, String> map;

	public static final String DATE_PATTERN_EN_US = "MM/dd/yyyy";
	private static final String DATE_PATTERN_EN = "dd/MM/yyyy";
	private static final String DATE_PATTERN_ZH_CN = "yyyy-MM-dd";

	public static final String EN_US = "en_US";
	public static final String EN = "en";
	public static final String ZH_CN = "zh_CN";

	private static final String ENCODING_UTF_8 = "UTF-8";
	private static final String ENCODING_ISO_8859_1 = "ISO-8859-1";

	/**
	 * @param lang
	 */
	public I18N(String lang) {
		this.lang = lang;
		this.map = loadLang(lang);
	}

	/**
	 * @param lang
	 * @return string
	 */
	private HashMap<String, String> loadLang(String lang) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					langPath + File.separator + lang + ".properties"));
			props.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> ret = new HashMap<String, String>();
		try {
			Enumeration<?> en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = props.getProperty(key);
				value = new String(value.getBytes(ENCODING_ISO_8859_1),
						ENCODING_UTF_8);
				ret.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @param date
	 * @return date
	 */
	public String toDate(String date) {
		switch (lang) {
		case ZH_CN:
			return Helper.toDate(date, DATE_PATTERN_EN_US, DATE_PATTERN_ZH_CN);
		case EN:
			return Helper.toDate(date, DATE_PATTERN_EN_US, DATE_PATTERN_EN);
		}
		return date;
	}

	/**
	 * @param string
	 * @return string
	 */
	public String toString(String string) {
		if (map.containsKey(string))
			return map.get(string);
		else
			return string;
	}
}
