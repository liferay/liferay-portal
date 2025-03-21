/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marcellus Tavares
 */
public interface DDMFormFieldValueRequestParameterRetriever {

	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue);

	public default JSONObject getJSONObject(Log log, String value) {
		if (value.startsWith(StringPool.QUOTE) &&
			value.endsWith(StringPool.QUOTE)) {

			value = (String)JSONFactoryUtil.deserialize(value);
		}

		try {
			return JSONFactoryUtil.createJSONObject(value);
		}
		catch (JSONException jsonException) {
			if (log.isDebugEnabled()) {
				log.debug(jsonException, jsonException);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

}