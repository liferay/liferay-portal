/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Highlight;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.internal.highlight.FieldConfigImpl;
import com.liferay.portal.search.internal.highlight.HighlightImpl;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Olivia Yu
 */
public class SearchSearchRequestAssemblerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testHighlightEnabledCarriesHighlightQuery() {
		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setHighlightEnabled(true);
		searchSearchRequest.setHighlightFieldNames(_FIELD_NAME);
		searchSearchRequest.setQuery(_createBooleanQuery());

		_assertHighlightQuery(_assemble(searchSearchRequest));
	}

	@Test
	public void testHighlightEnabledCarriesHighlightQueryForLegacyQuery() {
		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setHighlightEnabled(true);
		searchSearchRequest.setHighlightFieldNames(_FIELD_NAME);
		searchSearchRequest.setQuery(_createLegacyBooleanQuery());

		_assertHighlightQuery(_assemble(searchSearchRequest));
	}

	@Test
	public void testHighlightObjectCarriesHighlightQuery() {
		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		HighlightImpl.HighlightBuilderImpl highlightBuilderImpl =
			new HighlightImpl.HighlightBuilderImpl();

		FieldConfigImpl.FieldConfigBuilderImpl fieldConfigBuilderImpl =
			new FieldConfigImpl.FieldConfigBuilderImpl(_FIELD_NAME);

		searchSearchRequest.setHighlight(
			highlightBuilderImpl.fieldConfigs(
				Collections.singletonList(fieldConfigBuilderImpl.build())
			).build());

		searchSearchRequest.setQuery(_createLegacyBooleanQuery());

		_assertHighlightQuery(_assemble(searchSearchRequest));
	}

	private SearchRequest _assemble(SearchSearchRequest searchSearchRequest) {
		SearchRequest.Builder searchRequestBuilder =
			new SearchRequest.Builder();

		SearchSearchRequestAssembler.INSTANCE.assemble(
			searchRequestBuilder, searchSearchRequest);

		return searchRequestBuilder.build();
	}

	private void _assertHighlightQuery(SearchRequest searchRequest) {
		Query query = searchRequest.query();

		Assert.assertNotNull("No query was assembled", query);
		Assert.assertTrue(
			String.valueOf(query), _countProximityQueries(query) > 0);

		Highlight highlight = searchRequest.highlight();

		Assert.assertNotNull("No highlight was assembled", highlight);

		Query highlightQuery = highlight.highlightQuery();

		Assert.assertNotNull(
			"No highlight query was assembled", highlightQuery);
		Assert.assertEquals(
			String.valueOf(highlightQuery), 0,
			_countProximityQueries(highlightQuery));
		Assert.assertTrue(
			String.valueOf(highlightQuery),
			_countMatchQueries(highlightQuery) > 0);
	}

	private int _countMatchQueries(Query query) {
		if (query.isMatch()) {
			return 1;
		}

		if (!query.isBool()) {
			return 0;
		}

		BoolQuery boolQuery = query.bool();
		int count = 0;

		for (Query clauseQuery : _getClauses(boolQuery)) {
			count += _countMatchQueries(clauseQuery);
		}

		return count;
	}

	private int _countProximityQueries(Query query) {
		if (query.isMatchPhrase()) {
			MatchPhraseQuery matchPhraseQuery = query.matchPhrase();

			if (matchPhraseQuery.slop() != null) {
				return 1;
			}

			return 0;
		}

		if (!query.isBool()) {
			return 0;
		}

		BoolQuery boolQuery = query.bool();
		int count = 0;

		for (Query clauseQuery : _getClauses(boolQuery)) {
			count += _countProximityQueries(clauseQuery);
		}

		return count;
	}

	private BooleanQuery _createBooleanQuery() {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		MatchQuery matchQuery = QueriesUtil.match(_FIELD_NAME, _KEYWORDS);

		matchQuery.setSlop(50);
		matchQuery.setType(MatchQuery.Type.PHRASE);

		booleanQuery.addMustQueryClauses(
			QueriesUtil.match(_FIELD_NAME, _KEYWORDS));
		booleanQuery.addShouldQueryClauses(matchQuery);

		return booleanQuery;
	}

	private com.liferay.portal.kernel.search.BooleanQuery
		_createLegacyBooleanQuery() {

		com.liferay.portal.kernel.search.BooleanQuery booleanQuery =
			new com.liferay.portal.kernel.search.BooleanQuery();

		com.liferay.portal.kernel.search.MatchQuery matchQuery =
			new com.liferay.portal.kernel.search.MatchQuery(
				_FIELD_NAME, _KEYWORDS);

		matchQuery.setSlop(50);
		matchQuery.setType(
			com.liferay.portal.kernel.search.MatchQuery.Type.PHRASE);

		booleanQuery.add(
			new com.liferay.portal.kernel.search.MatchQuery(
				_FIELD_NAME, _KEYWORDS),
			BooleanClauseOccur.MUST);
		booleanQuery.add(matchQuery, BooleanClauseOccur.SHOULD);

		return booleanQuery;
	}

	private SearchSearchRequest _createSearchSearchRequest() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(_INDEX_NAME);

		return searchSearchRequest;
	}

	private List<Query> _getClauses(BoolQuery boolQuery) {
		return ListUtil.concat(
			boolQuery.filter(), boolQuery.must(), boolQuery.mustNot(),
			boolQuery.should());
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final String _INDEX_NAME = RandomTestUtil.randomString();

	private static final String _KEYWORDS = RandomTestUtil.randomString();

}