/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.util;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.model.CommerceOrder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alec Sloan
 */
@ProviderType
public interface CommercePaymentHttpHelper {

	public CommerceOrder getCommerceOrder(HttpServletRequest httpServletRequest)
		throws Exception;

}