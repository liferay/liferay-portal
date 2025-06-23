/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.AggregationResultTranslator;
import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationResultTranslator;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.elasticsearch7.internal.SearchHitDocumentTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.AggregationResultTranslatorFactory;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.ElasticsearchAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.ElasticsearchAggregationResultsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.PipelineAggregationResultTranslatorFactory;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.pipeline.ElasticsearchPipelineAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch7.internal.hits.SearchHitsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.geolocation.GeoBuilders;
import com.liferay.portal.search.groupby.GroupByResponseFactory;
import com.liferay.portal.search.highlight.HighlightFieldBuilderFactory;
import com.liferay.portal.search.hits.SearchHitBuilderFactory;
import com.liferay.portal.search.hits.SearchHitsBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsResultsTranslator;
import com.liferay.portal.search.searcher.SearchTimeValue;

import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = SearchSearchResponseAssembler.class)
public class SearchSearchResponseAssemblerImpl
	implements AggregationResultTranslatorFactory,
			   PipelineAggregationResultTranslatorFactory,
			   SearchSearchResponseAssembler {

	@Override
	public void assemble(
		SearchSourceBuilder searchRequestBuilder, SearchResponse searchResponse,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		_commonSearchResponseAssembler.assemble(
			searchRequestBuilder, searchResponse, searchSearchRequest,
			searchSearchResponse);

		_addAggregations(
			searchResponse, searchSearchResponse, searchSearchRequest);
		setCount(searchResponse, searchSearchResponse);
		_setScrollId(searchResponse, searchSearchResponse);
		_setSearchHits(
			searchResponse, searchSearchResponse, searchSearchRequest);
		_setSearchTimeValue(searchResponse, searchSearchResponse);

		_searchResponseTranslator.populate(
			searchSearchResponse, searchResponse, searchSearchRequest);
	}

	@Override
	public AggregationResultTranslator createAggregationResultTranslator(
		org.elasticsearch.search.aggregations.Aggregation
			elasticsearchAggregation) {

		return new ElasticsearchAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults,
			new SearchHitsTranslator(
				_searchHitBuilderFactory, _searchHitsBuilderFactory,
				_documentBuilderFactory, _highlightFieldBuilderFactory,
				_geoBuilders),
			_geoBuilders);
	}

	@Override
	public PipelineAggregationResultTranslator
		createPipelineAggregationResultTranslator(
			org.elasticsearch.search.aggregations.Aggregation
				elasticsearchAggregation) {

		return new ElasticsearchPipelineAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults);
	}

	@Activate
	protected void activate() {
		_searchResponseTranslator = new SearchResponseTranslator(
			_groupByResponseFactory, _searchHitDocumentTranslator,
			_statsRequestBuilderFactory, _statsResultsTranslator,
			_statsTranslator);
	}

	protected void setCount(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse) {

		SearchHits searchHits = searchResponse.getHits();

		TotalHits totalHits = searchHits.getTotalHits();

		searchSearchResponse.setCount(totalHits.value);
	}

	private void _addAggregations(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse,
		SearchSearchRequest searchSearchRequest) {

		Aggregations elasticsearchAggregations =
			searchResponse.getAggregations();

		if (elasticsearchAggregations == null) {
			return;
		}

		Map<String, Aggregation> aggregationsMap =
			searchSearchRequest.getAggregationsMap();

		Map<String, PipelineAggregation> pipelineAggregationsMap =
			searchSearchRequest.getPipelineAggregationsMap();

		ElasticsearchAggregationResultsTranslator
			elasticsearchAggregationResultsTranslator =
				new ElasticsearchAggregationResultsTranslator(
					this, this, aggregationsMap::get,
					pipelineAggregationsMap::get);

		List<AggregationResult> aggregationResults =
			elasticsearchAggregationResultsTranslator.translate(
				elasticsearchAggregations);

		for (AggregationResult aggregationResult : aggregationResults) {
			searchSearchResponse.addAggregationResult(aggregationResult);
		}
	}

	private void _setScrollId(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse) {

		if (Validator.isNotNull(searchResponse.getScrollId())) {
			searchSearchResponse.setScrollId(searchResponse.getScrollId());
		}
	}

	private void _setSearchHits(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse,
		SearchSearchRequest searchSearchRequest) {

		SearchHitsTranslator searchHitsTranslator = new SearchHitsTranslator(
			_searchHitBuilderFactory, _searchHitsBuilderFactory,
			_documentBuilderFactory, _highlightFieldBuilderFactory,
			_geoBuilders);

		SearchHits searchHits = searchResponse.getHits();

		searchSearchResponse.setSearchHits(
			searchHitsTranslator.translate(
				searchHits, searchSearchRequest.getAlternateUidFieldName()));
	}

	private void _setSearchTimeValue(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse) {

		TimeValue took = searchResponse.getTook();

		SearchTimeValue.Builder builder = SearchTimeValue.Builder.newBuilder();

		builder.duration(
			took.duration()
		).timeUnit(
			took.timeUnit()
		);

		searchSearchResponse.setSearchTimeValue(builder.build());
	}

	@Reference
	private AggregationResults _aggregationResults;

	@Reference
	private CommonSearchResponseAssembler _commonSearchResponseAssembler;

	@Reference
	private DocumentBuilderFactory _documentBuilderFactory;

	@Reference
	private GeoBuilders _geoBuilders;

	@Reference
	private GroupByResponseFactory _groupByResponseFactory;

	@Reference
	private HighlightFieldBuilderFactory _highlightFieldBuilderFactory;

	@Reference
	private SearchHitBuilderFactory _searchHitBuilderFactory;

	@Reference
	private SearchHitDocumentTranslator _searchHitDocumentTranslator;

	@Reference
	private SearchHitsBuilderFactory _searchHitsBuilderFactory;

	private SearchResponseTranslator _searchResponseTranslator;

	@Reference
	private StatsRequestBuilderFactory _statsRequestBuilderFactory;

	@Reference
	private StatsResultsTranslator _statsResultsTranslator;

	@Reference
	private StatsTranslator _statsTranslator;

}