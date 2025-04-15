/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * @author Marco Leo
 * @author Andrea Di Giorgi
 */
public interface CommerceOrderHttpHelper {

	public CommerceOrder addCommerceOrder(HttpServletRequest httpServletRequest)
		throws PortalException;

	public void deleteCommerceOrder(
			ActionRequest actionRequest, long commerceOrderId)
		throws PortalException;

	public CommerceOrder fetchCommerceOrderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	public String getCommerceCartBaseURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getCommerceCartPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getCommerceCartPortletURL(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

	public String getCommerceCartPortletURL(
			long groupId, HttpServletRequest httpServletRequest,
			CommerceOrder commerceOrder)
		throws PortalException;

	public PortletURL getCommerceCheckoutPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public BigDecimal getCommerceOrderItemsQuantity(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getCookieName(long commerceChannelId);

	public CommerceOrder getCurrentCommerceOrder(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean hasCommerceOrderPortlet(
			HttpServletRequest httpServletRequest, String portletKey)
		throws PortalException;

	public boolean isGuestCheckoutEnabled(HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean isMultishippingEnabled(
		HttpServletRequest httpServletRequest);

	public void setCurrentCommerceOrder(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

}