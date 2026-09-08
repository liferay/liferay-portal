/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.delivery.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.document.library.kernel.model.DLFolder"
	},
	service = DTOConverter.class
)
public class DocumentFolderDTOConverter
	implements DTOConverter<DLFolder, DocumentFolder> {

	@Override
	public String getContentType() {
		return DocumentFolder.class.getSimpleName();
	}

	@Override
	public DocumentFolder toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		Folder folder = _dlAppService.getFolder(
			(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.fetchGroup(folder.getGroupId());

		return new DocumentFolder() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(folder.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						DLFolder.class.getName(), folder.getFolderId(),
						folder.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(folder::getCreateDate);
				setDateModified(folder::getModifiedDate);
				setDescription(folder::getDescription);
				setExternalReferenceCode(folder::getExternalReferenceCode);
				setId(folder::getFolderId);
				setName(folder::getName);
				setNumberOfDocumentFolders(
					() -> _dlAppService.getFoldersCount(
						folder.getRepositoryId(), folder.getFolderId()));
				setNumberOfDocuments(
					() -> _dlAppService.getFileEntriesCount(
						folder.getRepositoryId(), folder.getFolderId()));
				setParentDocumentFolderId(
					() -> {
						if (folder.getParentFolderId() == 0L) {
							return null;
						}

						return folder.getParentFolderId();
					});
				setSiteId(() -> GroupUtil.getSiteId(group));
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						folder.getCompanyId(), dtoConverterContext.getUserId(),
						DLFolder.class.getName(), folder.getFolderId()));
			}
		};
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}