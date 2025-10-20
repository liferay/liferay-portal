/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.NumericDDMFormFieldUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class NumericDDMFormFieldTypeUtil {

	public static Map<String, Object> getParameters(
		String dataType, DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		boolean inputMask = GetterUtil.getBoolean(
			DDMFormFieldTypeUtil.getChangedPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext, "inputMask"));
		Locale locale = ddmFormFieldRenderingContext.getLocale();

		if (!inputMask) {
			if (!StringUtil.equals(dataType, "double")) {
				return new HashMap<>();
			}

			return HashMapBuilder.<String, Object>put(
				"localizedSymbols",
				_getLocalizedSymbols(ddmFormFieldRenderingContext.isViewMode())
			).put(
				"symbols", _getSymbols(locale)
			).build();
		}

		if (!StringUtil.equals(dataType, "double")) {
			return HashMapBuilder.<String, Object>put(
				"inputMask", inputMask
			).put(
				"inputMaskFormat",
				_getPropertyValue(
					ddmFormField, ddmFormFieldRenderingContext, locale,
					"inputMaskFormat")
			).build();
		}

		String numericInputMaskJSON =
			NumericInputMaskDDMFormFieldTypeUtil.getJSON(
				_getPropertyValue(
					ddmFormField, ddmFormFieldRenderingContext, locale,
					"numericInputMask"));

		return HashMapBuilder.<String, Object>put(
			"inputMask", true
		).put(
			"numericInputMask", numericInputMaskJSON
		).putAll(
			(Map<String, Object>)JSONFactoryUtil.looseDeserialize(
				numericInputMaskJSON)
		).build();
	}

	private static Map<String, Map<String, Object>> _getLocalizedSymbols(
		boolean viewMode) {

		if (viewMode) {
			return null;
		}

		Map<String, Map<String, Object>> localizedSymbols = new HashMap<>();

		for (Locale availableLocale : LanguageUtil.getAvailableLocales()) {
			localizedSymbols.put(
				LanguageUtil.getLanguageId(availableLocale),
				_getSymbols(availableLocale));
		}

		return localizedSymbols;
	}

	private static String _getPropertyValue(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		Locale locale, String propertyName) {

		Map<String, Object> changedProperties =
			(Map<String, Object>)ddmFormFieldRenderingContext.getProperty(
				"changedProperties");

		if (MapUtil.isNotEmpty(changedProperties)) {
			Object changedProperty = changedProperties.get(propertyName);

			if (changedProperty instanceof LocalizedValue) {
				LocalizedValue localizedValue = (LocalizedValue)changedProperty;

				String propertyValue = localizedValue.getString(locale);

				if (propertyValue != null) {
					return propertyValue;
				}
			}
		}

		return DDMFormFieldTypeUtil.getPropertyValue(
			ddmFormField, locale, propertyName);
	}

	private static Map<String, Object> _getSymbols(Locale locale) {
		DecimalFormat decimalFormat = NumericDDMFormFieldUtil.getDecimalFormat(
			locale);

		DecimalFormatSymbols decimalFormatSymbols =
			decimalFormat.getDecimalFormatSymbols();

		return HashMapBuilder.<String, Object>put(
			"decimalSymbol",
			String.valueOf(decimalFormatSymbols.getDecimalSeparator())
		).put(
			"thousandsSeparator",
			String.valueOf(decimalFormatSymbols.getGroupingSeparator())
		).build();
	}

}