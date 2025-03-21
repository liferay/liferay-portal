/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.display.context.helper;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceWishListRequestHelper extends BaseRequestHelper {

	public CommerceWishListRequestHelper(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

	public CommerceContext getCommerceContext() {
		HttpServletRequest httpServletRequest = getRequest();

		return (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
	}

	public HttpServletResponse getResponseHttpServletResponse() {
		return PortalUtil.getHttpServletResponse(getLiferayPortletResponse());
	}

}