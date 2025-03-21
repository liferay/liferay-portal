/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.tuning.rankings.web.internal.util.RankingResultUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
public class RankingGetSearchResultsBuilder {

	public RankingGetSearchResultsBuilder(
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		DLAppLocalService dlAppLocalService,
		FastDateFormatFactory fastDateFormatFactory,
		GroupLocalService groupLocalService, Queries queries,
		ResourceActions resourceActions, ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_dlAppLocalService = dlAppLocalService;
		_fastDateFormatFactory = fastDateFormatFactory;
		_groupLocalService = groupLocalService;
		_queries = queries;
		_resourceActions = resourceActions;
		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public JSONObject build() {
		SearchRequest searchRequest = buildSearchRequest();

		SearchResponse searchResponse = _searcher.search(searchRequest);

		return JSONUtil.put(
			"documents",
			JSONUtil.toJSONArray(
				searchResponse.getDocuments(), this::translate, _log)
		).put(
			"total", searchResponse.getTotalHits()
		);
	}

	public RankingGetSearchResultsBuilder companyId(long companyId) {
		_companyId = companyId;

		return this;
	}

	public RankingGetSearchResultsBuilder from(int from) {
		_from = from;

		return this;
	}

	public RankingGetSearchResultsBuilder queryString(String queryString) {
		_queryString = queryString;

		return this;
	}

	public RankingGetSearchResultsBuilder size(int size) {
		_size = size;

		return this;
	}

	protected SearchRequest buildSearchRequest() {
		RankingSearchRequestBuilder rankingSearchRequestBuilder =
			new RankingSearchRequestBuilder(
				_complexQueryPartBuilderFactory, _groupLocalService, _queries,
				_searchRequestBuilderFactory);

		return rankingSearchRequestBuilder.adminSearch(
			true
		).companyId(
			_companyId
		).from(
			_from
		).size(
			_size
		).queryString(
			_queryString
		).build(
		).build();
	}

	protected JSONObject translate(Document document) {
		RankingJSONBuilder rankingJSONBuilder = new RankingJSONBuilder(
			_dlAppLocalService, _fastDateFormatFactory, _resourceActions,
			_resourceRequest);

		return rankingJSONBuilder.deleted(
			_isAssetDeleted(document)
		).document(
			document
		).viewURL(
			_getViewURL(document)
		).build();
	}

	private String _getViewURL(Document document) {
		return RankingResultUtil.getRankingResultViewURL(
			document, _resourceRequest, _resourceResponse, true);
	}

	private boolean _isAssetDeleted(Document document) {
		return RankingResultUtil.isAssetDeleted(document);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingGetSearchResultsBuilder.class.getName());

	private long _companyId;
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory;
	private final DLAppLocalService _dlAppLocalService;
	private final FastDateFormatFactory _fastDateFormatFactory;
	private int _from;
	private final GroupLocalService _groupLocalService;
	private final Queries _queries;
	private String _queryString;
	private final ResourceActions _resourceActions;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private int _size;

}