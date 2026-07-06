/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.internal.filter.ComplexQueryPartBuilderFactoryImpl;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.IndexName;
import com.liferay.portal.search.opensearch2.internal.index.LiferayIndexFixture;
import com.liferay.portal.search.opensearch2.internal.query.SearchAssert;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.opensearch.client.opensearch.core.SearchRequest;

/**
 * @author Wade Cao
 */
public class CommonSearchRequestBuilderAssemblerImplTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_indexName = new IndexName(testName.getMethodName());

		_liferayIndexFixture = new LiferayIndexFixture(_indexName);

		_liferayIndexFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_liferayIndexFixture.tearDown();
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsFilterOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", QueriesUtil.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"filter", QueriesUtil.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", QueriesUtil.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"must_not", QueriesUtil.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsMustOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", QueriesUtil.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"must", QueriesUtil.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsShouldOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", QueriesUtil.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"should", QueriesUtil.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "bravo 1");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsFilterOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"filter", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must_not", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsMustOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsShouldOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"should", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsFilterOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"filter", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"must_not", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsMustOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"must", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsShouldOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"should", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Test
	public void testPrecedenceOfAdditiveFilterOccur() throws Exception {
		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"filter", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2", "bravo 1");
	}

	@Test
	public void testPrecedenceOfAdditiveMustNotOccur() throws Exception {
		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"must_not", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPrecedenceOfAdditiveMustOccur() throws Exception {
		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"must", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2", "bravo 1");
	}

	@Test
	public void testPrecedenceOfAdditiveShouldOccur() throws Exception {
		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"should", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2", "bravo 1");
	}

	@Test
	public void testRootClauseWithParentNestsUnderNamedParentQuery()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQuery);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartNamed("parent_query", "must", searchSearchRequest);

		_addPartRootWithParent(
			"should", "parent_query",
			QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanFilterOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"filter", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanMustNotOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must_not", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanMustOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanShouldOccur()
		throws Exception {

		_indexDocuments();

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"should", QueriesUtil.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Rule
	public TestName testName = new TestName();

	private void _addPart(
		String occur, Query query, SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).occur(
					occur
				).query(
					query
				).build()));
	}

	private void _addPartAdditive(
		String occur, Query query, SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).additive(
					true
				).occur(
					occur
				).query(
					query
				).build()));
	}

	private void _addPartAdditiveAndRoot(
		String occur, Query query, SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).additive(
					true
				).occur(
					occur
				).query(
					query
				).rootClause(
					true
				).build()));
	}

	private void _addPartNamed(
		String name, String occur, SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).name(
					name
				).occur(
					occur
				).query(
					QueriesUtil.booleanQuery()
				).build()));
	}

	private void _addPartRoot(
		String occur, Query query, SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).occur(
					occur
				).query(
					query
				).rootClause(
					true
				).build()));
	}

	private void _addPartRootWithParent(
		String occur, String parent, Query query,
		SearchSearchRequest searchSearchRequest) {

		searchSearchRequest.addComplexQueryParts(
			Arrays.asList(
				_complexQueryPartBuilderFactory.builder(
				).occur(
					occur
				).parent(
					parent
				).query(
					query
				).rootClause(
					true
				).build()));
	}

	private void _assertSearch(
			SearchSearchRequest searchSearchRequest, String... expected)
		throws Exception {

		SearchRequest.Builder builder = new SearchRequest.Builder();

		CommonSearchRequestBuilderAssembler.INSTANCE.assemble(
			searchSearchRequest, builder);

		SearchAssert.assertSearch(
			_liferayIndexFixture.getOpenSearchClient(), builder, "title",
			expected);
	}

	private SearchSearchRequest _createSearchSearchRequest() {
		return new SearchSearchRequest() {
			{
				setIndexNames(_indexName.getName());
			}
		};
	}

	private void _index(String entryClassName, String title) {
		_liferayIndexFixture.index(
			HashMapBuilder.<String, Object>put(
				"entryClassName", entryClassName
			).put(
				"title", title
			).build());
	}

	private void _indexDocuments() {
		_index("DLFileEntry", "alpha 2");
		_index("DLFileEntry", "bravo 1");
		_index("JournalArticle", "alpha 1");
	}

	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory =
			new ComplexQueryPartBuilderFactoryImpl();
	private IndexName _indexName;
	private LiferayIndexFixture _liferayIndexFixture;

}