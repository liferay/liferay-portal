/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.response;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.GroupBy;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Stats;
import com.liferay.portal.kernel.search.StatsResults;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.SearchHitDocumentTranslator;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetCollectorFactory;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetUtil;
import com.liferay.portal.search.elasticsearch7.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.groupby.GroupByResponse;
import com.liferay.portal.search.groupby.GroupByResponseFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsResultsTranslator;
import com.liferay.portal.search.stats.StatsRequest;
import com.liferay.portal.search.stats.StatsRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.search.TotalHits;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

/**
 * @author Dylan Rebelak
 */
public class SearchResponseTranslator {

	public SearchResponseTranslator(
		GroupByResponseFactory groupByResponseFactory,
		SearchHitDocumentTranslator searchHitDocumentTranslator,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsResultsTranslator statsResultsTranslator,
		StatsTranslator statsTranslator) {

		_groupByResponseFactory = groupByResponseFactory;
		_searchHitDocumentTranslator = searchHitDocumentTranslator;
		_statsRequestBuilderFactory = statsRequestBuilderFactory;
		_statsResultsTranslator = statsResultsTranslator;
		_statsTranslator = statsTranslator;
	}

	public void populate(
		SearchSearchResponse searchSearchResponse,
		SearchResponse searchResponse,
		SearchSearchRequest searchSearchRequest) {

		SearchHits searchHits = searchResponse.getHits();

		Hits hits = new HitsImpl();

		_updateFacetCollectors(searchResponse, searchSearchRequest.getFacets());

		_updateGroupedHits(
			searchSearchResponse, searchResponse, searchSearchRequest, hits,
			searchSearchRequest.getAlternateUidFieldName(),
			searchSearchRequest.getLocale());

		_updateStatsResults(
			hits, searchResponse.getAggregations(),
			searchSearchRequest.getStats());

		TimeValue timeValue = searchResponse.getTook();

		hits.setSearchTime((float)timeValue.getSecondsFrac());

		_processSearchHits(
			searchHits, hits, searchSearchRequest.getAlternateUidFieldName(),
			searchSearchRequest.getLocale());

		searchSearchResponse.setHits(hits);
	}

	protected StatsResults getStatsResults(
		Map<String, Aggregation> aggregationsMap, Stats stats) {

		return _statsResultsTranslator.translate(
			_statsTranslator.translateResponse(
				aggregationsMap, _translate(stats)));
	}

	private void _addSnippets(
		Document document, Map<String, HighlightField> highlightFields,
		String fieldName, Locale locale) {

		String snippetFieldName = fieldName;

		if (!fieldName.startsWith("nestedFieldArray.")) {
			snippetFieldName = Field.getLocalizedName(locale, fieldName);
		}

		HighlightField highlightField = highlightFields.get(snippetFieldName);

		if (highlightField == null) {
			highlightField = highlightFields.get(fieldName);

			snippetFieldName = fieldName;
		}

		if (highlightField == null) {
			return;
		}

		Object[] array = highlightField.fragments();

		document.add(
			new Field(
				StringBundler.concat(
					Field.SNIPPET, StringPool.UNDERLINE, snippetFieldName),
				StringUtil.merge(array, StringPool.TRIPLE_PERIOD)));
	}

	private void _addSnippets(SearchHit hit, Document document, Locale locale) {
		Map<String, HighlightField> highlightFields = hit.getHighlightFields();

		if (MapUtil.isEmpty(highlightFields)) {
			return;
		}

		highlightFields.forEach(
			(fieldName, highlightField) -> _addSnippets(
				document, highlightFields, fieldName, locale));
	}

	private FacetCollector _getFacetCollector(
		Facet facet, Map<String, Aggregation> aggregationsMap) {

		FacetCollectorFactory facetCollectorFactory =
			new FacetCollectorFactory();

		return facetCollectorFactory.getFacetCollector(
			aggregationsMap.get(FacetUtil.getAggregationName(facet)));
	}

	private void _populateUID(Document document, String alternateUidFieldName) {
		Field uidField = document.getField(Field.UID);

		if ((uidField != null) || Validator.isNull(alternateUidFieldName)) {
			return;
		}

		String uidValue = document.get(alternateUidFieldName);

		if (Validator.isNotNull(uidValue)) {
			uidField = new Field(Field.UID, uidValue);

			document.add(uidField);
		}
	}

	private Document _processSearchHit(
		SearchHit searchHit, String alternateUidFieldName) {

		Document document = _searchHitDocumentTranslator.translate(searchHit);

		_populateUID(document, alternateUidFieldName);

		return document;
	}

	private Hits _processSearchHits(
		SearchHits searchHits, Hits hits, String alternateUidFieldName,
		Locale locale) {

		List<Document> documents = new ArrayList<>();
		List<Float> scores = new ArrayList<>();

		TotalHits totalHits = searchHits.getTotalHits();

		if (totalHits.value > 0) {
			SearchHit[] searchHitsArray = searchHits.getHits();

			for (SearchHit searchHit : searchHitsArray) {
				Document document = _processSearchHit(
					searchHit, alternateUidFieldName);

				documents.add(document);

				scores.add(searchHit.getScore());

				_addSnippets(searchHit, document, locale);
			}
		}

		hits.setDocs(documents.toArray(new Document[0]));
		hits.setLength((int)totalHits.value);
		hits.setQueryTerms(new String[0]);
		hits.setScores(ArrayUtil.toFloatArray(scores));

		return hits;
	}

	private StatsRequest _translate(Stats stats) {
		StatsRequestBuilder statsRequestBuilder =
			_statsRequestBuilderFactory.getStatsRequestBuilder(stats);

		return statsRequestBuilder.build();
	}

	private void _updateFacetCollectors(
		SearchResponse searchResponse, Map<String, Facet> facetsMap) {

		Aggregations aggregations = searchResponse.getAggregations();

		if (aggregations == null) {
			return;
		}

		Map<String, Aggregation> aggregationsMap = aggregations.getAsMap();

		for (Facet facet : facetsMap.values()) {
			if (!facet.isStatic()) {
				facet.setFacetCollector(
					_getFacetCollector(facet, aggregationsMap));
			}
		}
	}

	private void _updateGroupedHits(
		SearchSearchResponse searchSearchResponse,
		SearchResponse searchResponse, SearchSearchRequest searchSearchRequest,
		Hits hits, String alternateUidFieldName, Locale locale) {

		List<GroupByRequest> groupByRequests =
			searchSearchRequest.getGroupByRequests();

		if (ListUtil.isNotEmpty(groupByRequests)) {
			for (GroupByRequest groupByRequest : groupByRequests) {
				_updateGroupedHits(
					searchSearchResponse, searchResponse,
					groupByRequest.getField(), hits, alternateUidFieldName,
					locale);
			}
		}

		GroupBy groupBy = searchSearchRequest.getGroupBy();

		if (groupBy != null) {
			_updateGroupedHits(
				searchSearchResponse, searchResponse, groupBy.getField(), hits,
				alternateUidFieldName, locale);
		}
	}

	private void _updateGroupedHits(
		SearchSearchResponse searchSearchResponse,
		SearchResponse searchResponse, String field, Hits hits,
		String alternateUidFieldName, Locale locale) {

		Aggregations aggregations = searchResponse.getAggregations();

		Map<String, Aggregation> aggregationsMap = aggregations.getAsMap();

		Terms terms = (Terms)aggregationsMap.get(
			GroupByTranslator.GROUP_BY_AGGREGATION_PREFIX + field);

		List<? extends Terms.Bucket> buckets = terms.getBuckets();

		GroupByResponse groupByResponse =
			_groupByResponseFactory.getGroupByResponse(field);

		searchSearchResponse.addGroupByResponse(groupByResponse);

		for (Terms.Bucket bucket : buckets) {
			Aggregations bucketAggregations = bucket.getAggregations();

			TopHits topHits = bucketAggregations.get(
				GroupByTranslator.TOP_HITS_AGGREGATION_NAME);

			SearchHits groupedSearchHits = topHits.getHits();

			Hits groupedHits = new HitsImpl();

			_processSearchHits(
				groupedSearchHits, groupedHits, alternateUidFieldName, locale);

			TotalHits totalHits = groupedSearchHits.getTotalHits();

			groupedHits.setLength((int)totalHits.value);

			hits.addGroupedHits(bucket.getKeyAsString(), groupedHits);

			groupByResponse.putHits(bucket.getKeyAsString(), groupedHits);
		}
	}

	private void _updateStatsResults(
		Hits hits, Aggregations aggregations, Map<String, Stats> statsMap) {

		if (aggregations != null) {
			_updateStatsResults(hits, aggregations.getAsMap(), statsMap);
		}
	}

	private void _updateStatsResults(
		Hits hits, Map<String, Aggregation> aggregationsMap,
		Map<String, Stats> statsMap) {

		if (MapUtil.isNotEmpty(statsMap)) {
			for (Stats stats : statsMap.values()) {
				hits.addStatsResults(getStatsResults(aggregationsMap, stats));
			}
		}
	}

	private final GroupByResponseFactory _groupByResponseFactory;
	private final SearchHitDocumentTranslator _searchHitDocumentTranslator;
	private final StatsRequestBuilderFactory _statsRequestBuilderFactory;
	private final StatsResultsTranslator _statsResultsTranslator;
	private final StatsTranslator _statsTranslator;

}