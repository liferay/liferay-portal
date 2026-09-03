/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.action.ContentDashboardItemActionProviderRegistry;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionActionProviderRegistry;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactory;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactoryRegistry;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ContentDashboardItemFactory.class)
public class FileEntryContentDashboardItemFactory
	implements ContentDashboardItemFactory<FileEntry> {

	@Override
	public ContentDashboardItem<FileEntry> create(long classPK)
		throws PortalException {

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(classPK);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		if (assetEntry == null) {
			throw new NoSuchModelException(
				"Unable to find an asset entry for file entry " +
					fileEntry.getPrimaryKey());
		}

		ContentDashboardItemSubtypeFactory contentDashboardItemSubtypeFactory =
			getContentDashboardItemSubtypeFactory();

		if (contentDashboardItemSubtypeFactory == null) {
			throw new NoSuchModelException();
		}

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		return new FileEntryContentDashboardItem(
			assetEntry.getCategories(), assetEntry.getTags(),
			_contentDashboardItemActionProviderRegistry,
			_contentDashboardItemVersionActionProviderRegistry,
			contentDashboardItemSubtypeFactory.create(
				dlFileEntry.getFileEntryTypeId(), dlFileEntry.getFileEntryId()),
			_ddmFieldLocalService, _dlDisplayContextProvider,
			_dlFileEntryMetadataLocalService, _dlURLHelper, fileEntry,
			_groupLocalService.fetchGroup(fileEntry.getGroupId()), _language,
			_getLatestApprovedFileVersion(fileEntry), _portal);
	}

	@Override
	public ContentDashboardItemSubtypeFactory
		getContentDashboardItemSubtypeFactory() {

		return _contentDashboardItemSubtypeFactoryRegistry.
			getContentDashboardItemSubtypeFactory(
				DLFileEntryType.class.getName());
	}

	@Reference
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	private FileVersion _getLatestApprovedFileVersion(FileEntry fileEntry) {
		List<FileVersion> approvedFileVersions = fileEntry.getFileVersions(
			WorkflowConstants.STATUS_APPROVED, 0, 1);

		if (ListUtil.isEmpty(approvedFileVersions)) {
			return null;
		}

		return approvedFileVersions.get(0);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ContentDashboardItemActionProviderRegistry
		_contentDashboardItemActionProviderRegistry;

	@Reference
	private ContentDashboardItemSubtypeFactoryRegistry
		_contentDashboardItemSubtypeFactoryRegistry;

	@Reference
	private ContentDashboardItemVersionActionProviderRegistry
		_contentDashboardItemVersionActionProviderRegistry;

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLDisplayContextProvider _dlDisplayContextProvider;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}