/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.engine.adapter.search.BaseSearchResponse;
import com.liferay.portal.search.stats.StatsRequest;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.profile.SearchProfileShardResult;
import org.elasticsearch.search.profile.query.QueryProfileShardResult;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = CommonSearchResponseAssembler.class)
public class CommonSearchResponseAssemblerImpl
	implements CommonSearchResponseAssembler {

	@Override
	public void assemble(
		BaseSearchRequest baseSearchRequest,
		BaseSearchResponse baseSearchResponse, String searchRequestString,
		SearchResponse searchResponse) {

		_setExecutionProfile(searchResponse, baseSearchResponse);
		_setExecutionTime(searchResponse, baseSearchResponse);
		_setPointInTimeId(searchResponse, baseSearchResponse);
		_setScrollId(searchResponse, baseSearchResponse);
		_setSearchRequestString(baseSearchResponse, searchRequestString);
		setSearchResponseString(
			searchResponse, baseSearchRequest, baseSearchResponse);
		_setTerminatedEarly(searchResponse, baseSearchResponse);
		_setTimedOut(searchResponse, baseSearchResponse);

		_updateStatsResponses(
			baseSearchResponse, searchResponse.getAggregations(),
			baseSearchRequest.getStatsRequests());
	}

	protected void setSearchResponseString(
		SearchResponse searchResponse, BaseSearchRequest baseSearchRequest,
		BaseSearchResponse baseSearchResponse) {

		if (baseSearchRequest.isIncludeResponseString()) {
			baseSearchResponse.setSearchResponseString(
				searchResponse.toString());
		}
	}

	private String _getSearchProfileShardResultString(
			SearchProfileShardResult searchProfileShardResult)
		throws IOException {

		XContentBuilder xContentBuilder = XContentFactory.contentBuilder(
			XContentType.JSON);

		List<QueryProfileShardResult> queryProfileShardResults =
			searchProfileShardResult.getQueryProfileResults();

		queryProfileShardResults.forEach(
			queryProfileShardResult -> {
				try {
					xContentBuilder.startObject();

					queryProfileShardResult.toXContent(
						xContentBuilder, ToXContent.EMPTY_PARAMS);

					xContentBuilder.endObject();
				}
				catch (IOException ioException) {
					if (_log.isDebugEnabled()) {
						_log.debug(ioException);
					}
				}
			});

		return Strings.toString(xContentBuilder);
	}

	private void _setExecutionProfile(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		Map<String, SearchProfileShardResult> searchProfileShardResults =
			searchResponse.getProfileResults();

		if (MapUtil.isEmpty(searchProfileShardResults)) {
			return;
		}

		Map<String, String> executionProfile = new HashMap<>();

		searchProfileShardResults.forEach(
			(shardKey, searchProfileShardResult) -> {
				try {
					executionProfile.put(
						shardKey,
						_getSearchProfileShardResultString(
							searchProfileShardResult));
				}
				catch (IOException ioException) {
					if (_log.isInfoEnabled()) {
						_log.info(ioException);
					}
				}
			});

		baseSearchResponse.setExecutionProfile(executionProfile);
	}

	private void _setExecutionTime(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		TimeValue tookTimeValue = searchResponse.getTook();

		baseSearchResponse.setExecutionTime(tookTimeValue.getMillis());
	}

	private void _setPointInTimeId(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		if (searchResponse.pointInTimeId() != null) {
			baseSearchResponse.setPointInTimeId(searchResponse.pointInTimeId());
		}
	}

	private void _setScrollId(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		if (searchResponse.getScrollId() != null) {
			baseSearchResponse.setScrollId(searchResponse.getScrollId());
		}
	}

	private void _setSearchRequestString(
		BaseSearchResponse baseSearchResponse, String searchRequestString) {

		baseSearchResponse.setSearchRequestString(searchRequestString);
	}

	private void _setTerminatedEarly(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		baseSearchResponse.setTerminatedEarly(
			GetterUtil.getBoolean(searchResponse.isTerminatedEarly()));
	}

	private void _setTimedOut(
		SearchResponse searchResponse, BaseSearchResponse baseSearchResponse) {

		baseSearchResponse.setTimedOut(searchResponse.isTimedOut());
	}

	private void _updateStatsResponse(
		BaseSearchResponse baseSearchResponse,
		Map<String, Aggregation> aggregationsMap, StatsRequest statsRequest) {

		baseSearchResponse.addStatsResponse(
			_statsTranslator.translateResponse(aggregationsMap, statsRequest));
	}

	private void _updateStatsResponses(
		BaseSearchResponse baseSearchResponse, Aggregations aggregations,
		Collection<StatsRequest> statsRequests) {

		if (aggregations == null) {
			return;
		}

		_updateStatsResponses(
			baseSearchResponse, aggregations.getAsMap(), statsRequests);
	}

	private void _updateStatsResponses(
		BaseSearchResponse baseSearchResponse,
		Map<String, Aggregation> aggregationsMap,
		Collection<StatsRequest> statsRequests) {

		for (StatsRequest statsRequest : statsRequests) {
			_updateStatsResponse(
				baseSearchResponse, aggregationsMap, statsRequest);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommonSearchResponseAssemblerImpl.class);

	@Reference
	private StatsTranslator _statsTranslator;

}