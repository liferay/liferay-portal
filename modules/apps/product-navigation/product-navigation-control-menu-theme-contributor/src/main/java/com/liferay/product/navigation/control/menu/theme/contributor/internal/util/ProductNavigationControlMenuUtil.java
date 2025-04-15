/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.theme.contributor.internal.util;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuCategory;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuCategoryRegistry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuEntryRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ProductNavigationControlMenuUtil {

	public static boolean isShowControlMenu(
		HttpServletRequest httpServletRequest,
		ProductNavigationControlMenuCategoryRegistry
			productNavigationControlMenuCategoryRegistry,
		ProductNavigationControlMenuEntryRegistry
			productNavigationControlMenuEntryRegistry) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return false;
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.PREVIEW)) {
			return false;
		}

		User user = themeDisplay.getUser();

		if (!themeDisplay.isImpersonated() && !user.isSetupComplete()) {
			return false;
		}

		List<ProductNavigationControlMenuCategory>
			productNavigationControlMenuCategories =
				productNavigationControlMenuCategoryRegistry.
					getProductNavigationControlMenuCategories(
						ProductNavigationControlMenuCategoryKeys.ROOT);

		for (ProductNavigationControlMenuCategory
				productNavigationControlMenuCategory :
					productNavigationControlMenuCategories) {

			List<ProductNavigationControlMenuEntry>
				productNavigationControlMenuEntries =
					productNavigationControlMenuEntryRegistry.
						getProductNavigationControlMenuEntries(
							productNavigationControlMenuCategory,
							httpServletRequest);

			for (ProductNavigationControlMenuEntry
					productNavigationControlMenuEntry :
						productNavigationControlMenuEntries) {

				if (productNavigationControlMenuEntry.isRelevant(
						httpServletRequest)) {

					return true;
				}
			}
		}

		return false;
	}

}