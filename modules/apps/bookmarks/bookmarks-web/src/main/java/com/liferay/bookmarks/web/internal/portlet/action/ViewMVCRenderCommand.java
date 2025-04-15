/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/bookmarks/view",
		"mvc.command.name=/bookmarks/view_folder"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			BookmarksFolder folder = ActionUtil.getFolder(renderRequest);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_FOLDER, folder);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_PORTLET_TOOLBAR_CONTRIBUTOR,
				_bookmarksPortletToolbarContributor);

			CTTimelineUtil.setClassName(renderRequest, BookmarksEntry.class);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFolderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/view.jsp";
	}

	@Reference(
		target = "(component.name=com.liferay.bookmarks.web.internal.portlet.toolbar.contributor.BookmarksPortletToolbarContributor)"
	)
	private PortletToolbarContributor _bookmarksPortletToolbarContributor;

}