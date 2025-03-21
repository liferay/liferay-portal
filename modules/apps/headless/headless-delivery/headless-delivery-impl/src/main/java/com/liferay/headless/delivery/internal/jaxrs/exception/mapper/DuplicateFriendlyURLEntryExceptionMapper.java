/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code DuplicateFriendlyURLEntryException} to a {@code 422}
 * error.
 *
 * @author Alejandro Hernández
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.DuplicateFriendlyURLEntryExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateFriendlyURLEntryExceptionMapper
	extends BaseExceptionMapper<DuplicateFriendlyURLEntryException> {

	@Override
	protected Problem getProblem(
		DuplicateFriendlyURLEntryException duplicateFriendlyURLEntryException) {

		return new Problem(
			Response.Status.CONFLICT, "The friendly URL already exists.");
	}

}