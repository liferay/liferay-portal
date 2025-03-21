/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.data.fetcher;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContext;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContextValidator;
import com.liferay.portal.vulcan.internal.graphql.data.processor.LiferayMethodDataFetchingProcessor;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;

/**
 * @author Carlos Correa
 */
public class LiferayMethodDataFetcher extends BaseDataFetcher {

	public LiferayMethodDataFetcher(
		GraphQLRequestContext graphQLRequestContext,
		ServiceTrackerList<GraphQLRequestContextValidator>
			graphQLRequestContextValidators,
		LiferayMethodDataFetchingProcessor liferayMethodDataFetchingProcessor,
		Method method) {

		super(graphQLRequestContext, graphQLRequestContextValidators);

		_liferayMethodDataFetchingProcessor =
			liferayMethodDataFetchingProcessor;
		_method = method;
	}

	@Override
	public Object get(
			DataFetchingEnvironment dataFetchingEnvironment,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		GraphQLFieldDefinition graphQLFieldDefinition =
			dataFetchingEnvironment.getFieldDefinition();

		return _liferayMethodDataFetchingProcessor.process(
			dataFetchingEnvironment.getArguments(),
			graphQLFieldDefinition.getName(), httpServletRequest,
			httpServletResponse, _method, dataFetchingEnvironment.getRoot(),
			dataFetchingEnvironment.getSource());
	}

	private final LiferayMethodDataFetchingProcessor
		_liferayMethodDataFetchingProcessor;
	private final Method _method;

}