/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Alicia Garcia
 */
public class DLInfoPanelDisplayContext {

	public DLInfoPanelDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_permissionChecker = _themeDisplay.getPermissionChecker();
	}

	public List<FileEntry> getFileEntries() {
		if (_fileEntries != null) {
			return _fileEntries;
		}

		_fileEntries = (List<FileEntry>)_httpServletRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_ENTRIES);

		return _fileEntries;
	}

	public Group getFileEntryGroup(long groupId) throws PortalException {
		Group fileEntryGroup = GroupLocalServiceUtil.getGroup(groupId);

		if (fileEntryGroup.isSite()) {
			while ((fileEntryGroup != null) && !fileEntryGroup.isSite()) {
				fileEntryGroup = fileEntryGroup.getParentGroup();
			}
		}
		else if (fileEntryGroup.isDepot()) {
			while ((fileEntryGroup != null) && !fileEntryGroup.isDepot()) {
				fileEntryGroup = fileEntryGroup.getParentGroup();
			}
		}

		return fileEntryGroup;
	}

	public String getFileEntryTypeName(FileEntry fileEntry, Locale locale)
		throws PortalException {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileEntryType dlFileEntryType = dlFileEntry.getDLFileEntryType();

		return HtmlUtil.escape(dlFileEntryType.getName(locale));
	}

	public List<FileShortcut> getFileShortcuts() {
		if (_fileShortcuts != null) {
			return _fileShortcuts;
		}

		_fileShortcuts = (List<FileShortcut>)_httpServletRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUTS);

		return _fileShortcuts;
	}

	public FileVersion getFileVersion(FileEntry fileEntry)
		throws PortalException {

		User user = _themeDisplay.getUser();

		if ((user.getUserId() == fileEntry.getUserId()) ||
			_permissionChecker.isContentReviewer(
				user.getCompanyId(), _themeDisplay.getScopeGroupId()) ||
			DLFileEntryPermission.contains(
				_permissionChecker, fileEntry, ActionKeys.UPDATE)) {

			return fileEntry.getLatestFileVersion();
		}

		return fileEntry.getFileVersion();
	}

	public long getFolderId(Folder folder) {
		if (folder != null) {
			return folder.getFolderId();
		}

		return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	public List<Folder> getFolders() throws PortalException {
		if (_folders != null) {
			return _folders;
		}

		_folders = (List<Folder>)_httpServletRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FOLDERS);

		if (ListUtil.isEmpty(_folders) && ListUtil.isEmpty(getFileEntries()) &&
			ListUtil.isEmpty(getFileShortcuts())) {

			long folderId = GetterUtil.getLong(
				(String)_httpServletRequest.getAttribute("view.jsp-folderId"),
				ParamUtil.getLong(_httpServletRequest, "folderId"));

			_folders = new ArrayList<>();

			if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				_folders.add(DLAppLocalServiceUtil.getFolder(folderId));
			}
			else {
				_folders.add(null);
			}
		}

		return _folders;
	}

	public long getRepositoryId() {
		return GetterUtil.getLong(
			(String)_httpServletRequest.getAttribute("view.jsp-repositoryId"),
			ParamUtil.getLong(_httpServletRequest, "repositoryId"));
	}

	public boolean isFileEntrySelected() throws PortalException {
		if (ListUtil.isEmpty(getFolders()) &&
			ListUtil.isEmpty(getFileShortcuts()) &&
			ListUtil.isNotEmpty(getFileEntries()) &&
			(getFileEntries().size() == 1)) {

			return true;
		}

		return false;
	}

	public boolean isFileShortcutSelected() throws PortalException {
		if (ListUtil.isEmpty(getFolders()) &&
			ListUtil.isEmpty(getFileEntries()) &&
			ListUtil.isNotEmpty(getFileShortcuts()) &&
			(getFileShortcuts().size() == 1)) {

			return true;
		}

		return false;
	}

	public boolean isFolderSelected() throws PortalException {
		if (ListUtil.isEmpty(getFileEntries()) &&
			ListUtil.isEmpty(getFileShortcuts()) &&
			ListUtil.isNotEmpty(getFolders()) && (getFolders().size() == 1)) {

			return true;
		}

		return false;
	}

	private List<FileEntry> _fileEntries;
	private List<FileShortcut> _fileShortcuts;
	private List<Folder> _folders;
	private final HttpServletRequest _httpServletRequest;
	private final PermissionChecker _permissionChecker;
	private final ThemeDisplay _themeDisplay;

}