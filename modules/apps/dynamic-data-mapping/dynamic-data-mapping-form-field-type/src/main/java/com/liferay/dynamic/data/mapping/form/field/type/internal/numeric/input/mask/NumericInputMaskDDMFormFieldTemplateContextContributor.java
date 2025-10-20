/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric.input.mask;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.NumericInputMaskDDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.NUMERIC_INPUT_MASK,
	service = DDMFormFieldTemplateContextContributor.class
)
public class NumericInputMaskDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		return HashMapBuilder.<String, Object>put(
			"decimalSymbols", _createOptions(_getDecimalSymbols())
		).put(
			"thousandsSeparators", _createOptions(_getThousandsSeparators())
		).putAll(
			_getValueParameters(ddmFormFieldRenderingContext.getValue())
		).build();
	}

	private List<Object> _createOptions(Map<String, Object> map) {
		List<Object> options = new ArrayList<>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			options.add(
				HashMapBuilder.put(
					"label", entry.getValue()
				).put(
					"reference", entry.getKey()
				).put(
					"value", entry.getKey()
				).build());
		}

		return options;
	}

	private Map<String, Object> _getDecimalSymbols() {
		return LinkedHashMapBuilder.<String, Object>put(
			".", "0.00"
		).put(
			",", "0,00"
		).build();
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		Class<?> clazz = getClass();

		return new AggregateResourceBundle(
			ResourceBundleUtil.getBundle(
				"content.Language", locale, clazz.getClassLoader()),
			_portal.getResourceBundle(locale));
	}

	private Map<String, String> _getThousandsSeparatorLabels() {
		Map<String, String> thousandsSeparatorLabels = new HashMap<>();

		for (Locale availableLocale : _language.getAvailableLocales()) {
			thousandsSeparatorLabels.put(
				_language.getLanguageId(availableLocale),
				_language.get(_getResourceBundle(availableLocale), "none"));
		}

		return thousandsSeparatorLabels;
	}

	private Map<String, Object> _getThousandsSeparators() {
		return LinkedHashMapBuilder.<String, Object>put(
			"none", _getThousandsSeparatorLabels()
		).put(
			",", "1,000"
		).put(
			".", "1.000"
		).put(
			" ", "1 000"
		).put(
			"\'", "1\'000"
		).build();
	}

	private Map<String, Object> _getValueParameters(String value) {
		try {
			JSONObject valueJSONObject = _jsonFactory.createJSONObject(
				NumericInputMaskDDMFormFieldTypeUtil.getJSON(value));

			JSONObject symbolsJSONObject = _jsonFactory.createJSONObject(
				valueJSONObject.getString("symbols"));

			return HashMapBuilder.<String, Object>put(
				"append", valueJSONObject.getString("append")
			).put(
				"appendType", valueJSONObject.getString("appendType")
			).put(
				"decimalPlaces", valueJSONObject.getInt("decimalPlaces")
			).put(
				"decimalSymbol", symbolsJSONObject.getString("decimalSymbol")
			).put(
				"thousandsSeparator",
				symbolsJSONObject.getString("thousandsSeparator")
			).build();
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}

			return new HashMap<>();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NumericInputMaskDDMFormFieldTemplateContextContributor.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}