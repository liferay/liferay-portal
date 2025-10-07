/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = "dto.class.name=com.liferay.portal.kernel.model.LayoutPageTemplateCollection",
	service = DTOConverter.class
)
public class DisplayPageTemplateFolderDTOConverter
	implements DTOConverter
		<LayoutPageTemplateCollection, DisplayPageTemplateFolder> {

	@Override
	public String getContentType() {
		return DisplayPageTemplateFolder.class.getSimpleName();
	}

	@Override
	public DisplayPageTemplateFolder toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _getDisplayPageTemplateFolder(layoutPageTemplateCollection);
	}

	private DisplayPageTemplateFolder _getDisplayPageTemplateFolder(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getParentLayoutPageTemplateCollectionId());

		return new DisplayPageTemplateFolder() {
			{
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							layoutPageTemplateCollection.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
							}
						};
					});
				setDateCreated(layoutPageTemplateCollection::getCreateDate);
				setDateModified(layoutPageTemplateCollection::getModifiedDate);
				setDescription(layoutPageTemplateCollection::getDescription);
				setExternalReferenceCode(
					layoutPageTemplateCollection::getExternalReferenceCode);
				setKey(
					layoutPageTemplateCollection::
						getLayoutPageTemplateCollectionKey);
				setName(layoutPageTemplateCollection::getName);
				setParentDisplayPageTemplateFolder(
					() -> {
						if (parentLayoutPageTemplateCollection == null) {
							return null;
						}

						return _getDisplayPageTemplateFolder(
							parentLayoutPageTemplateCollection);
					});
				setParentDisplayPageTemplateFolderExternalReferenceCode(
					() -> {
						if (parentLayoutPageTemplateCollection == null) {
							return null;
						}

						return parentLayoutPageTemplateCollection.
							getExternalReferenceCode();
					});
				setUuid(layoutPageTemplateCollection::getUuid);
			}
		};
	}

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private UserLocalService _userLocalService;

}