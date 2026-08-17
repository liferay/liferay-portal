/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.product.navigation.control.menu;

import com.liferay.frontend.data.set.constants.FDSAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=500"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class FDSAdminActionsProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/control_menu/fds_admin_actions.jsp";
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if ((portletDisplay == null) ||
			!FDSAdminPortletKeys.FDS_ADMIN.equals(
				portletDisplay.getPortletName())) {

			return false;
		}

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName");

		if (mvcRenderCommandName.isBlank()) {
			mvcRenderCommandName = ParamUtil.getString(
				httpServletRequest,
				portletDisplay.getNamespace() + "mvcRenderCommandName");
		}

		if (mvcRenderCommandName.isBlank() ||
			mvcRenderCommandName.equals("/frontend_data_set_admin/view")) {

			return true;
		}

		return false;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.data.set.admin.web)"
	)
	private ServletContext _servletContext;

}