/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class BNDLiferayEnterpriseAppCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("/bnd.bnd") || absolutePath.contains("-test/") ||
			absolutePath.contains("-test-util/")) {

			return content;
		}

		List<String> enterpriseAppModulePathNames = getAttributeValues(
			_ENTERPRISE_APP_MODULE_PATH_NAMES_KEY, absolutePath);

		if (enterpriseAppModulePathNames.isEmpty()) {
			return content;
		}

		for (String enterpriseAppModulePathName :
				enterpriseAppModulePathNames) {

			if (!absolutePath.contains(enterpriseAppModulePathName)) {
				continue;
			}

			String liferayEnterpriseApp = BNDSourceUtil.getDefinitionValue(
				content, "Liferay-Enterprise-App");

			if (liferayEnterpriseApp == null) {
				addMessage(fileName, "Missing Liferay-Enterprise-App");
			}

			_checkProperties(
				fileName, enterpriseAppModulePathName, liferayEnterpriseApp);

			return BNDSourceUtil.updateInstruction(
				content, "Liferay-Enterprise-App",
				_sortProperties(liferayEnterpriseApp));
		}

		return content;
	}

	private void _checkProperties(
		String fileName, String absolutePath, String liferayEnterpriseApp) {

		Matcher matcher = _dxpOnlyPattern.matcher(liferayEnterpriseApp);

		if (absolutePath.contains("modules/dxp/apps/")) {
			if (!matcher.find() || !Objects.equals(matcher.group(1), "true")) {
				addMessage(
					fileName,
					"Enterprise apps in \"dxp/apps/\" directory should " +
						"always set \"dxp.only=true\" in \"Liferay-" +
							"Enterprise-App\"");
			}
		}
		else {
			if (matcher.find()) {
				addMessage(
					fileName,
					"Enterprise apps not in \"dxp/apps/\" directory should " +
						"not set \"dxp.only\" in \"Liferay-Enterprise-App\"");
			}
		}
	}

	private String _sortProperties(String liferayEnterpriseApp) {
		String[] propertiesArray = StringUtil.split(
			liferayEnterpriseApp, StringPool.SEMICOLON);

		PropertyComparator propertyComparator = new PropertyComparator();

		for (int i = 1; i < propertiesArray.length; i++) {
			String previousProperty = propertiesArray[i - 1];
			String property = propertiesArray[i];

			if (propertyComparator.compare(previousProperty, property) <= 0) {
				continue;
			}

			liferayEnterpriseApp = StringUtil.replaceFirst(
				liferayEnterpriseApp, previousProperty, property);
			liferayEnterpriseApp = StringUtil.replaceLast(
				liferayEnterpriseApp, property, previousProperty);
		}

		return liferayEnterpriseApp;
	}

	private static final String _ENTERPRISE_APP_MODULE_PATH_NAMES_KEY =
		"enterpriseAppModulePathNames";

	private static final Pattern _dxpOnlyPattern = Pattern.compile(
		"dxp.only=([^;]*)");

	private class PropertyComparator extends NaturalOrderStringComparator {

		public int compare(String property1, String property2) {
			return super.compare(
				_getPropertyName(property1), _getPropertyName(property2));
		}

		private String _getPropertyName(String property) {
			int x = property.indexOf(StringPool.EQUAL);

			if (x != -1) {
				return property.substring(0, x);
			}

			return property;
		}

	}

}