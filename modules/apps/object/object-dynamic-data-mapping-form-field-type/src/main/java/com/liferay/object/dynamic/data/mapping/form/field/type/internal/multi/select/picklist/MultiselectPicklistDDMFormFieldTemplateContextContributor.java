/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.multi.select.picklist;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

		return HashMapBuilder.<String, Object>put(
			"editOnlyInDefaultLanguage",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("editOnlyInDefaultLanguage"))
		).put(
			"isLocalizationSupported",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("isLocalizationSupported"))
		).put(
			"localizedObjectField",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("localizedObjectField"))
		).put(
			"options",
			() -> {
				DDMFormFieldOptions ddmFormFieldOptions =
					(DDMFormFieldOptions)ddmFormField.getProperty("options");
				long listTypeDefinitionId = GetterUtil.getLong(
					ddmFormField.getProperty("listTypeDefinitionId"));

				return DDMFormFieldTemplateContextContributorUtil.getOptions(
					ddmFormFieldOptions, listTypeDefinitionId,
					_listTypeEntryLocalService);
			}
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
				ddmForm.getDefaultLocale())
		).build();
	}

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}