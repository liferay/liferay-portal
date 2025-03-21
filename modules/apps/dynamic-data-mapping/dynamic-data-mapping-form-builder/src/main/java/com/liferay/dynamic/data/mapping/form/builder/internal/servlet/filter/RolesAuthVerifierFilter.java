/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet.filter;

import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"filter.init.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=/dynamic-data-mapping-form-builder-roles/*",
		"osgi.http.whiteboard.filter.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.filter.FieldSettingsDDMFormContextAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/dynamic-data-mapping-form-builder-roles/*"
	},
	service = Filter.class
)
public class RolesAuthVerifierFilter extends AuthVerifierFilter {
}