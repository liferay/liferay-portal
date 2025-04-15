/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.data.fetcher;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContext;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContextValidator;

import graphql.GraphQLContext;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Carlos Correa
 */
public abstract class BaseDataFetcher implements DataFetcher<Object> {

	public BaseDataFetcher(
		GraphQLRequestContext graphQLRequestContext,
		ServiceTrackerList<GraphQLRequestContextValidator>
			graphQLRequestContextValidators) {

		_graphQLRequestContext = graphQLRequestContext;
		_graphQLRequestContextValidators = graphQLRequestContextValidators;
	}

	@Override
	public final Object get(DataFetchingEnvironment dataFetchingEnvironment)
		throws Exception {

		try {
			for (GraphQLRequestContextValidator graphQLRequestContextValidator :
					_graphQLRequestContextValidators) {

				graphQLRequestContextValidator.validate(_graphQLRequestContext);
			}

			return get(
				dataFetchingEnvironment,
				_getHttpServletRequest(dataFetchingEnvironment),
				_getHttpServletResponse(dataFetchingEnvironment));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw exception;
		}
	}

	public abstract Object get(
			DataFetchingEnvironment dataFetchingEnvironment,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	private HttpServletRequest _getHttpServletRequest(
		DataFetchingEnvironment dataFetchingEnvironment) {

		GraphQLContext graphQLContext =
			dataFetchingEnvironment.getGraphQlContext();

		return graphQLContext.get(HttpServletRequest.class);
	}

	private HttpServletResponse _getHttpServletResponse(
		DataFetchingEnvironment dataFetchingEnvironment) {

		GraphQLContext graphQLContext =
			dataFetchingEnvironment.getGraphQlContext();

		return graphQLContext.get(HttpServletResponse.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseDataFetcher.class);

	private final GraphQLRequestContext _graphQLRequestContext;
	private final ServiceTrackerList<GraphQLRequestContextValidator>
		_graphQLRequestContextValidators;

}