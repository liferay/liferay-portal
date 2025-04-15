/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.request;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Luca Pellizzon
 */
public interface CommercePaymentRequestProvider {

	public CommercePaymentRequest getCommercePaymentRequest(
			String cancelUrl, long commerceOrderId,
			HttpServletRequest httpServletRequest, Locale locale,
			String returnUrl, String transactionId)
		throws PortalException;

}