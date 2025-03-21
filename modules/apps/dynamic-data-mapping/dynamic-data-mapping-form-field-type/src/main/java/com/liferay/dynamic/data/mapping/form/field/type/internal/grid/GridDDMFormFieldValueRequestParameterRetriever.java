/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.grid;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Queiroz
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.GRID,
	service = DDMFormFieldValueRequestParameterRetriever.class
)
public class GridDDMFormFieldValueRequestParameterRetriever
	implements DDMFormFieldValueRequestParameterRetriever {

	@Override
	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue) {

		JSONObject jsonObject = jsonFactory.createJSONObject();

		String[] parameterValues = httpServletRequest.getParameterValues(
			ddmFormFieldParameterName);

		if (ArrayUtil.isEmpty(parameterValues)) {
			return jsonObject.toString();
		}

		if (parameterValues.length == 1) {
			jsonObject = getJSONObject(_log, parameterValues[0]);
		}

		for (String parameterValue : parameterValues) {
			if (parameterValue.isEmpty() ||
				!parameterValue.contains(StringPool.SEMICOLON)) {

				continue;
			}

			String[] parameterValueParts = parameterValue.split(
				StringPool.SEMICOLON);

			jsonObject.put(parameterValueParts[0], parameterValueParts[1]);
		}

		return jsonObject.toString();
	}

	@Reference
	protected JSONFactory jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(
		GridDDMFormFieldValueRequestParameterRetriever.class);

}