/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.web.internal.security.permission.resource.SiteNavigationMenuPermission;
import com.liferay.site.navigation.admin.web.internal.security.permission.resource.SiteNavigationPermission;
import com.liferay.site.navigation.constants.SiteNavigationActionKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationAdminManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public SiteNavigationAdminManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SiteNavigationAdminDisplayContext siteNavigationAdminDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			siteNavigationAdminDisplayContext.getSearchContainer());

		_siteNavigationAdminDisplayContext = siteNavigationAdminDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deleteSelectedSiteNavigationMenus");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(SiteNavigationMenu siteNavigationMenu)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (SiteNavigationMenuPermission.contains(
				themeDisplay.getPermissionChecker(), siteNavigationMenu,
				ActionKeys.DELETE)) {

			return "deleteSelectedSiteNavigationMenus";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addSiteNavigationMenu");
				dropdownItem.putData(
					"addSiteNavigationMenuURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/site_navigation_admin/add_site_navigation_menu"
					).setMVCPath(
						"/edit_site_navigation_menu.jsp"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "siteNavigationMenus";
	}

	@Override
	public Boolean isShowCreationMenu() {
		if (!_siteNavigationAdminDisplayContext.hasEditPermission()) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return SiteNavigationPermission.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getSiteGroupId(),
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);
	}

	@Override
	protected String getDisplayStyle() {
		return _siteNavigationAdminDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "name"};
	}

	private final SiteNavigationAdminDisplayContext
		_siteNavigationAdminDisplayContext;

}