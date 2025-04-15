/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Shinn Lok
 */
public class FaroAdminManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public FaroAdminManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderResponse renderResponse, SearchContainer<?> searchContainer) {

		super(
			liferayPortletRequest, liferayPortletResponse, httpServletRequest,
			searchContainer);

		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
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
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			return null;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			_renderResponse.createActionURL()
		).setRedirect(
			ParamUtil.getString(
				liferayPortletRequest.getHttpServletRequest(), "redirect",
				_themeDisplay.getURLCurrent())
		).buildPortletURL();

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					portletURL, ActionRequest.ACTION_NAME,
					"/faro_admin/refresh_project", "groupId", -1);
				dropdownItem.setLabel(
					LanguageUtil.get(request, "refresh-all-projects"));
			}
		).build();
	}

	@Override
	public String getInfoPanelId() {
		return "infoPanelId";
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "faro_admin";
	}

	@Override
	public Boolean isDisabled() {
		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {
			"all", "basic", "business", "enterprise", "inactive", "offline",
			"usage-limit-approaching", "usage-limit-exceeded"
		};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {
			"createDate", "individualsUsage", "name", "pageViewsUsage"
		};
	}

	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}