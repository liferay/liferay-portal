/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.servlet.filter;

import com.liferay.commerce.payment.method.mercanet.internal.constants.MercanetCommercePaymentMethodConstants;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/" + MercanetCommercePaymentMethodConstants.SERVLET_PATH + "/*",
		"osgi.http.whiteboard.filter.name=com.liferay.commerce.payment.method.mercanet.internal.servlet.filter.CommercePaymentMethodMercanetAuthVerifierFilter",
		"osgi.http.whiteboard.servlet.pattern=/" + MercanetCommercePaymentMethodConstants.SERVLET_PATH + "/*"
	},
	service = Filter.class
)
public class CommercePaymentMethodMercanetAuthVerifierFilter
	extends AuthVerifierFilter {
}