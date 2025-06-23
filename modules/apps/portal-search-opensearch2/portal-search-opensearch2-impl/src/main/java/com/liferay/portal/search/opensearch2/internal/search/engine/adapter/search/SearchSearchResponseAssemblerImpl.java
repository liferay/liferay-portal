/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.AggregationResultTranslator;
import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationResultTranslator;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.geolocation.GeoBuilders;
import com.liferay.portal.search.groupby.GroupByResponseFactory;
import com.liferay.portal.search.highlight.HighlightFieldBuilderFactory;
import com.liferay.portal.search.hits.SearchHitBuilderFactory;
import com.liferay.portal.search.hits.SearchHitsBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsResultsTranslator;
import com.liferay.portal.search.opensearch2.internal.aggregation.AggregationResultTranslatorFactory;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchAggregationResultTranslator;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchAggregationResultsTranslator;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchPipelineAggregationResultTranslator;
import com.liferay.portal.search.opensearch2.internal.aggregation.PipelineAggregationResultTranslatorFactory;
import com.liferay.portal.search.opensearch2.internal.hits.HitsMetadataTranslator;
import com.liferay.portal.search.opensearch2.internal.legacy.hits.HitDocumentTranslator;
import com.liferay.portal.search.opensearch2.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.opensearch2.internal.stats.StatsTranslator;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;
import com.liferay.portal.search.searcher.SearchTimeValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.opensearch.client.opensearch.core.search.TotalHits;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(service = SearchSearchResponseAssembler.class)
public class SearchSearchResponseAssemblerImpl
	implements AggregationResultTranslatorFactory,
			   PipelineAggregationResultTranslatorFactory,
			   SearchSearchResponseAssembler {

	@Override
	public void assemble(
		SearchRequest searchRequest, SearchResponse<JsonData> searchResponse,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		_commonSearchResponseAssembler.assemble(
			searchSearchRequest, searchSearchResponse, searchRequest,
			searchResponse);

		_addAggregations(
			searchResponse, searchSearchRequest, searchSearchResponse);
		setCount(searchResponse, searchSearchResponse);
		_setScrollId(searchResponse, searchSearchResponse);
		_setSearchHits(
			searchResponse, searchSearchResponse, searchSearchRequest);
		_setSearchTimeValue(searchResponse, searchSearchResponse);

		_searchResponseTranslator.populate(
			searchResponse, searchSearchRequest, searchSearchResponse);
	}

	@Override
	public AggregationResultTranslator createAggregationResultTranslator(
		Aggregate aggregate) {

		return new OpenSearchAggregationResultTranslator(
			aggregate, _aggregationResults, _geoBuilders,
			new HitsMetadataTranslator(
				_documentBuilderFactory, _geoBuilders,
				_highlightFieldBuilderFactory, _searchHitBuilderFactory,
				_searchHitsBuilderFactory));
	}

	@Override
	public PipelineAggregationResultTranslator
		createPipelineAggregationResultTranslator(Aggregate aggregate) {

		return new OpenSearchPipelineAggregationResultTranslator(
			aggregate, _aggregationResults);
	}

	@Activate
	protected void activate() {
		_searchResponseTranslator = new SearchResponseTranslator(
			_groupByResponseFactory, _hitDocumentTranslator,
			_statsRequestBuilderFactory, _statsResultsTranslator,
			_statsTranslator);
	}

	protected void setCount(
		SearchResponse<JsonData> searchResponse,
		SearchSearchResponse searchSearchResponse) {

		HitsMetadata<JsonData> hitsMetadata = searchResponse.hits();

		TotalHits totalHits = hitsMetadata.total();

		searchSearchResponse.setCount(totalHits.value());
	}

	private void _addAggregations(
		SearchResponse<JsonData> searchResponse,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		Map<String, Aggregate> aggregates = searchResponse.aggregations();

		if (MapUtil.isEmpty(aggregates)) {
			return;
		}

		Map<String, Aggregation> aggregations =
			searchSearchRequest.getAggregationsMap();

		if (MapUtil.isEmpty(aggregations)) {
			return;
		}

		Map<String, PipelineAggregation> pipelineAggregations =
			searchSearchRequest.getPipelineAggregationsMap();

		OpenSearchAggregationResultsTranslator
			openSearchAggregationResultsTranslator =
				new OpenSearchAggregationResultsTranslator(
					aggregations::get, this, pipelineAggregations::get, this);

		List<AggregationResult> aggregationResults =
			openSearchAggregationResultsTranslator.translate(aggregates);

		for (AggregationResult aggregationResult : aggregationResults) {
			searchSearchResponse.addAggregationResult(aggregationResult);
		}
	}

	private void _setScrollId(
		SearchResponse<JsonData> searchResponse,
		SearchSearchResponse searchSearchResponse) {

		SetterUtil.setNotBlankString(
			searchSearchResponse::setScrollId, searchResponse.scrollId());
	}

	private void _setSearchHits(
		SearchResponse<JsonData> searchResponse,
		SearchSearchResponse searchSearchResponse,
		SearchSearchRequest searchSearchRequest) {

		HitsMetadataTranslator hitsMetadataTranslator =
			new HitsMetadataTranslator(
				_documentBuilderFactory, _geoBuilders,
				_highlightFieldBuilderFactory, _searchHitBuilderFactory,
				_searchHitsBuilderFactory);

		searchSearchResponse.setSearchHits(
			hitsMetadataTranslator.translate(
				searchSearchRequest.getAlternateUidFieldName(),
				searchResponse.hits()));
	}

	private void _setSearchTimeValue(
		SearchResponse<JsonData> searchResponse,
		SearchSearchResponse searchSearchResponse) {

		SearchTimeValue.Builder builder = SearchTimeValue.Builder.newBuilder();

		builder.duration(
			searchResponse.took()
		).timeUnit(
			TimeUnit.MILLISECONDS
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
	private HitDocumentTranslator _hitDocumentTranslator;

	@Reference
	private SearchHitBuilderFactory _searchHitBuilderFactory;

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