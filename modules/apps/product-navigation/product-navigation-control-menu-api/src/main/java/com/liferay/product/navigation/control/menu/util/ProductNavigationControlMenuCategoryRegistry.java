/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.util;

import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuCategory;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Lance Ji
 */
public interface ProductNavigationControlMenuCategoryRegistry {

	public List<ProductNavigationControlMenuCategory>
		getProductNavigationControlMenuCategories(
			String productNavigationControlMenuCategoryKey);

	public List<ProductNavigationControlMenuCategory>
		getProductNavigationControlMenuCategories(
			String productNavigationControlMenuCategoryKey,
			HttpServletRequest httpServletRequest);

}