/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.jaxrs.exception.mapper;

import com.liferay.headless.commerce.core.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.odata.filter.InvalidFilterException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing.InvalidFilterExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class InvalidFilterExceptionMapper
	extends BaseExceptionMapper<InvalidFilterException> {

	@Override
	public String getErrorDescription() {
		return "Filters do not support such field";
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

}