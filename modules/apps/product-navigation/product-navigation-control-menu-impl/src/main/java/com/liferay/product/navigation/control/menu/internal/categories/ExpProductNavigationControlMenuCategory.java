/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.internal.categories;

import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuCategory;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.ROOT,
		"product.navigation.control.menu.category.order:Integer=300"
	},
	service = ProductNavigationControlMenuCategory.class
)
public class ExpProductNavigationControlMenuCategory
	implements ProductNavigationControlMenuCategory {

	@Override
	public String getKey() {
		return ProductNavigationControlMenuCategoryKeys.EXP;
	}

	@Override
	public boolean hasAccessPermission(HttpServletRequest httpServletRequest) {
		return true;
	}

}