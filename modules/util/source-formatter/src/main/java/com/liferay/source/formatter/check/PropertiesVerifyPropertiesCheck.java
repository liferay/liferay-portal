/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.LegacyPropertiesUtil;
import com.liferay.source.formatter.util.LegacyProperty;
import com.liferay.source.formatter.util.LegacyPropertyAction;
import com.liferay.source.formatter.util.LegacyPropertyType;

import java.io.StringReader;

import java.util.List;
import java.util.Properties;

/**
 * @author Hugo Huijser
 */
public class PropertiesVerifyPropertiesCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (absolutePath.contains("/modules/")) {
			return content;
		}

		LegacyPropertyType legacyPropertyType = null;

		if (absolutePath.matches(".*/portal([-\\w]+)?\\.properties")) {
			legacyPropertyType = LegacyPropertyType.PORTAL;
		}
		else if (absolutePath.matches(".*/system([-\\w]+)?\\.properties")) {
			legacyPropertyType = LegacyPropertyType.SYSTEM;
		}
		else {
			return content;
		}

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		String verifyPropertiesFileName = getAttributeValue(
			_VERIFY_PROPERTIES_FILE_NAME_KEY, absolutePath);

		List<LegacyProperty> legacyProperties = _getLegacyProperties(
			absolutePath, verifyPropertiesFileName);

		for (LegacyProperty legacyProperty : legacyProperties) {
			if (!properties.containsKey(
					legacyProperty.getLegacyPropertyName()) ||
				!legacyPropertyType.equals(
					legacyProperty.getLegacyPropertyType())) {

				continue;
			}

			StringBundler sb = new StringBundler(10);

			sb.append(legacyPropertyType.getValue());
			sb.append(" property \"");
			sb.append(legacyProperty.getLegacyPropertyName());

			LegacyPropertyAction legacyActionType =
				legacyProperty.getLegacyPropertyAction();

			if (legacyActionType.equals(LegacyPropertyAction.MIGRATED)) {
				sb.append("\" was migrated to ");

				if (legacyPropertyType.equals(LegacyPropertyType.PORTAL)) {
					sb.append("\"system.properties\"");
				}
				else {
					sb.append("\"portal.properties\"");
				}
			}
			else if (legacyActionType.equals(
						LegacyPropertyAction.MODULARIZED)) {

				sb.append("\" was modularized");
			}
			else if (legacyActionType.equals(LegacyPropertyAction.OBSOLETE)) {
				sb.append("\" is obsolete");
			}
			else {
				sb.append("\" was renamed");
			}

			sb.append(". See \"");
			sb.append(
				StringUtil.removeSubstring(verifyPropertiesFileName, ".java"));
			sb.append("#");
			sb.append(legacyProperty.getVariableName());
			sb.append("\".");

			addMessage(fileName, sb.toString());
		}

		return content;
	}

	private synchronized List<LegacyProperty> _getLegacyProperties(
			String absolutePath, String verifyPropertiesFileName)
		throws Exception {

		if (_legacyProperties != null) {
			return _legacyProperties;
		}

		_legacyProperties = LegacyPropertiesUtil.getLegacyProperties(
			verifyPropertiesFileName,
			getPortalContent(verifyPropertiesFileName, absolutePath));

		return _legacyProperties;
	}

	private static final String _VERIFY_PROPERTIES_FILE_NAME_KEY =
		"verifyPropertiesFileName";

	private List<LegacyProperty> _legacyProperties;

}