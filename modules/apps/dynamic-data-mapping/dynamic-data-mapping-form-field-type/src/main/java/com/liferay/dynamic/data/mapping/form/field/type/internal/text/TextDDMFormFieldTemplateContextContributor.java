/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.text;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.TEXT,
	service = DDMFormFieldTemplateContextContributor.class
)
public class TextDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> parameters = new HashMap<>();

		Locale locale = ddmFormFieldRenderingContext.getLocale();

		if (ddmFormFieldRenderingContext.isReturnFullContext()) {
			parameters = HashMapBuilder.<String, Object>put(
				"autocompleteEnabled", _isAutocompleteEnabled(ddmFormField)
			).put(
				"confirmationErrorMessage",
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, locale, "confirmationErrorMessage")
			).put(
				"confirmationLabel",
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, locale, "confirmationLabel")
			).put(
				"direction", ddmFormField.getProperty("direction")
			).put(
				"displayStyle", _getDisplayStyle(ddmFormField)
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
				"maxLength",
				() -> {
					Object maxLength = ddmFormField.getProperty("maxLength");

					if (Validator.isNotNull(maxLength)) {
						return GetterUtil.getInteger(maxLength);
					}

					return null;
				}
			).put(
				"placeholder",
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, locale, "placeholder")
			).put(
				"requireConfirmation",
				GetterUtil.getBoolean(
					ddmFormField.getProperty("requireConfirmation"))
			).put(
				"showCounter",
				() -> {
					Object showCounter = ddmFormField.getProperty(
						"showCounter");

					if (showCounter != null) {
						return GetterUtil.getBoolean(showCounter);
					}

					return null;
				}
			).put(
				"tooltip",
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, locale, "tooltip")
			).build();
		}

		return HashMapBuilder.<String, Object>put(
			"invalidCharacters",
			GetterUtil.getString(ddmFormField.getProperty("invalidCharacters"))
		).put(
			"normalizeField",
			GetterUtil.getBoolean(ddmFormField.getProperty("normalizeField"))
		).put(
			"options", _getOptions(ddmFormField, ddmFormFieldRenderingContext)
		).put(
			"predefinedValue",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext.getLocale(),
				"predefinedValue")
		).put(
			"preventChangeHandlerOnBlur",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("preventChangeHandlerOnBlur"))
		).putAll(
			parameters
		).build();
	}

	@Reference
	protected DDMFormFieldOptionsFactory ddmFormFieldOptionsFactory;

	private String _getDisplayStyle(DDMFormField ddmFormField) {
		return GetterUtil.getString(
			ddmFormField.getProperty("displayStyle"), "singleline");
	}

	private List<Object> _getOptions(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		List<Object> options = new ArrayList<>();

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormFieldOptionsFactory.create(
				ddmFormField, ddmFormFieldRenderingContext);

		if (ddmFormFieldOptions == null) {
			return options;
		}

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			options.add(
				HashMapBuilder.put(
					"label",
					() -> {
						LocalizedValue optionLabel =
							ddmFormFieldOptions.getOptionLabels(optionValue);

						return optionLabel.getString(
							ddmFormFieldRenderingContext.getLocale());
					}
				).put(
					"reference",
					ddmFormFieldOptions.getOptionReference(optionValue)
				).put(
					"value", optionValue
				).build());
		}

		return options;
	}

	private boolean _isAutocompleteEnabled(DDMFormField ddmFormField) {
		return GetterUtil.getBoolean(ddmFormField.getProperty("autocomplete"));
	}

}