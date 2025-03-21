/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.jaxrs.exception.mapper;

import com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author João Cordeiro
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Catalog.DuplicateProductAttachmentExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class DuplicateProductAttachmentExceptionMapper
	extends BaseExceptionMapper<DuplicateCPAttachmentFileEntryException> {

	@Override
	protected Problem getProblem(
		DuplicateCPAttachmentFileEntryException
			duplicateCPAttachmentFileEntryException) {

		return new Problem(
			Response.Status.CONFLICT,
			duplicateCPAttachmentFileEntryException.getMessage());
	}

}