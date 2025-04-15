/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal;

import com.liferay.commerce.payment.request.CommercePaymentRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Luca Pellizzon
 */
public class MercanetCommercePaymentRequest extends CommercePaymentRequest {

	public MercanetCommercePaymentRequest(
		BigDecimal amount, String cancelUrl, long commerceOrderId,
		Locale locale, HttpServletRequest httpServletRequest, String returnUrl,
		String transactionId) {

		super(
			amount, cancelUrl, commerceOrderId, locale, returnUrl,
			transactionId);

		_httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	private final HttpServletRequest _httpServletRequest;

}