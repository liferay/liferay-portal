/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.jaxrs.exception.mapper;

import com.liferay.commerce.term.exception.CommerceTermEntryPriorityException;
import com.liferay.headless.commerce.core.exception.mapper.BaseExceptionMapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Order)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Order.TermPriorityExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class TermPriorityExceptionMapper
	extends BaseExceptionMapper<CommerceTermEntryPriorityException> {

	@Override
	public String getErrorDescription() {
		return "Invalid term priority";
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

	@Override
	protected String toJSON(
		CommerceTermEntryPriorityException commerceTermEntryPriorityException,
		int status) {

		return super.toJSON("please-enter-a-valid-priority", status);
	}

}