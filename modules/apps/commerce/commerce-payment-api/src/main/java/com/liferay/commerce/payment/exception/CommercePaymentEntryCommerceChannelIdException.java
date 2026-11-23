/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Stefano Motta
 */
public class CommercePaymentEntryCommerceChannelIdException
	extends PortalException {

	public CommercePaymentEntryCommerceChannelIdException() {
	}

	public CommercePaymentEntryCommerceChannelIdException(String msg) {
		super(msg);
	}

	public CommercePaymentEntryCommerceChannelIdException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public CommercePaymentEntryCommerceChannelIdException(Throwable throwable) {
		super(throwable);
	}

}