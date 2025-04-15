/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.engine;

import com.liferay.commerce.payment.result.CommercePaymentResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luca Pellizzon
 */
public interface CommerceSubscriptionEngine {

	public boolean activateRecurringDelivery(long commerceSubscriptionEntryId)
		throws Exception;

	public boolean activateRecurringPayment(long commerceSubscriptionEntryId)
		throws Exception;

	public boolean cancelRecurringDelivery(long commerceSubscriptionEntryId)
		throws Exception;

	public boolean cancelRecurringPayment(long commerceSubscriptionEntryId)
		throws Exception;

	public CommercePaymentResult completeRecurringPayment(
			long commerceOrderId, String transactionId,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public boolean getSubscriptionValidity(long commerceOrderId)
		throws Exception;

	public CommercePaymentResult processRecurringPayment(
			long commerceOrderId, String checkoutStepUrl,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public boolean suspendRecurringDelivery(long commerceSubscriptionEntryId)
		throws Exception;

	public boolean suspendRecurringPayment(long commerceSubscriptionEntryId)
		throws Exception;

}