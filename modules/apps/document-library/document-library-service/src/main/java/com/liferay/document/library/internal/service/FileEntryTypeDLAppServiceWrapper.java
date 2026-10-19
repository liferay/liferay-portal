/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.service;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.exception.InvalidFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppServiceWrapper;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = ServiceWrapper.class)
public class FileEntryTypeDLAppServiceWrapper extends DLAppServiceWrapper {

	@Override
	public FileEntry copyFileEntry(
			long fileEntryId, long destinationFolderId,
			long destinationRepositoryId, long fileEntryTypeId, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException {

		_validateFileEntryType(fileEntryTypeId, destinationRepositoryId);

		if (fileEntryTypeId !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			_populateServiceContext(serviceContext, fileEntryId);
		}

		return super.copyFileEntry(
			fileEntryId, destinationFolderId, destinationRepositoryId,
			fileEntryTypeId, groupIds, serviceContext);
	}

	@Override
	public FileEntry moveFileEntry(
			long fileEntryId, long newFolderId, ServiceContext serviceContext)
		throws PortalException {

		_populateServiceContext(serviceContext, fileEntryId);

		return super.moveFileEntry(fileEntryId, newFolderId, serviceContext);
	}

	private void _populateServiceContext(
			ServiceContext serviceContext, long fileEntryId)
		throws PortalException {

		DLFileEntry dlFileEntry = _dlFileEntryService.getFileEntry(fileEntryId);

		long fileEntryTypeId = dlFileEntry.getFileEntryTypeId();

		if (fileEntryTypeId ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return;
		}

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(fileEntryTypeId);

		serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				dlFileEntry.getFileEntryId(), !dlFileEntry.isCheckedOut());

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		for (DDMStructure ddmStructure : ddmStructures) {
			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					dlFileVersion.getFileVersionId());

			if (dlFileEntryMetadata == null) {
				continue;
			}

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				_ddmBeanTranslator.translate(
					_ddmStorageEngineManager.getDDMFormValues(
						dlFileEntryMetadata.getDDMStorageId())));
		}
	}

	private void _validateFileEntryType(long fileEntryTypeId, long groupId)
		throws PortalException {

		if (fileEntryTypeId ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return;
		}

		DLFileEntryType destinationDLFileEntryType = null;

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(fileEntryTypeId);

		for (long connectedGroupId :
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(groupId)) {

			destinationDLFileEntryType =
				_dlFileEntryTypeLocalService.fetchFileEntryType(
					connectedGroupId, dlFileEntryType.getFileEntryTypeKey());

			if (destinationDLFileEntryType != null) {
				break;
			}
		}

		if (destinationDLFileEntryType == null) {
			throw new InvalidFileEntryTypeException(
				"the-document-type-does-not-exist-in-the-destination-site");
		}
	}

	@Reference
	private DDMBeanTranslator _ddmBeanTranslator;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryService _dlFileEntryService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}