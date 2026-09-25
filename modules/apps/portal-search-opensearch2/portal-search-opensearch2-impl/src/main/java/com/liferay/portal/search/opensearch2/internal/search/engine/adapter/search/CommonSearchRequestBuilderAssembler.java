/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.filter.ComplexQueryBuilder;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchAggregationVisitor;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchPipelineAggregationVisitor;
import com.liferay.portal.search.opensearch2.internal.facet.FacetTranslator;
import com.liferay.portal.search.opensearch2.internal.filter.OpenSearchFilterVisitor;
import com.liferay.portal.search.opensearch2.internal.legacy.query.OpenSearchQueryVisitor;
import com.liferay.portal.search.opensearch2.internal.stats.StatsTranslator;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;
import com.liferay.portal.search.pit.PointInTime;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.Pit;
import org.opensearch.client.opensearch.core.search.RescoreQuery;
import org.opensearch.client.opensearch.core.search.ScoreMode;
import org.opensearch.client.opensearch.core.search.TrackHits;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class CommonSearchRequestBuilderAssembler {

	public static final CommonSearchRequestBuilderAssembler INSTANCE =
		new CommonSearchRequestBuilderAssembler();

	public void assemble(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		_setAggregations(baseSearchRequest, searchRequestBuilder);
		_setExplain(baseSearchRequest, searchRequestBuilder);
		_setFacets(baseSearchRequest, searchRequestBuilder);
		_setIndexBoosts(baseSearchRequest, searchRequestBuilder);
		_setIndices(baseSearchRequest, searchRequestBuilder);
		_setMinScore(baseSearchRequest, searchRequestBuilder);
		_setPipelineAggregations(baseSearchRequest, searchRequestBuilder);
		_setPointInTime(baseSearchRequest, searchRequestBuilder);
		_setPostFilter(baseSearchRequest, searchRequestBuilder);
		setQuery(baseSearchRequest, searchRequestBuilder);
		_setRequestCache(baseSearchRequest, searchRequestBuilder);
		_setRescorers(baseSearchRequest, searchRequestBuilder);
		_setStatsRequests(baseSearchRequest, searchRequestBuilder);
		_setTimeout(baseSearchRequest, searchRequestBuilder);
		_setTrackTotalHits(baseSearchRequest, searchRequestBuilder);
	}

	public org.opensearch.client.opensearch._types.query_dsl.Query getQuery(
		BaseSearchRequest baseSearchRequest) {

		org.opensearch.client.opensearch._types.query_dsl.Query query1 =
			_combine(
				_translateQuery(baseSearchRequest.getQuery()),
				_translateQuery(baseSearchRequest.getQuery71()));

		List<ComplexQueryPart> complexQueryParts =
			baseSearchRequest.getComplexQueryParts();

		if (complexQueryParts.isEmpty()) {
			org.opensearch.client.opensearch._types.query_dsl.Query query2 =
				_combine(
					translateComplexQueryParts(Collections.emptyList()), query1,
					BoolQuery.Builder::must);

			return _combine(
				translateComplexQueryParts(Collections.emptyList()), query2,
				BoolQuery.Builder::should);
		}

		return _combine(complexQueryParts, query1);
	}

	protected void setQuery(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		searchRequestBuilder.query(getQuery(baseSearchRequest));
	}

	protected BoolQuery.Builder translateComplexQueryParts(
		List<ComplexQueryPart> complexQueryParts) {

		if (ListUtil.isEmpty(complexQueryParts)) {
			return null;
		}

		BooleanQuery booleanQuery = _buildComplexQuery(complexQueryParts);

		BoolQuery.Builder builder = QueryBuilders.bool();

		_transfer(booleanQuery, builder);

		return builder;
	}

	protected ScoreMode translateScoreMode(Rescore.ScoreMode scoreMode) {
		if (scoreMode == Rescore.ScoreMode.AVG) {
			return ScoreMode.Avg;
		}
		else if (scoreMode == Rescore.ScoreMode.MAX) {
			return ScoreMode.Max;
		}
		else if (scoreMode == Rescore.ScoreMode.MIN) {
			return ScoreMode.Min;
		}
		else if (scoreMode == Rescore.ScoreMode.MULTIPLY) {
			return ScoreMode.Multiply;
		}
		else if (scoreMode == Rescore.ScoreMode.TOTAL) {
			return ScoreMode.Total;
		}

		throw new IllegalArgumentException(
			"Invalid rescore score mode " + scoreMode);
	}

	private CommonSearchRequestBuilderAssembler() {
	}

	private BooleanQuery _buildComplexQuery(
		List<ComplexQueryPart> complexQueryParts) {

		ComplexQueryBuilder complexQueryBuilder = new ComplexQueryBuilder();

		return (BooleanQuery)complexQueryBuilder.addParts(
			complexQueryParts
		).build();
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query
		_buildPostFilterQuery(BaseSearchRequest baseSearchRequest) {

		org.opensearch.client.opensearch._types.query_dsl.Query query = null;

		if (baseSearchRequest.getPostFilterQuery() != null) {
			query = new org.opensearch.client.opensearch._types.query_dsl.Query(
				com.liferay.portal.search.opensearch2.internal.query.
					OpenSearchQueryVisitor.INSTANCE.translate(
						baseSearchRequest.getPostFilterQuery()));
		}

		List<ComplexQueryPart> complexQueryParts =
			baseSearchRequest.getPostFilterComplexQueryParts();

		if (!complexQueryParts.isEmpty()) {
			query = _combine(complexQueryParts, query);
		}

		return query;
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query _combine(
		BoolQuery boolQuery, ComplexQueryPart complexQueryPart) {

		ComplexQueryBuilder complexQueryBuilder = new ComplexQueryBuilder();

		Query query = complexQueryBuilder.buildPart(complexQueryPart);

		if (query == null) {
			return boolQuery._toQuery();
		}

		BoolQuery.Builder boolQueryBuilder = _createBoolQueryBuilder(boolQuery);

		String occur = GetterUtil.getString(
			complexQueryPart.getOccur(), "must");

		if (occur.equals("filter")) {
			boolQueryBuilder.filter(_translateQuery(query));
		}
		else if (occur.equals("must")) {
			boolQueryBuilder.must(_translateQuery(query));
		}
		else if (occur.equals("must_not")) {
			boolQueryBuilder.mustNot(_translateQuery(query));
		}
		else if (occur.equals("should")) {
			boolQueryBuilder.should(_translateQuery(query));
		}

		return new org.opensearch.client.opensearch._types.query_dsl.Query(
			boolQueryBuilder.build());
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query _combine(
		BoolQuery.Builder builder,
		org.opensearch.client.opensearch._types.query_dsl.Query query,
		BiConsumer
			<BoolQuery.Builder,
			 org.opensearch.client.opensearch._types.query_dsl.Query>
				biConsumer) {

		if (builder == null) {
			return query;
		}

		if (query != null) {
			biConsumer.accept(builder, query);
		}

		return new org.opensearch.client.opensearch._types.query_dsl.Query(
			builder.build());
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query _combine(
		List<ComplexQueryPart> complexQueryParts,
		org.opensearch.client.opensearch._types.query_dsl.Query query1) {

		List<ComplexQueryPart> additiveComplexQueryParts = new ArrayList<>();
		List<ComplexQueryPart> nonadditiveComplexQueryParts = new ArrayList<>();

		for (ComplexQueryPart complexQueryPart : complexQueryParts) {
			if (complexQueryPart.isAdditive()) {
				additiveComplexQueryParts.add(complexQueryPart);
			}
			else {
				if (complexQueryPart.isRootClause() &&
					Validator.isBlank(complexQueryPart.getParent()) &&
					query1.isBool()) {

					query1 = _combine(query1.bool(), complexQueryPart);
				}
				else {
					nonadditiveComplexQueryParts.add(complexQueryPart);
				}
			}
		}

		org.opensearch.client.opensearch._types.query_dsl.Query query2 =
			_combine(
				translateComplexQueryParts(nonadditiveComplexQueryParts),
				query1, BoolQuery.Builder::must);

		return _combine(
			translateComplexQueryParts(additiveComplexQueryParts), query2,
			BoolQuery.Builder::should);
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query _combine(
		org.opensearch.client.opensearch._types.query_dsl.Query query1,
		org.opensearch.client.opensearch._types.query_dsl.Query query2) {

		if (query1 == null) {
			return query2;
		}

		if (query2 == null) {
			return query1;
		}

		BoolQuery.Builder builder = QueryBuilders.bool();

		return new org.opensearch.client.opensearch._types.query_dsl.Query(
			builder.must(
				query1, query2
			).build());
	}

	private void _copy(
		List<Query> clauses,
		Consumer<org.opensearch.client.opensearch._types.query_dsl.Query>
			consumer) {

		for (Query query : clauses) {
			consumer.accept(_translateQuery(query));
		}
	}

	private BoolQuery.Builder _createBoolQueryBuilder(BoolQuery boolQuery) {
		BoolQuery.Builder builder = QueryBuilders.bool();

		builder.boost(boolQuery.boost());
		builder.filter(boolQuery.filter());
		builder.minimumShouldMatch(boolQuery.minimumShouldMatch());
		builder.must(boolQuery.must());
		builder.mustNot(boolQuery.mustNot());
		builder.queryName(boolQuery.queryName());
		builder.should(boolQuery.should());

		return builder;
	}

	private void _setAggregations(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		MapUtil.isNotEmptyForEach(
			baseSearchRequest.getAggregationsMap(),
			(key, aggregation) -> searchRequestBuilder.aggregations(
				key,
				aggregation.accept(OpenSearchAggregationVisitor.INSTANCE)));
	}

	private void _setExplain(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		SetterUtil.setNotNullBoolean(
			searchRequestBuilder::explain, baseSearchRequest.getExplain());
	}

	private void _setFacets(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		_facetTranslator.translate(
			baseSearchRequest.isBasicFacetSelection(),
			baseSearchRequest.getFacets(), baseSearchRequest.getQuery71(),
			searchRequestBuilder);
	}

	private void _setIndexBoosts(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		if (MapUtil.isEmpty(baseSearchRequest.getIndexBoosts())) {
			return;
		}

		Map<String, Double> indicesBoosts = new HashMap<>();

		MapUtil.isNotEmptyForEach(
			baseSearchRequest.getIndexBoosts(),
			(indexName, boost) -> indicesBoosts.put(
				indexName, boost.doubleValue()));

		searchRequestBuilder.indicesBoost(indicesBoosts);
	}

	private void _setIndices(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		if (baseSearchRequest.getPointInTime() == null) {
			searchRequestBuilder.index(
				ListUtil.fromArray(baseSearchRequest.getIndexNames()));
		}
	}

	private void _setMinScore(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		SetterUtil.setNotNullFloatAsDouble(
			searchRequestBuilder::minScore,
			baseSearchRequest.getMinimumScore());
	}

	private void _setPipelineAggregations(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		MapUtil.isNotEmptyForEach(
			baseSearchRequest.getPipelineAggregationsMap(),
			(key, pipelineAggregation) -> searchRequestBuilder.aggregations(
				key,
				pipelineAggregation.accept(
					OpenSearchPipelineAggregationVisitor.INSTANCE)));
	}

	private void _setPointInTime(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		PointInTime pointInTime = baseSearchRequest.getPointInTime();

		if (pointInTime == null) {
			return;
		}

		Pit.Builder builder = new Pit.Builder();

		builder.id(pointInTime.getPointInTimeId());

		if (pointInTime.getKeepAlive() != 0) {
			builder.keepAlive(
				pointInTime.getKeepAlive() + TimeUnit.Seconds.jsonValue());
		}

		searchRequestBuilder.pit(builder.build());
	}

	private void _setPostFilter(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		org.opensearch.client.opensearch._types.query_dsl.Query query =
			_buildPostFilterQuery(baseSearchRequest);

		if (query != null) {
			searchRequestBuilder.postFilter(query);
		}
		else if (baseSearchRequest.getPostFilter() != null) {
			Filter filter = baseSearchRequest.getPostFilter();

			searchRequestBuilder.postFilter(
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					filter.accept(OpenSearchFilterVisitor.INSTANCE)));
		}
	}

	private void _setRequestCache(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		SetterUtil.setNotNullBoolean(
			searchRequestBuilder::requestCache,
			baseSearchRequest.getRequestCache());
	}

	private void _setRescoreQuery(
		Query query, SearchRequest.Builder searchRequestBuilder) {

		if (query == null) {
			return;
		}

		RescoreQuery.Builder rescoreQueryBuilder = new RescoreQuery.Builder();

		rescoreQueryBuilder.query(
			new org.opensearch.client.opensearch._types.query_dsl.Query(
				com.liferay.portal.search.opensearch2.internal.query.
					OpenSearchQueryVisitor.INSTANCE.translate(query)));

		org.opensearch.client.opensearch.core.search.Rescore.Builder
			rescoreBuilder =
				new org.opensearch.client.opensearch.core.search.Rescore.
					Builder();

		rescoreBuilder.query(rescoreQueryBuilder.build());

		searchRequestBuilder.rescore(rescoreBuilder.build());
	}

	private void _setRescorers(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		_setRescores(baseSearchRequest.getRescores(), searchRequestBuilder);

		_setRescoreQuery(
			baseSearchRequest.getRescoreQuery(), searchRequestBuilder);
	}

	private void _setRescores(
		List<Rescore> rescores, SearchRequest.Builder searchRequestBuilder) {

		if (ListUtil.isEmpty(rescores)) {
			return;
		}

		for (Rescore rescore : rescores) {
			RescoreQuery.Builder rescoreQueryBuilder =
				new RescoreQuery.Builder();

			rescoreQueryBuilder.query(
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					com.liferay.portal.search.opensearch2.internal.query.
						OpenSearchQueryVisitor.INSTANCE.translate(
							rescore.getQuery())));

			SetterUtil.setNotNullFloatAsDouble(
				rescoreQueryBuilder::queryWeight, rescore.getQueryWeight());
			SetterUtil.setNotNullFloatAsDouble(
				rescoreQueryBuilder::rescoreQueryWeight,
				rescore.getRescoreQueryWeight());

			if (rescore.getScoreMode() != null) {
				rescoreQueryBuilder.scoreMode(
					translateScoreMode(rescore.getScoreMode()));
			}

			org.opensearch.client.opensearch.core.search.Rescore.Builder
				rescoreBuilder =
					new org.opensearch.client.opensearch.core.search.Rescore.
						Builder();

			rescoreBuilder.query(rescoreQueryBuilder.build());

			SetterUtil.setNotNullInteger(
				rescoreBuilder::windowSize, rescore.getWindowSize());

			searchRequestBuilder.rescore(rescoreBuilder.build());
		}
	}

	private void _setStatsRequests(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		List<StatsRequest> statsRequests = baseSearchRequest.getStatsRequests();

		if (ListUtil.isNotEmpty(statsRequests)) {
			statsRequests.forEach(
				statsRequest -> _statsTranslator.populateRequest(
					searchRequestBuilder, statsRequest));
		}
	}

	private void _setTimeout(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		if (baseSearchRequest.getTimeoutInMilliseconds() != null) {
			searchRequestBuilder.timeout(
				baseSearchRequest.getTimeoutInMilliseconds() +
					TimeUnit.Milliseconds.jsonValue());
		}
	}

	private void _setTrackTotalHits(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		if (baseSearchRequest.getTrackTotalHitsLimit() != null) {
			searchRequestBuilder.trackTotalHits(
				TrackHits.of(
					trackHits -> trackHits.count(
						baseSearchRequest.getTrackTotalHitsLimit())));
		}
		else if (baseSearchRequest.getTrackTotalHits() != null) {
			searchRequestBuilder.trackTotalHits(
				TrackHits.of(
					trackHits -> trackHits.enabled(
						baseSearchRequest.getTrackTotalHits())));
		}
	}

	private void _transfer(
		BooleanQuery booleanQuery, BoolQuery.Builder boolQueryBuilder) {

		_copy(booleanQuery.getFilterQueryClauses(), boolQueryBuilder::filter);
		_copy(booleanQuery.getMustNotQueryClauses(), boolQueryBuilder::mustNot);
		_copy(booleanQuery.getMustQueryClauses(), boolQueryBuilder::must);
		_copy(booleanQuery.getShouldQueryClauses(), boolQueryBuilder::should);
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query
		_translateQuery(com.liferay.portal.kernel.search.Query query) {

		if (query == null) {
			return null;
		}

		org.opensearch.client.opensearch._types.query_dsl.Query
			translatedQuery =
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					OpenSearchQueryVisitor.INSTANCE.translate(query));

		if ((query.getPreBooleanFilter() == null) ||
			(query instanceof com.liferay.portal.kernel.search.BooleanQuery)) {

			return translatedQuery;
		}

		// LPS-86537 the following is only present to allow for backwards
		// compatibility. Not all Query should have filters allowed according
		// to OpenSearch's API.

		// See related note in BooleanQueryTranslatorImpl

		BoolQuery.Builder builder = QueryBuilders.bool();

		Filter filter = query.getPreBooleanFilter();

		builder.filter(
			new org.opensearch.client.opensearch._types.query_dsl.Query(
				filter.accept(OpenSearchFilterVisitor.INSTANCE)));

		builder.must(translatedQuery);

		return new org.opensearch.client.opensearch._types.query_dsl.Query(
			builder.build());
	}

	private org.opensearch.client.opensearch._types.query_dsl.Query
		_translateQuery(Query query) {

		if (query == null) {
			return null;
		}

		return new org.opensearch.client.opensearch._types.query_dsl.Query(
			com.liferay.portal.search.opensearch2.internal.query.
				OpenSearchQueryVisitor.INSTANCE.translate(query));
	}

	private final FacetTranslator _facetTranslator = new FacetTranslator();
	private final StatsTranslator _statsTranslator = new StatsTranslator();

}