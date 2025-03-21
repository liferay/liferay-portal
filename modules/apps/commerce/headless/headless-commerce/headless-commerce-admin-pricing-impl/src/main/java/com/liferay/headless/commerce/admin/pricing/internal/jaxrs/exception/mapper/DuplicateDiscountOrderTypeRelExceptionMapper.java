/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.jaxrs.exception.mapper;

import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountOrderTypeRelException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing.DuplicateDiscountOrderTypeRelExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateDiscountOrderTypeRelExceptionMapper
	extends BaseExceptionMapper
		<DuplicateCommerceDiscountOrderTypeRelException> {

	@Override
	protected Problem getProblem(
		DuplicateCommerceDiscountOrderTypeRelException
			duplicateCommerceDiscountOrderTypeRelException) {

		return new Problem(
			Response.Status.CONFLICT,
			"The order type relation already exists.");
	}

}