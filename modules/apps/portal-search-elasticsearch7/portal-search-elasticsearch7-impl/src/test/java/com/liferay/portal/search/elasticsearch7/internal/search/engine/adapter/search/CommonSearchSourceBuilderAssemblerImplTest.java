/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.index.LiferayIndexFixture;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.SearchAssert;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.filter.ComplexQueryBuilderFactory;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.internal.filter.ComplexQueryBuilderImpl;
import com.liferay.portal.search.internal.filter.ComplexQueryPartBuilderFactoryImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Wade Cao
 */
public class CommonSearchSourceBuilderAssemblerImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_indexName = new IndexName(testName.getMethodName());

		Class<?> clazz = getClass();

		_liferayIndexFixture = new LiferayIndexFixture(
			clazz.getSimpleName(), _indexName);

		_liferayIndexFixture.setUp();

		Queries queries = new QueriesImpl();

		_commonSearchSourceBuilderAssembler =
			createCommonSearchSourceBuilderAssembler(queries);
		_queries = queries;
	}

	@After
	public void tearDown() throws Exception {
		_liferayIndexFixture.tearDown();
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsFilterOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", _queries.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"filter", _queries.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", _queries.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"must_not", _queries.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsMustOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", _queries.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"must", _queries.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWhenAdditiveWillAppendToWhatMainQueryFindsShouldOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(
			new MatchQuery("entryClassName", "DLFileEntry"));

		_addPart(
			"filter", _queries.term("title", "bravo"), searchSearchRequest);

		_assertSearch(searchSearchRequest, "bravo 1");

		_addPartAdditive(
			"should", _queries.term("entryClassName", "JournalArticle"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "bravo 1");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsFilterOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"filter", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must_not", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsMustOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillModifyWhatMainQueryFindsShouldOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"should", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsFilterOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"filter", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsMustNotOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"must_not", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsMustOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"must", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testPartsWillNarrowDownWhatMainQueryFindsShouldOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPart(
			"should", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Test
	public void testPrecedenceOfAdditiveFilterOccur() throws Exception {
		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"filter", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2", "bravo 1");
	}

	@Test
	public void testPrecedenceOfAdditiveMustNotOccur() throws Exception {
		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"must_not", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testPrecedenceOfAdditiveMustOccur() throws Exception {
		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"must", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2", "bravo 1");
	}

	@Test
	public void testPrecedenceOfAdditiveShouldOccur() throws Exception {
		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("title", "alpha"), BooleanClauseOccur.MUST);

		searchSearchRequest.setQuery(booleanQueryImpl);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartAdditiveAndRoot(
			"should", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2", "bravo 1");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanFilterOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"filter", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanMustNotOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must_not", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanMustOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"must", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 2");
	}

	@Test
	public void testRootOnlyAppliedWhenMainQueryIsBooleanShouldOccur()
		throws Exception {

		_index("alpha 1", "JournalArticle");
		_index("alpha 2", "DLFileEntry");
		_index("bravo 1", "DLFileEntry");

		SearchSearchRequest searchSearchRequest = _createSearchSearchRequest();

		searchSearchRequest.setQuery(new MatchQuery("title", "alpha"));

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");

		_addPartRoot(
			"should", _queries.term("entryClassName", "DLFileEntry"),
			searchSearchRequest);

		_assertSearch(searchSearchRequest, "alpha 1", "alpha 2");
	}

	@Rule
	public TestName testName = new TestName();

	protected static CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler(Queries queries) {

		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler =
			new CommonSearchSourceBuilderAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_complexQueryBuilderFactory",
			createComplexQueryBuilderFactory(queries));
		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_facetTranslator",
			new DefaultFacetTranslator());

		ElasticsearchQueryTranslatorFixture
			legacyElasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		com.liferay.portal.search.elasticsearch7.internal.legacy.query.
			ElasticsearchQueryTranslator legacyElasticsearchQueryTranslator =
				legacyElasticsearchQueryTranslatorFixture.
					getElasticsearchQueryTranslator();

		ElasticsearchFilterTranslatorFixture
			elasticsearchFilterTranslatorFixture =
				new ElasticsearchFilterTranslatorFixture(
					legacyElasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_filterTranslator",
			elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_legacyQueryTranslator",
			legacyElasticsearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_queryTranslator",
			new ElasticsearchQueryTranslator());

		return commonSearchSourceBuilderAssembler;
	}

	protected static ComplexQueryBuilderFactory
		createComplexQueryBuilderFactory(Queries queries) {

		return () -> new ComplexQueryBuilderImpl(queries, null);
	}

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

	private void _assertSearch(
			SearchSearchRequest searchSearchRequest, String... expected)
		throws Exception {

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		SearchRequest searchRequest = new SearchRequest();

		_commonSearchSourceBuilderAssembler.assemble(
			searchSourceBuilder, searchSearchRequest, searchRequest);

		SearchAssert.assertSearch(
			_liferayIndexFixture.getRestHighLevelClient(), searchSourceBuilder,
			searchRequest, "title", expected);
	}

	private SearchSearchRequest _createSearchSearchRequest() {
		return new SearchSearchRequest() {
			{
				setIndexNames(_indexName.getName());
			}
		};
	}

	private void _index(String title, String entryClassName) {
		_liferayIndexFixture.index(
			HashMapBuilder.<String, Object>put(
				"entryClassName", entryClassName
			).put(
				"title", title
			).build());
	}

	private CommonSearchSourceBuilderAssembler
		_commonSearchSourceBuilderAssembler;
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory =
			new ComplexQueryPartBuilderFactoryImpl();
	private IndexName _indexName;
	private LiferayIndexFixture _liferayIndexFixture;
	private Queries _queries;

}