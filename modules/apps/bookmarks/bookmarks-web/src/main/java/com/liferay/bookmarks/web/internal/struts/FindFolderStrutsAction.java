/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.struts;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.portal.kernel.portlet.BasePortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.struts.FindStrutsAction;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	property = "path=/bookmarks/find_folder", service = StrutsAction.class
)
public class FindFolderStrutsAction extends FindStrutsAction {

	@Override
	protected void addRequiredParameters(
		HttpServletRequest httpServletRequest, String portletId,
		PortletURL portletURL) {

		portletURL.setParameter("struts_action", "/bookmarks/view_folder");
	}

	@Override
	protected long getGroupId(long primaryKey) throws Exception {
		BookmarksFolder folder = _bookmarksFolderLocalService.getFolder(
			primaryKey);

		return folder.getGroupId();
	}

	@Override
	protected PortletLayoutFinder getPortletLayoutFinder() {
		return new BasePortletLayoutFinder() {

			@Override
			protected String[] getPortletIds() {
				return new String[] {BookmarksPortletKeys.BOOKMARKS};
			}

		};
	}

	@Override
	protected String getPrimaryKeyParameterName() {
		return "folderId";
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

}