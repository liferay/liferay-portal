/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.toolbar.contributor;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.portlet.toolbar.contributor.DLPortletToolbarContributorContext;
import com.liferay.document.library.web.internal.portlet.toolbar.contributor.helper.MenuItemProvider;
import com.liferay.document.library.web.internal.portlet.toolbar.contributor.util.DLPortletToolbarContributorUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 * @author Mauro Mariuzzo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"mvc.render.command.name=-",
		"mvc.render.command.name=/document_library/view",
		"mvc.render.command.name=/document_library/view_folder"
	},
	service = PortletToolbarContributor.class
)
public class DLPortletToolbarContributor extends BasePortletToolbarContributor {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_dlPortletToolbarContributorContexts = ServiceTrackerListFactory.open(
			bundleContext, DLPortletToolbarContributorContext.class);
	}

	@Deactivate
	protected void deactivate() {
		_dlPortletToolbarContributorContexts.close();
	}

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (_isDLPortlet(themeDisplay) &&
			!DLPortletToolbarContributorUtil.isShowActionsEnabled(
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
			_menuItemProvider.getAddRepositoryMenuItem(
				folder, themeDisplay, portletRequest));

		_add(
			menuItems,
			_menuItemProvider.getAddShortcutMenuItem(
				folder, themeDisplay, portletRequest));

		MenuItem lastStaticMenuItem = null;

		if (!menuItems.isEmpty()) {
			lastStaticMenuItem = menuItems.get(menuItems.size() - 1);
		}

		for (DLPortletToolbarContributorContext
				dlPortletToolbarContributorContext :
					_dlPortletToolbarContributorContexts) {

			dlPortletToolbarContributorContext.updatePortletTitleMenuItems(
				menuItems, folder, themeDisplay, portletRequest,
				portletResponse);
		}

		_add(
			menuItems,
			_menuItemProvider.getAICreatorMenuItem(
				folder, themeDisplay, portletRequest));

		MenuItem lastExtensionMenuItem = null;

		if (!menuItems.isEmpty()) {
			lastExtensionMenuItem = menuItems.get(menuItems.size() - 1);
		}

		menuItems.addAll(
			_menuItemProvider.getAddDocumentTypesMenuItems(
				folder, themeDisplay, portletRequest));

		if ((lastStaticMenuItem != null) &&
			(lastStaticMenuItem != menuItems.get(menuItems.size() - 1))) {

			lastStaticMenuItem.setSeparator(true);
		}

		if ((lastExtensionMenuItem != null) &&
			(lastExtensionMenuItem != menuItems.get(menuItems.size() - 1))) {

			lastExtensionMenuItem.setSeparator(true);
		}

		return menuItems;
	}

	private void _add(List<MenuItem> menuItems, MenuItem menuItem) {
		if (menuItem != null) {
			menuItems.add(menuItem);
		}
	}

	private boolean _isDLPortlet(ThemeDisplay themeDisplay) {
		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Objects.equals(
				portletDisplay.getRootPortletId(),
				DLPortletKeys.DOCUMENT_LIBRARY) ||
			Objects.equals(
				portletDisplay.getRootPortletId(),
				DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {

			return true;
		}

		return false;
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	private ServiceTrackerList<DLPortletToolbarContributorContext>
		_dlPortletToolbarContributorContexts;

	@Reference
	private MenuItemProvider _menuItemProvider;

}