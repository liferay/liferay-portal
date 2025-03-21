/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.internal.servlet.filter;

import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"filter.init.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/" + CommerceMediaConstants.SERVLET_PATH + "/*",
		"filter.init.auth.verifier.OAuth2RESTAuthVerifier.urls.includes=/" + CommerceMediaConstants.SERVLET_PATH + "/*",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/" + CommerceMediaConstants.SERVLET_PATH + "/*",
		"osgi.http.whiteboard.filter.name=com.liferay.commerce.media.servlet.CommerceMediaAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/" + CommerceMediaConstants.SERVLET_PATH + "/*"
	},
	service = Filter.class
)
public class CommerceMediaAuthVerifierFilter extends AuthVerifierFilter {
}