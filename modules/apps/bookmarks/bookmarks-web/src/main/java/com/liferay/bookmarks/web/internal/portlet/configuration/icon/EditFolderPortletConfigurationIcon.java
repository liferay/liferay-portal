/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.configuration.icon;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.web.internal.portlet.action.ActionUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"path=/bookmarks/view_folder"
	},
	service = PortletConfigurationIcon.class
)
public class EditFolderPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "edit");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, BookmarksPortletKeys.BOOKMARKS_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/bookmarks/edit_folder"
		).setRedirect(
			_portal.getCurrentURL(portletRequest)
		).buildPortletURL();

		BookmarksFolder folder = null;

		try {
			folder = ActionUtil.getFolder(portletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		portletURL.setParameter(
			"folderId", String.valueOf(folder.getFolderId()));
		portletURL.setParameter(
			"mergeWithParentFolderDisabled", Boolean.TRUE.toString());

		return portletURL.toString();
	}

	@Override
	public double getWeight() {
		return 103;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			BookmarksFolder folder = ActionUtil.getFolder(portletRequest);

			if (folder.getFolderId() ==
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				return false;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (_bookmarksFolderModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), folder,
					ActionKeys.UPDATE)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditFolderPortletConfigurationIcon.class);

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	private ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}