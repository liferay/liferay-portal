/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.request.CommercePaymentRequestProvider;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "commerce.payment.engine.method.key=" + AuthorizeNetCommercePaymentMethod.KEY,
	service = CommercePaymentRequestProvider.class
)
public class AuthorizeNetCommercePaymentRequestProvider
	implements CommercePaymentRequestProvider {

	@Override
	public CommercePaymentRequest getCommercePaymentRequest(
			String cancelUrl, long commerceOrderId,
			HttpServletRequest httpServletRequest, Locale locale,
			String returnUrl, String transactionId)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		return new AuthorizeNetCommercePaymentRequest(
			commerceOrder.getTotal(), cancelUrl, commerceOrderId, locale,
			httpServletRequest, returnUrl, transactionId);
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

}