package org.yiwan.webcore.util;

import org.apache.xml.utils.DefaultErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Kenny Wang
 */
public class Helper {
    private final static Logger logger = LoggerFactory.getLogger(Helper.class);
    public final static String DISCRIMINATOR_KEY = "scenario";

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
        SimpleDateFormat dateFormat = new SimpleDateFormat(I18N.DATE_PATTERN_EN_US);
        try {
            dateFormat.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
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
        return "<a href = 'javascript:void(0)' onclick=\"window.open ('" + source
                + "','newwindow','height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')\">"
                + text + "</a>";
    }

    /**
     * read xml and xls feed mapping rule from a file
     *
     * @param stream
     * @return Map
     */
    public static Map<String, Map<String, String>> getFeedMapping(InputStream stream) {
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
                        m.put(kv.substring(0, kv.indexOf('=')).trim(), kv.substring(kv.indexOf('=') + 1).trim());
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
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * get file extension
     *
     * @param filename
     * @return file extension string
     */
    public static String getFileExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * get file name without extension
     *
     * @param filename
     * @return file name string without extension
     */
    public static String removeFileExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * get an generic object from a list by specified id
     *
     * @param list
     * @param id
     * @return an generic object
     */
    public static <T> Object filterListById(List<T> list, String id) {
        return filterListBy(list, "getId", id);
    }

    /**
     * get an generic object from a list by specified name
     *
     * @param list
     * @param name
     * @return an generic object
     */
    public static <T> Object filterListByName(List<T> list, String name) {
        return filterListBy(list, "getName", name);
    }

    /**
     * get an generic object from a list by specified method
     *
     * @param list
     * @param by
     * @param value
     * @return an generic object
     */
    public static <T> Object filterListBy(List<T> list, String by, String value) {
        try {
            Method method = list.get(0).getClass().getDeclaredMethod(by);
            for (T element : list) {
                if (method.invoke(element).equals(value))
                    return element;
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * use uuid or current time in milliseconds to generate a random string
     *
     * @return random string
     */
    public static String randomize() {
        String ret;
        if (PropHelper.RANDOM_RULE.equals("uuid")) {
            String s = UUID.randomUUID().toString();
            ret = s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
        } else {
            ret = String.valueOf(System.currentTimeMillis());
        }
        return ret;
    }

    /**
     * merge source and target xml input stream into a single xml string
     *
     * @param src
     * @param tar
     * @return xml string
     */
    public static String mergeXml(InputStream src, InputStream tar) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
        }
        Document d1 = null;
        Document d2 = null;
        try {
            d1 = builder.parse(tar);
            d2 = builder.parse(src);
        } catch (SAXException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        NodeList n1 = d1.getDocumentElement().getChildNodes();
        NodeList n2 = d2.getDocumentElement().getChildNodes();

        for (int i = 0; i < n2.getLength(); i = i + 1) {
            Node n = (Node) d1.importNode(n2.item(i), true);
            n1.item(i).getParentNode().appendChild(n);
        }

        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            logger.error(e.getMessage(), e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(d1);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            logger.error(e.getMessage(), e);
        }
        return result.getWriter().toString();
    }

    /**
     * remove list items with the same id in src object from tar object
     *
     * @param src
     * @param tar
     * @return generic object same as parameters
     */
    public static <T> Object removeListItemById(T src, T tar) {
        Method[] methods = src.getClass().getMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0 && method.getReturnType().equals(List.class)) {
                try {
                    List<?> list1 = (List<?>) method.invoke(src);
                    List<?> list2 = (List<?>) method.invoke(tar);
                    for (int i = 0; i < list1.size(); i++) {
                        Object obj = list1.get(i);
                        Method m = null;
                        try {
                            m = obj.getClass().getMethod("getId");
                        } catch (NoSuchMethodException | SecurityException e) {
                            logger.error(e.getMessage(), e);
                        }
                        String id = (String) m.invoke(obj);
                        list2.remove(filterListById(list2, id));
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return tar;
    }
}
