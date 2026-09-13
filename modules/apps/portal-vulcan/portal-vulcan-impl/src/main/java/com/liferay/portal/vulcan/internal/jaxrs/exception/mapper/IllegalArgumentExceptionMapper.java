/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;

/**
 * Converts any {@code IllegalArgumentException} to a {@code 400} error, unless
 * a {@code NoSuchModelException} caused it, in which case the cause is
 * converted to a {@code 404} error.
 *
 * @author Rubén Pulido
 * @review
 */
public class IllegalArgumentExceptionMapper
	extends BaseExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(
		IllegalArgumentException illegalArgumentException) {

		Throwable throwable = illegalArgumentException.getCause();

		if (throwable instanceof NoSuchModelException) {
			ExceptionMapper<NoSuchModelException> exceptionMapper =
				_providers.getExceptionMapper(NoSuchModelException.class);

			return exceptionMapper.toResponse((NoSuchModelException)throwable);
		}

		return super.toResponse(illegalArgumentException);
	}

	@Override
	protected Problem getProblem(
		IllegalArgumentException illegalArgumentException) {

		return new Problem(illegalArgumentException);
	}

	@Context
	private Providers _providers;

}