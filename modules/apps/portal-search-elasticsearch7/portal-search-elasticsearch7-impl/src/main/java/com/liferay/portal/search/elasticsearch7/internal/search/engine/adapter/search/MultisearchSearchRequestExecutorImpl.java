/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = MultisearchSearchRequestExecutor.class)
public class MultisearchSearchRequestExecutorImpl
	implements MultisearchSearchRequestExecutor {

	@Override
	public MultisearchSearchResponse execute(
		MultisearchSearchRequest multisearchSearchRequest) {

		MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

		List<SearchSearchRequest> searchSearchRequests =
			multisearchSearchRequest.getSearchSearchRequests();

		List<SearchRequestHolder> searchRequestHolders = new ArrayList<>(
			searchSearchRequests.size());

		searchSearchRequests.forEach(
			searchSearchRequest -> {
				SearchRequest searchRequest = new SearchRequest(
					searchSearchRequest.getIndexNames());

				SearchSourceBuilder searchSourceBuilder =
					new SearchSourceBuilder();

				_searchSearchRequestAssembler.assemble(
					searchSourceBuilder, searchSearchRequest, searchRequest);

				SearchRequestHolder searchRequestHolder =
					new SearchRequestHolder(
						searchSearchRequest, searchSourceBuilder);

				searchRequestHolders.add(searchRequestHolder);

				multiSearchRequest.add(searchRequest);
			});

		MultiSearchResponse multiSearchResponse = _getMultiSearchResponse(
			multiSearchRequest, multisearchSearchRequest);

		Iterator<MultiSearchResponse.Item> iterator =
			multiSearchResponse.iterator();

		MultisearchSearchResponse multisearchSearchResponse =
			new MultisearchSearchResponse();

		int counter = 0;

		while (iterator.hasNext()) {
			MultiSearchResponse.Item multiSearchResponseItem = iterator.next();

			SearchResponse searchResponse =
				multiSearchResponseItem.getResponse();

			SearchSearchResponse searchSearchResponse =
				new SearchSearchResponse();

			SearchRequestHolder searchRequestHolder = searchRequestHolders.get(
				counter);

			SearchSearchRequest searchSearchRequest =
				searchRequestHolder.getSearchSearchRequest();

			_searchSearchResponseAssembler.assemble(
				DebugStringsUtil.getSearchRequestString(
					searchRequestHolder.getSearchSourceBuilder()),
				searchResponse, searchSearchRequest, searchSearchResponse);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"The search engine processed ",
						searchSearchResponse.getSearchRequestString(), " in ",
						searchSearchResponse.getExecutionTime(), " ms"));
			}

			if (searchSearchRequest.isIncludeResponseString()) {
				searchSearchResponse.setSearchResponseString(
					searchResponse.toString());
			}

			counter++;
		}

		return multisearchSearchResponse;
	}

	private MultiSearchResponse _getMultiSearchResponse(
		MultiSearchRequest multiSearchRequest,
		MultisearchSearchRequest multisearchSearchRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				multisearchSearchRequest.getConnectionId(),
				multisearchSearchRequest.isPreferLocalCluster());

		try {
			return restHighLevelClient.msearch(
				multiSearchRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultisearchSearchRequestExecutorImpl.class);

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	@Reference
	private SearchSearchRequestAssembler _searchSearchRequestAssembler;

	@Reference
	private SearchSearchResponseAssembler _searchSearchResponseAssembler;

	private class SearchRequestHolder {

		public SearchRequestHolder(
			SearchSearchRequest searchSearchRequest,
			SearchSourceBuilder searchSourceBuilder) {

			_searchSearchRequest = searchSearchRequest;
			_searchSourceBuilder = searchSourceBuilder;
		}

		public SearchSearchRequest getSearchSearchRequest() {
			return _searchSearchRequest;
		}

		public SearchSourceBuilder getSearchSourceBuilder() {
			return _searchSourceBuilder;
		}

		private final SearchSearchRequest _searchSearchRequest;
		private final SearchSourceBuilder _searchSourceBuilder;

	}

}