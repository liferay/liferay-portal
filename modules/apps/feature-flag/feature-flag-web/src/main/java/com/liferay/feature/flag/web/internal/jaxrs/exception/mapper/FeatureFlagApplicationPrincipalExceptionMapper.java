/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.security.auth.PrincipalException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gabriel Lima
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=com.liferay.feature.flag.web.internal.jaxrs.application.FeatureFlagApplication)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=FeatureFlagApplicationPrincipalExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class FeatureFlagApplicationPrincipalExceptionMapper
	implements ExceptionMapper<PrincipalException> {

	@Override
	public Response toResponse(PrincipalException principalException) {
		return Response.status(
			Response.Status.FORBIDDEN
		).build();
	}

}