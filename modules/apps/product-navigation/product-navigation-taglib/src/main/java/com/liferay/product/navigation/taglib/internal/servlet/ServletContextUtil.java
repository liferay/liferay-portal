/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.taglib.internal.servlet;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuCategoryRegistry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuEntryRegistry;

import jakarta.servlet.ServletContext;

/**
 * @author Julio Camarero
 */
public class ServletContextUtil {

	public static PanelAppRegistry getPanelAppRegistry() {
		return _panelAppRegistrySnapshot.get();
	}

	public static ProductNavigationControlMenuCategoryRegistry
		getProductNavigationControlMenuCategoryRegistry() {

		return _productNavigationControlMenuCategoryRegistrySnapshot.get();
	}

	public static ProductNavigationControlMenuEntryRegistry
		getProductNavigationControlMenuEntryRegistry() {

		return _productNavigationControlMenuEntryRegistrySnapshot.get();
	}

	public static ProductNavigationControlMenuManager
		getProductNavigationControlMenuManager() {

		return _productNavigationControlMenuManagerSnapshot.get();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<PanelAppRegistry> _panelAppRegistrySnapshot =
		new Snapshot<>(ServletContextUtil.class, PanelAppRegistry.class);
	private static final Snapshot<ProductNavigationControlMenuCategoryRegistry>
		_productNavigationControlMenuCategoryRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class,
			ProductNavigationControlMenuCategoryRegistry.class);
	private static final Snapshot<ProductNavigationControlMenuEntryRegistry>
		_productNavigationControlMenuEntryRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class,
			ProductNavigationControlMenuEntryRegistry.class);
	private static final Snapshot<ProductNavigationControlMenuManager>
		_productNavigationControlMenuManagerSnapshot = new Snapshot<>(
			ServletContextUtil.class,
			ProductNavigationControlMenuManager.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.product.navigation.taglib)");

}