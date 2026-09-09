/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;

/**
 * Converts any {@code IllegalArgumentException} to a {@code 400} error, unless
 * the permission checker raised it because the resource does not exist, in
 * which case the cause is mapped to a {@code 404} error.
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

		if (!(throwable instanceof NoSuchResourcePermissionException)) {
			return super.toResponse(illegalArgumentException);
		}

		ExceptionMapper<NoSuchResourcePermissionException> exceptionMapper =
			_providers.getExceptionMapper(
				NoSuchResourcePermissionException.class);

		return exceptionMapper.toResponse(
			(NoSuchResourcePermissionException)throwable);
	}

	@Override
	protected Problem getProblem(
		IllegalArgumentException illegalArgumentException) {

		return new Problem(illegalArgumentException);
	}

	@Context
	private Providers _providers;

}