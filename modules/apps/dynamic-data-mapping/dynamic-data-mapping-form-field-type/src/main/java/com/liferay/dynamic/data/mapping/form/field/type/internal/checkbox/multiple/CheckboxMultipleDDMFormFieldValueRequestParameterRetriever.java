/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.checkbox.multiple;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE,
	service = DDMFormFieldValueRequestParameterRetriever.class
)
public class CheckboxMultipleDDMFormFieldValueRequestParameterRetriever
	implements DDMFormFieldValueRequestParameterRetriever {

	@Override
	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue) {

		return jsonFactory.serialize(
			_getParameterValues(
				httpServletRequest, ddmFormFieldParameterName,
				_getDefaultDDMFormFieldParameterValues(
					defaultDDMFormFieldParameterValue)));
	}

	@Reference
	protected JSONFactory jsonFactory;

	private String[] _getDefaultDDMFormFieldParameterValues(
		String defaultDDMFormFieldParameterValue) {

		if (Validator.isNull(defaultDDMFormFieldParameterValue) ||
			Objects.equals(defaultDDMFormFieldParameterValue, "[]")) {

			return GetterUtil.DEFAULT_STRING_VALUES;
		}

		try {
			return jsonFactory.looseDeserialize(
				defaultDDMFormFieldParameterValue, String[].class);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringUtil.split(defaultDDMFormFieldParameterValue);
		}
	}

	private String[] _getParameterValues(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String[] defaultDDMFormFieldParameterValues) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isLifecycleAction()) {
			return ParamUtil.getParameterValues(
				httpServletRequest, ddmFormFieldParameterName);
		}

		return ParamUtil.getParameterValues(
			httpServletRequest, ddmFormFieldParameterName,
			defaultDDMFormFieldParameterValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CheckboxMultipleDDMFormFieldValueRequestParameterRetriever.class);

}