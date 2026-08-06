/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

/**
 * @author Matthew Kong
 */
public class SchemaOrgUtil {

	public static String getRawType(String displayType, String type) {
		if (type.startsWith(_SCHEMA_ORG_URL)) {
			type = type.substring(_SCHEMA_ORG_URL.length());
		}

		if (StringUtil.equalsIgnoreCase(
				displayType, FieldMappingConstants.TYPE_BOOLEAN) ||
			StringUtil.equalsIgnoreCase(
				type, FieldMappingConstants.TYPE_BOOLEAN)) {

			return FieldMappingConstants.TYPE_BOOLEAN;
		}
		else if (StringUtil.equalsIgnoreCase(
					displayType, FieldMappingConstants.TYPE_DATE) ||
				 StringUtil.equalsIgnoreCase(
					 type, FieldMappingConstants.TYPE_DATE)) {

			return FieldMappingConstants.TYPE_DATE;
		}
		else if (StringUtil.equalsIgnoreCase(
					type, FieldMappingConstants.TYPE_DECIMAL) ||
				 StringUtil.equalsIgnoreCase(
					 type, FieldMappingConstants.TYPE_INTEGER) ||
				 StringUtil.equalsIgnoreCase(
					 type, FieldMappingConstants.TYPE_NUMBER)) {

			return FieldMappingConstants.TYPE_NUMBER;
		}
		else if (StringUtil.equalsIgnoreCase(
					type, FieldMappingConstants.TYPE_SELECT_TEXT)) {

			return FieldMappingConstants.TYPE_SELECT_TEXT;
		}
		else if (StringUtil.equalsIgnoreCase(
					type, FieldMappingConstants.TYPE_TEXT)) {

			return FieldMappingConstants.TYPE_TEXT;
		}

		return _rawTypes.getOrDefault(type, FieldMappingConstants.TYPE_TEXT);
	}

	public static boolean isSubtype(String type, String rawType) {
		return rawType.equals(getRawType(type, type));
	}

	private static final String _SCHEMA_ORG_URL = "http://schema.org/";

	private static final Map<String, String> _rawTypes = HashMapBuilder.put(
		"birthDate", FieldMappingConstants.TYPE_DATE
	).build();

}