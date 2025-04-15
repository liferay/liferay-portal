/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.select;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.SELECT,
	service = DDMFormFieldValueRequestParameterRetriever.class
)
public class SelectDDMFormFieldValueRequestParameterRetriever
	implements DDMFormFieldValueRequestParameterRetriever {

	@Override
	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue) {

		String ddmFormFieldParameterValue = httpServletRequest.getParameter(
			ddmFormFieldParameterName);

		if (ddmFormFieldParameterValue != null) {
			if (JSONUtil.isJSONArray(ddmFormFieldParameterValue)) {
				return ddmFormFieldParameterValue;
			}

			return "[]";
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isLifecycleAction() ||
			Validator.isNull(defaultDDMFormFieldParameterValue) ||
			StringUtil.equals(defaultDDMFormFieldParameterValue, "[]")) {

			return "[]";
		}

		try {
			return _jsonFactory.serialize(
				_jsonFactory.looseDeserialize(
					defaultDDMFormFieldParameterValue, String[].class));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return _jsonFactory.serialize(
				StringUtil.split(defaultDDMFormFieldParameterValue));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectDDMFormFieldValueRequestParameterRetriever.class);

	@Reference
	private JSONFactory _jsonFactory;

}