/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.toolbar.contributor;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.web.internal.portlet.toolbar.contributor.helper.MenuItemProvider;
import com.liferay.document.library.web.internal.portlet.toolbar.contributor.util.DLPortletToolbarContributorUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.render.command.name=-",
		"mvc.render.command.name=/image_gallery_display/view"
	},
	service = PortletToolbarContributor.class
)
public class IGPortletToolbarContributor extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!DLPortletToolbarContributorUtil.isShowActionsEnabled(
				themeDisplay)) {

			return null;
		}

		Folder folder = DLPortletToolbarContributorUtil.getFolder(
			_dlAppLocalService, themeDisplay, portletRequest);

		List<MenuItem> menuItems = new ArrayList<>();

		_add(
			menuItems,
			_menuItemProvider.getAddFileMenuItem(
				folder, themeDisplay, portletRequest));

		_add(
			menuItems,
			_menuItemProvider.getAddMultipleFilesMenuItem(
				folder, themeDisplay, portletRequest));

		_add(
			menuItems,
			_menuItemProvider.getAddFolderMenuItem(
				folder, themeDisplay, portletRequest));

		_add(
			menuItems,
			_menuItemProvider.getAddShortcutMenuItem(
				folder, themeDisplay, portletRequest));

		return menuItems;
	}

	private void _add(List<MenuItem> menuItems, MenuItem menuItem) {
		if (menuItem != null) {
			menuItems.add(menuItem);
		}
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private MenuItemProvider _menuItemProvider;

}