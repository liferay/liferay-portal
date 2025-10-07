/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.response;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.GroupBy;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.StatsResults;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.groupby.GroupByResponse;
import com.liferay.portal.search.groupby.GroupByResponseFactory;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHitBuilder;
import com.liferay.portal.search.hits.SearchHitBuilderFactory;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.hits.SearchHitsBuilderFactory;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.legacy.stats.StatsResultsTranslator;
import com.liferay.portal.search.searcher.SearchTimeValue;
import com.liferay.portal.search.solr8.internal.facet.SolrFacetFieldCollector;
import com.liferay.portal.search.solr8.internal.facet.SolrFacetQueryCollector;
import com.liferay.portal.search.solr8.internal.stats.StatsTranslator;
import com.liferay.portal.search.stats.StatsResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SearchSearchResponseAssemblerHelper.class)
public class DefaultSearchSearchResponseAssemblerHelperImpl
	implements SearchSearchResponseAssemblerHelper {

	@Override
	public void populate(
		SearchSearchResponse searchSearchResponse, QueryResponse queryResponse,
		SearchSearchRequest searchSearchRequest) {

		Hits hits = new HitsImpl();

		updateFacetCollectors(queryResponse, searchSearchRequest);
		updateGroupedHits(
			searchSearchResponse, queryResponse, searchSearchRequest, hits);
		updateStatsResults(queryResponse, searchSearchRequest, hits);

		hits.setQuery(searchSearchRequest.getQuery71());
		hits.setSearchTime(queryResponse.getQTime());

		processSearchHits(
			queryResponse, queryResponse.getResults(),
			searchSearchRequest.getQuery71(), hits, searchSearchResponse);

		_setSearchTimeValue(queryResponse, searchSearchResponse);
	}

	protected void addSnippets(
		Document document, Map<String, List<String>> highlights,
		String fieldName, Locale locale) {

		String snippetFieldName = Field.getLocalizedName(locale, fieldName);

		List<String> list = highlights.get(snippetFieldName);

		if (list == null) {
			list = highlights.get(fieldName);

			snippetFieldName = fieldName;
		}

		if (ListUtil.isEmpty(list)) {
			return;
		}

		document.addText(
			StringBundler.concat(
				Field.SNIPPET, StringPool.UNDERLINE, snippetFieldName),
			StringUtil.merge(list, StringPool.TRIPLE_PERIOD));
	}

	protected void addSnippets(
		SolrDocument solrDocument, Document document, QueryConfig queryConfig,
		QueryResponse queryResponse) {

		if (!queryConfig.isHighlightEnabled()) {
			return;
		}

		Map<String, Map<String, List<String>>> highlights =
			queryResponse.getHighlighting();

		if (MapUtil.isEmpty(highlights)) {
			return;
		}

		String uid = (String)solrDocument.getFieldValue(Field.UID);

		for (String highlightFieldName : queryConfig.getHighlightFieldNames()) {
			addSnippets(
				document, highlights.get(uid), highlightFieldName,
				queryConfig.getLocale());
		}
	}

	protected FacetCollector getFacetCollector(
		Facet facet, NamedList<?> namedList) {

		if (facet instanceof RangeFacet) {
			return new SolrFacetQueryCollector(facet, namedList);
		}

		return new SolrFacetFieldCollector(facet, namedList);
	}

	protected Map<String, StatsResponse> getStatsResponseMap(
		QueryResponse queryResponse) {

		Map<String, FieldStatsInfo> fieldStatsInfoMap =
			queryResponse.getFieldStatsInfo();

		if (MapUtil.isEmpty(fieldStatsInfoMap)) {
			return null;
		}

		Map<String, StatsResponse> statsResponseMap = new LinkedHashMap<>();

		for (FieldStatsInfo fieldStatsInfo : fieldStatsInfoMap.values()) {
			StatsResponse statsResponse = _statsTranslator.translateResponse(
				fieldStatsInfo);

			statsResponseMap.put(fieldStatsInfo.getName(), statsResponse);
		}

		return statsResponseMap;
	}

	protected StatsResults getStatsResults(StatsResponse statsResponse) {
		return _statsResultsTranslator.translate(statsResponse);
	}

	protected void populateUID(Document document, QueryConfig queryConfig) {
		Field uidField = document.getField(Field.UID);

		if ((uidField != null) ||
			Validator.isNull(queryConfig.getAlternateUidFieldName())) {

			return;
		}

		String uidValue = document.get(queryConfig.getAlternateUidFieldName());

		if (Validator.isNotNull(uidValue)) {
			uidField = new Field(Field.UID, uidValue);

			document.add(uidField);
		}
	}

	protected void processSearchHits(
		QueryResponse queryResponse, SolrDocumentList solrDocumentList,
		Query query, Hits hits, SearchSearchResponse searchSearchResponse) {

		List<Document> documents = new ArrayList<>();
		List<Float> scores = new ArrayList<>();

		processSolrDocumentList(
			queryResponse, solrDocumentList, query, hits, documents, scores);

		hits.setDocs(documents.toArray(new Document[0]));
		hits.setQueryTerms(new String[0]);
		hits.setScores(ArrayUtil.toFloatArray(scores));

		searchSearchResponse.setCount(hits.getLength());
		searchSearchResponse.setHits(hits);
		searchSearchResponse.setSearchHits(
			toSearchHits(documents, hits.getLength()));
	}

	protected Document processSolrDocument(
		SolrDocument solrDocument, QueryConfig queryConfig) {

		Document document = new DocumentImpl();

		Collection<String> fieldNames = solrDocument.getFieldNames();

		for (String fieldName : fieldNames) {
			if (ArrayUtil.contains(_EXCLUDED_FIELDS, fieldName)) {
				continue;
			}

			Collection<Object> fieldValues = solrDocument.getFieldValues(
				fieldName);

			Field field = new Field(
				fieldName,
				ArrayUtil.toStringArray(fieldValues.toArray(new Object[0])));

			document.add(field);
		}

		populateUID(document, queryConfig);

		return document;
	}

	protected void processSolrDocumentList(
		QueryResponse queryResponse, SolrDocumentList solrDocumentList,
		Query query, Hits hits, List<Document> documents, List<Float> scores) {

		if (solrDocumentList == null) {
			Collections.addAll(documents, hits.getDocs());

			return;
		}

		hits.setLength((int)solrDocumentList.getNumFound());

		for (SolrDocument solrDocument : solrDocumentList) {
			QueryConfig queryConfig = query.getQueryConfig();

			Document document = processSolrDocument(solrDocument, queryConfig);

			documents.add(document);

			addSnippets(solrDocument, document, queryConfig, queryResponse);

			float score = GetterUtil.getFloat(
				String.valueOf(solrDocument.getFieldValue("score")));

			scores.add(score);
		}
	}

	protected SearchHit toSearchHit(Document document) {
		SearchHitBuilder searchHitBuilder =
			_searchHitBuilderFactory.getSearchHitBuilder();

		return searchHitBuilder.document(
			_documentBuilderFactory.builder(
				document
			).build()
		).id(
			document.get("uid")
		).build();
	}

	protected SearchHits toSearchHits(List<Document> documents, int totalHits) {
		SearchHitsBuilder searchHitsBuilder =
			_searchHitsBuilderFactory.getSearchHitsBuilder();

		return searchHitsBuilder.addSearchHits(
			TransformUtil.transform(documents, this::toSearchHit)
		).totalHits(
			totalHits
		).build();
	}

	protected void updateFacetCollectors(
		QueryResponse queryResponse, SearchSearchRequest searchSearchRequest) {

		NamedList<Object> namedList1 = queryResponse.getResponse();

		NamedList<?> namedList2 = (NamedList)namedList1.get("facets");

		if (namedList2 == null) {
			return;
		}

		Map<String, Facet> facetsMap = searchSearchRequest.getFacets();

		for (Facet facet : facetsMap.values()) {
			if (!facet.isStatic()) {
				facet.setFacetCollector(getFacetCollector(facet, namedList2));
			}
		}
	}

	protected void updateGroupedHits(
		SearchSearchResponse searchSearchResponse, QueryResponse queryResponse,
		SearchSearchRequest searchSearchRequest, Hits hits) {

		GroupBy groupBy = searchSearchRequest.getGroupBy();

		if ((groupBy == null) &&
			ListUtil.isEmpty(searchSearchRequest.getGroupByRequests())) {

			return;
		}

		GroupResponse groupResponse = queryResponse.getGroupResponse();

		List<GroupCommand> groupCommands = groupResponse.getValues();

		for (GroupCommand groupCommand : groupCommands) {
			GroupByResponse groupByResponse =
				_groupByResponseFactory.getGroupByResponse(
					groupCommand.getName());

			List<Group> groups = groupCommand.getValues();

			for (Group group : groups) {
				Hits groupedHits = new HitsImpl();

				processSearchHits(
					queryResponse, group.getResult(),
					searchSearchRequest.getQuery71(), groupedHits,
					searchSearchResponse);

				hits.addGroupedHits(group.getGroupValue(), groupedHits);

				Document[] docs = groupedHits.getDocs();

				hits.setDocs(docs);
				hits.setLength(docs.length);

				groupByResponse.putHits(group.getGroupValue(), groupedHits);
			}

			searchSearchResponse.addGroupByResponse(groupByResponse);
		}
	}

	protected void updateStatsResults(
		Hits hits, Map<String, StatsResponse> statsResponseMap,
		SearchSearchRequest searchSearchRequest) {

		if (MapUtil.isNotEmpty(searchSearchRequest.getStats())) {
			statsResponseMap.forEach(
				(key, statsResponse) -> hits.addStatsResults(
					getStatsResults(statsResponse)));
		}
	}

	protected void updateStatsResults(
		QueryResponse queryResponse, SearchSearchRequest searchSearchRequest,
		Hits hits) {

		Map<String, StatsResponse> statsResponseMap = getStatsResponseMap(
			queryResponse);

		if (statsResponseMap != null) {
			updateStatsResults(hits, statsResponseMap, searchSearchRequest);
		}
	}

	private void _setSearchTimeValue(
		QueryResponse queryResponse,
		SearchSearchResponse searchSearchResponse) {

		SearchTimeValue.Builder builder = SearchTimeValue.Builder.newBuilder();

		builder.duration(
			queryResponse.getQTime()
		).timeUnit(
			TimeUnit.MILLISECONDS
		);

		searchSearchResponse.setSearchTimeValue(builder.build());
	}

	private static final String[] _EXCLUDED_FIELDS = {"_root_", "_version_"};

	@Reference
	private DocumentBuilderFactory _documentBuilderFactory;

	@Reference
	private GroupByResponseFactory _groupByResponseFactory;

	@Reference
	private SearchHitBuilderFactory _searchHitBuilderFactory;

	@Reference
	private SearchHitsBuilderFactory _searchHitsBuilderFactory;

	@Reference
	private StatsResultsTranslator _statsResultsTranslator;

	@Reference
	private StatsTranslator _statsTranslator;

}