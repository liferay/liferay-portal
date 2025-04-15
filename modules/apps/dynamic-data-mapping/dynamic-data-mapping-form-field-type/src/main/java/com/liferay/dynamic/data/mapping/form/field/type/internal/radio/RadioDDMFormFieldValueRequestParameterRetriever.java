/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.radio;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.RADIO,
	service = DDMFormFieldValueRequestParameterRetriever.class
)
public class RadioDDMFormFieldValueRequestParameterRetriever
	implements DDMFormFieldValueRequestParameterRetriever {

	@Override
	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue) {

		String parameterValue = httpServletRequest.getParameter(
			ddmFormFieldParameterName);

		if (parameterValue == null) {
			return _getPredefinedValue(defaultDDMFormFieldParameterValue);
		}

		return GetterUtil.getString(parameterValue);
	}

	private String _getPredefinedValue(String predefinedValue) {
		try {
			JSONArray predefinedValueJSONArray = _jsonFactory.createJSONArray(
				predefinedValue);

			return predefinedValueJSONArray.getString(0);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON", jsonException);
			}
		}

		return predefinedValue;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RadioDDMFormFieldValueRequestParameterRetriever.class);

	@Reference
	private JSONFactory _jsonFactory;

}