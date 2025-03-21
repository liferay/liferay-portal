/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.theme.contributor.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.theme.contributor.internal.util.ProductNavigationControlMenuUtil;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuCategoryRegistry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuEntryRegistry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class ProductNavigationControlMenuServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setShowControlMenu(
			ProductNavigationControlMenuUtil.isShowControlMenu(
				httpServletRequest,
				_productNavigationControlMenuCategoryRegistry,
				_productNavigationControlMenuEntryRegistry));
	}

	@Reference
	private ProductNavigationControlMenuCategoryRegistry
		_productNavigationControlMenuCategoryRegistry;

	@Reference
	private ProductNavigationControlMenuEntryRegistry
		_productNavigationControlMenuEntryRegistry;

}