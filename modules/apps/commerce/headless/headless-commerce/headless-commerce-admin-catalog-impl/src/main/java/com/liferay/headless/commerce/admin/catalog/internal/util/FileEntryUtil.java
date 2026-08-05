/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.upload.UniqueFileNameProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.HashMap;

/**
 * @author Stefano Motta
 */
public class FileEntryUtil {

	public static String getBase64EncodedContent(FileEntry fileEntry)
		throws Exception {

		if (fileEntry == null) {
			return null;
		}

		try (InputStream inputStream = fileEntry.getContentStream()) {
			return Base64.encode(FileUtil.getBytes(inputStream));
		}
	}

	public static long getFileEntryId(
			BinaryFile binaryFile, long commerceCatalogGroupId,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider)
		throws PortalException {

		FileEntry fileEntry = _addFileEntry(
			commerceCatalogGroupId, cpdVirtualSettingFileEntryService,
			dlAppService, repositoryLocalService, uniqueFileNameProvider,
			binaryFile);

		return fileEntry.getFileEntryId();
	}

	public static long getFileEntryId(
			String attachment, String url, long commerceCatalogGroupId,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		if (Validator.isNotNull(attachment) && Validator.isNull(url)) {
			serviceContext.setExpandoBridgeAttributes(new HashMap<>());

			File tempFile = FileUtil.createTempFile(Base64.decode(attachment));

			FileEntry fileEntry = _addFileEntry(
				commerceCatalogGroupId, cpdVirtualSettingFileEntryService,
				dlAppService, repositoryLocalService, uniqueFileNameProvider,
				new BinaryFile(
					MimeTypesUtil.getContentType(tempFile), tempFile.getName(),
					new FileInputStream(tempFile), tempFile.length()));

			FileUtil.delete(tempFile);

			return fileEntry.getFileEntryId();
		}

		return 0;
	}

	private static FileEntry _addFileEntry(
			long commerceCatalogGroupId,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			BinaryFile binaryFile)
		throws PortalException {

		DLValidatorUtil.validateFileSize(
			commerceCatalogGroupId, binaryFile.getFileName(),
			binaryFile.getContentType(), binaryFile.getSize());

		Repository repository = repositoryLocalService.fetchRepository(
			commerceCatalogGroupId, CPConstants.SERVICE_NAME_PRODUCT);

		String uniqueFileName = uniqueFileNameProvider.provide(
			binaryFile.getFileName(),
			curFileName -> _exists(
				commerceCatalogGroupId, dlAppService, repository, curFileName));

		return cpdVirtualSettingFileEntryService.addFileEntry(
			commerceCatalogGroupId,
			(repository != null) ? repository.getDlFolderId() : 0,
			binaryFile.getInputStream(), uniqueFileName,
			binaryFile.getContentType(), CPConstants.SERVICE_NAME_PRODUCT);
	}

	private static boolean _exists(
		long groupId, DLAppService dlAppService, Repository repository,
		String fileName) {

		try {
			if (repository == null) {
				return false;
			}

			FileEntry fileEntry = dlAppService.getFileEntryByFileName(
				groupId, repository.getDlFolderId(), fileName);

			if (fileEntry != null) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(FileEntryUtil.class);

}