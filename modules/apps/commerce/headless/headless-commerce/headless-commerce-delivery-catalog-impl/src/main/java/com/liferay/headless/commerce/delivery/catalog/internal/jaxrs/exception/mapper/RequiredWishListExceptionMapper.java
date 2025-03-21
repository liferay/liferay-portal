/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.jaxrs.exception.mapper;

import com.liferay.commerce.wish.list.exception.RequiredCommerceWishListException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Catalog)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Delivery.Catalog.RequiredWishListExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class RequiredWishListExceptionMapper
	extends BaseExceptionMapper<RequiredCommerceWishListException> {

	@Override
	protected Problem getProblem(
		RequiredCommerceWishListException requiredCommerceWishListException) {

		return new Problem(
			Response.Status.BAD_REQUEST, "Unable to delete required wish list");
	}

}