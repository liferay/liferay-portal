/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.servlet.filter;

import com.liferay.commerce.payment.method.authorize.net.internal.constants.AuthorizeNetCommercePaymentMethodConstants;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/" + AuthorizeNetCommercePaymentMethodConstants.COMPLETE_PAYMENT_SERVLET_PATH + "/*",
		"osgi.http.whiteboard.filter.name=com.liferay.commerce.payment.method.authorize.net.internal.servlet.filter.StartPaymentAuthorizeNetAuthVerifierFilter",
		"osgi.http.whiteboard.servlet.pattern=/" + AuthorizeNetCommercePaymentMethodConstants.COMPLETE_PAYMENT_SERVLET_PATH + "/*"
	},
	service = Filter.class
)
public class CompletePaymentAuthorizeNetAuthVerifierFilter
	extends AuthVerifierFilter {
}