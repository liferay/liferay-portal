/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Sergio González
 */
public class DLBreadcrumbUtil {

	public static void addPortletBreadcrumbEntries(
			FileEntry fileEntry, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		Folder folder = fileEntry.getFolder();

		if (folder.getFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			addPortletBreadcrumbEntries(
				folder, httpServletRequest, renderResponse);
		}

		FileEntry unescapedFileEntry = fileEntry.toUnescapedModel();

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, unescapedFileEntry.getTitle(),
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/document_library/view_file_entry"
			).setParameter(
				"fileEntryId", fileEntry.getFileEntryId()
			).buildString());
	}

	public static void addPortletBreadcrumbEntries(
			FileShortcut fileShortcut, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		Folder folder = fileShortcut.getFolder();

		if (folder.getFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			addPortletBreadcrumbEntries(
				folder, httpServletRequest, renderResponse);
		}

		FileShortcut unescapedDLFileShortcut = fileShortcut.toUnescapedModel();

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, unescapedDLFileShortcut.getToTitle(),
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/document_library/view_file_entry"
			).setParameter(
				"fileEntryId", fileShortcut.getToFileEntryId()
			).buildString());
	}

	public static void addPortletBreadcrumbEntries(
			Folder folder, HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/document_library/view"
		).buildPortletURL();

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, themeDisplay.translate("home"),
			portletURL.toString(),
			HashMapBuilder.<String, Object>put(
				"direction-right", Boolean.TRUE.toString()
			).put(
				"folder-id",
				() -> {
					DLPortletInstanceSettingsHelper
						dlPortletInstanceSettingsHelper =
							new DLPortletInstanceSettingsHelper(
								new DLRequestHelper(httpServletRequest));

					return dlPortletInstanceSettingsHelper.getRootFolderId();
				}
			).build());

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/view_folder");

		addPortletBreadcrumbEntries(folder, httpServletRequest, portletURL);
	}

	public static void addPortletBreadcrumbEntries(
			Folder folder, HttpServletRequest httpServletRequest,
			PortletURL portletURL)
		throws Exception {

		long rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		boolean ignoreRootFolder = ParamUtil.getBoolean(
			httpServletRequest, "ignoreRootFolder");

		if (!ignoreRootFolder) {
			DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
				new DLPortletInstanceSettingsHelper(
					new DLRequestHelper(httpServletRequest));

			rootFolderId = dlPortletInstanceSettingsHelper.getRootFolderId();
		}

		List<Folder> ancestorFolders = Collections.emptyList();

		if ((folder != null) && (folder.getFolderId() != rootFolderId) &&
			!folder.isRoot()) {

			ancestorFolders = folder.getAncestors();

			int indexOfRootFolder = -1;

			for (int i = 0; i < ancestorFolders.size(); i++) {
				Folder ancestorFolder = ancestorFolders.get(i);

				if (rootFolderId == ancestorFolder.getFolderId()) {
					indexOfRootFolder = i;
				}
			}

			if (indexOfRootFolder > -1) {
				ancestorFolders = ancestorFolders.subList(0, indexOfRootFolder);
			}
		}

		Collections.reverse(ancestorFolders);

		for (Folder ancestorFolder : ancestorFolders) {
			portletURL.setParameter(
				"folderId", String.valueOf(ancestorFolder.getFolderId()));

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, ancestorFolder.getName(),
				portletURL.toString(),
				HashMapBuilder.<String, Object>put(
					"direction-right", Boolean.TRUE.toString()
				).put(
					"folder-id", ancestorFolder.getFolderId()
				).build());
		}

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (folder != null) {
			folderId = folder.getFolderId();
		}

		portletURL.setParameter("folderId", String.valueOf(folderId));

		if ((folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
			(folderId != rootFolderId)) {

			Folder unescapedFolder = folder.toUnescapedModel();

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, unescapedFolder.getName(), StringPool.BLANK,
				HashMapBuilder.<String, Object>put(
					"direction-right", Boolean.TRUE.toString()
				).put(
					"folder-id", folderId
				).build());
		}
	}

	public static void addPortletBreadcrumbEntries(
			Folder folder, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		PortletURL portletURL = renderResponse.createRenderURL();

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (folder != null) {
			folderId = folder.getFolderId();
		}

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view_folder");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view");
		}

		addPortletBreadcrumbEntries(folder, httpServletRequest, portletURL);
	}

	public static void addPortletBreadcrumbEntries(
			long folderId, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder folder = DLAppLocalServiceUtil.getFolder(folderId);

			if (folder.getFolderId() !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				addPortletBreadcrumbEntries(
					folder, httpServletRequest, renderResponse);
			}
		}
	}

}