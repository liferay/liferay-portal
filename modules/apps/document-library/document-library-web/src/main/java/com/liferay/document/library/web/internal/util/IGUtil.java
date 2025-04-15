/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.IGRequestHelper;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class IGUtil {

	public static void addPortletBreadcrumbEntries(
			Folder folder, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			PortalUtil.getLiferayPortletResponse(renderResponse)
		).setMVCRenderCommandName(
			"/image_gallery_display/view"
		).buildRenderURL();

		DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
			new DLPortletInstanceSettingsHelper(
				new IGRequestHelper(httpServletRequest));

		long rootFolderId = dlPortletInstanceSettingsHelper.getRootFolderId();

		List<Folder> ancestorFolders = Collections.emptyList();

		if ((folder != null) && (folder.getFolderId() != rootFolderId)) {
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

		Collections.reverse(new ArrayList<>(ancestorFolders));

		long repositoryId = 0;

		if (folder != null) {
			repositoryId = folder.getRepositoryId();
		}
		else {
			repositoryId =
				dlPortletInstanceSettingsHelper.getSelectedRepositoryId();
		}

		for (Folder ancestorFolder : ancestorFolders) {
			portletURL.setParameter(
				"repositoryId", String.valueOf(repositoryId));
			portletURL.setParameter(
				"folderId", String.valueOf(ancestorFolder.getFolderId()));

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, ancestorFolder.getName(),
				portletURL.toString());
		}

		portletURL.setParameter("repositoryId", String.valueOf(repositoryId));

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (folder != null) {
			folderId = folder.getFolderId();
		}

		portletURL.setParameter("folderId", String.valueOf(folderId));

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, folder.getName(), portletURL.toString());
	}

	public static void addPortletBreadcrumbEntries(
			long folderId, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		addPortletBreadcrumbEntries(
			DLAppLocalServiceUtil.getFolder(folderId), httpServletRequest,
			renderResponse);
	}

}