/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.jaxrs.application.exception.mapper;

import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Order)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Order.OrderStatusExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class OrderStatusExceptionMapper
	extends BaseExceptionMapper<CommerceOrderStatusException> {

	@Override
	protected Problem getProblem(
		CommerceOrderStatusException commerceOrderStatusException) {

		return new Problem(
			Response.Status.BAD_REQUEST, "The order status is invalid");
	}

}