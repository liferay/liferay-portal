/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.jaxrs.exception.mapper;

import com.liferay.commerce.payment.exception.CommercePaymentEntryPaymentIntegrationTypeException;
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
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Payment)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Payment.PaymentPaymentIntegrationTypeExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class PaymentPaymentIntegrationTypeExceptionMapper
	extends BaseExceptionMapper
		<CommercePaymentEntryPaymentIntegrationTypeException> {

	@Override
	public String getErrorDescription() {
		return "Payment payment integration type is invalid";
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

}