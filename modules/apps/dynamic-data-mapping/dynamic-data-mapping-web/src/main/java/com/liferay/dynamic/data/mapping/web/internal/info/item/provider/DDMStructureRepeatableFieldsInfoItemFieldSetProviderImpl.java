/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.info.item.provider;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.info.field.converter.DDMFormFieldInfoFieldConverter;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMStructureRepeatableFieldsInfoItemFieldSetProvider;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.web.internal.info.item.provider.constants.InfoItemConstants;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = DDMStructureRepeatableFieldsInfoItemFieldSetProvider.class)
public class DDMStructureRepeatableFieldsInfoItemFieldSetProviderImpl
	implements DDMStructureRepeatableFieldsInfoItemFieldSetProvider {

	@Override
	public List<InfoFieldSetEntry> getInfoItemFieldSet(long ddmStructureId) {
		List<InfoFieldSetEntry> infoFieldSetEntries = new ArrayList<>();

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			ddmStructureId);

		if (ddmStructure == null) {
			return infoFieldSetEntries;
		}

		Set<DDMFormField> convertedDDMFormFields = new HashSet<>();

		for (DDMFormField ddmFormField : ddmStructure.getDDMFormFields(true)) {
			if (convertedDDMFormFields.contains(ddmFormField)) {
				continue;
			}

			infoFieldSetEntries.addAll(
				_getInfoFieldSetEntry(convertedDDMFormFields, ddmFormField));
		}

		return infoFieldSetEntries;
	}

	private List<InfoFieldSetEntry> _getInfoFieldSetEntry(
		Set<DDMFormField> convertedDDMFormFields, DDMFormField ddmFormField) {

		convertedDDMFormFields.add(ddmFormField);

		if (Objects.equals(
				ddmFormField.getType(), DDMFormFieldTypeConstants.FIELDSET)) {

			List<InfoFieldSetEntry> nestedInfoFieldsetEntries =
				new ArrayList<>();

			for (DDMFormField nestedDDMFormField :
					ddmFormField.getNestedDDMFormFields()) {

				List<InfoFieldSetEntry> infoFieldSetEntries =
					_getInfoFieldSetEntry(
						convertedDDMFormFields, nestedDDMFormField);

				if (ListUtil.isEmpty(infoFieldSetEntries)) {
					continue;
				}

				nestedInfoFieldsetEntries.addAll(infoFieldSetEntries);
			}

			if (ddmFormField.isRepeatable()) {
				LocalizedValue label = ddmFormField.getLabel();

				return Collections.singletonList(
					InfoFieldSet.builder(
					).labelInfoLocalizedValue(
						InfoLocalizedValue.<String>builder(
						).values(
							label.getValues()
						).defaultLocale(
							label.getDefaultLocale()
						).build()
					).infoFieldSetEntry(
						unsafeConsumer -> nestedInfoFieldsetEntries.forEach(
							infoFieldSetEntry -> {
								if (infoFieldSetEntry != null) {
									unsafeConsumer.accept(infoFieldSetEntry);
								}
							})
					).name(
						ddmFormField.getName()
					).build());
			}

			return nestedInfoFieldsetEntries;
		}
		else if (ArrayUtil.contains(
					InfoItemConstants.SELECTABLE_DDM_STRUCTURE_FIELDS,
					ddmFormField.getType())) {

			return Collections.singletonList(
				_ddmFormFieldInfoFieldConverter.convert(ddmFormField));
		}

		return Collections.emptyList();
	}

	@Reference
	private DDMFormFieldInfoFieldConverter _ddmFormFieldInfoFieldConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

}