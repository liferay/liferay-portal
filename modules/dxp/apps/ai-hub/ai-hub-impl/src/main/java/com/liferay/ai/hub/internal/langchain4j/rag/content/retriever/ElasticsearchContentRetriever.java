/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.rag.content.retriever;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class ElasticsearchContentRetriever extends BaseContentRetriever {

	public ElasticsearchContentRetriever(
		FieldConfigBuilderFactory fieldConfigBuilderFactory,
		HighlightBuilderFactory highlightBuilderFactory, String[] indexNames,
		SearchEngineAdapter searchEngineAdapter, long userId,
		long workflowInstanceId) {

		super(userId, workflowInstanceId);

		_fieldConfigBuilderFactory = fieldConfigBuilderFactory;
		_highlightBuilderFactory = highlightBuilderFactory;
		_indexNames = indexNames;
		_searchEngineAdapter = searchEngineAdapter;
	}

	@Override
	protected String getSearchTarget() {
		return StringUtil.merge(_indexNames);
	}

	@Override
	protected List<Content> search(Query query) {
		List<Content> contents = new ArrayList<>();

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setFetchSourceIncludes(new String[] {"url"});
		searchSearchRequest.setHighlight(
			_highlightBuilderFactory.builder(
			).addFieldConfig(
				_fieldConfigBuilderFactory.builder(
					"text_embedding"
				).build()
			).build());
		searchSearchRequest.setIndexNames(_indexNames);
		searchSearchRequest.setQuery(
			QueriesUtil.wrapper(
				JSONUtil.put(
					"semantic",
					JSONUtil.put(
						"field", "text_embedding"
					).put(
						"query", query.text()
					)
				).toString()));
		searchSearchRequest.setStoredFields("text_embedding");

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			if (searchHit.getScore() < _MIN_SCORE) {
				continue;
			}

			Map<String, HighlightField> highlightFields =
				searchHit.getHighlightFieldsMap();

			HighlightField highlightField = highlightFields.get(
				"text_embedding");

			Metadata metadata = Metadata.from(
				"url", MapUtil.getString(searchHit.getSourcesMap(), "url"));

			for (String fragment : highlightField.getFragments()) {
				contents.add(
					Content.from(TextSegment.from(fragment, metadata)));
			}
		}

		return contents;
	}

	private static final float _MIN_SCORE = 0.75F;

	private final FieldConfigBuilderFactory _fieldConfigBuilderFactory;
	private final HighlightBuilderFactory _highlightBuilderFactory;
	private final String[] _indexNames;
	private final SearchEngineAdapter _searchEngineAdapter;

}