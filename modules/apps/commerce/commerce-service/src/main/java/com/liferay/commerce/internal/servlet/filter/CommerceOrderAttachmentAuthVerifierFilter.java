/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.servlet.filter;

import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Balazs Breier
 */
@Component(
	property = {
		"filter.init.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/commerce-order-attachment/*",
		"filter.init.auth.verifier.OAuth2RESTAuthVerifier.urls.includes=/commerce-order-attachment/*",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/commerce-order-attachment/*",
		"osgi.http.whiteboard.filter.name=com.liferay.commerce.internal.servlet.filter.CommerceOrderAttachmentAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/commerce-order-attachment/*"
	},
	service = Filter.class
)
public class CommerceOrderAttachmentAuthVerifierFilter
	extends AuthVerifierFilter {
}