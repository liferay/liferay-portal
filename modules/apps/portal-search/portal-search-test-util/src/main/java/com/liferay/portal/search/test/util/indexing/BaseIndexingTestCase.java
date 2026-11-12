/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.indexing;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.HierarchicalAggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.search.internal.aggregation.AggregationsImpl;
import com.liferay.portal.search.internal.filter.ComplexQueryPartBuilderFactoryImpl;
import com.liferay.portal.search.internal.highlight.FieldConfigBuilderFactoryImpl;
import com.liferay.portal.search.internal.highlight.HighlightBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderImpl;
import com.liferay.portal.search.internal.rescore.RescoreBuilderFactoryImpl;
import com.liferay.portal.search.internal.sort.SortsImpl;
import com.liferay.portal.search.rescore.RescoreBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.SearchMapUtil;
import com.liferay.portal.search.test.util.document.DocumentTranslator;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * @author Miguel Angelo Caldas Gallindo
 */
public abstract class BaseIndexingTestCase {

	@BeforeClass
	public static void setUpClassBaseIndexingTestCase() {
		_indexingFixture = null;

		_documentFixture.setUp();

		ReflectionTestUtil.setFieldValue(
			FastDateFormatFactoryUtil.class, "_fastDateFormatFactory",
			new FastDateFormatFactoryImpl());
	}

	@AfterClass
	public static void tearDownClassBaseIndexingTestCase() throws Exception {
		_documentFixture.tearDown();

		if (_indexingFixture == null) {
			return;
		}

		if (_indexingFixture.isSearchEngineAvailable()) {
			_indexingFixture.tearDown();
		}

		_indexingFixture = null;
	}

	@Before
	public void setUp() throws Exception {
		_setUpIndexingFixture();

		Class<?> clazz = getClass();

		_entryClassName = StringUtil.toLowerCase(
			clazz.getSimpleName() + CharPool.PERIOD + testName.getMethodName());

		_indexSearcher = _indexingFixture.getIndexSearcher();
		_indexWriter = _indexingFixture.getIndexWriter();
	}

	@After
	public void tearDown() throws Exception {
		if ((_indexingFixture == null) ||
			!_indexingFixture.isSearchEngineAvailable()) {

			return;
		}

		_indexWriter.deleteEntityDocuments(
			createSearchContext(), _entryClassName);
	}

	@Rule
	public TestName testName = new TestName();

	protected static <K, V> Map<K, V> toMap(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	protected void addDocument(Document document) {
		try {
			_indexWriter.addDocument(createSearchContext(), document);
		}
		catch (SearchException searchException) {
			_handle(searchException);

			throw new RuntimeException(searchException);
		}
	}

	protected void addDocument(DocumentBuilder documentBuilder) {
		DocumentTranslator documentTranslator = new DocumentTranslator();

		addDocument(
			documentTranslator.toLegacyDocument(documentBuilder.build()));
	}

	protected void addDocument(DocumentCreationHelper documentCreationHelper) {
		Document document = DocumentFixture.newDocument(
			getCompanyId(), _GROUP_ID, _entryClassName);

		documentCreationHelper.populate(document);

		addDocument(document);
	}

	protected void addDocuments(
		Function<Double, DocumentCreationHelper> function, double... values) {

		for (double value : values) {
			addDocument(function.apply(value));
		}
	}

	protected void addDocuments(
		Function<String, DocumentCreationHelper> function, String... values) {

		for (String value : values) {
			addDocument(function.apply(value));
		}
	}

	protected void assertSearch(
		Consumer<IndexingTestHelper> indexingTestHelperConsumer) {

		try {
			IdempotentRetryAssert.retryAssert(
				10, TimeUnit.SECONDS,
				() -> indexingTestHelperConsumer.accept(
					new IndexingTestHelper()));
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected abstract IndexingFixture createIndexingFixture() throws Exception;

	protected SearchContext createSearchContext() {
		SearchContext searchContext = new SearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setHitsProcessingEnabled(true);
		queryConfig.setScoreEnabled(false);

		searchContext.setCommitImmediately(true);
		searchContext.setCompanyId(getCompanyId());
		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setGroupIds(new long[] {_GROUP_ID});
		searchContext.setStart(QueryUtil.ALL_POS);

		return searchContext;
	}

	protected long getCompanyId() {
		return _indexingFixture.getCompanyId();
	}

	protected Query getDefaultQuery() {
		Map<String, String> map = SearchMapUtil.join(
			toMap(Field.COMPANY_ID, String.valueOf(getCompanyId())),
			toMap(Field.ENTRY_CLASS_NAME, _entryClassName));

		BooleanQuery booleanQuery = new BooleanQuery();

		map.forEach(
			(key, value) -> booleanQuery.add(
				new TermQuery(key, value), BooleanClauseOccur.MUST));

		return booleanQuery;
	}

	protected String getEntryClassName() {
		return _entryClassName;
	}

	protected long getGroupId() {
		return _GROUP_ID;
	}

	protected String getIndexName() {
		return _indexingFixture.getIndexName();
	}

	protected IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	protected IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	protected SearchEngineAdapter getSearchEngineAdapter() {
		return _indexingFixture.getSearchEngineAdapter();
	}

	protected DocumentBuilder newDocumentBuilder() {
		return DocumentBuilderFactory.builder(
		).setLong(
			Field.COMPANY_ID, getCompanyId()
		).setString(
			Field.ENTRY_CLASS_NAME, getEntryClassName()
		).setLong(
			Field.GROUP_ID, getGroupId()
		);
	}

	protected Hits search(SearchContext searchContext) {
		return search(searchContext, getDefaultQuery());
	}

	protected Hits search(SearchContext searchContext, Query query) {
		try {
			return _indexSearcher.search(searchContext, query);
		}
		catch (SearchException searchException) {
			_handle(searchException);

			throw new RuntimeException(searchException);
		}
	}

	protected long searchCount(SearchContext searchContext, Query query) {
		try {
			return _indexSearcher.searchCount(searchContext, query);
		}
		catch (SearchException searchException) {
			_handle(searchException);

			throw new RuntimeException(searchException);
		}
	}

	protected void setPreBooleanFilter(Filter filter, Query query) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(filter, BooleanClauseOccur.MUST);

		query.setPreBooleanFilter(booleanFilter);
	}

	protected final AggregationFixture aggregationFixture =
		new AggregationFixture();
	protected final Aggregations aggregations = new AggregationsImpl();
	protected final ComplexQueryPartBuilderFactory
		complexQueryPartBuilderFactory =
			new ComplexQueryPartBuilderFactoryImpl();
	protected final FieldConfigBuilderFactory fieldConfigBuilderFactory =
		new FieldConfigBuilderFactoryImpl();
	protected final HighlightBuilderFactory highlightBuilderFactory =
		new HighlightBuilderFactoryImpl();
	protected final RescoreBuilderFactory rescoreBuilderFactory =
		new RescoreBuilderFactoryImpl();
	protected final Sorts sorts = new SortsImpl();

	protected class IndexingTestHelper {

		public IndexingTestHelper() {
			_searchContext = createSearchContext();

			_searchRequestBuilder = new SearchRequestBuilderImpl(
				null, _searchContext);
		}

		public void assertResultCount(int expected) {
			Document[] documents = _hits.getDocs();

			Assert.assertEquals(
				(String)_searchContext.getAttribute("queryString") + "->" +
					Arrays.toString(documents),
				expected, documents.length);
		}

		public void assertValues(
			String fieldName, List<String> expectedValues) {

			DocumentsAssert.assertValues(
				(String)_searchContext.getAttribute("queryString"),
				_hits.getDocs(), fieldName, expectedValues);
		}

		public void define(Consumer<SearchContext> searchContextConsumer) {
			searchContextConsumer.accept(_searchContext);
		}

		public void defineRequest(
			Consumer<SearchRequestBuilder> searchRequestBuilderConsumer) {

			searchRequestBuilderConsumer.accept(_searchRequestBuilder);
		}

		public <AR extends AggregationResult> AR getAggregationResult(
			Aggregation aggregation) {

			return _getAggregationResult(aggregation.getName());
		}

		public <AR extends AggregationResult> AR getAggregationResult(
			PipelineAggregation pipelineAggregation) {

			return _getAggregationResult(pipelineAggregation.getName());
		}

		public <AR extends AggregationResult> AR getChildAggregationResult(
			Bucket bucket, Aggregation aggregation) {

			return (AR)bucket.getChildAggregationResult(aggregation.getName());
		}

		public <AR extends AggregationResult> AR getChildAggregationResult(
			HierarchicalAggregationResult aggregationResult,
			Aggregation aggregation) {

			return (AR)aggregationResult.getChildAggregationResult(
				aggregation.getName());
		}

		public String getRequestString() {
			return (String)_searchContext.getAttribute("queryString");
		}

		public SearchContext getSearchContext() {
			return _searchContext;
		}

		public void search() {
			_hits = BaseIndexingTestCase.this.search(
				_searchContext, _getQuery());

			SearchResponseBuilder searchResponseBuilder =
				new SearchResponseBuilderImpl(_searchContext);

			_searchResponse = searchResponseBuilder.build();
		}

		public long searchCount() {
			long count = BaseIndexingTestCase.this.searchCount(
				_searchContext, _getQuery());

			SearchResponseBuilder searchResponseBuilder =
				new SearchResponseBuilderImpl(_searchContext);

			_searchResponse = searchResponseBuilder.build();

			return count;
		}

		public void setFilter(Filter filter) {
			_filter = filter;
		}

		public void setPostFilter(Filter postFilter) {
			_postFilter = postFilter;
		}

		public void setQuery(Query query) {
			_query = query;
		}

		public void setQueryContributor(QueryContributor queryContributor) {
			_queryContributor = queryContributor;
		}

		public void setSearchContextAttribute(String name, Serializable value) {
			_searchContext.setAttribute(name, value);
		}

		public void verify(Consumer<Hits> hitsConsumer) {
			hitsConsumer.accept(_hits);
		}

		public void verifyContext(
			Consumer<SearchContext> searchContextConsumer) {

			searchContextConsumer.accept(_searchContext);
		}

		public void verifyResponse(
			Consumer<SearchResponse> searchResponseConsumer) {

			searchResponseConsumer.accept(_searchResponse);
		}

		private <AR extends AggregationResult> AR _getAggregationResult(
			String name) {

			AggregationResult aggregationResult =
				_searchResponse.getAggregationResult(name);

			Assert.assertNotNull(aggregationResult);

			return (AR)aggregationResult;
		}

		private Query _getQuery() {
			Query query = _query;

			if (query == null) {
				query = getDefaultQuery();
			}

			if (_queryContributor != null) {
				_queryContributor.contribute(query);
			}

			if (_filter != null) {
				setPreBooleanFilter(_filter, query);
			}

			if (_postFilter != null) {
				query.setPostFilter(_postFilter);
			}

			return query;
		}

		private Filter _filter;
		private Hits _hits;
		private Filter _postFilter;
		private Query _query;
		private QueryContributor _queryContributor;
		private final SearchContext _searchContext;
		private final SearchRequestBuilder _searchRequestBuilder;
		private SearchResponse _searchResponse;

	}

	private void _handle(SearchException searchException) {
		Throwable throwable = searchException.getCause();

		if (throwable instanceof RuntimeException) {
			throw (RuntimeException)throwable;
		}

		if (throwable != null) {
			throw new RuntimeException(throwable);
		}
	}

	private void _setUpIndexingFixture() throws Exception {
		if (_indexingFixture != null) {
			Assume.assumeTrue(_indexingFixture.isSearchEngineAvailable());

			return;
		}

		_indexingFixture = createIndexingFixture();

		Assume.assumeTrue(_indexingFixture.isSearchEngineAvailable());

		_indexingFixture.setUp();
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final DocumentFixture _documentFixture =
		new DocumentFixture();
	private static IndexingFixture _indexingFixture;

	private String _entryClassName;
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;

}