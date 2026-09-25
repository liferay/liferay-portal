/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.search;

import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.TimeUnit;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.PointInTimeReference;
import co.elastic.clients.elasticsearch.core.search.RescoreQuery;
import co.elastic.clients.elasticsearch.core.search.ScoreMode;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.util.NamedValue;

import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch8.internal.aggregation.ElasticsearchAggregationVisitor;
import com.liferay.portal.search.elasticsearch8.internal.aggregation.ElasticsearchPipelineAggregationVisitor;
import com.liferay.portal.search.elasticsearch8.internal.facet.FacetTranslator;
import com.liferay.portal.search.elasticsearch8.internal.filter.ElasticsearchFilterVisitor;
import com.liferay.portal.search.elasticsearch8.internal.legacy.query.ElasticsearchQueryVisitor;
import com.liferay.portal.search.elasticsearch8.internal.stats.StatsTranslator;
import com.liferay.portal.search.elasticsearch8.internal.util.SetterUtil;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.filter.ComplexQueryBuilder;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.pit.PointInTime;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Michael C. Han
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

	public co.elastic.clients.elasticsearch._types.query_dsl.Query getQuery(
		BaseSearchRequest baseSearchRequest) {

		co.elastic.clients.elasticsearch._types.query_dsl.Query query1 =
			_combine(
				_translateQuery(baseSearchRequest.getQuery()),
				_translateQuery(baseSearchRequest.getQuery71()));

		List<ComplexQueryPart> complexQueryParts =
			baseSearchRequest.getComplexQueryParts();

		if (complexQueryParts.isEmpty()) {
			co.elastic.clients.elasticsearch._types.query_dsl.Query query2 =
				_combine(
					BoolQuery.Builder::must,
					translateComplexQueryParts(Collections.emptyList()),
					query1);

			return _combine(
				BoolQuery.Builder::should,
				translateComplexQueryParts(Collections.emptyList()), query2);
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

	private co.elastic.clients.elasticsearch._types.query_dsl.Query
		_buildPostFilterQuery(BaseSearchRequest baseSearchRequest) {

		co.elastic.clients.elasticsearch._types.query_dsl.Query query = null;

		if (baseSearchRequest.getPostFilterQuery() != null) {
			query = new co.elastic.clients.elasticsearch._types.query_dsl.Query(
				com.liferay.portal.search.elasticsearch8.internal.query.
					ElasticsearchQueryVisitor.INSTANCE.translate(
						baseSearchRequest.getPostFilterQuery()));
		}

		List<ComplexQueryPart> complexQueryParts =
			baseSearchRequest.getPostFilterComplexQueryParts();

		if (!complexQueryParts.isEmpty()) {
			query = _combine(complexQueryParts, query);
		}

		return query;
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query _combine(
		BiConsumer
			<BoolQuery.Builder,
			 co.elastic.clients.elasticsearch._types.query_dsl.Query>
				biConsumer,
		BoolQuery.Builder builder,
		co.elastic.clients.elasticsearch._types.query_dsl.Query query) {

		if (builder == null) {
			return query;
		}

		if (query != null) {
			biConsumer.accept(builder, query);
		}

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			builder.build());
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query _combine(
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

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			boolQueryBuilder.build());
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query _combine(
		co.elastic.clients.elasticsearch._types.query_dsl.Query query1,
		co.elastic.clients.elasticsearch._types.query_dsl.Query query2) {

		if (query1 == null) {
			return query2;
		}

		if (query2 == null) {
			return query1;
		}

		BoolQuery.Builder builder = QueryBuilders.bool();

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			builder.must(
				query1, query2
			).build());
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query _combine(
		List<ComplexQueryPart> complexQueryParts,
		co.elastic.clients.elasticsearch._types.query_dsl.Query query1) {

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

		co.elastic.clients.elasticsearch._types.query_dsl.Query query2 =
			_combine(
				BoolQuery.Builder::must,
				translateComplexQueryParts(nonadditiveComplexQueryParts),
				query1);

		return _combine(
			BoolQuery.Builder::should,
			translateComplexQueryParts(additiveComplexQueryParts), query2);
	}

	private void _copy(
		List<Query> clauses,
		Consumer<co.elastic.clients.elasticsearch._types.query_dsl.Query>
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
				aggregation.accept(ElasticsearchAggregationVisitor.INSTANCE)));
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

		List<NamedValue<Double>> indicesBoosts = new ArrayList<>();

		MapUtil.isNotEmptyForEach(
			baseSearchRequest.getIndexBoosts(),
			(indexName, boost) -> indicesBoosts.add(
				NamedValue.of(indexName, boost.doubleValue())));

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
					ElasticsearchPipelineAggregationVisitor.INSTANCE)));
	}

	private void _setPointInTime(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		PointInTime pointInTime = baseSearchRequest.getPointInTime();

		if (pointInTime == null) {
			return;
		}

		PointInTimeReference.Builder builder =
			new PointInTimeReference.Builder();

		builder.id(pointInTime.getPointInTimeId());

		if (pointInTime.getKeepAlive() != 0) {
			builder.keepAlive(
				Time.of(
					time -> time.time(
						pointInTime.getKeepAlive() +
							TimeUnit.Seconds.jsonValue())));
		}

		searchRequestBuilder.pit(builder.build());
	}

	private void _setPostFilter(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		co.elastic.clients.elasticsearch._types.query_dsl.Query query =
			_buildPostFilterQuery(baseSearchRequest);

		if (query != null) {
			searchRequestBuilder.postFilter(query);
		}
		else if (baseSearchRequest.getPostFilter() != null) {
			Filter filter = baseSearchRequest.getPostFilter();

			searchRequestBuilder.postFilter(
				new co.elastic.clients.elasticsearch._types.query_dsl.Query(
					filter.accept(ElasticsearchFilterVisitor.INSTANCE)));
		}
	}

	private void _setRequestCache(
		BaseSearchRequest baseSearchRequest,
		SearchRequest.Builder searchRequestBuilder) {

		if (baseSearchRequest.isRequestCache()) {
			searchRequestBuilder.requestCache(
				baseSearchRequest.isRequestCache());
		}
	}

	private void _setRescoreQuery(
		Query query, SearchRequest.Builder searchRequestBuilder) {

		if (query == null) {
			return;
		}

		RescoreQuery.Builder rescoreQueryBuilder = new RescoreQuery.Builder();

		rescoreQueryBuilder.query(
			new co.elastic.clients.elasticsearch._types.query_dsl.Query(
				com.liferay.portal.search.elasticsearch8.internal.query.
					ElasticsearchQueryVisitor.INSTANCE.translate(query)));

		co.elastic.clients.elasticsearch.core.search.Rescore.Builder
			rescoreBuilder =
				new co.elastic.clients.elasticsearch.core.search.Rescore.
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
				new co.elastic.clients.elasticsearch._types.query_dsl.Query(
					com.liferay.portal.search.elasticsearch8.internal.query.
						ElasticsearchQueryVisitor.INSTANCE.translate(
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

			co.elastic.clients.elasticsearch.core.search.Rescore.Builder
				rescoreBuilder =
					new co.elastic.clients.elasticsearch.core.search.Rescore.
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

	private co.elastic.clients.elasticsearch._types.query_dsl.Query
		_translateQuery(com.liferay.portal.kernel.search.Query query) {

		if (query == null) {
			return null;
		}

		co.elastic.clients.elasticsearch._types.query_dsl.Query
			translatedQuery =
				new co.elastic.clients.elasticsearch._types.query_dsl.Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(query));

		if ((query.getPreBooleanFilter() == null) ||
			(query instanceof com.liferay.portal.kernel.search.BooleanQuery)) {

			return translatedQuery;
		}

		// LPS-86537 the following is only present to allow for backwards
		// compatibility. Not all Query should have filters allowed according
		// to Elasticsearch's API.

		// See related note in BooleanQueryTranslatorImpl

		BoolQuery.Builder builder = QueryBuilders.bool();

		Filter filter = query.getPreBooleanFilter();

		builder.filter(
			new co.elastic.clients.elasticsearch._types.query_dsl.Query(
				filter.accept(ElasticsearchFilterVisitor.INSTANCE)));

		builder.must(translatedQuery);

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			builder.build());
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query
		_translateQuery(Query query) {

		if (query == null) {
			return null;
		}

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			com.liferay.portal.search.elasticsearch8.internal.query.
				ElasticsearchQueryVisitor.INSTANCE.translate(query));
	}

	private final FacetTranslator _facetTranslator = new FacetTranslator();
	private final StatsTranslator _statsTranslator = new StatsTranslator();

}