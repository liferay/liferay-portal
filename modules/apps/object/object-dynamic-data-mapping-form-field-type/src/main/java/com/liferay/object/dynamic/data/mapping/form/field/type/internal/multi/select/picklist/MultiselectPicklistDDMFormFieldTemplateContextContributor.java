/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.multi.select.picklist;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pedro Leite
 */
@Component(
	property = "ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.MULTISELECT_PICKLIST,
	service = DDMFormFieldTemplateContextContributor.class
)
public class MultiselectPicklistDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		DDMForm ddmForm = ddmFormField.getDDMForm();
		boolean localizedObjectField = GetterUtil.getBoolean(
			ddmFormField.getProperty("localizedObjectField"));

		return HashMapBuilder.<String, Object>put(
			"editOnlyInDefaultLanguage",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("editOnlyInDefaultLanguage"))
		).put(
			"isLocalizationSupported",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("isLocalizationSupported"))
		).put(
			"localizedObjectField", localizedObjectField
		).put(
			"options",
			() -> {
				DDMFormFieldOptions ddmFormFieldOptions =
					(DDMFormFieldOptions)ddmFormField.getProperty("options");
				List<Map<String, Object>> options = new ArrayList<>();

				for (String optionValue :
						ddmFormFieldOptions.getOptionsValues()) {

					if (optionValue == null) {
						continue;
					}

					ThemeDisplay themeDisplay = getThemeDisplay(ddmFormFieldRenderingContext.
						getHttpServletRequest());

					LocalizedValue localizedValue =
						ddmFormFieldOptions.getOptionLabels(optionValue);

					options.add(
						HashMapBuilder.<String, Object>put(
							"label", () -> {
								if(localizedObjectField) {
									return GetterUtil.getString(localizedValue.getString(
										localizedValue.getDefaultLocale()));
								}

								return GetterUtil.getString(localizedValue.getString(
									themeDisplay.getLocale()));
							}
						).put(
							"labelMap",
							() -> {
								Map<Locale, String> labeMap =
									DDMFormFieldTemplateContextContributorUtil.
										getListTypeEntryNameMap(
											ddmFormField, optionValue,
											_listTypeEntryLocalService);

								if (labeMap != null) {
									return labeMap;
								}

								return localizedValue.getValues();
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
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
				ddmForm.getDefaultLocale())
		).build();
	}

	protected ThemeDisplay getThemeDisplay(
		HttpServletRequest httpServletRequest) {

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}