/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch7.configuration.DeepPaginationConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.indexing.ElasticsearchIndexingFixture;
import com.liferay.portal.search.internal.sort.FieldSortImpl;
import com.liferay.portal.search.internal.sort.ScoreSortImpl;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
@FeatureFlag("LPS-172416")
public class ElasticsearchIndexSearcherSearchAfterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpIndexingFixture();

		_setUpIndexSearcherAndWriter();

		_setUpDeepPagination(60);
		_setUpIndexSearchLimit();
		_setUpSorts();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_indexingFixture == null) {
			return;
		}

		if (_indexingFixture.isSearchEngineAvailable()) {
			_indexingFixture.tearDown();
		}

		_indexingFixture = null;
		_indexSearcher = null;
		_indexWriter = null;
	}

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		_entryClassName = StringUtil.toLowerCase(
			clazz.getSimpleName() + CharPool.PERIOD + testName.getMethodName());

		_addDocuments();
	}

	@After
	public void tearDown() throws Exception {
		if (!_documents.isEmpty()) {
			_indexWriter.deleteDocuments(
				_getSearchContext(),
				TransformUtil.transform(
					_documents, document -> document.get(Field.UID)));

			_documents.clear();
		}
	}

	@Test
	public void testElasticsearchIndexSearcher() throws Exception {
		_assertHits(_INDEX_MAX_RESULT_WINDOW, 0, _INDEX_MAX_RESULT_WINDOW);
	}

	@Test
	public void testElasticsearchIndexSearcherAcrossIndexMaxResultWindow()
		throws Exception {

		_assertHits(_INDEX_MAX_RESULT_WINDOW, 1, _INDEX_MAX_RESULT_WINDOW + 1);
	}

	@Test
	public void testElasticsearchIndexSearcherAcrossMultiplesOfIndexMaxResultWindow()
		throws Exception {

		_assertHits(
			_INDEX_MAX_RESULT_WINDOW, _INDEX_MAX_RESULT_WINDOW + 1,
			_NUMBER_INDEXED_DOCUMENTS);
	}

	@Test
	public void testElasticsearchIndexSearcherIndexSearchLimit()
		throws Exception {

		_assertHits(
			_INDEX_SEARCH_LIMIT, QueryUtil.ALL_POS, QueryUtil.ALL_POS, 0,
			_INDEX_SEARCH_LIMIT);
	}

	@Test
	public void testElasticsearchIndexSearcherKeepAliveTime() throws Exception {
		_setUpDeepPagination(61);

		_assertHits(_INDEX_MAX_RESULT_WINDOW, 0, _INDEX_MAX_RESULT_WINDOW);

		_setUpDeepPagination(0);

		_assertHits(_INDEX_MAX_RESULT_WINDOW, 0, _INDEX_MAX_RESULT_WINDOW);
	}

	@Test
	public void testElasticsearchIndexSearcherLargerThanIndexMaxResultWindow()
		throws Exception {

		_assertHits(
			_INDEX_MAX_RESULT_WINDOW, 0, _NUMBER_INDEXED_DOCUMENTS, 0,
			_INDEX_MAX_RESULT_WINDOW);
	}

	@Test
	public void testElasticsearchIndexSearcherNoSort() throws Exception {
		_assertHits(
			_INDEX_MAX_RESULT_WINDOW, 0, _INDEX_MAX_RESULT_WINDOW, 0,
			_INDEX_MAX_RESULT_WINDOW, null);
	}

	@Test
	public void testElasticsearchIndexSearcherSizeGreaterThanIndexedDocuments()
		throws Exception {

		_assertHits(
			1, _NUMBER_INDEXED_DOCUMENTS - 1, 12, _NUMBER_INDEXED_DOCUMENTS - 1,
			_NUMBER_INDEXED_DOCUMENTS);
	}

	@Test
	public void testElasticsearchIndexSearcherSort() throws Exception {
		_assertHits(
			_INDEX_MAX_RESULT_WINDOW, 0, _INDEX_MAX_RESULT_WINDOW, 0,
			_INDEX_MAX_RESULT_WINDOW, new Sort(Field.TITLE, true));
	}

	@Test
	public void testElasticsearchIndexSearcherStartAndSizeGreaterThanIndexedDocuments()
		throws Exception {

		_assertHits(
			0, _NUMBER_INDEXED_DOCUMENTS, _NUMBER_INDEXED_DOCUMENTS + 1);
	}

	@Rule
	public TestName testName = new TestName();

	protected static final long GROUP_ID = RandomTestUtil.randomLong();

	private static IndexingFixture _createIndexingFixture() {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				ElasticsearchIndexSearcherSearchAfterTest.class.getSimpleName()
			).elasticsearchConfigurationProperties(
				Collections.singletonMap(
					"indexMaxResultWindow", _INDEX_MAX_RESULT_WINDOW)
			).build();

		return new ElasticsearchIndexingFixture() {
			{
				setElasticsearchFixture(
					new ElasticsearchFixture(elasticsearchConnectionFixture));
				setLiferayMappingsAddedToIndex(true);
			}
		};
	}

	private static void _setUpDeepPagination(int pointInTimeKeepAliveSeconds)
		throws Exception {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		DeepPaginationConfiguration deepPaginationConfiguration = Mockito.mock(
			DeepPaginationConfiguration.class);

		Mockito.when(
			deepPaginationConfiguration.enableDeepPagination()
		).thenReturn(
			true
		);

		Mockito.when(
			deepPaginationConfiguration.pointInTimeKeepAliveSeconds()
		).thenReturn(
			pointInTimeKeepAliveSeconds
		);

		Mockito.when(
			configurationProvider.getSystemConfiguration(
				Mockito.eq(DeepPaginationConfiguration.class))
		).thenReturn(
			deepPaginationConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			_indexSearcher, "_configurationProvider", configurationProvider);
	}

	private static void _setUpIndexingFixture() throws Exception {
		if (_indexingFixture != null) {
			Assume.assumeTrue(_indexingFixture.isSearchEngineAvailable());

			return;
		}

		_indexingFixture = _createIndexingFixture();

		Assume.assumeTrue(_indexingFixture.isSearchEngineAvailable());
	}

	private static void _setUpIndexSearcherAndWriter() throws Exception {
		_indexingFixture.setUp();

		_indexSearcher = _indexingFixture.getIndexSearcher();
		_indexWriter = _indexingFixture.getIndexWriter();
	}

	private static void _setUpIndexSearchLimit() {
		PropsUtil.set(
			PropsKeys.INDEX_SEARCH_LIMIT, String.valueOf(_INDEX_SEARCH_LIMIT));
	}

	private static void _setUpSorts() {
		Sorts sorts = Mockito.mock(Sorts.class);

		Mockito.doReturn(
			new ScoreSortImpl()
		).when(
			sorts
		).score();

		Mockito.doReturn(
			new FieldSortImpl("_shard_doc")
		).when(
			sorts
		).field(
			"_shard_doc"
		);

		ReflectionTestUtil.setFieldValue(_indexSearcher, "_sorts", sorts);
	}

	private void _addDocument(
		String fieldName1, String fieldValue1, String fieldName2,
		String fieldValue2) {

		Document document = _createDocument(
			fieldName1, fieldValue1, fieldName2, fieldValue2);

		try {
			_indexWriter.addDocument(_getSearchContext(), document);
		}
		catch (SearchException searchException) {
			Throwable throwable = searchException.getCause();

			if (throwable instanceof RuntimeException) {
				throw (RuntimeException)throwable;
			}

			if (throwable != null) {
				throw new RuntimeException(throwable);
			}

			throw new RuntimeException(searchException);
		}

		_documents.add(document);
	}

	private void _addDocuments() {
		for (int i = 1; i <= _NUMBER_INDEXED_DOCUMENTS; i++) {
			_addDocument(Field.TITLE, "Title " + i, Field.CONTENT, "example");
		}
	}

	private void _assertDocumentOrder(
		Document[] documents, int start, int end, Sort sort) {

		List<Document> expectedDocuments = new ArrayList<>(_documents);

		if (sort.isReverse()) {
			Collections.reverse(expectedDocuments);
		}

		for (int i = 0; (i < documents.length) && (start < end); i++) {
			Document expectedDocument = expectedDocuments.get(start++);

			Assert.assertEquals(
				expectedDocument.get(Field.TITLE),
				documents[i].get(Field.TITLE));
		}
	}

	private void _assertHits(int expectedHitsReturned, int start, int end)
		throws Exception {

		_assertHits(expectedHitsReturned, start, end, start, end);
	}

	private void _assertHits(
			int expectedHitsReturned, int start, int end, int expectedStart,
			int expectedEnd)
		throws Exception {

		_assertHits(
			expectedHitsReturned, start, end, expectedStart, expectedEnd,
			new Sort(Field.TITLE, false));
	}

	private void _assertHits(
			int expectedHitsReturned, int start, int end, int expectedStart,
			int expectedEnd, Sort sort)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				SearchContext searchContext = _getSearchContext();

				if (sort != null) {
					searchContext.setSorts(sort);
				}

				searchContext.setEnd(end);
				searchContext.setStart(start);

				try {
					Hits hits = _indexSearcher.search(
						searchContext, new MatchAllQuery());

					Document[] documents = hits.getDocs();
					float[] scores = hits.getScores();

					Assert.assertEquals(
						hits.toString(), expectedHitsReturned,
						documents.length);
					Assert.assertEquals(
						hits.toString(), _NUMBER_INDEXED_DOCUMENTS,
						hits.getLength());

					Assert.assertEquals(
						scores.toString(), expectedHitsReturned, scores.length);

					_assertDocumentOrder(
						documents, expectedStart, expectedEnd, sort);
				}
				catch (Exception exception) {
				}
			});
	}

	private Document _createDocument(
		String fieldName1, String fieldValue1, String fieldName2,
		String fieldValue2) {

		Document document = DocumentFixture.newDocument(
			_indexingFixture.getCompanyId(), GROUP_ID, _entryClassName);

		document.addText(fieldName1, fieldValue1);
		document.addText(fieldName2, fieldValue2);

		return document;
	}

	private SearchContext _getSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_indexingFixture.getCompanyId());

		return searchContext;
	}

	private static final int _INDEX_MAX_RESULT_WINDOW = 3;

	private static final int _INDEX_SEARCH_LIMIT = 2;

	private static final int _NUMBER_INDEXED_DOCUMENTS = 7;

	private static IndexingFixture _indexingFixture;
	private static IndexSearcher _indexSearcher;
	private static IndexWriter _indexWriter;

	private final List<Document> _documents = new ArrayList<>();
	private String _entryClassName;

}