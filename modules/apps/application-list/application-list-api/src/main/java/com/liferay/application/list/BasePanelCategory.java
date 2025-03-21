/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

/**
 * Provides a skeletal implementation of the {@link PanelCategory} to minimize
 * the effort required to implement this interface.
 *
 * <p>
 * To implement an application category, this class should be extended and
 * {@link #include(HttpServletRequest, HttpServletResponse)} and
 * #includeHeader(HttpServletRequest, HttpServletResponse)} should be
 * overridden. The <code>include</code> override method should return
 * <code>true</code> when the application view successfully renders and
 * <code>false</code> otherwise. The <code>includeHeader</code> override method
 * should return <code>true</code> when the category header successfully renders
 * and <code>false</code> otherwise.
 * </p>
 *
 * @author Adolfo Pérez
 * @see    PanelCategory
 */
public abstract class BasePanelCategory implements PanelCategory {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PanelCategory)) {
			return false;
		}

		PanelCategory panelCategory = (PanelCategory)object;

		return Objects.equals(getKey(), panelCategory.getKey());
	}

	@Override
	public int getNotificationsCount(
		PanelCategoryHelper panelCategoryHelper,
		PermissionChecker permissionChecker, Group group, User user) {

		return panelCategoryHelper.getNotificationsCount(
			getKey(), permissionChecker, group, user);
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getKey());
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	@Override
	public boolean includeHeader(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	@Override
	public boolean isActive(
		HttpServletRequest httpServletRequest,
		PanelCategoryHelper panelCategoryHelper, Group group) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String ppid = ParamUtil.getString(
			httpServletRequest, "selPpid", themeDisplay.getPpid());

		return panelCategoryHelper.containsPortlet(
			ppid, getKey(), themeDisplay.getPermissionChecker(), group);
	}

	@Override
	public boolean isPersistState() {
		return false;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		return true;
	}

}