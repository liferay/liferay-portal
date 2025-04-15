/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet.filter;

import com.liferay.portal.search.elasticsearch.monitoring.web.internal.constants.MonitoringWebConstants;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(
	enabled = false,
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/" + MonitoringWebConstants.SERVLET_PATH + "/*",
		"osgi.http.whiteboard.filter.name=com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet.filter.MonitoringProxyAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/" + MonitoringWebConstants.SERVLET_PATH + "/*"
	},
	service = Filter.class
)
public class MonitoringProxyAuthVerifierFilter extends AuthVerifierFilter {
}