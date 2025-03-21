/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.NumericDDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.dynamic.data.mapping.util.NumericDDMFormFieldUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.Validator;

import java.text.DecimalFormat;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.NUMERIC,
	service = DDMFormFieldTemplateContextContributor.class
)
public class NumericDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String dataType = GetterUtil.getString(
			DDMFormFieldTypeUtil.getChangedPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext, "dataType"));
		DDMForm ddmForm = ddmFormField.getDDMForm();
		Locale locale = ddmFormFieldRenderingContext.getLocale();
		boolean localizedObjectField = GetterUtil.getBoolean(
			ddmFormField.getProperty("localizedObjectField"));

		return HashMapBuilder.<String, Object>put(
			"confirmationErrorMessage",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "confirmationErrorMessage")
		).put(
			"confirmationLabel",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "confirmationLabel")
		).put(
			"dataType", dataType
		).put(
			"direction", ddmFormField.getProperty("direction")
		).put(
			"editOnlyInDefaultLanguage",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("editOnlyInDefaultLanguage"))
		).put(
			"hideField",
			GetterUtil.getBoolean(ddmFormField.getProperty("hideField"))
		).put(
			"htmlAutocompleteAttribute",
			GetterUtil.getString(
				ddmFormField.getProperty("htmlAutocompleteAttribute"))
		).put(
			"isLocalizationSupported",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("isLocalizationSupported"))
		).put(
			"localizedObjectField", localizedObjectField
		).put(
			"placeholder",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "placeholder")
		).put(
			"predefinedValue",
			getFormattedValue(
				ddmFormFieldRenderingContext, locale,
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, ddmFormFieldRenderingContext.getLocale(),
					"predefinedValue"))
		).put(
			"requireConfirmation",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("requireConfirmation"))
		).put(
			"tooltip",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "tooltip")
		).put(
			"value",
			() -> {
				if (localizedObjectField) {
					JSONObject localizedValueJSONObject =
						DDMFormFieldValueUtil.getValueJSONObject(
							ddmFormFieldRenderingContext);

					Map<String, Object> localizedValue =
						localizedValueJSONObject.toMap();

					for (Map.Entry<String, Object> entry :
							localizedValue.entrySet()) {

						localizedValue.put(
							entry.getKey(),
							_getValue(String.valueOf(entry.getValue())));
					}

					return _jsonFactory.createJSONObject(localizedValue);
				}

				return getFormattedValue(
					ddmFormFieldRenderingContext, locale,
					_getValue(ddmFormFieldRenderingContext.getValue()));
			}
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
				ddmForm.getDefaultLocale())
		).putAll(
			NumericDDMFormFieldTypeUtil.getParameters(
				dataType, ddmFormField, ddmFormFieldRenderingContext)
		).build();
	}

	protected String getFormattedValue(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		Locale locale, String value) {

		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		if (GetterUtil.getBoolean(
				ddmFormFieldRenderingContext.getProperty("valueChanged"))) {

			DecimalFormat decimalFormat =
				NumericDDMFormFieldUtil.getDecimalFormat(locale);

			return decimalFormat.format(GetterUtil.getNumber(value));
		}

		return value;
	}

	private String _getValue(String value) {
		value = _htmlParser.extractText(value);

		if (Objects.equals(value, "NaN")) {
			return StringPool.BLANK;
		}

		return value;
	}

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private JSONFactory _jsonFactory;

}