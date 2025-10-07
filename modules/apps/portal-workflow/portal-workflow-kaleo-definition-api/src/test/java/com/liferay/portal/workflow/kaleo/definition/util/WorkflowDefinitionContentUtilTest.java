/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Rafael Praxedes
 */
public class WorkflowDefinitionContentUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());
	}

	@Test
	public void testCDATAToJSON() throws Exception {
		JSONObject jsonObject = _toJSONObject("cdata.xml");

		Assert.assertEquals("metadata", jsonObject.getString("#tag-name"));

		Assert.assertTrue(jsonObject.has("#cdata-value"));

		JSONArray jsonArray = jsonObject.getJSONArray("#cdata-value");

		Assert.assertEquals(jsonArray.toString(), 8, jsonArray.length());

		String value = jsonArray.getString(2);

		Assert.assertTrue(value.contains("xy"));

		value = jsonArray.getString(3);

		Assert.assertTrue(value.contains("168,"));

		value = jsonArray.getString(4);

		Assert.assertTrue(value.contains("36"));

		// 1 tab to 4 spaces

		Assert.assertEquals(
			jsonArray.getString(1), 12,
			StringUtils.countMatches(jsonArray.getString(1), StringPool.SPACE));
	}

	@Test
	public void testCDATAToXML() throws Exception {
		Document document = _toDocument("cdata.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("metadata", rootElement.getTagName());
		Assert.assertTrue(rootElement.hasChildNodes());

		Node cdataNode = null;

		NodeList nodeList = rootElement.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode instanceof CDATASection) {
				cdataNode = childNode;

				break;
			}
		}

		Assert.assertNotNull(cdataNode);

		String content = cdataNode.getTextContent();

		String[] contentLines = content.split(StringPool.NEW_LINE);

		Assert.assertTrue(contentLines[1].contains("xy"));
		Assert.assertTrue(contentLines[2].contains("168"));
		Assert.assertTrue(contentLines[3].contains("36"));

		// 4 spaces to 1 tab

		Assert.assertEquals(
			contentLines[0], 0,
			StringUtils.countMatches(contentLines[0], StringPool.TAB));
	}

	@Test
	public void testRepeatableTagToJSON() throws Exception {
		JSONObject jsonObject = _toJSONObject("repeatable-tag.xml");

		Assert.assertEquals("container-tag", jsonObject.getString("#tag-name"));

		JSONArray childJSONArray = jsonObject.getJSONArray("#child-nodes");

		Assert.assertEquals(2, childJSONArray.length());

		JSONObject childJSONObject = childJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"repeatable-tag", childJSONObject.getString("#tag-name"));
		Assert.assertEquals("First", childJSONObject.getString("#value"));

		childJSONObject = childJSONArray.getJSONObject(1);

		Assert.assertEquals(
			"repeatable-tag", childJSONObject.getString("#tag-name"));
		Assert.assertEquals("Second", childJSONObject.getString("#value"));
	}

	@Test
	public void testRepeatableTagToXML() throws Exception {
		Document document = _toDocument("repeatable-tag.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("container-tag", rootElement.getTagName());

		NodeList repeatableTagNodeList = rootElement.getElementsByTagName(
			"repeatable-tag");

		Assert.assertEquals(2, repeatableTagNodeList.getLength());

		Node repeatableTagNode = repeatableTagNodeList.item(0);

		Assert.assertEquals("First", repeatableTagNode.getTextContent());

		repeatableTagNode = repeatableTagNodeList.item(1);

		Assert.assertEquals("Second", repeatableTagNode.getTextContent());
	}

	@Test
	public void testSimpleTagToJSON() throws Exception {
		JSONObject jsonObject = _toJSONObject("simple-tag.xml");

		Assert.assertEquals("test", jsonObject.getString("#tag-name"));
		Assert.assertEquals("Simple Tag", jsonObject.getString("#value"));
	}

	@Test
	public void testSimpleTagToXML() throws Exception {
		Document document = _toDocument("simple-tag.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("test", rootElement.getTagName());

		String content = rootElement.getTextContent();

		Assert.assertTrue(content.contains("Simple Tag"));
	}

	@Test
	public void testTagWithAttributesToJSON() throws Exception {
		JSONObject jsonObject = _toJSONObject("tag-with-attributes.xml");

		Assert.assertEquals("labels", jsonObject.getString("#tag-name"));

		JSONArray childJSONArray = jsonObject.getJSONArray("#child-nodes");

		Assert.assertEquals(1, childJSONArray.length());

		JSONObject labelJSONObject = childJSONArray.getJSONObject(0);

		Assert.assertEquals("Label", labelJSONObject.getString("#value"));
		Assert.assertEquals("en_US", labelJSONObject.getString("language-id"));
	}

	@Test
	public void testTagWithAttributesToXML() throws Exception {
		Document document = _toDocument("tag-with-attributes.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("labels", rootElement.getTagName());

		NodeList labelNodeList = rootElement.getElementsByTagName("label");

		Assert.assertEquals(1, labelNodeList.getLength());

		Node labelNode = labelNodeList.item(0);

		Assert.assertEquals("Label", labelNode.getTextContent());

		NamedNodeMap attributes = labelNode.getAttributes();

		Node attributeNode = attributes.getNamedItem("language-id");

		Assert.assertEquals("en_US", attributeNode.getNodeValue());
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream(
				"dependencies/WorkflowDefinitionContentUtilTest." + fileName));
	}

	private Document _toDocument(String jsonFileName) throws Exception {
		DocumentBuilderFactory documentBuilderFactory =
			SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		return documentBuilder.parse(
			new InputSource(
				new StringReader(
					WorkflowDefinitionContentUtil.toXML(_read(jsonFileName)))));
	}

	private JSONObject _toJSONObject(String xmlFileName) throws Exception {
		return JSONFactoryUtil.createJSONObject(
			WorkflowDefinitionContentUtil.toJSON(_read(xmlFileName)));
	}

}