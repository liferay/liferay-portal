/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.util;

import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Andrea Di Giorgi
 */
public interface CommerceWishListHttpHelper {

	public PortletURL getCommerceWishListPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public CommerceWishList getCurrentCommerceWishList(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

	public int getCurrentCommerceWishListItemsCount(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

}