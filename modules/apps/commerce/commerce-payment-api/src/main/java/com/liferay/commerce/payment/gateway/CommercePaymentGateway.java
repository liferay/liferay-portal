/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.gateway;

import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luca Pellizzon
 */
public interface CommercePaymentGateway {

	public CommercePaymentEntry authorize(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	public CommercePaymentEntry cancel(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	public CommercePaymentEntry capture(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	public CommercePaymentEntry setUpPayment(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

}