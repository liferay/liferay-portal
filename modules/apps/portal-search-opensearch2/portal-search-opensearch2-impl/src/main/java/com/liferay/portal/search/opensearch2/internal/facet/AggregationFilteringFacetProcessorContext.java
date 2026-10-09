/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.facet;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.facet.nested.NestedFacet;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ChildScoreMode;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField;

/**
 * @author Bryan Engler
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class AggregationFilteringFacetProcessorContext
	implements FacetProcessorContext {

	public static FacetProcessorContext newInstance(Collection<Facet> facets) {
		return new AggregationFilteringFacetProcessorContext(
			_getQueriesMap(facets));
	}

	@Override
	public Builder.ContainerBuilder postProcessAggregationBuilder(
		Builder.ContainerBuilder containerBuilder, String facetName) {

		Builder.ContainerBuilder selectionFilterContainerBuilder =
			_getFacetSelectionFilterContainerBuilder(facetName);

		if (selectionFilterContainerBuilder != null) {
			return selectionFilterContainerBuilder.aggregations(
				facetName, containerBuilder.build());
		}

		return containerBuilder;
	}

	private static void _addNestedFacetChildAggregationFilterQueries(
		BoolQuery.Builder builder, String fieldName, NestedFacet nestedFacet) {

		if (nestedFacet.getChildAggregation() instanceof DateRangeAggregation) {
			for (String value : nestedFacet.getSelections()) {
				DateRangeAggregation dateRangeAggregation =
					(DateRangeAggregation)nestedFacet.getChildAggregation();

				builder.must(
					_createRangeQuery(
						fieldName, dateRangeAggregation.getFormat(),
						RangeParserUtil.parserRange(value)));
			}
		}
		else if (nestedFacet.getChildAggregation() instanceof
					RangeAggregation) {

			for (String value : nestedFacet.getSelections()) {
				RangeAggregation rangeAggregation =
					(RangeAggregation)nestedFacet.getChildAggregation();

				builder.must(
					_createRangeQuery(
						fieldName, rangeAggregation.getFormat(),
						RangeParserUtil.parserRange(value)));
			}
		}
		else {
			Aggregation childAggregation = nestedFacet.getChildAggregation();

			Class<?> clazz = childAggregation.getClass();

			throw new UnsupportedOperationException(
				"Nested facet does not support child aggregation " +
					clazz.getName());
		}
	}

	private static Query _createRangeQuery(
		String fieldName, String format, String[] rangeParts) {

		RangeQuery.Builder builder = new RangeQuery.Builder();

		builder.field(fieldName);
		builder.gte(JsonData.of(rangeParts[0]));
		builder.lte(JsonData.of(rangeParts[1]));

		if (!Validator.isBlank(format)) {
			builder.format(format);
		}

		return new Query(builder.build());
	}

	private static List<Query> _getFacetSelectionFilterQueries(
		com.liferay.portal.search.facet.Facet facet) {

		List<Query> queries = new ArrayList<>();

		String fieldName = facet.getFieldName();

		if (facet instanceof NestedFacet) {
			NestedFacet nestedFacet = (NestedFacet)facet;

			BoolQuery.Builder builder = QueryBuilders.bool();

			if (Validator.isNotNull(nestedFacet.getFilterField())) {
				builder.must(
					new Query(
						QueryBuilders.terms(
						).field(
							nestedFacet.getFilterField()
						).terms(
							TermsQueryField.of(
								termsQueryField -> termsQueryField.value(
									ConversionUtil.toFieldValues(
										nestedFacet.getFilterValue())))
						).build()));
			}

			if (nestedFacet.getChildAggregation() != null) {
				_addNestedFacetChildAggregationFilterQueries(
					builder, fieldName, nestedFacet);
			}
			else {
				builder.must(
					new Query(
						QueryBuilders.terms(
						).field(
							fieldName
						).terms(
							TermsQueryField.of(
								termsQueryField -> termsQueryField.value(
									ConversionUtil.toFieldValues(
										facet.getSelections())))
						).build()));
			}

			queries.add(
				new Query(
					QueryBuilders.nested(
					).path(
						nestedFacet.getPath()
					).query(
						new Query(builder.build())
					).scoreMode(
						ChildScoreMode.Sum
					).build()));
		}
		else if (facet instanceof RangeFacet) {
			for (String value : facet.getSelections()) {
				queries.add(
					_createRangeQuery(
						fieldName, null, RangeParserUtil.parserRange(value)));
			}
		}
		else {
			queries.add(
				new Query(
					QueryBuilders.terms(
					).field(
						fieldName
					).terms(
						TermsQueryField.of(
							termsQueryField -> termsQueryField.value(
								ConversionUtil.toFieldValues(
									facet.getSelections())))
					).build()));
		}

		return queries;
	}

	private static Map<String, List<Query>> _getQueriesMap(
		Collection<Facet> facets) {

		Map<String, List<Query>> queriesMap = new HashMap<>();

		for (Facet facet : facets) {
			if (!(facet instanceof com.liferay.portal.search.facet.Facet) ||
				facet.isStatic()) {

				continue;
			}

			com.liferay.portal.search.facet.Facet osgiFacet =
				(com.liferay.portal.search.facet.Facet)facet;

			if (ArrayUtil.isEmpty(osgiFacet.getSelections())) {
				continue;
			}

			queriesMap.put(
				osgiFacet.getAggregationName(),
				_getFacetSelectionFilterQueries(osgiFacet));
		}

		return queriesMap;
	}

	private AggregationFilteringFacetProcessorContext(
		Map<String, List<Query>> queriesMap) {

		_queriesMap = queriesMap;
	}

	private Builder.ContainerBuilder _getFacetSelectionFilterContainerBuilder(
		String aggregationName) {

		if (_queriesMap.isEmpty()) {
			return null;
		}

		BoolQuery boolQuery = _getRelatedSelectionsFilterQuery(aggregationName);

		if (ListUtil.isEmpty(boolQuery.must()) &&
			ListUtil.isEmpty(boolQuery.mustNot()) &&
			ListUtil.isEmpty(boolQuery.should())) {

			return null;
		}

		Builder builder = new Builder();

		return builder.filter(new Query(boolQuery));
	}

	private BoolQuery _getRelatedSelectionsFilterQuery(String aggregationName) {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		for (Map.Entry<String, List<Query>> entry : _queriesMap.entrySet()) {
			String filterAggregationName = entry.getKey();

			if (filterAggregationName.equals(aggregationName)) {
				continue;
			}

			List<Query> queries = entry.getValue();

			if (queries.size() == 1) {
				boolQueryBuilder.must(queries.get(0));
			}
			else if (queries.size() > 1) {
				BoolQuery.Builder filterBoolQueryBuilder = QueryBuilders.bool();

				for (Query query : queries) {
					filterBoolQueryBuilder.should(query);
				}

				boolQueryBuilder.must(
					new Query(filterBoolQueryBuilder.build()));
			}
		}

		return boolQueryBuilder.build();
	}

	private final Map<String, List<Query>> _queriesMap;

}