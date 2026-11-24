/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.request;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.request.CommercePaymentRequestProvider;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Luca Pellizzon
 */
public class DefaultCommercePaymentRequestProvider
	implements CommercePaymentRequestProvider {

	public DefaultCommercePaymentRequestProvider(
		CommerceOrderLocalService commerceOrderLocalService) {

		_commerceOrderLocalService = commerceOrderLocalService;
	}

	@Override
	public CommercePaymentRequest getCommercePaymentRequest(
			String cancelUrl, long commerceOrderId,
			HttpServletRequest httpServletRequest, Locale locale,
			String returnUrl, String transactionId)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		return new CommercePaymentRequest(
			commerceOrder.getTotal(), cancelUrl, commerceOrderId, locale,
			returnUrl, transactionId);
	}

	private final CommerceOrderLocalService _commerceOrderLocalService;

}