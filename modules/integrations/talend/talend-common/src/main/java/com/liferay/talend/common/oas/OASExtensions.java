/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Beslic
 */
public class OASExtensions {

	public String getI18nFieldName(String name) {
		if (!name.endsWith("_i18n")) {
			throw new OASException(
				String.format(
					"Name %s is not valid i18n extension field name", name));
		}

		for (int idx = name.indexOf("_i18n") - 1; idx >= 0; idx--) {
			if (name.charAt(idx) == '_') {
				return name.substring(idx + 1);
			}
		}

		return name;
	}

	public boolean isI18nFieldName(String name) {
		int idx = name.indexOf("_i18n");

		if (idx < 0) {
			return false;
		}

		if ((idx > 0) && (name.lastIndexOf("_") == idx)) {
			return true;
		}

		throw new OASException(
			"Unsupported usage of _i18n in OpenAPI schema property name");
	}

	public boolean isI18nFieldNameNested(String name) {
		if (name.indexOf("_") < name.indexOf("_i18n")) {
			return true;
		}

		return false;
	}

	public boolean isObjectDefinitionReferenceFieldName(String name) {
		Matcher matcher = _objectDefinitionReferenceFieldNamePattern.matcher(
			name);

		return matcher.matches();
	}

	private static final Pattern _objectDefinitionReferenceFieldNamePattern =
		Pattern.compile("r_[a-zA-Z0-9]+(_c)?_[a-zA-Z0-9]+Id");

}