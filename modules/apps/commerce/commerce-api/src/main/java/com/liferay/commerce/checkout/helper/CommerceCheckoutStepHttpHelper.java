/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.helper;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jan Brychta
 */
@ProviderType
public interface CommerceCheckoutStepHttpHelper {

	public String getOrderDetailURL(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

	public boolean isActiveBillingAddressCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

	public boolean isActiveDeliveryTermCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder,
			String languageId)
		throws PortalException;

	public boolean isActivePaymentMethodCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

	public boolean isActivePaymentTermCommerceCheckoutStep(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			String languageId)
		throws PortalException;

	public boolean isActiveShippingMethodCommerceCheckoutStep(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean isCommercePaymentComplete(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException;

}