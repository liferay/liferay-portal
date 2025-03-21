/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.checkout.web.internal.display.context.helper.CommerceCheckoutRequestHelper;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
public class PaymentProcessCheckoutStepDisplayContext {

	public PaymentProcessCheckoutStepDisplayContext(
		CartResource.Factory cartResourceFactory,
		HttpServletRequest httpServletRequest) {

		_cartResourceFactory = cartResourceFactory;

		_commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		_commerceCheckoutRequestHelper = new CommerceCheckoutRequestHelper(
			httpServletRequest);
	}

	public String getPaymentURL() throws Exception {
		CartResource.Builder cartResourceBuilder =
			_cartResourceFactory.create();

		CartResource cartResource = cartResourceBuilder.httpServletRequest(
			_commerceCheckoutRequestHelper.getRequest()
		).preferredLocale(
			_commerceCheckoutRequestHelper.getLocale()
		).user(
			_commerceCheckoutRequestHelper.getUser()
		).build();

		return cartResource.getCartPaymentURL(
			_commerceOrder.getCommerceOrderId(), StringPool.BLANK);
	}

	private final CartResource.Factory _cartResourceFactory;
	private final CommerceCheckoutRequestHelper _commerceCheckoutRequestHelper;
	private final CommerceOrder _commerceOrder;

}