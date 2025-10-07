/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.attachment;

import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;

/**
 * @author Carlos Correa
 */
public interface AttachmentManager {

	public String[] getAcceptedFileExtensions(long objectFieldId);

	public DLFolder getDLFolder(
			long companyId, long groupId, long objectFieldId,
			ServiceContext serviceContext, long userId)
		throws PortalException;

	public DLFolder getDLFolder(
			long companyId, long groupId, String portletId,
			ServiceContext serviceContext, long userId)
		throws PortalException;

	public long getMaximumFileSize(long objectFieldId, boolean signedIn);

	public FileEntry getOrAddFileEntry(
			long companyId, String externalReferenceCode, byte[] fileContent,
			String fileName, long groupId, long objectFieldId,
			ServiceContext serviceContext)
		throws Exception;

	public FileEntry getOrAddFileEntry(
			long companyId, String externalReferenceCode, byte[] fileContent,
			String fileName, String folderExternalReferenceCode, long groupId,
			long objectFieldId, ServiceContext serviceContext)
		throws Exception;

	public void validateFileExtension(String fileName, long objectFieldId)
		throws FileExtensionException;

	public void validateFileName(String fileName) throws FileNameException;

	public void validateFileSize(
			String fileName, long fileSize, long objectFieldId,
			boolean signedIn)
		throws FileSizeException;

}