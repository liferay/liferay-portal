/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.PortalJSONObjectUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLUpgradeRemovedDefinitionsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return content;
		}

		DocumentType documentType = document.getDocType();

		if (documentType == null) {
			return content;
		}

		String systemID = documentType.getSystemID();

		if (systemID == null) {
			return content;
		}

		int x = systemID.lastIndexOf("/");

		if (x == -1) {
			return content;
		}

		String dtdFileName = systemID.substring(x + 1);

		String upgradeFromVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_FROM_VERSION, absolutePath);

		JSONObject upgradeFromXMLDefinitionsJSONObject =
			_getXMLDefinitionsJSONObject(upgradeFromVersion);

		JSONObject upgradeFromXMLDefinitionJSONObject =
			upgradeFromXMLDefinitionsJSONObject.getJSONObject(dtdFileName);

		if (upgradeFromXMLDefinitionJSONObject == null) {
			return content;
		}

		String upgradeToVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_TO_LIFERAY_VERSION, absolutePath);

		String upgradeToVersionDTDFileName = _getUpgradeToVersionDTDFileName(
			dtdFileName, upgradeFromVersion, upgradeToVersion);

		if (upgradeToVersionDTDFileName == null) {
			return content;
		}

		JSONObject upgradeToXMLDefinitionsJSONObject =
			_getXMLDefinitionsJSONObject(upgradeToVersion);

		JSONObject upgradeToXMLDefinitionJSONObject =
			upgradeToXMLDefinitionsJSONObject.getJSONObject(
				upgradeToVersionDTDFileName);

		if (upgradeToXMLDefinitionJSONObject != null) {
			_checkXMLDefinitions(
				fileName, content, upgradeFromXMLDefinitionJSONObject,
				upgradeToVersion, upgradeToXMLDefinitionJSONObject);
		}

		return content;
	}

	private void _checkElement(
		String fileName, Element element,
		JSONObject upgradeFromXMLDefinitionJSONObject, String upgradeToVersion,
		JSONObject upgradeToXMLDefinitionJSONObject) {

		List<Element> childElements = element.elements();

		if (childElements.isEmpty()) {
			return;
		}

		String elementName = element.getName();

		JSONArray elementJSONArray =
			upgradeToXMLDefinitionJSONObject.getJSONArray(elementName);

		if (elementJSONArray == null) {
			return;
		}

		List<String> attributeNames = new ArrayList<>();

		Iterator<String> iterator = elementJSONArray.iterator();

		while (iterator.hasNext()) {
			attributeNames.add(iterator.next());
		}

		for (Element childElement : childElements) {
			String childElementName = childElement.getName();

			if (!attributeNames.contains(childElementName)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Attribute \"", childElementName,
						"\" is no longer a valid attribute for element \"",
						elementName, "\" in version \"", upgradeToVersion,
						"\""));
			}

			_checkElement(
				fileName, childElement, upgradeFromXMLDefinitionJSONObject,
				upgradeToVersion, upgradeToXMLDefinitionJSONObject);
		}
	}

	private void _checkXMLDefinitions(
		String fileName, String content,
		JSONObject upgradeFromXMLDefinitionJSONObject, String upgradeToVersion,
		JSONObject upgradeToXMLDefinitionJSONObject) {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		_checkElement(
			fileName, document.getRootElement(),
			upgradeFromXMLDefinitionJSONObject, upgradeToVersion,
			upgradeToXMLDefinitionJSONObject);
	}

	private String _getUpgradeToVersionDTDFileName(
		String dtdFileName, String upgradeFromVersion,
		String upgradeToVersion) {

		Matcher matcher1 = _versionPattern.matcher(upgradeFromVersion);

		if (!matcher1.find()) {
			return null;
		}

		Matcher matcher2 = _dtdFileNamePattern.matcher(dtdFileName);

		if (!matcher2.find() ||
			!Objects.equals(matcher1.group(1), matcher2.group(1)) ||
			!Objects.equals(matcher1.group(2), matcher2.group(2))) {

			return null;
		}

		Matcher matcher3 = _versionPattern.matcher(upgradeToVersion);

		if (matcher3.find()) {
			return StringUtil.replaceLast(
				dtdFileName, matcher2.group(),
				StringBundler.concat(
					"_", matcher3.group(1), "_", matcher3.group(2), "_0.dtd"));
		}

		return null;
	}

	private synchronized JSONObject _getXMLDefinitionsJSONObject(String version)
		throws Exception {

		JSONObject xmlDefinitionsJSONObject = _xmlDefinitionsJSONObjectMap.get(
			version);

		if (xmlDefinitionsJSONObject != null) {
			return xmlDefinitionsJSONObject;
		}

		JSONObject portalJSONObject =
			PortalJSONObjectUtil.getPortalJSONObjectByVersion(version);

		if (portalJSONObject.has("xmlDefinitions")) {
			xmlDefinitionsJSONObject = portalJSONObject.getJSONObject(
				"xmlDefinitions");
		}
		else {
			xmlDefinitionsJSONObject = new JSONObjectImpl();
		}

		_xmlDefinitionsJSONObjectMap.put(version, xmlDefinitionsJSONObject);

		return xmlDefinitionsJSONObject;
	}

	private static final Pattern _dtdFileNamePattern = Pattern.compile(
		"_(\\d+)_(\\d+)_\\d+\\.dtd");
	private static final Pattern _versionPattern = Pattern.compile(
		"^(\\d+)\\.(\\d+)\\.");

	private final Map<String, JSONObject> _xmlDefinitionsJSONObjectMap =
		new HashMap<>();

}