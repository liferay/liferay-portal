/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Andrea Sbarra
 */
public class PIMFieldMappingUtil {

	public static final String TYPE_ATTRIBUTE = "attribute";

	public static final String TYPE_FIXED_VALUE = "fixedValue";

	public static JSONArray getMappingsJSONArray(
		JSONObject fieldMappingJSONObject, String channelField) {

		JSONArray mappingsJSONArray = fieldMappingJSONObject.getJSONArray(
			channelField);

		if (mappingsJSONArray != null) {
			return mappingsJSONArray;
		}

		String attribute = fieldMappingJSONObject.getString(channelField);

		if (Validator.isNull(attribute)) {
			return JSONFactoryUtil.createJSONArray();
		}

		return JSONUtil.putAll(
			JSONUtil.put(
				"attribute", attribute
			).put(
				"source", StringPool.BLANK
			).put(
				"type", TYPE_ATTRIBUTE
			));
	}

	public static boolean isFixedValue(JSONObject mappingJSONObject) {
		String type = mappingJSONObject.getString("type");

		return type.equals(TYPE_FIXED_VALUE);
	}

}