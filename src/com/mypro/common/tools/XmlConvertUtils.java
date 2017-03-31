/**
 * 
 */
package com.mypro.common.tools;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * @author jack.li
 * 
 */
@SuppressWarnings("unchecked")
public class XmlConvertUtils {

	private static final String MAP = "map";
	private static final String LIST = "list";

	@SuppressWarnings("rawtypes")
	public static Element maptoXml(Map<String, Object> map) {
		Element element = DocumentHelper.createElement(MAP);
		for (String key : map.keySet()) {
			Object value = map.get(key);
			Element entry = element.addElement(key.toLowerCase());
			if (value instanceof Map) {
				entry.add(maptoXml((Map) value));
			} else if (value instanceof List) {
				entry.add(listtoXml((List) value));
			} else {
				if(value == null) {
					entry.addText("");
				} else {
					entry.addText(value.toString());
				}
			}
		}
		return element;
	}
	
	@SuppressWarnings("rawtypes")
	public static Element maptoXmlV2(Map<String, Object> map) {
		Element element = DocumentHelper.createElement(MAP);
		for (String key : map.keySet()) {
			Object value = map.get(key);
			Element entry = element.addElement(key);
			if (value instanceof Map) {
				entry.add(maptoXmlV2((Map) value));
			} else if (value instanceof List) {
				entry.add(listtoXmlV2((List) value));
			} else {
				if(value == null) {
					entry.addText("");
				} else {
					entry.addText(value.toString());
				}
			}
		}
		return element;
	}
	
	@SuppressWarnings("rawtypes")
	public static Element listtoXml(List list) {
		Element element = DocumentHelper.createElement(LIST);
		for (Object o : list) {
			if (o instanceof Map) {
				element.add(maptoXml((Map) o));
			}
		}
		return element;
	}
	
	@SuppressWarnings("rawtypes")
	public static Element listtoXmlV2(List list) {
		Element element = DocumentHelper.createElement(LIST);
		for (Object o : list) {
			if (o instanceof Map) {
				element.add(maptoXmlV2((Map) o));
			}
		}
		return element;
	}

	@SuppressWarnings("rawtypes")
	public static Map xmltoMap(Element element) {
		try {
			Map map = new HashMap();
			String node = element.getName();
			if (MAP.equals(node)) {
				List<Element> entrys = element.elements();
				for (Element e : entrys) {
					String key = e.getName();
					Node ele = null;
					if ((ele = e.selectSingleNode(MAP)) != null) {
						map.put(key, xmltoMap((Element) ele));
					} else if ((ele = e.selectSingleNode(LIST)) != null) {
						map.put(key, xmltoList((Element) ele));
					} else {
						String value = e.getStringValue();
						map.put(key, value);
					}
				}
			} else {

			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map xmltoMapV2(Element element) {
		try {
			Map map = new HashMap();
			String node = element.getName();
			if (MAP.equals(node)) {
				List<Element> entrys = element.attributes();
				for (Element e : entrys) {
					String key = e.getName();
					Node ele = null;
					if ((ele = e.selectSingleNode(MAP)) != null) {
						map.put(key, xmltoMapV2((Element) ele));
					} else if ((ele = e.selectSingleNode(LIST)) != null) {
						map.put(key, xmltoListV2((Element) ele));
					} else {
						String value = e.getStringValue();
						map.put(key, value);
					}
				}
			} else {

			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static List xmltoList(Element element) {
		try {
			List<Map> list = new ArrayList<Map>();
			String node = element.getName();
			if (LIST.equals(node)) {
				List<Element> maps = element.selectNodes(MAP);
				for (Element ele : maps) {
					list.add(xmltoMap(ele));
				}
			} else {

			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static List xmltoListV2(Element element) {
		try {
			List<Map> list = new ArrayList<Map>();
			String node = element.getName();
			if (LIST.equals(node)) {
				List<Element> maps = element.selectNodes(MAP);
				for (Element ele : maps) {
					list.add(xmltoMapV2(ele));
				}
			} else {

			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Element string2Doc(String xmlString) {
		StringReader source = new StringReader(xmlString);
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(source);
//			doc.setXMLEncoding("UTF-8");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return doc.getRootElement();
	}

	public static Element reader2Doc(Reader source) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(source);
//			doc.setXMLEncoding("UTF-8");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return doc.getRootElement();
	}
	
	public static Element transform(DOMSource source) {
		try {
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(source, result);
			return string2Doc(writer.getBuffer().toString());
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static void main(String args[]) {
		Map map1 = new HashMap();
		map1.put("A3", "<!a3");
		map1.put("A4", "a4");
		List list = new ArrayList();
		list.add(map1);

		Map map2 = new HashMap();
		map2.put("A5", "a5");
		map2.put("A6", "a6");
		list.add(map2);

		Map map = new HashMap();
		map.put("A1", "a1");
		map.put("A2", list);
		String xml = XmlConvertUtils.maptoXmlV2(map).asXML();
		System.out.println(xml);

//		try {
//			Document document = DocumentHelper.parseText(xml);
//			Element root = document.getRootElement();
//			Map map3 = XmlConvertUtils.xmltoMap(root);
//			System.out.println(map3);
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
		
//		Document doc = XmlConvertUtils.string2Doc("<map>测试</map>");
		//String xml = doc.asXML();
		//System.out.println(xml);
	}
}
