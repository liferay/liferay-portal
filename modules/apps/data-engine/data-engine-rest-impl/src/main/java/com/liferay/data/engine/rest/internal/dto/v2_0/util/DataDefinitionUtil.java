/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.dto.v2_0.util;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.internal.content.type.DataDefinitionContentTypeRegistryUtil;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.spi.converter.SPIDDMFormRuleConverter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jeyvison Nascimento
 */
public class DataDefinitionUtil {

	public static String getContentType(DDMStructure structure) {
		DataDefinitionContentType dataDefinitionContentType =
			DataDefinitionContentTypeRegistryUtil.getDataDefinitionContentType(
				structure.getClassNameId());

		if (dataDefinitionContentType == null) {
			return null;
		}

		return dataDefinitionContentType.getContentType();
	}

	public static DataDefinition toDataDefinition(
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMStructure ddmStructure,
			DDMStructureLayoutLocalService ddmStructureLayoutLocalService,
			DDMStructureLocalService ddmStructureLocalService,
			HttpServletRequest httpServletRequest,
			SPIDDMFormRuleConverter spiDDMFormRuleConverter)
		throws Exception {

		DDMForm ddmForm = ddmStructure.getDDMForm();

		return new DataDefinition() {
			{
				setAvailableLanguageIds(
					() -> TransformUtil.transformToArray(
						ddmForm.getAvailableLocales(),
						LanguageUtil::getLanguageId, String.class));
				setContentType(
					() -> DataDefinitionUtil.getContentType(ddmStructure));
				setDataDefinitionFields(
					() -> TransformUtil.transformToArray(
						ddmForm.getDDMFormFields(),
						ddmFormField ->
							DataDefinitionFieldUtil.toDataDefinitionField(
								ddmFormField, ddmFormFieldTypeServicesRegistry,
								ddmStructureLayoutLocalService,
								ddmStructureLocalService, httpServletRequest),
						DataDefinitionField.class));
				setDataDefinitionKey(ddmStructure::getStructureKey);
				setDateCreated(ddmStructure::getCreateDate);
				setDateModified(ddmStructure::getModifiedDate);
				setDefaultDataLayout(
					() -> DataLayoutUtil.toDataLayout(
						ddmFormFieldTypeServicesRegistry,
						ddmStructure.fetchDDMStructureLayout(),
						spiDDMFormRuleConverter));
				setDefaultLanguageId(
					() -> LanguageUtil.getLanguageId(
						ddmForm.getDefaultLocale()));
				setDescription(
					() -> LocalizedValueUtil.toStringObjectMap(
						ddmStructure.getDescriptionMap()));
				setExternalReferenceCode(
					ddmStructure::getExternalReferenceCode);
				setId(ddmStructure::getStructureId);
				setName(
					() -> LocalizedValueUtil.toStringObjectMap(
						ddmStructure.getNameMap()));
				setSiteId(ddmStructure::getGroupId);
				setStorageType(ddmStructure::getStorageType);
				setUserId(ddmStructure::getUserId);
			}
		};
	}

}