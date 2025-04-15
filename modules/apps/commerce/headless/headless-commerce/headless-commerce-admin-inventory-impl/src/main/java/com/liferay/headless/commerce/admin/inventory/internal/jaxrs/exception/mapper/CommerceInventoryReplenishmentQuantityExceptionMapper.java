/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.jaxrs.exception.mapper;

import com.liferay.commerce.inventory.exception.CommerceInventoryReplenishmentQuantityException;
import com.liferay.headless.commerce.core.exception.mapper.BaseExceptionMapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Inventory)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Inventory.CommerceInventoryReplenishmentQuantityExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class CommerceInventoryReplenishmentQuantityExceptionMapper
	extends BaseExceptionMapper
		<CommerceInventoryReplenishmentQuantityException> {

	@Override
	public String getErrorDescription() {
		return "Invalid quantity";
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

}