/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.manager;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mikel Lorza
 */
public interface ProductNavigationControlMenuManager {

	public boolean isShowControlMenu(HttpServletRequest httpServletRequest);

}