/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.jaxrs.exception.mapper;

import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.headless.commerce.core.exception.mapper.BaseExceptionMapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Cart)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Cart.OrderShippingAddressExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class OrderShippingAddressExceptionMapper
	extends BaseExceptionMapper<CommerceOrderShippingAddressException> {

	@Override
	public String getErrorDescription() {
		return "Invalid order shipping address";
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

}