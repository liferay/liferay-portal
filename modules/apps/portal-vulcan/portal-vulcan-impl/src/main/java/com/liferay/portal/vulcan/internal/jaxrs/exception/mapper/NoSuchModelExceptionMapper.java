/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;

/**
 * Converts any {@code NoSuchModelException} to a {@code 404} error.
 *
 *
 * @author Alejandro Hernández
 * @review
 */
public class NoSuchModelExceptionMapper
	extends BaseExceptionMapper<NoSuchModelException> {

	@Override
	public Response toResponse(NoSuchModelException noSuchModelException) {
		ExceptionMapper<NotFoundException> exceptionMapper =
			_providers.getExceptionMapper(NotFoundException.class);

		return exceptionMapper.toResponse(
			new NotFoundException(noSuchModelException));
	}

	@Override
	protected Problem getProblem(NoSuchModelException noSuchModelException) {
		throw new UnsupportedOperationException("This should not be called");
	}

	@Context
	private Providers _providers;

}