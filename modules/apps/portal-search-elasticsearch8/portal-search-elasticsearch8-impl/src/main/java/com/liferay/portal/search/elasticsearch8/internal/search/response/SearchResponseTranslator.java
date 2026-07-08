/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.response;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregate;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;

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
import com.liferay.portal.search.elasticsearch8.internal.facet.FacetCollectorFactory;
import com.liferay.portal.search.elasticsearch8.internal.facet.FacetUtil;
import com.liferay.portal.search.elasticsearch8.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.elasticsearch8.internal.stats.StatsTranslator;
import com.liferay.portal.search.elasticsearch8.internal.util.ConversionUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.groupby.GroupByResponse;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsResultsTranslator;
import com.liferay.portal.search.stats.StatsRequest;
import com.liferay.portal.search.stats.StatsRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dylan Rebelak
 */
public class SearchResponseTranslator {

	public void populate(
		ResponseBody<JsonData> responseBody,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		HitsMetadata<JsonData> hitsMetadata = responseBody.hits();

		Hits hits = new HitsImpl();

		hits.setSearchTime((float)(responseBody.took() / 1000.0));

		_updateFacetCollectors(searchSearchRequest.getFacets(), responseBody);

		_updateGroupedHits(
			searchSearchRequest.getAlternateUidFieldName(), hits,
			searchSearchRequest.getLocale(), responseBody, searchSearchRequest,
			searchSearchResponse);

		_updateStatsResults(
			responseBody.aggregations(), hits, searchSearchRequest.getStats());

		_processHits(
			searchSearchRequest.getAlternateUidFieldName(), hits, hitsMetadata,
			searchSearchRequest.getLocale());

		searchSearchResponse.setHits(hits);
	}

	protected StatsResults getStatsResults(
		Map<String, Aggregate> aggregates, Stats stats) {

		return StatsResultsTranslator.INSTANCE.translate(
			_statsTranslator.translateResponse(aggregates, _translate(stats)));
	}

	private void _addInnerHitsSnippets(
		Document document, Hit<JsonData> hit, Locale locale) {

		Map<String, InnerHitsResult> innerHitsResults = hit.innerHits();

		if (MapUtil.isEmpty(innerHitsResults)) {
			return;
		}

		for (InnerHitsResult innerHitsResult : innerHitsResults.values()) {
			HitsMetadata<JsonData> hitsMetadata = innerHitsResult.hits();

			if ((hitsMetadata == null) ||
				ListUtil.isEmpty(hitsMetadata.hits())) {

				continue;
			}

			for (Hit<JsonData> innerHit : hitsMetadata.hits()) {
				_addSnippets(document, innerHit, locale);
			}
		}
	}

	private void _addSnippets(
		Document document, Hit<JsonData> hit, Locale locale) {

		Map<String, List<String>> highlights = hit.highlight();

		MapUtil.isNotEmptyForEach(
			hit.highlight(),
			(fieldName, fragments) -> _addSnippets(
				document, fieldName, highlights, locale));

		_addInnerHitsSnippets(document, hit, locale);
	}

	private void _addSnippets(
		Document document, String fieldName,
		Map<String, List<String>> highlights, Locale locale) {

		String snippetFieldName = fieldName;

		if (!fieldName.startsWith("nestedFieldArray.")) {
			snippetFieldName = Field.getLocalizedName(locale, fieldName);
		}

		List<String> fragments = highlights.get(snippetFieldName);

		if (fragments == null) {
			fragments = highlights.get(fieldName);
			snippetFieldName = fieldName;
		}

		if (ListUtil.isEmpty(fragments)) {
			return;
		}

		String snippetName = StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, snippetFieldName);
		String snippetValue = StringUtil.merge(
			fragments, StringPool.TRIPLE_PERIOD);

		if (document.getField(snippetName) != null) {
			String existingSnippetValue = document.get(snippetName);

			if (snippetValue.equals(existingSnippetValue)) {
				return;
			}

			snippetValue = StringBundler.concat(
				existingSnippetValue, StringPool.TRIPLE_PERIOD, snippetValue);
		}

		document.add(new Field(snippetName, snippetValue));
	}

	private FacetCollector _getFacetCollector(
		Map<String, Aggregate> aggregations, Facet facet) {

		FacetCollectorFactory facetCollectorFactory =
			new FacetCollectorFactory();

		return facetCollectorFactory.getFacetCollector(
			aggregations.get(FacetUtil.getAggregationName(facet)),
			FacetUtil.getAggregationName(facet));
	}

	private void _populateUID(String alternateUidFieldName, Document document) {
		Field uidField = document.getField(Field.UID);

		if ((uidField != null) || Validator.isNull(alternateUidFieldName)) {
			return;
		}

		String uidValue = document.get(alternateUidFieldName);

		if (Validator.isNotNull(uidValue)) {
			document.add(new Field(Field.UID, uidValue));
		}
	}

	private Document _processHit(
		String alternateUidFieldName, Hit<JsonData> hit) {

		Document document = SearchHitDocumentTranslator.INSTANCE.translate(hit);

		_populateUID(alternateUidFieldName, document);

		return document;
	}

	private Hits _processHits(
		String alternateUidFieldName, Hits hits,
		HitsMetadata<JsonData> hitsMetadata, Locale locale) {

		List<Document> documents = new ArrayList<>();
		List<Float> scores = new ArrayList<>();

		TotalHits totalHits = hitsMetadata.total();

		if (totalHits.value() > 0) {
			for (Hit<JsonData> hit : hitsMetadata.hits()) {
				Document document = _processHit(alternateUidFieldName, hit);

				documents.add(document);

				scores.add(ConversionUtil.toFloat(hit.score(), 0.0F));

				_addSnippets(document, hit, locale);
			}
		}

		hits.setDocs(documents.toArray(new Document[0]));
		hits.setLength((int)totalHits.value());
		hits.setQueryTerms(new String[0]);
		hits.setScores(ArrayUtil.toFloatArray(scores));

		return hits;
	}

	private StatsRequest _translate(Stats stats) {
		StatsRequestBuilder statsRequestBuilder =
			StatsRequestBuilderFactory.getStatsRequestBuilder(stats);

		return statsRequestBuilder.build();
	}

	private void _updateFacetCollectors(
		Map<String, Facet> facets, ResponseBody<JsonData> responseBody) {

		Map<String, Aggregate> aggregations = responseBody.aggregations();

		if (aggregations == null) {
			return;
		}

		for (Facet facet : facets.values()) {
			if (!facet.isStatic()) {
				facet.setFacetCollector(
					_getFacetCollector(aggregations, facet));
			}
		}
	}

	private void _updateGroupedHits(
		String alternateUidFieldName, Hits hits, Locale locale,
		ResponseBody<JsonData> responseBody,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		List<GroupByRequest> groupByRequests =
			searchSearchRequest.getGroupByRequests();

		if (ListUtil.isNotEmpty(groupByRequests)) {
			for (GroupByRequest groupByRequest : groupByRequests) {
				_updateGroupedHits(
					alternateUidFieldName, groupByRequest.getField(), hits,
					locale, responseBody, searchSearchResponse);
			}
		}

		GroupBy groupBy = searchSearchRequest.getGroupBy();

		if (groupBy != null) {
			_updateGroupedHits(
				alternateUidFieldName, groupBy.getField(), hits, locale,
				responseBody, searchSearchResponse);
		}
	}

	private void _updateGroupedHits(
		String alternateUidFieldName, String field, Hits hits, Locale locale,
		ResponseBody<JsonData> responseBody,
		SearchSearchResponse searchSearchResponse) {

		Map<String, Aggregate> aggregations = responseBody.aggregations();

		Aggregate aggregate1 = aggregations.get(
			GroupByTranslator.GROUP_BY_AGGREGATION_PREFIX + field);

		StringTermsAggregate stringTermsAggregate = aggregate1.sterms();

		Buckets<StringTermsBucket> buckets = stringTermsAggregate.buckets();

		List<StringTermsBucket> stringTermsBuckets = buckets.array();

		GroupByResponse groupByResponse = new GroupByResponse(field);

		searchSearchResponse.addGroupByResponse(groupByResponse);

		for (StringTermsBucket stringTermsBucket : stringTermsBuckets) {
			Map<String, Aggregate> stringTermsBucketAggregations =
				stringTermsBucket.aggregations();

			Aggregate aggregate2 = stringTermsBucketAggregations.get(
				GroupByTranslator.TOP_HITS_AGGREGATION_NAME);

			TopHitsAggregate topHitsAggregate = aggregate2.topHits();

			HitsMetadata<JsonData> hitsMetadata = topHitsAggregate.hits();

			Hits groupedHits = new HitsImpl();

			_processHits(
				alternateUidFieldName, groupedHits, hitsMetadata, locale);

			TotalHits totalHits = hitsMetadata.total();

			groupedHits.setLength((int)totalHits.value());

			FieldValue fieldValue = stringTermsBucket.key();

			hits.addGroupedHits(fieldValue._toJsonString(), groupedHits);

			groupByResponse.putHits(fieldValue._toJsonString(), groupedHits);
		}
	}

	private void _updateStatsResults(
		Map<String, Aggregate> aggregations, Hits hits,
		Map<String, Stats> statsMap) {

		if ((aggregations == null) || MapUtil.isEmpty(statsMap)) {
			return;
		}

		for (Stats stats : statsMap.values()) {
			hits.addStatsResults(getStatsResults(aggregations, stats));
		}
	}

	private final StatsTranslator _statsTranslator = new StatsTranslator();

}