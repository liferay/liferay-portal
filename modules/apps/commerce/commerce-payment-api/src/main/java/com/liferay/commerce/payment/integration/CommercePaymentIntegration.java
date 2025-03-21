/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.integration;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Luca Pellizzon
 */
public interface CommercePaymentIntegration {

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

	public String getDescription(Locale locale);

	public String getKey();

	public String getName(Locale locale);

	public int getPaymentIntegrationType();

	public default UnicodeProperties getPaymentIntegrationTypeSettings() {
		return null;
	}

	public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	public default CommercePaymentEntry setUpPayment(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_CREATED);

		return commercePaymentEntry;
	}

}