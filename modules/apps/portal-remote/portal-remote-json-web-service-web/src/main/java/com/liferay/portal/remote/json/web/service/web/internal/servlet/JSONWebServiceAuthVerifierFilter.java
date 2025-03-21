/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.servlet;

import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/portal/api/jsonws/*",
		"osgi.http.whiteboard.filter.name=com.liferay.portal.remote.json.web.service.extender.internal.servlet.JSONWebServiceAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/portal/api/jsonws/*"
	},
	service = Filter.class
)
public class JSONWebServiceAuthVerifierFilter extends AuthVerifierFilter {
}