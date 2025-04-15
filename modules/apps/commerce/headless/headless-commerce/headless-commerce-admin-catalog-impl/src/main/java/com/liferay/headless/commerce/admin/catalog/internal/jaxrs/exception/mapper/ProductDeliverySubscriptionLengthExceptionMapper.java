/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.jaxrs.exception.mapper;

import com.liferay.commerce.product.exception.CPDefinitionDeliverySubscriptionLengthException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog.ProductDeliverySubscriptionLengthExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ProductDeliverySubscriptionLengthExceptionMapper
	extends BaseExceptionMapper
		<CPDefinitionDeliverySubscriptionLengthException> {

	@Override
	protected Problem getProblem(
		CPDefinitionDeliverySubscriptionLengthException
			cpDefinitionDeliverySubscriptionLengthException) {

		return new Problem(cpDefinitionDeliverySubscriptionLengthException);
	}

}