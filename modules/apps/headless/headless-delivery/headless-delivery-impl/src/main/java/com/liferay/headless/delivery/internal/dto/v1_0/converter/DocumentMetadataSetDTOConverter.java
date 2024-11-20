/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.headless.delivery.dto.v1_0.DocumentMetadataSet;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(
	property = "dto.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure",
	service = DTOConverter.class
)
public class DocumentMetadataSetDTOConverter
	implements DTOConverter<DDMStructure, DocumentMetadataSet> {

	@Override
	public String getContentType() {
		return DDMStructure.class.getSimpleName();
	}

	@Override
	public DocumentMetadataSet toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.getGroup(ddmStructure.getGroupId());

		return new DocumentMetadataSet() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						ddmStructure.getAvailableLanguageIds()));
				setDataDefinitionFields(
					() -> {
						DataDefinitionResource.Builder
							dataDefinitionResourceBuilder =
								_dataDefinitionResourceFactory.create();

						DataDefinitionResource dataDefinitionResource =
							dataDefinitionResourceBuilder.user(
								dtoConverterContext.getUser()
							).build();

						DataDefinition dataDefinition =
							dataDefinitionResource.getDataDefinition(
								ddmStructure.getStructureId());

						return dataDefinition.getDataDefinitionFields();
					});
				setDateCreated(ddmStructure::getCreateDate);
				setDateModified(ddmStructure::getModifiedDate);
				setDescription(
					() -> ddmStructure.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						ddmStructure.getDescriptionMap()));
				setExternalReferenceCode(
					ddmStructure::getExternalReferenceCode);
				setId(ddmStructure::getStructureId);
				setName(
					() -> ddmStructure.getName(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						ddmStructure.getNameMap()));
				setSiteId(ddmStructure::getGroupId);
			}
		};
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private GroupLocalService _groupLocalService;

}