/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.change.tracking.spi.display;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksEntryPermission;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
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
public class BookmarksEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<BookmarksEntry> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			BookmarksEntry bookmarksEntry)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!BookmarksEntryPermission.contains(
				themeDisplay.getPermissionChecker(), bookmarksEntry,
				ActionKeys.UPDATE)) {

			return null;
		}

		Group group = _groupLocalService.getGroup(bookmarksEntry.getGroupId());

		if (group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, BookmarksPortletKeys.BOOKMARKS_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/bookmarks/edit_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"entryId", bookmarksEntry.getEntryId()
		).buildString();
	}

	@Override
	public Class<BookmarksEntry> getModelClass() {
		return BookmarksEntry.class;
	}

	@Override
	public String getTitle(Locale locale, BookmarksEntry model) {
		return model.getName();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<BookmarksEntry> displayBuilder)
		throws PortalException {

		BookmarksEntry bookmarksEntry = displayBuilder.getModel();

		displayBuilder.display(
			"name", bookmarksEntry.getName()
		).display(
			"description", bookmarksEntry.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = bookmarksEntry.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", bookmarksEntry.getCreateDate()
		).display(
			"last-modified", bookmarksEntry.getModifiedDate()
		).display(
			"folder",
			() -> {
				BookmarksFolder bookmarksFolder = bookmarksEntry.getFolder();

				if (bookmarksFolder == null) {
					return null;
				}

				return bookmarksFolder.getName();
			}
		).display(
			"url", bookmarksEntry.getUrl()
		).display(
			"visits", bookmarksEntry.getVisits()
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}