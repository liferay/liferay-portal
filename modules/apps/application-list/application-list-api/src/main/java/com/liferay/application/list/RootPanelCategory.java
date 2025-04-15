/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * Represents the root panel category for all implemented categories in the
 * {@link PanelCategoryRegistryUtil}.
 *
 * @author Adolfo Pérez
 * @see    PanelCategory
 * @see    PanelCategoryRegistryUtil
 */
public class RootPanelCategory implements PanelCategory {

	public static PanelCategory getInstance() {
		return _panelCategory;
	}

	@Override
	public String getKey() {
		return _ROOT_PANEL_CATEGORY_KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public int getNotificationsCount(
		PanelCategoryHelper panelCategoryHelper,
		PermissionChecker permissionChecker, Group group, User user) {

		return 0;
	}

	@Override
	public boolean include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return false;
	}

	@Override
	public boolean includeHeader(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return false;
	}

	@Override
	public boolean isActive(
		HttpServletRequest httpServletRequest,
		PanelCategoryHelper panelCategoryHelper, Group group) {

		return false;
	}

	@Override
	public boolean isPersistState() {
		return false;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group) {
		return true;
	}

	private RootPanelCategory() {
	}

	private static final String _ROOT_PANEL_CATEGORY_KEY = "root";

	private static final PanelCategory _panelCategory = new RootPanelCategory();

}