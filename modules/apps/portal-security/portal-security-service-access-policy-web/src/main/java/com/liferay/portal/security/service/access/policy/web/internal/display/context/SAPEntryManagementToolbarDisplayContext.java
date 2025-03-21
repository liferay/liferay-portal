/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.service.access.policy.constants.SAPActionKeys;
import com.liferay.portal.security.service.access.policy.web.internal.security.permission.resource.SAPPermission;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SAPEntryManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public SAPEntryManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!SAPPermission.contains(
				_themeDisplay.getPermissionChecker(),
				SAPActionKeys.ACTION_ADD_SAP_ENTRY)) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> dropdownItem.setHref(
				liferayPortletResponse.createRenderURL(), "mvcPath",
				"/edit_entry.jsp", "redirect", _themeDisplay.getURLCurrent())
		).build();
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	public Boolean isShowSearch() {
		return false;
	}

	private final ThemeDisplay _themeDisplay;

}