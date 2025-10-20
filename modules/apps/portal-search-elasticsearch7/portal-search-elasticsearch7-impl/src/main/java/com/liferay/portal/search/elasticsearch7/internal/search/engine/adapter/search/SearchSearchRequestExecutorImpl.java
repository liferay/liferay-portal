/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = SearchSearchRequestExecutor.class)
public class SearchSearchRequestExecutorImpl
	implements SearchSearchRequestExecutor {

	@Override
	public SearchSearchResponse execute(
		SearchSearchRequest searchSearchRequest) {

		SearchRequest searchRequest = new SearchRequest();

		if (searchSearchRequest.getPointInTime() == null) {
			searchRequest.indices(searchSearchRequest.getIndexNames());
		}

		if (searchSearchRequest.isRequestCache()) {
			searchRequest.requestCache(searchSearchRequest.isRequestCache());
		}

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_searchSearchRequestAssembler.assemble(
			searchSourceBuilder, searchSearchRequest, searchRequest);

		String indexNames = ArrayUtil.toString(
			searchSearchRequest.getIndexNames(), "");

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Stack trace for [", indexNames, "]: ",
					DebugStringsUtil.getStackTraceString()));
		}

		String searchRequestString = DebugStringsUtil.getSearchRequestString(
			searchSourceBuilder);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Search request string for [", indexNames, "]: ",
					searchRequestString));
		}

		SearchResponse searchResponse = null;

		if (searchSearchRequest.getScrollId() != null) {
			searchResponse = _getScrollSearchResponse(searchSearchRequest);
		}
		else {
			searchResponse = _getSearchResponse(
				searchRequest, searchSearchRequest);
		}

		SearchSearchResponse searchSearchResponse = new SearchSearchResponse();

		_searchSearchResponseAssembler.assemble(
			searchRequestString, searchResponse, searchSearchRequest,
			searchSearchResponse);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"The search engine processed the request in ",
					searchSearchResponse.getExecutionTime(), " ms"));
		}

		return searchSearchResponse;
	}

	private SearchResponse _getScrollSearchResponse(
		SearchSearchRequest searchSearchRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				searchSearchRequest.getConnectionId(),
				searchSearchRequest.isPreferLocalCluster());

		SearchScrollRequest searchScrollRequest = new SearchScrollRequest(
			searchSearchRequest.getScrollId());

		if (searchSearchRequest.getScrollKeepAliveMinutes() > 0) {
			searchScrollRequest.scroll(
				TimeValue.timeValueMinutes(
					searchSearchRequest.getScrollKeepAliveMinutes()));
		}

		try {
			return restHighLevelClient.scroll(
				searchScrollRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private SearchResponse _getSearchResponse(
		SearchRequest searchRequest, SearchSearchRequest searchSearchRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				searchSearchRequest.getConnectionId(),
				searchSearchRequest.isPreferLocalCluster());

		try {
			return restHighLevelClient.search(
				searchRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchSearchRequestExecutorImpl.class);

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	@Reference
	private SearchSearchRequestAssembler _searchSearchRequestAssembler;

	@Reference
	private SearchSearchResponseAssembler _searchSearchResponseAssembler;

}