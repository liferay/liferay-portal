/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.processor.RawMetadataProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.service.DLFileVersionPreviewLocalServiceUtil;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLPermission;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RepositoryServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Sergio González
 * @author Roberto Díaz
 */
public class ActionUtil {

	public static List<FileEntry> getFileEntries(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<FileEntry> fileEntries = new ArrayList<>();

		long[] fileEntryIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsFileEntry");

		for (long fileEntryId : fileEntryIds) {
			try {
				fileEntries.add(DLAppServiceUtil.getFileEntry(fileEntryId));
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}
			}
		}

		return fileEntries;
	}

	public static List<FileEntry> getFileEntries(PortletRequest portletRequest)
		throws PortalException {

		return getFileEntries(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static FileEntry getFileEntry(HttpServletRequest httpServletRequest)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(httpServletRequest, "fileEntryId");

		if (fileEntryId <= 0) {
			return null;
		}

		FileEntry fileEntry = DLAppServiceUtil.getFileEntry(fileEntryId);

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (fileEntry.isInTrash() && !cmd.equals(Constants.MOVE_FROM_TRASH)) {
			throw new NoSuchFileEntryException(
				"{fileEntryId=" + fileEntryId + "}");
		}

		return fileEntry;
	}

	public static FileEntry getFileEntry(PortletRequest portletRequest)
		throws PortalException {

		return getFileEntry(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static FileShortcut getFileShortcut(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long fileShortcutId = ParamUtil.getLong(
			httpServletRequest, "fileShortcutId");

		if (fileShortcutId <= 0) {
			return null;
		}

		return DLAppServiceUtil.getFileShortcut(fileShortcutId);
	}

	public static FileShortcut getFileShortcut(PortletRequest portletRequest)
		throws PortalException {

		return getFileShortcut(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static List<FileShortcut> getFileShortcuts(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<FileShortcut> fileShortcuts = new ArrayList<>();

		long[] fileShortcutIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsDLFileShortcut");

		for (long fileShortcutId : fileShortcutIds) {
			try {
				fileShortcuts.add(
					DLAppServiceUtil.getFileShortcut(fileShortcutId));
			}
			catch (NoSuchFileShortcutException noSuchFileShortcutException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileShortcutException);
				}
			}
		}

		return fileShortcuts;
	}

	public static List<FileShortcut> getFileShortcuts(
			PortletRequest portletRequest)
		throws PortalException {

		return getFileShortcuts(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static FileVersion getFileVersion(
			HttpServletRequest httpServletRequest, FileEntry fileEntry)
		throws PortalException {

		if (fileEntry == null) {
			return null;
		}

		FileVersion fileVersion = null;

		String version = ParamUtil.getString(httpServletRequest, "version");

		if (Validator.isNotNull(version)) {
			fileVersion = fileEntry.getFileVersion(version);
		}
		else {
			fileVersion = fileEntry.getFileVersion();
		}

		if (RawMetadataProcessorUtil.isSupported(fileVersion) &&
			!DLFileVersionPreviewLocalServiceUtil.hasDLFileVersionPreview(
				fileEntry.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE)) {

			RawMetadataProcessorUtil.generateMetadata(fileVersion);
		}

		return fileVersion;
	}

	public static FileVersion getFileVersion(
			PortletRequest portletRequest, FileEntry fileEntry)
		throws PortalException {

		return getFileVersion(
			PortalUtil.getHttpServletRequest(portletRequest), fileEntry);
	}

	public static Folder getFolder(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(httpServletRequest, "folderId");

		boolean ignoreRootFolder = ParamUtil.getBoolean(
			httpServletRequest, "ignoreRootFolder");

		if ((folderId <= 0) && !ignoreRootFolder) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				DLPortletInstanceSettingsHelper
					dlPortletInstanceSettingsHelper =
						new DLPortletInstanceSettingsHelper(
							new DLRequestHelper(httpServletRequest));

				folderId = dlPortletInstanceSettingsHelper.getRootFolderId();
			}
		}

		if (folderId <= 0) {
			DLPermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), ActionKeys.VIEW);

			return null;
		}

		Folder folder = DLAppServiceUtil.getFolder(folderId);

		DLFolderUtil.validateDepotFolder(
			folderId, folder.getGroupId(), themeDisplay.getScopeGroupId());

		if (folder.isMountPoint()) {
			com.liferay.portal.kernel.repository.Repository repository =
				RepositoryProviderUtil.getRepository(folder.getRepositoryId());

			folder = repository.getFolder(folder.getFolderId());
		}

		if (!folder.isRepositoryCapabilityProvided(TrashCapability.class)) {
			return folder;
		}

		TrashCapability trashCapability = folder.getRepositoryCapability(
			TrashCapability.class);

		if (trashCapability.isInTrash(folder)) {
			throw new NoSuchFolderException("{folderId=" + folderId + "}");
		}

		return folder;
	}

	public static Folder getFolder(PortletRequest portletRequest)
		throws PortalException {

		return getFolder(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static List<Folder> getFolders(HttpServletRequest httpServletRequest)
		throws PortalException {

		List<Folder> folders = new ArrayList<>();

		long[] folderIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsFolder");

		for (long folderId : folderIds) {
			try {
				folders.add(DLAppServiceUtil.getFolder(folderId));
			}
			catch (NoSuchFolderException noSuchFolderException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFolderException);
				}
			}
		}

		return folders;
	}

	public static List<Folder> getFolders(PortletRequest portletRequest)
		throws PortalException {

		return getFolders(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static Repository getRepository(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long repositoryId = ParamUtil.getLong(
			httpServletRequest, "repositoryId");

		if (repositoryId > 0) {
			return RepositoryServiceUtil.getRepository(repositoryId);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		DLPermission.check(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
			ActionKeys.VIEW);

		return null;
	}

	public static Repository getRepository(PortletRequest portletRequest)
		throws PortalException {

		return getRepository(PortalUtil.getHttpServletRequest(portletRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(ActionUtil.class);

}