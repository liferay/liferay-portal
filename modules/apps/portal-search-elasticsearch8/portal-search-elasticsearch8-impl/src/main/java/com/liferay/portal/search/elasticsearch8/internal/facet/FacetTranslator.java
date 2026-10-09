/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.facet;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsInclude;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.util.NamedValue;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.facet.DateRangeFacet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.elasticsearch8.internal.filter.ElasticsearchFilterVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class FacetTranslator {

	public void translate(
		boolean basicFacetSelection, Map<String, Facet> facetsMap, Query query,
		SearchRequest.Builder searchRequestBuilder) {

		if (MapUtil.isEmpty(facetsMap)) {
			return;
		}

		Collection<Facet> facets = facetsMap.values();

		FacetProcessorContext facetProcessorContext = _getFacetProcessorContext(
			facets, basicFacetSelection);

		List<co.elastic.clients.elasticsearch._types.query_dsl.Query>
			postFilterQueries = new ArrayList<>();

		if ((query != null) && (query.getPostFilter() != null)) {
			Filter filter = query.getPostFilter();

			postFilterQueries.add(
				new co.elastic.clients.elasticsearch._types.query_dsl.Query(
					filter.accept(ElasticsearchFilterVisitor.INSTANCE)));
		}

		for (Facet facet : facets) {
			if (facet.isStatic()) {
				continue;
			}

			BooleanClause<Filter> booleanClause =
				facet.getFacetFilterBooleanClause();

			if (booleanClause != null) {
				co.elastic.clients.elasticsearch._types.query_dsl.Query
					postFilterQuery = _getPostFilterQuery(booleanClause);

				if (postFilterQuery != null) {
					postFilterQueries.add(postFilterQuery);
				}
			}

			Aggregation.Builder.ContainerBuilder containerBuilder =
				_processFacet(facet);

			if (containerBuilder == null) {
				continue;
			}

			String facetName = FacetUtil.getAggregationName(facet);

			Aggregation.Builder.ContainerBuilder postProcessContainerBuilder =
				postProcessAggregation(
					containerBuilder, facetName, facetProcessorContext);

			if (postProcessContainerBuilder == null) {
				continue;
			}

			searchRequestBuilder.aggregations(
				facetName, postProcessContainerBuilder.build());
		}

		if (ListUtil.isNotEmpty(postFilterQueries)) {
			searchRequestBuilder.postFilter(
				_getPostFilterQuery(postFilterQueries));
		}
	}

	protected Aggregation.Builder.ContainerBuilder postProcessAggregation(
		Aggregation.Builder.ContainerBuilder containerBuilder, String facetName,
		FacetProcessorContext facetProcessorContext) {

		if (facetProcessorContext != null) {
			return facetProcessorContext.postProcessAggregationBuilder(
				containerBuilder, facetName);
		}

		return containerBuilder;
	}

	private FacetProcessorContext _getFacetProcessorContext(
		Collection<Facet> facets, boolean basicFacetSelection) {

		if (basicFacetSelection) {
			return null;
		}

		return AggregationFilteringFacetProcessorContext.newInstance(facets);
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query
		_getPostFilterQuery(BooleanClause<Filter> booleanClause) {

		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(
			booleanClause.getClause(), booleanClause.getBooleanClauseOccur());

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			booleanFilter.accept(ElasticsearchFilterVisitor.INSTANCE));
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query
		_getPostFilterQuery(
			List<co.elastic.clients.elasticsearch._types.query_dsl.Query>
				queries) {

		if (queries.size() == 1) {
			return queries.get(0);
		}

		BoolQuery.Builder builder = QueryBuilders.bool();

		for (co.elastic.clients.elasticsearch._types.query_dsl.Query query :
				queries) {

			builder.must(query);
		}

		return new co.elastic.clients.elasticsearch._types.query_dsl.Query(
			builder.build());
	}

	private Aggregation.Builder.ContainerBuilder _processFacet(Facet facet) {
		Class<?> clazz = facet.getClass();

		FacetProcessor<SearchRequest.Builder> facetProcessor =
			_facetProcessors.getOrDefault(
				clazz.getName(), _defaultFacetProcessor);

		return facetProcessor.processFacet(facet);
	}

	private static final FacetProcessor<SearchRequest.Builder>
		_defaultFacetProcessor = new FacetProcessor<SearchRequest.Builder>() {

			@Override
			public Aggregation.Builder.ContainerBuilder processFacet(
				Facet facet) {

				Aggregation.Builder aggregationBuilder =
					new Aggregation.Builder();

				TermsAggregation.Builder builder = AggregationBuilders.terms();

				builder.field(facet.getFieldName());

				FacetConfiguration facetConfiguration =
					facet.getFacetConfiguration();

				JSONObject dataJSONObject = facetConfiguration.getData();

				String include = dataJSONObject.getString("include", null);

				if (include != null) {
					builder.include(
						TermsInclude.of(
							termsInclude -> termsInclude.regexp(include)));
				}

				int minDocCount = dataJSONObject.getInt(
					"frequencyThreshold", -1);

				if (minDocCount >= 0) {
					builder.minDocCount(minDocCount);
				}

				builder.order(NamedValue.of("_count", SortOrder.Desc));

				int size = dataJSONObject.getInt("maxTerms");

				if (size > 0) {
					builder.size(size);
				}

				return aggregationBuilder.terms(builder.build());
			}

		};

	private static final Map<String, FacetProcessor<SearchRequest.Builder>>
		_facetProcessors =
			HashMapBuilder.<String, FacetProcessor<SearchRequest.Builder>>put(
				DateRangeFacet.class.getName(), DateRangeFacetProcessor.INSTANCE
			).put(
				"com.liferay.portal.search.internal.facet.DateRangeFacetImpl",
				DateRangeFacetProcessor.INSTANCE
			).put(
				"com.liferay.portal.search.internal.facet.ModifiedFacetImpl",
				DateRangeFacetProcessor.INSTANCE
			).put(
				"com.liferay.portal.search.internal.facet.NestedFacetImpl",
				NestedFacetProcessor.INSTANCE
			).put(
				RangeFacet.class.getName(), RangeFacetProcessor.INSTANCE
			).put(
				"com.liferay.portal.search.internal.facet.RangeFacetImpl",
				RangeFacetProcessor.INSTANCE
			).build();

}