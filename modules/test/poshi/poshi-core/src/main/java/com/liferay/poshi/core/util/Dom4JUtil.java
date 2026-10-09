/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.util;

import com.liferay.poshi.core.PoshiProperties;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.dom4j.util.NodeComparator;

/**
 * @author Peter Yoo
 */
public class Dom4JUtil {

	public static void addToElement(Element element, Object... items) {
		for (Object item : items) {
			if (item == null) {
				continue;
			}

			if (item instanceof Attribute) {
				element.add((Attribute)item);

				continue;
			}

			if (item instanceof Element) {
				element.add((Element)item);

				continue;
			}

			if (item instanceof String) {
				element.addText((String)item);

				continue;
			}

			throw new IllegalArgumentException(
				"Only attributes, elements, and strings may be added");
		}
	}

	public static boolean elementsEqual(Node node1, Node node2) {
		NodeComparator nodeComparator = new NodeComparator();

		int compare = nodeComparator.compare(node1, node2);

		if (compare != 0) {
			return false;
		}

		return true;
	}

	public static String format(Element element) throws IOException {
		Node node = element;

		return format(node, true);
	}

	public static String format(Element element, boolean pretty)
		throws IOException {

		Node node = element;

		return format(node, pretty);
	}

	public static String format(Node node) throws IOException {
		return format(node, true);
	}

	public static String format(Node node, boolean pretty) throws IOException {
		Writer writer = new CharArrayWriter();

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();

		outputFormat.setIndent("\t");
		outputFormat.setTrimText(false);

		XMLWriter xmlWriter = null;

		if (pretty) {
			xmlWriter = new XMLWriter(writer, outputFormat);
		}
		else {
			xmlWriter = new XMLWriter(writer);
		}

		xmlWriter.write(node);

		return writer.toString();
	}

	public static Element getNewAnchorElement(
		String href, Element parentElement, Object... items) {

		if (ArrayUtil.isEmpty(items)) {
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

	public static Document parse(String xml) throws DocumentException {
		SAXReader saxReader = new SAXReader();

		return saxReader.read(new StringReader(xml));
	}

	public static void removeWhiteSpaceTextNodes(Element element) {
		for (Node node : toNodeList(element.content())) {
			if (!(node instanceof Text)) {
				continue;
			}

			String nodeText = node.getText();

			nodeText = nodeText.trim();

			if (nodeText.length() != 0) {
				continue;
			}

			node.detach();
		}

		for (Element childElement : toElementList(element.elements())) {
			removeWhiteSpaceTextNodes(childElement);
		}
	}

	public static void replace(
		Element element, boolean cascade, String replacementText,
		String targetText) {

		Iterator<?> attributeIterator = element.attributeIterator();

		while (attributeIterator.hasNext()) {
			Attribute attribute = (Attribute)attributeIterator.next();

			String text = attribute.getValue();

			attribute.setValue(
				StringUtil.replace(text, targetText, replacementText));
		}

		Iterator<?> nodeIterator = element.nodeIterator();

		while (nodeIterator.hasNext()) {
			Node node = (Node)nodeIterator.next();

			if (node instanceof Text) {
				Text textNode = (Text)node;

				String text = textNode.getText();

				if (text.contains(targetText)) {
					text = StringUtil.replace(
						text, targetText, replacementText);

					textNode.setText(text);
				}

				continue;
			}

			if (!(node instanceof Element) || !cascade) {
				continue;
			}

			replace((Element)node, cascade, replacementText, targetText);
		}
	}

	public static List<Attribute> toAttributeList(List<?> list) {
		if (list == null) {
			return null;
		}

		List<Attribute> attributeList = new ArrayList<>(list.size());

		for (Object object : list) {
			attributeList.add((Attribute)object);
		}

		return attributeList;
	}

	public static List<Element> toElementList(List<?> list) {
		if (list == null) {
			return null;
		}

		List<Element> elementList = new ArrayList<>(list.size());

		for (Object object : list) {
			elementList.add((Element)object);
		}

		return elementList;
	}

	public static List<Node> toNodeList(List<?> list) {
		if (list == null) {
			return null;
		}

		List<Node> nodeList = new ArrayList<>(list.size());

		for (Object object : list) {
			nodeList.add((Node)object);
		}

		return nodeList;
	}

	public static void validateDocument(URL url) throws DocumentException {
		SAXReader saxReader = new SAXReader();

		try {
			saxReader.read(url);
		}
		catch (DocumentException documentException) {
			PoshiProperties poshiProperties =
				PoshiProperties.getPoshiProperties();

			if (poshiProperties.debugStacktrace) {
				documentException.printStackTrace();
			}

			throw documentException;
		}
	}

}