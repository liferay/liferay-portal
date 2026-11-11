/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.radio;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.radio.helper.RadioDDMFormFieldContextHelper;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.RADIO,
	service = DDMFormFieldTemplateContextContributor.class
)
public class RadioDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		return HashMapBuilder.<String, Object>put(
			"inline", GetterUtil.getBoolean(ddmFormField.getProperty("inline"))
		).put(
			"options", _getOptions(ddmFormField, ddmFormFieldRenderingContext)
		).put(
			"predefinedValue",
			getValue(
				GetterUtil.getString(
					DDMFormFieldTypeUtil.getPropertyValue(
						ddmFormField, ddmFormFieldRenderingContext.getLocale(),
						"predefinedValue"),
					"[]"))
		).put(
			"value",
			getValue(
				GetterUtil.getString(
					ddmFormFieldRenderingContext.getValue(), "[]"))
		).build();
	}

	protected DDMFormFieldOptions getDDMFormFieldOptions(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		if (Objects.equals(ddmFormField.getDataSourceType(), "manual")) {
			List<Map<String, String>> keyValuePairs =
				(List<Map<String, String>>)
					ddmFormFieldRenderingContext.getProperty("options");

			if (ListUtil.isEmpty(keyValuePairs)) {
				return ddmFormField.getDDMFormFieldOptions();
			}

			for (Map<String, String> keyValuePair : keyValuePairs) {
				ddmFormFieldOptions.addOptionLabel(
					keyValuePair.get("value"),
					ddmFormFieldRenderingContext.getLocale(),
					keyValuePair.get("label"));
				ddmFormFieldOptions.addOptionReference(
					keyValuePair.get("value"), keyValuePair.get("reference"));
			}
		}

		return ddmFormFieldOptions;
	}

	protected String getValue(String valueString) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(valueString);

			return GetterUtil.getString(jsonArray.get(0));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return valueString;
		}
	}

	@Reference
	protected JSONFactory jsonFactory;

	private List<Object> _getOptions(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		RadioDDMFormFieldContextHelper radioDDMFormFieldContextHelper =
			new RadioDDMFormFieldContextHelper(
				getDDMFormFieldOptions(
					ddmFormField, ddmFormFieldRenderingContext),
				ddmFormFieldRenderingContext.getLocale());

		return radioDDMFormFieldContextHelper.getOptions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RadioDDMFormFieldTemplateContextContributor.class);

}