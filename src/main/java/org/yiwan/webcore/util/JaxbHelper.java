package org.yiwan.webcore.util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * @author Kenny Wang
 * 
 */
public class JaxbHelper {

	/**
	 * xml default encoding is UTF-8
	 * 
	 * @param obj
	 * @return
	 */
	public static String marshal(Object obj) {
		return marshal(obj, "UTF-8");
	}

	/**
	 * convert Java bean to xml string
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String marshal(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * convert xml string java bean
	 * 
	 * @param xml
	 * @param xsd
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String xml, String xsd, Class<T> clazz) {
		T t = null;
		ValidationEventCollector validation = new ValidationEventCollector();
		try {
			// SchemaFactory factory = SchemaFactory
			// .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = factory.newSchema(new File(xsd));
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(validation);
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (JAXBException | SAXException e) {
			e.printStackTrace();
		} finally {
			if (validation != null && validation.hasEvents()) {
				for (ValidationEvent ve : validation.getEvents()) {
					String msg = ve.getMessage();
					ValidationEventLocator vel = ve.getLocator();
					int line = vel.getLineNumber();
					int column = vel.getColumnNumber();
					System.out.println();
					System.err.println("At line " + line + ", column " + column
							+ ": " + msg);
				}
			}
		}

		return t;
	}

}
