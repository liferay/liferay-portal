/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.change.tracking.spi.display;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksFolderPermission;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class BookmarksFolderCTDisplayRenderer
	extends BaseCTDisplayRenderer<BookmarksFolder> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			BookmarksFolder bookmarksFolder)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!BookmarksFolderPermission.contains(
				themeDisplay.getPermissionChecker(), bookmarksFolder,
				ActionKeys.UPDATE)) {

			return null;
		}

		Group group = _groupLocalService.getGroup(bookmarksFolder.getGroupId());

		if (group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, BookmarksPortletKeys.BOOKMARKS_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/bookmarks/edit_folder"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"folderId", bookmarksFolder.getFolderId()
		).buildString();
	}

	@Override
	public Class<BookmarksFolder> getModelClass() {
		return BookmarksFolder.class;
	}

	@Override
	public String getTitle(Locale locale, BookmarksFolder model) {
		return model.getName();
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<BookmarksFolder> displayBuilder) {

		BookmarksFolder bookmarksFolder = displayBuilder.getModel();

		displayBuilder.display(
			"name", bookmarksFolder.getName()
		).display(
			"description", bookmarksFolder.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = bookmarksFolder.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", bookmarksFolder.getCreateDate()
		).display(
			"last-modified", bookmarksFolder.getModifiedDate()
		).display(
			"parent-folder",
			() -> {
				BookmarksFolder parentFolder =
					bookmarksFolder.getParentFolder();

				if (parentFolder == null) {
					return null;
				}

				return parentFolder.getName();
			}
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}