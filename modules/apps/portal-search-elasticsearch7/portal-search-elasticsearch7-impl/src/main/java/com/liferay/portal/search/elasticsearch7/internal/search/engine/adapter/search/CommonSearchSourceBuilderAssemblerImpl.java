/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.filter.ComplexQueryBuilderFactory;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.pit.PointInTime;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.builder.PointInTimeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.rescore.QueryRescoreMode;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = CommonSearchSourceBuilderAssembler.class)
public class CommonSearchSourceBuilderAssemblerImpl
	implements CommonSearchSourceBuilderAssembler {

	@Override
	public void assemble(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest, SearchRequest searchRequest) {

		_setAggregations(searchSourceBuilder, baseSearchRequest);
		_setExplain(searchSourceBuilder, baseSearchRequest);
		_setFacets(searchSourceBuilder, baseSearchRequest);
		_setIndexBoosts(searchSourceBuilder, baseSearchRequest);
		_setIndices(searchRequest, baseSearchRequest);
		_setMinScore(searchSourceBuilder, baseSearchRequest);
		_setPipelineAggregations(searchSourceBuilder, baseSearchRequest);
		_setPointInTime(searchSourceBuilder, baseSearchRequest);
		_setPostFilter(searchSourceBuilder, baseSearchRequest);
		setQuery(searchSourceBuilder, baseSearchRequest);
		_setRequestCache(searchRequest, baseSearchRequest);
		_setRescorer(searchSourceBuilder, baseSearchRequest);
		_setStatsRequests(searchSourceBuilder, baseSearchRequest);
		_setTimeout(searchSourceBuilder, baseSearchRequest);
		_setTrackTotalHits(searchSourceBuilder, baseSearchRequest);
		_setTypes(searchRequest, baseSearchRequest);

		searchRequest.source(searchSourceBuilder);
	}

	@Activate
	protected void activate() {
		_legacyQueryTranslator = new ElasticsearchQueryTranslator(
			_indexNameBuilder);
	}

	protected void setQuery(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		searchSourceBuilder.query(_getQueryBuilder(baseSearchRequest));
	}

	protected BoolQueryBuilder translate(
		List<ComplexQueryPart> complexQueryParts) {

		if (ListUtil.isEmpty(complexQueryParts)) {
			return null;
		}

		BooleanQuery booleanQuery = _buildComplexQuery(complexQueryParts);

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		_transfer(booleanQuery, boolQueryBuilder);

		return boolQueryBuilder;
	}

	protected QueryRescoreMode translate(Rescore.ScoreMode scoreMode) {
		if (scoreMode == Rescore.ScoreMode.AVG) {
			return QueryRescoreMode.Avg;
		}
		else if (scoreMode == Rescore.ScoreMode.MAX) {
			return QueryRescoreMode.Max;
		}
		else if (scoreMode == Rescore.ScoreMode.MIN) {
			return QueryRescoreMode.Min;
		}
		else if (scoreMode == Rescore.ScoreMode.MULTIPLY) {
			return QueryRescoreMode.Multiply;
		}
		else if (scoreMode == Rescore.ScoreMode.TOTAL) {
			return QueryRescoreMode.Total;
		}

		throw new IllegalArgumentException(
			"Invalid Rescore.ScoreMode: " + scoreMode);
	}

	private BooleanQuery _buildComplexQuery(
		List<ComplexQueryPart> complexQueryParts) {

		return (BooleanQuery)_complexQueryBuilderFactory.builder(
		).addParts(
			complexQueryParts
		).build();
	}

	private QueryBuilder _buildQueryBuilder(
		BaseSearchRequest baseSearchRequest) {

		QueryBuilder queryBuilder = null;

		if (baseSearchRequest.getPostFilterQuery() != null) {
			queryBuilder = _queryTranslator.translate(
				baseSearchRequest.getPostFilterQuery());
		}

		List<ComplexQueryPart> postFilterQueryParts =
			baseSearchRequest.getPostFilterComplexQueryParts();

		if (!postFilterQueryParts.isEmpty()) {
			queryBuilder = _combine(queryBuilder, postFilterQueryParts);
		}

		return queryBuilder;
	}

	private void _combine(
		BoolQueryBuilder boolQueryBuilder, ComplexQueryPart complexQueryPart) {

		Query query = _complexQueryBuilderFactory.builder(
		).buildPart(
			complexQueryPart
		);

		if (query == null) {
			return;
		}

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
	}

	private QueryBuilder _combine(
		BoolQueryBuilder boolQueryBuilder, QueryBuilder queryBuilder,
		BiConsumer<BoolQueryBuilder, QueryBuilder> biConsumer) {

		if (boolQueryBuilder == null) {
			return queryBuilder;
		}

		if (queryBuilder != null) {
			biConsumer.accept(boolQueryBuilder, queryBuilder);
		}

		return boolQueryBuilder;
	}

	private QueryBuilder _combine(
		QueryBuilder queryBuilder, List<ComplexQueryPart> complexQueryParts) {

		List<ComplexQueryPart> additiveComplexQueryParts = new ArrayList<>();
		List<ComplexQueryPart> nonadditiveComplexQueryParts = new ArrayList<>();

		for (ComplexQueryPart complexQueryPart : complexQueryParts) {
			if (complexQueryPart.isAdditive()) {
				additiveComplexQueryParts.add(complexQueryPart);
			}
			else {
				if (complexQueryPart.isRootClause() &&
					(queryBuilder instanceof BoolQueryBuilder)) {

					BoolQueryBuilder boolQueryBuilder =
						(BoolQueryBuilder)queryBuilder;

					_combine(boolQueryBuilder, complexQueryPart);
				}
				else {
					nonadditiveComplexQueryParts.add(complexQueryPart);
				}
			}
		}

		QueryBuilder queryBuilder1 = _combine(
			translate(nonadditiveComplexQueryParts), queryBuilder,
			BoolQueryBuilder::must);

		return _combine(
			translate(additiveComplexQueryParts), queryBuilder1,
			BoolQueryBuilder::should);
	}

	private QueryBuilder _combine(
		QueryBuilder queryBuilder1, QueryBuilder queryBuilder2) {

		if (queryBuilder1 == null) {
			return queryBuilder2;
		}

		if (queryBuilder2 == null) {
			return queryBuilder1;
		}

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		return boolQueryBuilder.must(
			queryBuilder1
		).must(
			queryBuilder2
		);
	}

	private void _copy(List<Query> clauses, Consumer<QueryBuilder> consumer) {
		for (Query query : clauses) {
			consumer.accept(_translateQuery(query));
		}
	}

	private QueryBuilder _getQueryBuilder(BaseSearchRequest baseSearchRequest) {
		QueryBuilder queryBuilder1 = _combine(
			_translateQuery(baseSearchRequest.getQuery()),
			_translateQuery(baseSearchRequest.getQuery71()));

		List<ComplexQueryPart> complexQueryParts =
			baseSearchRequest.getComplexQueryParts();

		if (complexQueryParts.isEmpty()) {
			QueryBuilder queryBuilder2 = _combine(
				translate(Collections.emptyList()), queryBuilder1,
				BoolQueryBuilder::must);

			return _combine(
				translate(Collections.emptyList()), queryBuilder2,
				BoolQueryBuilder::should);
		}

		return _combine(queryBuilder1, complexQueryParts);
	}

	private void _setAggregations(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		Map<String, Aggregation> aggregationsMap =
			baseSearchRequest.getAggregationsMap();

		if (MapUtil.isNotEmpty(aggregationsMap)) {
			Collection<Aggregation> aggregations = aggregationsMap.values();

			aggregations.forEach(
				aggregation -> {
					AggregationBuilder aggregationBuilder =
						_aggregationTranslator.translate(aggregation);

					searchSourceBuilder.aggregation(aggregationBuilder);
				});
		}
	}

	private void _setExplain(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getExplain() != null) {
			searchSourceBuilder.explain(baseSearchRequest.getExplain());
		}
	}

	private void _setFacets(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		_facetTranslator.translate(
			searchSourceBuilder, baseSearchRequest.getQuery71(),
			baseSearchRequest.getFacets(),
			baseSearchRequest.isBasicFacetSelection());
	}

	private void _setIndexBoosts(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		Map<String, Float> indexBoosts = baseSearchRequest.getIndexBoosts();

		if (MapUtil.isNotEmpty(indexBoosts)) {
			indexBoosts.forEach(searchSourceBuilder::indexBoost);
		}
	}

	private void _setIndices(
		SearchRequest searchRequest, BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getPointInTime() == null) {
			searchRequest.indices(baseSearchRequest.getIndexNames());
		}
	}

	private void _setMinScore(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getMinimumScore() != null) {
			searchSourceBuilder.minScore(baseSearchRequest.getMinimumScore());
		}
	}

	private void _setPipelineAggregations(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		Map<String, PipelineAggregation> pipelineAggregationsMap =
			baseSearchRequest.getPipelineAggregationsMap();

		if (MapUtil.isNotEmpty(pipelineAggregationsMap)) {
			Collection<PipelineAggregation> pipelineAggregations =
				pipelineAggregationsMap.values();

			pipelineAggregations.forEach(
				pipelineAggregation -> {
					PipelineAggregationBuilder pipelineAggregationBuilder =
						_pipelineAggregationTranslator.translate(
							pipelineAggregation);

					searchSourceBuilder.aggregation(pipelineAggregationBuilder);
				});
		}
	}

	private void _setPointInTime(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		PointInTime pointInTime = baseSearchRequest.getPointInTime();

		if (pointInTime != null) {
			PointInTimeBuilder pointInTimeBuilder = new PointInTimeBuilder(
				pointInTime.getPointInTimeId());

			if (pointInTime.getKeepAlive() != 0) {
				pointInTimeBuilder.setKeepAlive(
					TimeValue.timeValueSeconds(pointInTime.getKeepAlive()));
			}

			searchSourceBuilder.pointInTimeBuilder(pointInTimeBuilder);
		}
	}

	private void _setPostFilter(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		QueryBuilder queryBuilder = _buildQueryBuilder(baseSearchRequest);

		if (queryBuilder != null) {
			searchSourceBuilder.postFilter(queryBuilder);
		}
		else if (baseSearchRequest.getPostFilter() != null) {
			searchSourceBuilder.postFilter(
				_filterTranslator.translate(
					baseSearchRequest.getPostFilter(), null));
		}
	}

	private void _setRequestCache(
		SearchRequest searchRequest, BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getRequestCache() != null) {
			searchRequest.requestCache(baseSearchRequest.getRequestCache());
		}
	}

	private void _setRescorer(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		_setRescorers(searchSourceBuilder, baseSearchRequest.getRescores());

		_setRescorerQuery(
			searchSourceBuilder, baseSearchRequest.getRescoreQuery());
	}

	private void _setRescorerQuery(
		SearchSourceBuilder searchSourceBuilder, Query query) {

		if (query == null) {
			return;
		}

		searchSourceBuilder.addRescorer(
			new QueryRescorerBuilder(_queryTranslator.translate(query)));
	}

	private void _setRescorers(
		SearchSourceBuilder searchSourceBuilder, List<Rescore> rescores) {

		if (rescores == null) {
			return;
		}

		for (Rescore rescore : rescores) {
			QueryRescorerBuilder queryRescorerBuilder =
				new QueryRescorerBuilder(
					_queryTranslator.translate(rescore.getQuery()));

			if (rescore.getQueryWeight() != null) {
				queryRescorerBuilder.setQueryWeight(rescore.getQueryWeight());
			}

			if (rescore.getRescoreQueryWeight() != null) {
				queryRescorerBuilder.setRescoreQueryWeight(
					rescore.getRescoreQueryWeight());
			}

			if (rescore.getScoreMode() != null) {
				queryRescorerBuilder.setScoreMode(
					translate(rescore.getScoreMode()));
			}

			if (rescore.getWindowSize() != null) {
				queryRescorerBuilder.windowSize(rescore.getWindowSize());
			}

			searchSourceBuilder.addRescorer(queryRescorerBuilder);
		}
	}

	private void _setStatsRequests(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		List<StatsRequest> statsRequests = baseSearchRequest.getStatsRequests();

		if (ListUtil.isNotEmpty(statsRequests)) {
			statsRequests.forEach(
				statsRequest -> _statsTranslator.populateRequest(
					searchSourceBuilder, statsRequest));
		}
	}

	private void _setTimeout(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getTimeoutInMilliseconds() != null) {
			searchSourceBuilder.timeout(
				TimeValue.timeValueMillis(
					baseSearchRequest.getTimeoutInMilliseconds()));
		}
	}

	private void _setTrackTotalHits(
		SearchSourceBuilder searchSourceBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getTrackTotalHitsLimit() != null) {
			searchSourceBuilder.trackTotalHitsUpTo(
				baseSearchRequest.getTrackTotalHitsLimit());
		}
		else if (baseSearchRequest.getTrackTotalHits() != null) {
			searchSourceBuilder.trackTotalHits(
				baseSearchRequest.getTrackTotalHits());
		}
	}

	private void _setTypes(
		SearchRequest searchRequest, BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getTypes() != null) {
			searchRequest.types(baseSearchRequest.getTypes());
		}
	}

	private void _transfer(
		BooleanQuery booleanQuery, BoolQueryBuilder boolQueryBuilder) {

		_copy(booleanQuery.getFilterQueryClauses(), boolQueryBuilder::filter);
		_copy(booleanQuery.getMustNotQueryClauses(), boolQueryBuilder::mustNot);
		_copy(booleanQuery.getMustQueryClauses(), boolQueryBuilder::must);
		_copy(booleanQuery.getShouldQueryClauses(), boolQueryBuilder::should);
	}

	private QueryBuilder _translateQuery(
		com.liferay.portal.kernel.search.Query query) {

		if (query == null) {
			return null;
		}

		QueryBuilder queryBuilder = _legacyQueryTranslator.translate(
			query, null);

		if ((query.getPreBooleanFilter() == null) ||
			(query instanceof com.liferay.portal.kernel.search.BooleanQuery)) {

			return queryBuilder;
		}

		// LPS-86537 the following is only present to allow for backwards
		// compatibility. Not all Query should have filters allowed according
		// to Elasticsearch's API.

		// See related note in BooleanQueryTranslatorImpl

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.filter(
			_filterTranslator.translate(query.getPreBooleanFilter(), null));
		boolQueryBuilder.must(queryBuilder);

		return boolQueryBuilder;
	}

	private QueryBuilder _translateQuery(Query query) {
		if (query != null) {
			return _queryTranslator.translate(query);
		}

		return null;
	}

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private AggregationTranslator<AggregationBuilder> _aggregationTranslator;

	@Reference
	private ComplexQueryBuilderFactory _complexQueryBuilderFactory;

	@Reference
	private FacetTranslator _facetTranslator;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private FilterTranslator<QueryBuilder> _filterTranslator;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private com.liferay.portal.kernel.search.query.QueryTranslator<QueryBuilder>
		_legacyQueryTranslator;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private PipelineAggregationTranslator<PipelineAggregationBuilder>
		_pipelineAggregationTranslator;

	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new com.liferay.portal.search.elasticsearch7.internal.query.
			ElasticsearchQueryTranslator();

	@Reference
	private StatsTranslator _statsTranslator;

}