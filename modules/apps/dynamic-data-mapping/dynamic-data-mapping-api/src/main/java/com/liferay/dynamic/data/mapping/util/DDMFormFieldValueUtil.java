/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Renan Vasconcelos
 */
public class DDMFormFieldValueUtil {

	public static JSONArray createJSONArray(String json) {
		try {
			return JSONFactoryUtil.createJSONArray(json);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON array", jsonException);
			}

			return JSONFactoryUtil.createJSONArray();
		}
	}

	public static String getOptionLabel(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		DDMFormFieldOptions ddmFormFieldOptions = _getDDMFormFieldOptions(
			ddmFormFieldValue);

		String optionValue = getOptionValue(ddmFormFieldValue, locale);

		LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(
			optionValue);

		if (optionLabel == null) {
			return optionValue;
		}

		return optionLabel.getString(locale);
	}

	public static String getOptionsLabels(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		return getOptionsLabels(
			ddmFormFieldValue, locale, ddmFormField -> true);
	}

	public static String getOptionsLabels(
		DDMFormFieldValue ddmFormFieldValue, Locale locale,
		Predicate<DDMFormField> ddmFormFieldPredicate) {

		JSONArray optionsValuesJSONArray = getOptionsValuesJSONArray(
			ddmFormFieldValue, locale);

		if (optionsValuesJSONArray.length() == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			(optionsValuesJSONArray.length() * 2) - 1);

		DDMFormFieldOptions ddmFormFieldOptions = _getDDMFormFieldOptions(
			ddmFormFieldValue);

		for (int i = 0; i < optionsValuesJSONArray.length(); i++) {
			String optionValue = optionsValuesJSONArray.getString(i);

			LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			if ((optionLabel != null) &&
				ddmFormFieldPredicate.test(
					ddmFormFieldValue.getDDMFormField())) {

				sb.append(optionLabel.getString(locale));
			}
			else {
				sb.append(optionValue);
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	public static JSONArray getOptionsValuesJSONArray(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return createJSONArray("[]");
		}

		String valueString = value.getString(locale);

		if (!JSONUtil.isJSONArray(valueString)) {
			return createJSONArray("[" + valueString + "]");
		}

		return createJSONArray(valueString);
	}

	public static String getOptionValue(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return StringPool.BLANK;
		}

		return value.getString(locale);
	}

	public static JSONObject getValueJSONObject(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		try {
			return JSONFactoryUtil.createJSONObject(
				ddmFormFieldRenderingContext.getValue());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return JSONFactoryUtil.createJSONObject();
	}

	public static boolean isManualDataSourceType(DDMFormField ddmFormField) {
		return Objects.equals(ddmFormField.getDataSourceType(), "manual");
	}

	private static DDMFormFieldOptions _getDDMFormFieldOptions(
		DDMFormFieldValue ddmFormFieldValue) {

		DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

		return ddmFormField.getDDMFormFieldOptions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldValueUtil.class);

}