/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.servlet.filter;

import com.liferay.adaptive.media.web.internal.constants.AMWebConstants;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/" + AMWebConstants.SERVLET_PATH + "/*",
		"osgi.http.whiteboard.filter.name=com.liferay.adaptive.media.web.internal.servlet.filter.AMAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/" + AMWebConstants.SERVLET_PATH + "/*"
	},
	service = Filter.class
)
public class AMAuthVerifierFilter extends AuthVerifierFilter {
}