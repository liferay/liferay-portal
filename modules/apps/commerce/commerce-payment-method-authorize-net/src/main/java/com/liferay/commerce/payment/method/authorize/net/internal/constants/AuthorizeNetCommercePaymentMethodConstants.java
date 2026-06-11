/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.constants;

import com.liferay.portal.kernel.util.StringUtil;

import net.authorize.Environment;

/**
 * @author Luca Pellizzon
 */
public class AuthorizeNetCommercePaymentMethodConstants {

	public static final String AUTH_CAPTURE_CREATED_EVENT_TYPE =
		"net.authorize.payment.authcapture.created";

	public static final String COMPLETE_PAYMENT_SERVLET_PATH =
		"complete-authorizenet-payment";

	public static final String[] ENVIRONMENTS = {
		StringUtil.toLowerCase(Environment.PRODUCTION.name()),
		StringUtil.toLowerCase(Environment.SANDBOX.name())
	};

	public static final String PRODUCTION_REDIRECT_URL =
		"https://accept.authorize.net/payment/payment";

	public static final String SANDBOX_REDIRECT_URL =
		"https://test.authorize.net/payment/payment";

	public static final String SERVICE_NAME =
		"com.liferay.commerce.payment.method.authorize.net";

	public static final String START_PAYMENT_SERVLET_PATH =
		"start-authorizenet-payment";

}