/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.portal.vulcan.problem.ProblemProvider;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Providers;

import java.util.Locale;

/**
 * @author Javier Gamarra
 */
public class ExceptionMapper extends BaseExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		Class<? extends Exception> exceptionClass = exception.getClass();

		String exceptionClassName = exceptionClass.getSimpleName();

		if (exceptionClassName.startsWith("Duplicate") &&
			exceptionClassName.endsWith("ExternalReferenceCodeException")) {

			return Response.status(
				Response.Status.BAD_REQUEST
			).entity(
				exception.getMessage()
			).type(
				getMediaType()
			).build();
		}

		Throwable throwable = exception.getCause();

		if (throwable == null) {
			return super.toResponse(exception);
		}

		jakarta.ws.rs.ext.ExceptionMapper<Throwable> exceptionMapper =
			_providers.getExceptionMapper(
				(Class<Throwable>)throwable.getClass());

		if (exceptionMapper != null) {
			return exceptionMapper.toResponse(throwable);
		}

		return super.toResponse(exception);
	}

	@Override
	protected Problem getProblem(Exception exception) {
		ProblemProvider problemProvider =
			_problemProviderRegistrySnapshot.get();

		com.liferay.portal.vulcan.problem.Problem problem =
			problemProvider.getProblem(exception);

		if (problem != null) {
			Locale locale = _acceptLanguage.getPreferredLocale();

			return new Problem(
				problem.getDetail(locale),
				_getResponseStatus(problem.getStatus()),
				problem.getTitle(locale), problem.getType());
		}

		_log.error(exception);

		return new Problem(
			Response.Status.INTERNAL_SERVER_ERROR,
			Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	private Response.Status _getResponseStatus(
		com.liferay.portal.vulcan.problem.Problem.Status status) {

		if (com.liferay.portal.vulcan.problem.Problem.Status.BAD_REQUEST.equals(
				status)) {

			return Response.Status.BAD_REQUEST;
		}

		return Response.Status.INTERNAL_SERVER_ERROR;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExceptionMapper.class);

	private static final Snapshot<ProblemProvider>
		_problemProviderRegistrySnapshot = new Snapshot<>(
			ExceptionMapper.class, ProblemProvider.class);

	@Context
	private AcceptLanguage _acceptLanguage;

	@Context
	private Providers _providers;

}