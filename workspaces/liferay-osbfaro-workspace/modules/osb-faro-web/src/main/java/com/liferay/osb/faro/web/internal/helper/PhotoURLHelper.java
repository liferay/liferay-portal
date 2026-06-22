/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = PhotoURLHelper.class)
public class PhotoURLHelper {

	public String getPhotoURL(String url) throws Exception {
		if (Validator.isNull(url)) {
			return null;
		}

		long userId = _userLocalService.getDefaultUserId(
			_portal.getDefaultCompanyId());

		Group group = _groupLocalService.getCompanyGroup(
			_portal.getDefaultCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(false);

		long folderId = getFolderId(userId, group.getGroupId(), serviceContext);

		url = HttpComponentsUtil.getDomain(url);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchFileEntry(
			group.getGroupId(), folderId, url);

		if (dlFileEntry != null) {
			return getPhotoURL(dlFileEntry);
		}

		byte[] bytes = _http.URLtoByteArray(
			_CLEARBIT_URL + HttpComponentsUtil.encodeParameters(url));

		try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
			String mimeType = MimeTypesUtil.getContentType(inputStream, null);

			FileEntry fileEntry = _dlAppLocalService.addFileEntry(
				dlFileEntry.getExternalReferenceCode(), userId,
				dlFileEntry.getRepositoryId(), folderId,
				dlFileEntry.getFileName(), mimeType, bytes,
				dlFileEntry.getDisplayDate(), dlFileEntry.getExpirationDate(),
				dlFileEntry.getReviewDate(), serviceContext);

			if (bytes.length > _MINIMUM_IMAGE_SIZE) {
				dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
					fileEntry.getFileEntryId());

				return getPhotoURL(dlFileEntry);
			}

			FileVersion fileVersion = fileEntry.getFileVersion();

			_dlFileEntryLocalService.updateStatus(
				userId, fileVersion.getFileVersionId(),
				WorkflowConstants.STATUS_DRAFT, serviceContext,
				Collections.emptyMap());
		}

		return null;
	}

	protected String getExtension(String mimeType) {
		Set<String> extensions = MimeTypesUtil.getExtensions(mimeType);

		Iterator<String> iterator = extensions.iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	protected long getFolderId(
			long userId, long groupId, ServiceContext serviceContext)
		throws Exception {

		DLFolder dlFolder = _dlFolderLocalService.fetchFolder(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _FOLDER_NAME);

		if (dlFolder != null) {
			return dlFolder.getFolderId();
		}

		Folder folder = _dlAppLocalService.addFolder(
			dlFolder.getExternalReferenceCode(), userId,
			dlFolder.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _FOLDER_NAME, null,
			serviceContext);

		return folder.getFolderId();
	}

	protected String getPhotoURL(DLFileEntry dlFileEntry) {
		if ((dlFileEntry == null) ||
			(dlFileEntry.getStatus() != WorkflowConstants.STATUS_APPROVED)) {

			return null;
		}

		return StringBundler.concat(
			"/documents/", dlFileEntry.getGroupId(), StringPool.SLASH,
			dlFileEntry.getFolderId(), StringPool.SLASH, dlFileEntry.getTitle(),
			StringPool.SLASH, dlFileEntry.getUuid());
	}

	private static final String _CLEARBIT_URL =
		"https://logo-core.clearbit.com/";

	private static final String _FOLDER_NAME = "accounts";

	private static final int _MINIMUM_IMAGE_SIZE = 100;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}