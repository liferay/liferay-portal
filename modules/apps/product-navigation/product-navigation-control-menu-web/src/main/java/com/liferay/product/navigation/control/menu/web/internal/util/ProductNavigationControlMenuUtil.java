/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class ProductNavigationControlMenuUtil {

	public static boolean isEditEnabled(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isStateMaximized()) {
			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypePortlet() || layout.isTypeAssetDisplay() ||
			layout.isTypeContent() || StagingUtil.isIncomplete(layout)) {

			return false;
		}

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		LayoutTypeController layoutTypeController =
			layoutTypePortlet.getLayoutTypeController();

		if (layoutTypeController.isFullPageDisplayable() ||
			!_hasAddContentOrApplicationPermission(themeDisplay) ||
			!(_hasUpdateLayoutPermission(themeDisplay) ||
			  _hasCustomizePermission(themeDisplay))) {

			return false;
		}

		return true;
	}

	private static boolean _hasAddContentOrApplicationPermission(
		ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		return !layout.isLayoutPrototypeLinkActive();
	}

	private static boolean _hasCustomizePermission(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();
		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		if (!layout.isTypePortlet() || (layoutTypePortlet == null) ||
			!layoutTypePortlet.isCustomizable() ||
			!layoutTypePortlet.isCustomizedView()) {

			return false;
		}

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.CUSTOMIZE);
	}

	private static boolean _hasUpdateLayoutPermission(ThemeDisplay themeDisplay)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			ActionKeys.UPDATE);
	}

}