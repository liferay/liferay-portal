/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.jaxrs.exception.mapper;

import com.liferay.commerce.product.exception.CPDisplayLayoutEntryUuidException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Channel)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Channel.DisplayPageUuidExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class DisplayPageUuidExceptionMapper
	extends BaseExceptionMapper<CPDisplayLayoutEntryUuidException> {

	@Override
	protected Problem getProblem(
		CPDisplayLayoutEntryUuidException cpDisplayLayoutEntryUuidException) {

		return new Problem(
			Response.Status.BAD_REQUEST, "The display page UUID is invalid");
	}

}