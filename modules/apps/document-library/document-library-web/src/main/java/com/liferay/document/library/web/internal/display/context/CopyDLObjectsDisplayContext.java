/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public class CopyDLObjectsDisplayContext {

	public CopyDLObjectsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, long size,
		ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_size = size;
		_themeDisplay = themeDisplay;
	}

	public String getActionURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/document_library/copy_dl_objects"
		).buildString();
	}

	public long[] getDLObjectIds() {
		if (ArrayUtil.isEmpty(_dlObjectIds)) {
			_dlObjectIds = ParamUtil.getLongValues(
				_httpServletRequest, "dlObjectIds");
		}

		return _dlObjectIds;
	}

	public String getDLObjectName() throws PortalException {
		if (_dlObjectName != null) {
			return _dlObjectName;
		}

		long[] dlObjectIds = getDLObjectIds();

		if (ArrayUtil.isEmpty(dlObjectIds)) {
			_dlObjectName = StringPool.BLANK;

			return _dlObjectName;
		}

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			dlObjectIds[0]);

		if (dlFileEntry != null) {
			_dlObjectName = _getFolderName(dlFileEntry.getFolder());

			return _dlObjectName;
		}

		DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(
			dlObjectIds[0]);

		if (dlFolder != null) {
			_dlObjectName = _getFolderName(dlFolder.getParentFolder());

			return _dlObjectName;
		}

		DLFileShortcut dlFileShortcut =
			DLFileShortcutLocalServiceUtil.getDLFileShortcut(dlObjectIds[0]);

		_dlObjectName = _getFolderName(dlFileShortcut.getDLFolder());

		return _dlObjectName;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getSelectionModalURL() throws PortalException {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		FolderItemSelectorURLProvider folderItemSelectorURLProvider =
			new FolderItemSelectorURLProvider(
				_httpServletRequest, itemSelector);

		return folderItemSelectorURLProvider.getSelectCopyToFolderURL(
			getSourceRepositoryId(), _getSourceFolderId(), _getFolderId());
	}

	public long getSize() {
		return _size;
	}

	public long getSourceRepositoryId() {
		if (_sourceRepositoryId != 0) {
			return _sourceRepositoryId;
		}

		_sourceRepositoryId = ParamUtil.getLong(
			_httpServletRequest, "sourceRepositoryId");

		return _sourceRepositoryId;
	}

	public void setViewAttributes() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(getRedirect());

		if (_liferayPortletResponse instanceof RenderResponse) {
			RenderResponse renderResponse =
				(RenderResponse)_liferayPortletResponse;

			renderResponse.setTitle(
				LanguageUtil.get(_httpServletRequest, "copy-to"));
		}
	}

	private long _getFolderId() {
		if (_dlObjectIds.length > 1) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(
			_dlObjectIds[0]);

		if (dlFolder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return dlFolder.getFolderId();
	}

	private String _getFolderName(DLFolder dlFolder) {
		if ((dlFolder == null) ||
			(dlFolder.getFolderId() ==
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return LanguageUtil.get(_httpServletRequest, "home");
		}

		return dlFolder.getName();
	}

	private long _getSourceFolderId() {
		if (_sourceFolderId < 0) {
			_sourceFolderId = ParamUtil.getLong(
				_httpServletRequest, "sourceFolderId");
		}

		return _sourceFolderId;
	}

	private long[] _dlObjectIds;
	private String _dlObjectName;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _redirect;
	private final long _size;
	private long _sourceFolderId = -1;
	private long _sourceRepositoryId;
	private final ThemeDisplay _themeDisplay;

}