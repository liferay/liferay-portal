/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.servlet.filter;

import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/friendly-url/*",
		"osgi.http.whiteboard.filter.name=com.liferay.adaptive.media.web.internal.servlet.filter.AMAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/friendly-url/*"
	},
	service = Filter.class
)
public class FriendlyURLAuthVerifierFilter extends AuthVerifierFilter {
}