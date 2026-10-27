/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.jaxrs.exception.mapper;

import com.liferay.commerce.discount.exception.CommerceDiscountRelUnitOfMeasureKeyException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing.DiscountRelUnitOfMeasureKeyExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DiscountRelUnitOfMeasureKeyExceptionMapper
	extends BaseExceptionMapper<CommerceDiscountRelUnitOfMeasureKeyException> {

	@Override
	protected Problem getProblem(
		CommerceDiscountRelUnitOfMeasureKeyException
			commerceDiscountRelUnitOfMeasureKeyException) {

		return new Problem(commerceDiscountRelUnitOfMeasureKeyException);
	}

}