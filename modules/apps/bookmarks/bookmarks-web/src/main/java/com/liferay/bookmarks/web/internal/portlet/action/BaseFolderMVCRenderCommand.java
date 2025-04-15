/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Iván Zaera
 */
public abstract class BaseFolderMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			BookmarksFolder folder = ActionUtil.getFolder(renderRequest);

			if (folder != null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				checkPermissions(themeDisplay.getPermissionChecker(), folder);
			}

			renderRequest.setAttribute(
				BookmarksWebKeys.BOOKMARKS_FOLDER, folder);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFolderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/bookmarks/error.jsp";
			}

			throw new PortletException(exception);
		}

		return getPath();
	}

	protected void checkPermissions(
			PermissionChecker permissionChecker, BookmarksFolder folder)
		throws PortalException {
	}

	protected abstract String getPath();

}