/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.rest.internal.graphql.client.GraphQLException;
import com.liferay.osb.faro.rest.internal.graphql.model.GraphQLError;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcos Martins
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.GraphQLExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class GraphQLExceptionMapper
	extends BaseExceptionMapper<GraphQLException> {

	@Override
	protected Problem getProblem(GraphQLException graphQLException) {
		List<GraphQLError> graphQLErrors = graphQLException.getGraphQLErrors();

		if (ListUtil.isEmpty(graphQLErrors)) {
			return _getInternalServerErrorProblem(graphQLException);
		}

		for (GraphQLError graphQLError : graphQLErrors) {
			Integer httpStatus = graphQLError.getHTTPStatus();

			if (httpStatus == null) {
				continue;
			}

			if (httpStatus == HttpServletResponse.SC_NOT_FOUND) {
				return new Problem(
					Response.Status.NOT_FOUND,
					Response.Status.NOT_FOUND.getReasonPhrase());
			}

			if (httpStatus == HttpServletResponse.SC_BAD_REQUEST) {
				if (_log.isDebugEnabled()) {
					_log.debug(graphQLException);
				}

				return new Problem(
					Response.Status.BAD_REQUEST,
					"One or more parameter values are invalid");
			}
		}

		for (GraphQLError graphQLError : graphQLErrors) {
			if (_isNotFound(graphQLError.getMessageKey())) {
				return new Problem(
					Response.Status.NOT_FOUND,
					Response.Status.NOT_FOUND.getReasonPhrase());
			}
		}

		for (GraphQLError graphQLError : graphQLErrors) {
			if (_clientErrorClassifications.contains(
					graphQLError.getClassification())) {

				if (_log.isDebugEnabled()) {
					_log.debug(graphQLException);
				}

				return new Problem(
					Response.Status.BAD_REQUEST,
					"One or more parameter values are invalid");
			}
		}

		return _getInternalServerErrorProblem(graphQLException);
	}

	private Problem _getInternalServerErrorProblem(
		GraphQLException graphQLException) {

		_log.error(graphQLException);

		return new Problem(
			Response.Status.INTERNAL_SERVER_ERROR,
			Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	private boolean _isNotFound(String messageKey) {
		if (messageKey == null) {
			return false;
		}

		if (messageKey.startsWith("no-") && messageKey.contains("-was-found")) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GraphQLExceptionMapper.class);

	private static final Set<String> _clientErrorClassifications =
		SetUtil.fromArray(
			"InvalidSyntax", "OperationNotSupported", "ValidationError");

}