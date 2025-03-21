/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.jaxrs.exception.mapper;

import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.discount.exception.CommerceDiscountMaxPriceValueException;
import com.liferay.headless.commerce.core.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Chiappa
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing.DiscountMaxPriceValueExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class DiscountMaxPriceValueExceptionMapper
	extends BaseExceptionMapper<CommerceDiscountMaxPriceValueException> {

	@Override
	public String getErrorDescription() {
		return _language.format(
			LocaleUtil.US, "price-max-value-is-x",
			CommercePriceConstants.PRICE_VALUE_MAX);
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

	@Reference
	private Language _language;

}