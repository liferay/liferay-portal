/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.jaxrs.exception.mapper;

import com.liferay.commerce.product.exception.CommerceCatalogSystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balazs Breier
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog.CatalogSystemExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class CatalogSystemExceptionMapper
	extends BaseExceptionMapper<CommerceCatalogSystemException> {

	@Override
	protected Problem getProblem(
		CommerceCatalogSystemException commerceCatalogSystemException) {

		return new Problem(
			Response.Status.FORBIDDEN,
			_language.get(
				_acceptLanguage.getPreferredLocale(),
				"you-cannot-delete-master-catalog"));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}