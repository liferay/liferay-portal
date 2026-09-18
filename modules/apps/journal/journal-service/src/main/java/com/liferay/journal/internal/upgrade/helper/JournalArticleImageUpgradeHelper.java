/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.helper;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = JournalArticleImageUpgradeHelper.class)
public class JournalArticleImageUpgradeHelper {

	public String getDocumentLibraryValue(String url) {
		try {
			FileEntry fileEntry = getFileEntryFromURL(url);

			if (fileEntry == null) {
				return StringPool.BLANK;
			}

			return JSONUtil.put(
				"groupId", fileEntry.getGroupId()
			).put(
				"title", fileEntry.getTitle()
			).put(
				"type", "document"
			).put(
				"uuid", fileEntry.getUuid()
			).toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public FileEntry getFileEntryFromURL(String url) {
		FileEntry fileEntry = null;

		try {
			if (url.contains("/c/document_library/get_file?") ||
				url.contains("/image/image_gallery?")) {

				fileEntry = _getFileEntryByOldDocumentLibraryURL(url);
			}
			else if (url.contains("/documents/")) {
				fileEntry = _getFileEntryByDocumentLibraryURL(url);
			}
		}
		catch (PortalException portalException) {
			String message = "Unable to get file entry from URL " + url;

			if (_log.isDebugEnabled()) {
				_log.debug(message, portalException);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(message);
			}
		}

		return fileEntry;
	}

	public long getFolderId(long userId, long groupId, long resourcePrimKey)
		throws PortalException {

		long repositoryId = _getRepositoryId(groupId);

		try {
			Folder folder = _portletFileRepository.getPortletFolder(
				repositoryId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(resourcePrimKey));

			return folder.getFolderId();
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get folder for " +
						String.valueOf(resourcePrimKey),
					noSuchFolderException);
			}
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Folder folder = _portletFileRepository.addPortletFolder(
			userId, repositoryId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			String.valueOf(resourcePrimKey), serviceContext);

		return folder.getFolderId();
	}

	private FileEntry _getFileEntryByDocumentLibraryURL(String url)
		throws PortalException {

		int x = url.indexOf("/documents/");

		int y = url.indexOf(StringPool.QUESTION);

		if (y == -1) {
			y = url.length();
		}

		url = url.substring(x, y);

		String[] parts = StringUtil.split(url, CharPool.SLASH);

		long groupId = GetterUtil.getLong(parts[2]);

		String uuid = null;

		if (parts.length == 5) {
			uuid = _getUuidByDocumentLibraryURLWithoutUuid(parts);
		}
		else {
			uuid = parts[5];
		}

		return _dlAppLocalService.getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	private FileEntry _getFileEntryByOldDocumentLibraryURL(String url)
		throws PortalException {

		Matcher matcher = _oldDocumentLibraryURLPattern.matcher(url);

		if (!matcher.find()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse file entry from URL " + url);
			}

			return null;
		}

		long groupId = GetterUtil.getLong(matcher.group(2));

		return _dlAppLocalService.getFileEntryByUuidAndGroupId(
			matcher.group(1), groupId);
	}

	private long _getRepositoryId(long groupId) throws PortalException {
		Repository repository = _portletFileRepository.fetchPortletRepository(
			groupId, JournalConstants.SERVICE_NAME);

		if (repository != null) {
			return repository.getRepositoryId();
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		repository = _portletFileRepository.addPortletRepository(
			groupId, JournalConstants.SERVICE_NAME, serviceContext);

		return repository.getRepositoryId();
	}

	private String _getUuidByDocumentLibraryURLWithoutUuid(String[] splitURL)
		throws PortalException {

		long groupId = GetterUtil.getLong(splitURL[2]);
		long folderId = GetterUtil.getLong(splitURL[3]);
		String title = HttpComponentsUtil.decodeURL(
			HtmlUtil.escape(splitURL[4]));

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(
				groupId, folderId, title);

			return fileEntry.getUuid();
		}
		catch (PortalException portalException) {
			String message = StringBundler.concat(
				"Unable to get file entry with group ID ", groupId,
				", folder ID ", folderId, ", and title ", title);

			if (_log.isDebugEnabled()) {
				_log.debug(message, portalException);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(message);
			}

			throw portalException;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleImageUpgradeHelper.class);

	private static final Pattern _oldDocumentLibraryURLPattern =
		Pattern.compile("uuid=([^&]+)&groupId=([^&]+)");

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}