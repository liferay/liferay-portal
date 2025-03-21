/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.util;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.request.CommercePaymentRequestProvider;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Luca Pellizzon
 */
@ProviderType
public interface CommercePaymentHelper {

	public CommercePaymentResult emptyResult(
		long commerceOrderId, String transactionId);

	public CommercePaymentIntegration getCommercePaymentIntegration(
			long commerceChannelId, String paymentMethodKey)
		throws PortalException;

	public CommercePaymentMethod getCommercePaymentMethod(long commerceOrderId)
		throws PortalException;

	public CommercePaymentRequest getCommercePaymentRequest(
			CommerceOrder commerceOrder, Locale locale, String transactionId,
			String checkoutStepUrl, HttpServletRequest httpServletRequest,
			CommercePaymentMethod commercePaymentMethod)
		throws Exception;

	public CommercePaymentRequestProvider getCommercePaymentRequestProvider(
		CommercePaymentMethod commercePaymentMethod);

	public boolean isDeliveryOnlySubscription(CommerceOrder commerceOrder);

}