/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides an interface that defines categories to be used by a
 * <code>product-navigation:control-menu</code> tag instance to render a new
 * Control Menu category. Control Menu categories include Control Menu entries
 * defined by {@link ProductNavigationControlMenuEntry} implementations.
 *
 * <p>
 * Implementations must be registered in the OSGi Registry. The order of the
 * Control Menu categories is determined by the
 * <code>product.navigation.control.menu.category.order</code> property value.
 * The parent Control Menu category key can be defined by the
 * <code>product.navigation.control.menu.category.key</code> property value.
 * </p>
 *
 * @author Julio Camarero
 */
public interface ProductNavigationControlMenuCategory {

	/**
	 * Returns the Control Menu category's key. This key must be unique in the
	 * scope of the Control Menu, since it is referred to by Control Menu
	 * entries to be added to this Control Menu category.
	 *
	 * @return the Control Menu category's key
	 */
	public String getKey();

	/**
	 * Returns <code>true</code> if the Control Menu category should be
	 * displayed in the request's context.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         category
	 * @return <code>true</code> if the Control Menu category should be
	 *         displayed in the request's context; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public boolean hasAccessPermission(HttpServletRequest httpServletRequest)
		throws PortalException;

}