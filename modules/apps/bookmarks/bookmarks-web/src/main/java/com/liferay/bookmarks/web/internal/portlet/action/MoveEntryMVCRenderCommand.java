/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/bookmarks/move_entry"
	},
	service = MVCRenderCommand.class
)
public class MoveEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			List<BookmarksEntry> entries = ActionUtil.getEntries(renderRequest);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_ENTRIES, entries);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_ENTRY,
				ActionUtil.getEntry(renderRequest));

			List<BookmarksFolder> folders = ActionUtil.getFolders(
				renderRequest);

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_FOLDERS, folders);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/bookmarks/move_entries.jsp";
	}

}