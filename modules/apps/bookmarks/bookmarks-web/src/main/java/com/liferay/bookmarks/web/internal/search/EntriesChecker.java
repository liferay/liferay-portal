/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.search;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class EntriesChecker extends EmptyOnClickRowChecker {

	public EntriesChecker(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletResponse);

		_liferayPortletResponse = liferayPortletResponse;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		long entryId = GetterUtil.getLong(primaryKey);

		BookmarksEntry entry =
			BookmarksEntryLocalServiceUtil.fetchBookmarksEntry(entryId);

		BookmarksFolder folder = null;

		if (entry == null) {
			folder = BookmarksFolderLocalServiceUtil.fetchBookmarksFolder(
				entryId);
		}

		String name = null;

		if (entry != null) {
			name = BookmarksEntry.class.getSimpleName();
		}
		else if (folder != null) {
			name = BookmarksFolder.class.getSimpleName();
		}

		String checkBoxRowIds = _getEntryRowIds();
		String checkBoxAllRowIds = "'#" + getAllRowIds() + "'";

		return getRowCheckBox(
			httpServletRequest, checked, disabled,
			_liferayPortletResponse.getNamespace() + RowChecker.ROW_IDS + name,
			primaryKey, checkBoxRowIds, checkBoxAllRowIds, StringPool.BLANK);
	}

	private String _getEntryRowIds() {
		return StringBundler.concat(
			"['", _liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			Folder.class.getSimpleName(), "', '",
			_liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			DLFileShortcut.class.getSimpleName(), "', '",
			_liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			FileEntry.class.getSimpleName(), "']");
	}

	private final LiferayPortletResponse _liferayPortletResponse;
	private final PermissionChecker _permissionChecker;

}