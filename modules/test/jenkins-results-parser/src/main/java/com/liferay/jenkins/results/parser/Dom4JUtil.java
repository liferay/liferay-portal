/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import org.json.JSONArray;
import org.json.JSONObject;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Peter Yoo
 */
public class Dom4JUtil {

	public static void addRawXMLToElement(Element element, String xml)
		throws DocumentException {

		Document document = parse("<div>" + xml + "</div>");

		Element rootElement = document.getRootElement();

		List<Element> elements = new ArrayList<>();

		for (Element childElement : rootElement.elements()) {
			rootElement.remove(childElement);

			elements.add(childElement);
		}

		addToElement(element, elements.toArray());
	}

	public static void addToElement(Element element, Object... items) {
		for (Object item : items) {
			if (item == null) {
				continue;
			}

			if (item instanceof Element) {
				Element itemElement = (Element)item;

				itemElement.detach();

				element.add(itemElement);

				continue;
			}

			if (item instanceof Element[]) {
				for (Element itemElement : (Element[])item) {
					itemElement.detach();

					element.add(itemElement);
				}

				continue;
			}

			if (item instanceof String) {
				element.addText((String)item);

				continue;
			}

			throw new IllegalArgumentException(
				"Only elements and strings may be added");
		}
	}

	public static void detach(Object... items) {
		for (Object item : items) {
			if (item instanceof Node) {
				Node node = (Node)item;

				node.detach();
			}
		}
	}

	public static String format(Element element) throws IOException {
		return format(element, true);
	}

	public static String format(Element element, boolean pretty)
		throws IOException {

		Writer writer = new CharArrayWriter();

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();

		outputFormat.setTrimText(false);

		XMLWriter xmlWriter = null;

		if (pretty) {
			xmlWriter = new XMLWriter(writer, outputFormat);
		}
		else {
			xmlWriter = new XMLWriter(writer);
		}

		xmlWriter.write(element);

		return writer.toString();
	}

	public static Element getNewAnchorElement(
		String href, Element parentElement, Object... items) {

		if ((items == null) || (items.length == 0)) {
			return null;
		}

		Element anchorElement = getNewElement("a", parentElement, items);

		anchorElement.addAttribute("href", href);

		return anchorElement;
	}

	public static Element getNewAnchorElement(String href, Object... items) {
		return getNewAnchorElement(href, null, items);
	}

	public static Element getNewElement(String childElementTag) {
		return getNewElement(childElementTag, null);
	}

	public static Element getNewElement(
		String childElementTag, Element parentElement, Object... items) {

		Element childElement = new DefaultElement(childElementTag);

		if (parentElement != null) {
			parentElement.add(childElement);
		}

		if ((items != null) && (items.length > 0)) {
			addToElement(childElement, items);
		}

		return childElement;
	}

	public static Node getNodeByXPath(Document document, String xpathString) {
		List<Node> nodes = getNodesByXPath(document, xpathString);

		if (nodes.isEmpty()) {
			return null;
		}

		return nodes.get(0);
	}

	public static List<Node> getNodesByXPath(
		Document document, String xpathString) {

		XPath xPath = DocumentHelper.createXPath(xpathString);

		return xPath.selectNodes(document);
	}

	public static Element getOrderedListElement(
		List<Element> itemElements, Element parentElement, int maxItems) {

		Element orderedListElement = getNewElement("ol", parentElement);

		int i = 0;

		for (Element itemElement : itemElements) {
			if (i < maxItems) {
				String itemElementName = itemElement.getName();

				if (itemElementName.equals("li")) {
					orderedListElement.add(itemElement);
				}
				else {
					getNewElement("li", orderedListElement, itemElement);
				}

				i++;

				continue;
			}

			getNewElement("li", orderedListElement, "...");

			break;
		}

		return orderedListElement;
	}

	public static Element getOrderedListElement(
		List<Element> itemElements, int maxItems) {

		return getOrderedListElement(itemElements, null, maxItems);
	}

	public static void insertElementAfter(
		Element parentElement, Element targetElement, Element newElement) {

		List<Element> elements = parentElement.elements();

		int targetElementIndex = -1;

		if (targetElement != null) {
			if (!elements.contains(targetElement)) {
				try {
					throw new IllegalArgumentException(
						"Invalid target element\n" + format(targetElement));
				}
				catch (IOException ioException) {
					throw new IllegalArgumentException(
						"Invalid target element");
				}
			}

			targetElementIndex = elements.indexOf(targetElement);
		}

		elements.add(targetElementIndex + 1, newElement);

		setElements(parentElement, elements);
	}

	public static void insertElementBefore(
		Element parentElement, Element targetElement, Element newElement) {

		List<Element> elements = parentElement.elements();

		int targetElementIndex = elements.size();

		if (targetElement != null) {
			if (!elements.contains(targetElement)) {
				try {
					throw new IllegalArgumentException(
						"Invalid target element\n" + format(targetElement));
				}
				catch (IOException ioException) {
					throw new IllegalArgumentException(
						"Invalid target element");
				}
			}

			targetElementIndex = elements.indexOf(targetElement);
		}

		elements.add(targetElementIndex, newElement);

		setElements(parentElement, elements);
	}

	public static Document parse(String xml) throws DocumentException {
		if (xml != null) {
			xml = xml.replaceAll("&#27;", "");
			xml = xml.replaceAll("\u001B", "");
			xml = xml.trim();
		}

		try {
			SAXReader saxReader = new SAXReader();

			return saxReader.read(new StringReader(xml));
		}
		catch (Exception exception1) {
			try {
				DOMReader domReader = new DOMReader();

				DocumentBuilderFactory documentBuilderFactory =
					DocumentBuilderFactory.newInstance();

				DocumentBuilder documentBuilder =
					documentBuilderFactory.newDocumentBuilder();

				org.w3c.dom.Document orgW3CDomDocument = null;

				try {
					String processedXML = JenkinsResultsParserUtil.combine(
						"<!DOCTYPE definition [", _getEntities(), "]>\n",
						xml.replaceAll("<\\?xml[^\\n]+\\n", ""));

					orgW3CDomDocument = documentBuilder.parse(
						new InputSource(new StringReader(processedXML)));
				}
				catch (Exception exception2) {
					try {
						String processedXML = JenkinsResultsParserUtil.combine(
							"<!DOCTYPE definition [", _getEntities(), "]>\n",
							xml);

						orgW3CDomDocument = documentBuilder.parse(
							new InputSource(new StringReader(processedXML)));
					}
					catch (Exception exception3) {
						orgW3CDomDocument = documentBuilder.parse(
							new InputSource(new StringReader(xml)));
					}
				}

				return domReader.read(orgW3CDomDocument);
			}
			catch (IOException | ParserConfigurationException | SAXException
						exception2) {

				throw new RuntimeException(exception2);
			}
		}
	}

	public static void removeWhitespaceTextNodes(Element element) {
		List<Node> nodes = element.content();

		for (int i = nodes.size() - 1; i >= 0; i--) {
			Node node = nodes.get(i);

			if (node instanceof Element) {
				removeWhitespaceTextNodes((Element)node);

				continue;
			}

			if (!(node instanceof Text)) {
				continue;
			}

			String text = node.getText();

			text = text.trim();

			if (text.isEmpty()) {
				nodes.remove(i);
			}
		}
	}

	public static void replace(
		Element element, boolean cascade, String replacementText,
		String targetText) {

		for (Attribute attribute : element.attributes()) {
			String text = attribute.getValue();

			attribute.setValue(text.replace(targetText, replacementText));
		}

		Iterator<? extends Node> nodeIterator = element.nodeIterator();

		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.next();

			if (node instanceof Text) {
				Text textNode = (Text)node;

				String text = textNode.getText();

				if (text.contains(targetText)) {
					text = text.replace(targetText, replacementText);

					textNode.setText(text);
				}
			}
			else if ((node instanceof Element) && cascade) {
				replace((Element)node, cascade, replacementText, targetText);
			}
		}
	}

	public static void setElements(
		Element parentElement, List<Element> elements) {

		if (parentElement == null) {
			throw new IllegalArgumentException("Parent is null");
		}

		for (Element element : parentElement.elements()) {
			parentElement.remove(element);
		}

		for (Element element : elements) {
			parentElement.add(element);
		}
	}

	public static Element toCodeSnippetElement(String content) {
		content = content.replaceAll("\\t", "  ");

		return getNewElement(
			"pre", null, JenkinsResultsParserUtil.redact(content));
	}

	public static void truncateElement(Element element, int size) {
		List<Node> nodes = new ArrayList<>();

		nodes.add(element);
		nodes.addAll(element.attributes());

		for (Node node : nodes) {
			String nodeText = node.getText();

			if ((nodeText != null) && (nodeText.length() > size)) {
				node.setText(nodeText.substring(0, size));
			}
		}

		for (Iterator<Element> iterator = element.elementIterator();
			 iterator.hasNext();) {

			truncateElement(iterator.next(), size);
		}
	}

	private static synchronized String _getEntities() throws IOException {
		if (_entities != null) {
			return _entities;
		}

		JSONObject entitiesJSONObject = _getEntitiesJSONObject();

		Map<String, Integer> map = new TreeMap<>();

		for (String key : entitiesJSONObject.keySet()) {
			JSONObject entityJSONObject = entitiesJSONObject.getJSONObject(key);

			JSONArray codepointsJSONArray = entityJSONObject.getJSONArray(
				"codepoints");

			if ((codepointsJSONArray == null) ||
				codepointsJSONArray.isEmpty()) {

				continue;
			}

			map.put(
				key.replaceAll("([&;])", ""), codepointsJSONArray.getInt(0));
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			sb.append("  <!ENTITY ");
			sb.append(entry.getKey());
			sb.append(" \"&#");
			sb.append(entry.getValue());
			sb.append(";\">\n");
		}

		_entities = sb.toString();

		return _entities;
	}

	private static synchronized JSONObject _getEntitiesJSONObject()
		throws IOException {

		String path = "www.w3.org/TR/html5-author/entities.json";

		File file = new File(
			JenkinsResultsParserUtil.getUserHomeDir(),
			".liferay/mirrors/" + path);

		if (file.exists()) {
			try {
				return new JSONObject(JenkinsResultsParserUtil.read(file));
			}
			catch (Exception exception) {
				System.out.println(
					"WARNING: Unable to get entities from " + file);
			}
		}

		StringBuilder sb = new StringBuilder();

		if (JenkinsResultsParserUtil.isCINode()) {
			sb.append("http://mirrors.lax.liferay.com/");
		}
		else {
			sb.append("https://");
		}

		sb.append(path);

		return JenkinsResultsParserUtil.toJSONObject(sb.toString());
	}

	private static String _entities;

}