/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowException;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.xml.sax.InputSource;

/**
 * @author Rafael Praxedes
 */
public class WorkflowDefinitionContentUtil {

	public static String toJSON(String content) throws WorkflowException {
		if (Validator.isNull(content) ||
			content.startsWith(StringPool.OPEN_CURLY_BRACE)) {

			return content;
		}

		try {
			DocumentBuilderFactory documentBuilderFactory =
				SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(
				new InputSource(new StringReader(content)));

			JSONObject jsonObject = _toJSONObject(
				document.getDocumentElement());

			return jsonObject.toString();
		}
		catch (Exception exception) {
			throw new WorkflowException(
				"Unable to convert XML to JSON", exception);
		}
	}

	public static String toXML(String content) throws WorkflowException {
		if (Validator.isNull(content) ||
			content.startsWith(StringPool.LESS_THAN)) {

			return content;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\"?>");

		try {
			_toNode(JSONFactoryUtil.createJSONObject(content), sb);
		}
		catch (JSONException jsonException) {
			throw new WorkflowException(
				"Unable to convert JSON to XML", jsonException);
		}

		return sb.toString();
	}

	private static void _appendAttributes(
		Element element, JSONObject jsonObject) {

		NamedNodeMap namedNodeMap = element.getAttributes();

		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node node = namedNodeMap.item(i);

			jsonObject.put(node.getNodeName(), node.getNodeValue());
		}
	}

	private static void _appendAttributes(
		JSONObject jsonObject, StringBuilder sb) {

		for (String key : jsonObject.keySet()) {
			if (key.equals("#cdata-value") || key.equals("#child-nodes") ||
				key.equals("#tag-name") || key.equals("#value")) {

				continue;
			}

			sb.append(StringPool.SPACE);
			sb.append(key);
			sb.append(StringPool.EQUAL);
			sb.append(StringPool.QUOTE);
			sb.append(_escapeXML(jsonObject.getString(key)));
			sb.append(StringPool.QUOTE);
		}
	}

	private static void _appendValue(Element element, JSONObject jsonObject) {
		if (!element.hasChildNodes()) {
			return;
		}

		Node valueNode = null;

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if ((node instanceof Text) && _hasContent(node.getNodeValue())) {
				valueNode = node;

				break;
			}
		}

		if (valueNode == null) {
			return;
		}

		String content = valueNode.getNodeValue();

		if (valueNode instanceof CDATASection) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			for (String line : content.split(StringPool.NEW_LINE)) {
				jsonArray.put(
					line.replaceAll(StringPool.TAB, StringPool.FOUR_SPACES));
			}

			jsonObject.put("#cdata-value", jsonArray);
		}
		else {
			jsonObject.put(
				"#value", content.replaceAll("[\n\t]", StringPool.BLANK));
		}
	}

	private static void _appendValue(JSONObject jsonObject, StringBuilder sb) {
		if (jsonObject.has("#cdata-value")) {
			JSONArray jsonArray = jsonObject.getJSONArray("#cdata-value");

			StringBundler cdataSB = new StringBundler(
				(jsonArray.length() * 2) + 2);

			cdataSB.append(StringPool.CDATA_OPEN);

			for (int i = 0; i < jsonArray.length(); i++) {
				String line = jsonArray.getString(i);

				cdataSB.append(line.replaceAll("\\s\\s\\s\\s", StringPool.TAB));

				cdataSB.append(StringPool.NEW_LINE);
			}

			cdataSB.append(StringPool.CDATA_CLOSE);

			sb.append(cdataSB);
		}
		else if (jsonObject.has("#value")) {
			sb.append(_escapeXML(jsonObject.getString("#value")));
		}
	}

	private static String _escapeXML(String value) {
		return StringUtil.replace(
			value,
			new char[] {
				CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.GREATER_THAN,
				CharPool.LESS_THAN, CharPool.QUOTE
			},
			new String[] {"&amp;", "&apos;", "&gt;", "&lt;", "&quot;"});
	}

	private static boolean _hasContent(String value) {
		if (Validator.isNull(value)) {
			return false;
		}

		return Validator.isNotNull(
			value.replaceAll("[\n\t]", StringPool.BLANK));
	}

	private static JSONObject _toJSONObject(Element element) {
		JSONObject jsonObject = JSONUtil.put("#tag-name", element.getTagName());

		_appendAttributes(element, jsonObject);
		_appendValue(element, jsonObject);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (!(node instanceof Element)) {
				continue;
			}

			jsonArray.put(_toJSONObject((Element)node));
		}

		if (jsonArray.length() > 0) {
			jsonObject.put("#child-nodes", jsonArray);
		}

		return jsonObject;
	}

	private static void _toNode(JSONObject jsonObject, StringBuilder sb) {
		sb.append(StringPool.LESS_THAN);
		sb.append(jsonObject.getString("#tag-name"));

		_appendAttributes(jsonObject, sb);

		sb.append(StringPool.GREATER_THAN);

		_appendValue(jsonObject, sb);

		JSONArray childNodesJSONArray = jsonObject.getJSONArray("#child-nodes");

		if (childNodesJSONArray != null) {
			childNodesJSONArray.forEach(
				object -> _toNode((JSONObject)object, sb));
		}

		sb.append(StringPool.LESS_THAN);
		sb.append(StringPool.FORWARD_SLASH);
		sb.append(jsonObject.getString("#tag-name"));
		sb.append(StringPool.GREATER_THAN);
	}

}