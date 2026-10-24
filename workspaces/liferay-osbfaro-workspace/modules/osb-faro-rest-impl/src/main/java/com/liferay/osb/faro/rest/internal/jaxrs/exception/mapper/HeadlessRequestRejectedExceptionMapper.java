/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.engine.client.exception.HeadlessRequestRejectedException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.HeadlessRequestRejectedExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class HeadlessRequestRejectedExceptionMapper
	extends BaseExceptionMapper<HeadlessRequestRejectedException> {

	@Override
	public Response toResponse(
		HeadlessRequestRejectedException headlessRequestRejectedException) {

		Response response = super.toResponse(headlessRequestRejectedException);

		Long retryAfterSeconds =
			headlessRequestRejectedException.getRetryAfterSeconds();

		if ((retryAfterSeconds == null) ||
			(response.getStatus() !=
				Response.Status.SERVICE_UNAVAILABLE.getStatusCode())) {

			return response;
		}

		Response.ResponseBuilder responseBuilder = Response.fromResponse(
			response);

		responseBuilder.header(HttpHeaders.RETRY_AFTER, retryAfterSeconds);

		return responseBuilder.build();
	}

	@Override
	protected Problem getProblem(
		HeadlessRequestRejectedException headlessRequestRejectedException) {

		String messageKey = headlessRequestRejectedException.getMessageKey();

		if (Objects.equals(messageKey, "date-range-limit-exceeded")) {
			return new Problem(
				Response.Status.BAD_REQUEST,
				_getLimitTitle(
					"Date range is too large",
					headlessRequestRejectedException.getLimit(),
					"Date range must be at most %d days"));
		}

		if (Objects.equals(messageKey, "page-size-limit-exceeded")) {
			return new Problem(
				Response.Status.BAD_REQUEST,
				_getLimitTitle(
					"Page size is too large",
					headlessRequestRejectedException.getLimit(),
					"Page size must be at most %d"));
		}

		if (Objects.equals(messageKey, "query-in-progress")) {
			return new Problem(
				Response.Status.SERVICE_UNAVAILABLE,
				_getRetryTitle(
					headlessRequestRejectedException.getRetryAfterSeconds(),
					"Query is still running, repeat the same request with " +
						"the same parameters %s"));
		}

		if (Objects.equals(messageKey, "query-timeout-exceeded") ||
			Objects.equals(messageKey, "request-deadline-exceeded")) {

			return new Problem(
				Response.Status.GATEWAY_TIMEOUT,
				"Request took too long, narrow it with a shorter date range " +
					"or a smaller page size");
		}

		return new Problem(
			Response.Status.SERVICE_UNAVAILABLE,
			_getRetryTitle(
				headlessRequestRejectedException.getRetryAfterSeconds(),
				"Unable to process the request right now, retry %s"));
	}

	private String _getLimitTitle(
		String defaultTitle, Long limit, String limitTitle) {

		if (limit == null) {
			return defaultTitle;
		}

		return String.format(limitTitle, limit);
	}

	private String _getRetryTitle(Long retryAfterSeconds, String title) {
		if (retryAfterSeconds == null) {
			return String.format(title, "later");
		}

		return String.format(title, "after " + retryAfterSeconds + " seconds");
	}

}