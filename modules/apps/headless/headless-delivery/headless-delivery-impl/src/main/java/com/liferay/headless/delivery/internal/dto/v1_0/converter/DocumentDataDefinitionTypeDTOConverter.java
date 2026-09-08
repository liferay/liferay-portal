/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureLinkManager;
import com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.dto.v1_0.DocumentType;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.document.library.kernel.model.DLFileEntryType"
	},
	service = DTOConverter.class
)
public class DocumentDataDefinitionTypeDTOConverter
	implements DTOConverter<DLFileEntryType, DocumentDataDefinitionType> {

	@Override
	public String getContentType() {
		return DocumentType.class.getSimpleName();
	}

	@Override
	public DocumentDataDefinitionType toDTO(
			DTOConverterContext dtoConverterContext,
			DLFileEntryType dlFileEntryType)
		throws Exception {

		Group group = _groupLocalService.getGroup(dlFileEntryType.getGroupId());

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				dtoConverterContext.getUser()
			).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.getDataDefinition(
				dlFileEntryType.getDataDefinitionId());

		return new DocumentDataDefinitionType() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						dlFileEntryType.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							dlFileEntryType.getUserId())));
				setDataDefinitionFields(
					dataDefinition::getDataDefinitionFields);
				setDataLayout(dataDefinition::getDefaultDataLayout);
				setDateCreated(dlFileEntryType::getCreateDate);
				setDateModified(dlFileEntryType::getModifiedDate);
				setDescription(
					() -> dlFileEntryType.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						dlFileEntryType.getDescriptionMap()));
				setDocumentMetadataSetIds(
					() -> TransformUtil.transformToArray(
						_ddmStructureLinkManager.getStructureLinks(
							_classNameLocalService.getClassNameId(
								DLFileEntryType.class),
							dlFileEntryType.getFileEntryTypeId()),
						ddmStructureLink -> ddmStructureLink.getStructureId(),
						Long.class));
				setExternalReferenceCode(
					dlFileEntryType::getExternalReferenceCode);
				setId(dlFileEntryType::getFileEntryTypeId);
				setName(
					() -> dlFileEntryType.getName(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						dlFileEntryType.getNameMap()));
				setSiteId(() -> GroupUtil.getSiteId(group));
			}
		};
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureLinkManager _ddmStructureLinkManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}