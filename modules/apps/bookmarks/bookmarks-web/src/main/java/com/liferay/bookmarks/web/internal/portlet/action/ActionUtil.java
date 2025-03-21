/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class ActionUtil {

	public static List<BookmarksEntry> getEntries(
			HttpServletRequest httpServletRequest)
		throws Exception {

		List<BookmarksEntry> entries = new ArrayList<>();

		long[] entryIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsBookmarksEntry");

		for (long entryId : entryIds) {
			entries.add(BookmarksEntryServiceUtil.getEntry(entryId));
		}

		return entries;
	}

	public static List<BookmarksEntry> getEntries(PortletRequest portletRequest)
		throws Exception {

		return getEntries(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static BookmarksEntry getEntry(HttpServletRequest httpServletRequest)
		throws Exception {

		BookmarksEntry entry = null;

		long entryId = ParamUtil.getLong(httpServletRequest, "entryId");

		if (entryId > 0) {
			entry = BookmarksEntryServiceUtil.getEntry(entryId);

			if (entry.isInTrash()) {
				throw new NoSuchEntryException("{entryId=" + entryId + "}");
			}
		}

		return entry;
	}

	public static BookmarksEntry getEntry(PortletRequest portletRequest)
		throws Exception {

		return getEntry(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static BookmarksFolder getFolder(
			HttpServletRequest httpServletRequest)
		throws Exception {

		BookmarksFolder folder = null;

		long folderId = ParamUtil.getLong(httpServletRequest, "folderId");

		if ((folderId > 0) &&
			(folderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			folder = BookmarksFolderServiceUtil.getFolder(folderId);

			if (folder.isInTrash()) {
				throw new NoSuchFolderException("{folderId=" + folderId + "}");
			}
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			BookmarksResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), ActionKeys.VIEW);
		}

		return folder;
	}

	public static BookmarksFolder getFolder(PortletRequest portletRequest)
		throws Exception {

		return getFolder(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static List<BookmarksFolder> getFolders(
			HttpServletRequest httpServletRequest)
		throws Exception {

		List<BookmarksFolder> folders = new ArrayList<>();

		long[] folderIds = ParamUtil.getLongValues(
			httpServletRequest, "rowIdsBookmarksFolder");

		for (long folderId : folderIds) {
			if ((folderId > 0) &&
				(folderId !=
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				folders.add(BookmarksFolderServiceUtil.getFolder(folderId));
			}
		}

		return folders;
	}

	public static List<BookmarksFolder> getFolders(
			PortletRequest portletRequest)
		throws Exception {

		return getFolders(PortalUtil.getHttpServletRequest(portletRequest));
	}

}