/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.info.item.provider;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.info.field.converter.DDMFormFieldInfoFieldConverter;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMStructureInfoItemFieldSetProvider;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.web.internal.info.item.provider.constants.InfoItemConstants;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(service = DDMStructureInfoItemFieldSetProvider.class)
public class DDMStructureInfoItemFieldSetProviderImpl
	implements DDMStructureInfoItemFieldSetProvider {

	@Override
	public InfoFieldSet getInfoItemFieldSet(long ddmStructureId)
		throws NoSuchStructureException {

		return getInfoItemFieldSet(ddmStructureId, null);
	}

	@Override
	public InfoFieldSet getInfoItemFieldSet(
			long ddmStructureId,
			InfoLocalizedValue<String> fieldSetNameInfoLocalizedValue)
		throws NoSuchStructureException {

		try {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(ddmStructureId);

			if (fieldSetNameInfoLocalizedValue == null) {
				fieldSetNameInfoLocalizedValue =
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							ddmStructure.getDefaultLanguageId())
					).values(
						ddmStructure.getNameMap()
					).build();
			}

			return InfoFieldSet.builder(
			).infoFieldSetEntry(
				unsafeConsumer -> {
					for (DDMFormField ddmFormField :
							ddmStructure.getDDMFormFields(false)) {

						if (!ArrayUtil.contains(
								InfoItemConstants.
									SELECTABLE_DDM_STRUCTURE_FIELDS,
								ddmFormField.getType())) {

							continue;
						}

						unsafeConsumer.accept(
							_ddmFormFieldInfoFieldConverter.convert(
								ddmFormField));
					}
				}
			).labelInfoLocalizedValue(
				fieldSetNameInfoLocalizedValue
			).name(
				ddmStructure.getStructureKey()
			).build();
		}
		catch (NoSuchStructureException noSuchStructureException) {
			throw noSuchStructureException;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				"Caught unexpected exception", portalException);
		}
	}

	@Reference
	private DDMFormFieldInfoFieldConverter _ddmFormFieldInfoFieldConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

}