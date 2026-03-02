/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.ThumbnailURLReference;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class FileEntryUtil {

	public static long getPreviewFileEntryId(
			long groupId, String resourceName, ServiceContext serviceContext,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		if ((thumbnailURLReference == null) ||
			Validator.isNull(
				thumbnailURLReference.getExternalReferenceCode())) {

			return 0;
		}

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					thumbnailURLReference.getExternalReferenceCode(), groupId);

		if (fileEntry == null) {
			fileEntry = _getFileEntry(
				groupId, resourceName, serviceContext, thumbnailURLReference,
				serviceContext.getUserId());
		}

		return fileEntry.getFileEntryId();
	}

	private static FileEntry _getFileEntry(
			long groupId, String resourceName, ServiceContext serviceContext,
			ThumbnailURLReference thumbnailURLReference, long userId)
		throws Exception {

		File file = null;

		try {
			file = FileUtil.createTempFile(
				URLUtil.getByteArray(thumbnailURLReference.getUrl()));

			String mimeType = MimeTypesUtil.getContentType(file);

			Set<String> extensions = MimeTypesUtil.getExtensions(mimeType);

			String extension = StringPool.BLANK;

			if (!extensions.isEmpty()) {
				Iterator<String> iterator = extensions.iterator();

				extension = iterator.next();
			}

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setIndexingEnabled(false);

			Repository repository =
				PortletFileRepositoryUtil.addPortletRepository(
					groupId, LayoutAdminPortletKeys.GROUP_PAGES,
					serviceContext);

			String fileName =
				thumbnailURLReference.getExternalReferenceCode() + "_preview" +
					extension;

			return DLAppLocalServiceUtil.addFileEntry(
				thumbnailURLReference.getExternalReferenceCode(), userId,
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				resourceName + "_" + fileName, mimeType, fileName, null, null,
				null, file, null, null, null, serviceContext);
		}
		catch (IOException ioException) {
			throw new IllegalArgumentException(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				ioException);
		}
		finally {
			if (file != null) {
				FileUtil.delete(file);
			}
		}
	}

}