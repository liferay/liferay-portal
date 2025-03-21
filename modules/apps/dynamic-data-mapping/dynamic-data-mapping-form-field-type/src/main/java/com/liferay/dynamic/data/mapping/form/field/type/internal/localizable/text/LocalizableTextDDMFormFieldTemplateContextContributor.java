/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.localizable.text;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.LOCALIZABLE_TEXT,
	service = DDMFormFieldTemplateContextContributor.class
)
public class LocalizableTextDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> parameters = new HashMap<>();

		if (ddmFormFieldRenderingContext.isReturnFullContext()) {
			parameters.put("displayStyle", _getDisplayStyle(ddmFormField));
			parameters.put(
				"editOnlyInDefaultLanguage",
				GetterUtil.getBoolean(
					ddmFormField.getProperty("editOnlyInDefaultLanguage")));
			parameters.put(
				"isLocalizationSupported",
				GetterUtil.getBoolean(
					ddmFormField.getProperty("isLocalizationSupported")));
			parameters.put(
				"placeholder",
				_getPlaceholder(ddmFormField, ddmFormFieldRenderingContext));
			parameters.put(
				"placeholdersSubmitLabel",
				JSONUtil.toJSONArray(
					_language.getAvailableLocales(),
					this::_getPlaceholdersSubmitLabelJSONObject, _log));
			parameters.put(
				"tooltip",
				_getTooltip(ddmFormField, ddmFormFieldRenderingContext));

			DDMForm ddmForm = ddmFormField.getDDMForm();

			parameters.putAll(
				DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
					ddmForm.getDefaultLocale()));
		}

		parameters.put(
			"localizedObjectField",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("localizedObjectField")));

		String predefinedValue = _getPredefinedValue(
			ddmFormField, ddmFormFieldRenderingContext);

		if (predefinedValue != null) {
			parameters.put("predefinedValue", predefinedValue);
		}

		parameters.put(
			"value", _getValueJSONObject(ddmFormFieldRenderingContext));

		return parameters;
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		return new AggregateResourceBundle(
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()),
			portal.getResourceBundle(locale));
	}

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private String _getDisplayStyle(DDMFormField ddmFormField) {
		return GetterUtil.getString(
			ddmFormField.getProperty("displayStyle"), "singleline");
	}

	private String _getPlaceholder(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue localizedValue =
			(LocalizedValue)ddmFormField.getProperty("placeholder");

		if (localizedValue == null) {
			return StringPool.BLANK;
		}

		return localizedValue.getString(
			ddmFormFieldRenderingContext.getLocale());
	}

	private JSONObject _getPlaceholdersSubmitLabelJSONObject(Locale locale) {
		JSONObject placeholdersSubmitLabelJSONObject =
			jsonFactory.createJSONObject();

		return placeholdersSubmitLabelJSONObject.put(
			"localeId", LocaleUtil.toLanguageId(locale)
		).put(
			"placeholderSubmitLabel",
			_language.get(getResourceBundle(locale), "submit-form")
		);
	}

	private String _getPredefinedValue(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

		if (localizedValue == null) {
			return null;
		}

		return localizedValue.getString(
			ddmFormFieldRenderingContext.getLocale());
	}

	private String _getTooltip(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue localizedValue =
			(LocalizedValue)ddmFormField.getProperty("tooltip");

		if (localizedValue == null) {
			return StringPool.BLANK;
		}

		return localizedValue.getString(
			ddmFormFieldRenderingContext.getLocale());
	}

	private JSONObject _getValueJSONObject(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		try {
			return jsonFactory.createJSONObject(
				ddmFormFieldRenderingContext.getValue());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return jsonFactory.createJSONObject();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocalizableTextDDMFormFieldTemplateContextContributor.class);

	@Reference
	private Language _language;

}