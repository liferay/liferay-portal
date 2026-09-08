/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
@Component(
	property = {
		"default=true", "dto.class.name=com.liferay.journal.model.JournalFolder"
	},
	service = DTOConverter.class
)
public class StructuredContentFolderDTOConverter
	implements DTOConverter<DLFolder, StructuredContentFolder> {

	@Override
	public String getContentType() {
		return StructuredContentFolder.class.getSimpleName();
	}

	@Override
	public StructuredContentFolder toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.fetchGroup(journalFolder.getGroupId());

		return new StructuredContentFolder() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							journalFolder.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						JournalFolder.class.getName(),
						journalFolder.getFolderId(),
						journalFolder.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(journalFolder::getCreateDate);
				setDateModified(journalFolder::getModifiedDate);
				setDescription(journalFolder::getDescription);
				setExternalReferenceCode(
					journalFolder::getExternalReferenceCode);
				setId(journalFolder::getFolderId);
				setName(journalFolder::getName);
				setNumberOfStructuredContentFolders(
					() -> _journalFolderService.getFoldersCount(
						journalFolder.getGroupId(),
						journalFolder.getFolderId()));
				setNumberOfStructuredContents(
					() -> _journalArticleService.getArticlesCount(
						journalFolder.getGroupId(), journalFolder.getFolderId(),
						WorkflowConstants.STATUS_APPROVED));
				setParentStructuredContentFolderId(
					() -> {
						if (journalFolder.getParentFolderId() == 0L) {
							return null;
						}

						return journalFolder.getParentFolderId();
					});
				setSiteId(() -> GroupUtil.getSiteId(group));
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						journalFolder.getCompanyId(),
						dtoConverterContext.getUserId(),
						JournalFolder.class.getName(),
						journalFolder.getFolderId()));
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}