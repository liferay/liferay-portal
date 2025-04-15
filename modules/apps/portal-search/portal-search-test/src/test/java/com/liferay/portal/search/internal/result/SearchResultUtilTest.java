/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.result;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.SummaryFactory;
import com.liferay.portal.kernel.search.result.SearchResultTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.test.util.BaseSearchResultUtilTestCase;
import com.liferay.portal.search.test.util.SearchTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class SearchResultUtilTest extends BaseSearchResultUtilTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_searchResultManagerImpl.deactivate();
	}

	@Test
	public void testBlankDocument() {
		SearchResult searchResult = assertOneSearchResult(new DocumentImpl());

		Assert.assertNull(searchResult.getSummary());

		_assertSearchResult(searchResult);
	}

	@Test
	public void testNoDocuments() {
		List<SearchResult> searchResults = SearchTestUtil.getSearchResults(
			searchResultTranslator);

		Assert.assertEquals(searchResults.toString(), 0, searchResults.size());
	}

	@Test
	public void testSummaryFromAssetRenderer() throws Exception {
		Mockito.when(
			_assetRenderer.getSearchSummary(Mockito.any())
		).thenReturn(
			SearchTestUtil.SUMMARY_CONTENT
		);

		Mockito.when(
			_assetRenderer.getTitle(Mockito.any())
		).thenReturn(
			SearchTestUtil.SUMMARY_TITLE
		);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			_assetRenderer
		);

		Mockito.when(
			_serviceTrackerMap.getService(Mockito.anyString())
		).thenReturn(
			(AssetRendererFactory)_assetRendererFactory
		);

		ReflectionTestUtil.setFieldValue(
			AssetRendererFactoryRegistryUtil.class,
			"_classNameAssetRenderFactoriesServiceTrackerMap",
			_serviceTrackerMap);

		SearchResult searchResult = assertOneSearchResult(new DocumentImpl());

		Summary summary = searchResult.getSummary();

		Assert.assertEquals(
			SearchTestUtil.SUMMARY_CONTENT, summary.getContent());
		Assert.assertEquals(
			SummaryFactoryImpl.SUMMARY_MAX_CONTENT_LENGTH,
			summary.getMaxContentLength());
		Assert.assertEquals(SearchTestUtil.SUMMARY_TITLE, summary.getTitle());

		_assertSearchResult(searchResult);
	}

	@Test
	public void testSummaryFromIndexer() throws Exception {
		Summary summary = new Summary(
			null, SearchTestUtil.SUMMARY_TITLE, SearchTestUtil.SUMMARY_CONTENT);

		Mockito.when(
			_indexer.getSummary(
				Mockito.any(), Mockito.anyString(),
				(PortletRequest)Mockito.isNull(),
				(PortletResponse)Mockito.isNull())
		).thenReturn(
			summary
		);

		Mockito.when(
			_indexerRegistry.getIndexer(Mockito.anyString())
		).thenReturn(
			_indexer
		);

		SearchResult searchResult = assertOneSearchResult(new DocumentImpl());

		Assert.assertSame(summary, searchResult.getSummary());

		_assertSearchResult(searchResult);
	}

	@Test
	public void testTwoDocumentsWithSameEntryKey() {
		String className = RandomTestUtil.randomString();

		Document document1 = SearchTestUtil.createDocument(className);
		Document document2 = SearchTestUtil.createDocument(className);

		List<SearchResult> searchResults = SearchTestUtil.getSearchResults(
			searchResultTranslator, document1, document2);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals(searchResult.getClassName(), className);
		Assert.assertEquals(
			searchResult.getClassPK(), SearchTestUtil.ENTRY_CLASS_PK);
	}

	@Override
	protected SearchResultTranslator createSearchResultTranslator() {
		SearchResultTranslatorImpl searchResultTranslatorImpl =
			new SearchResultTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			searchResultTranslatorImpl, "_searchResultManager",
			_createSearchResultManagerImpl());

		return searchResultTranslatorImpl;
	}

	private void _assertSearchResult(SearchResult searchResult) {
		Assert.assertEquals(StringPool.BLANK, searchResult.getClassName());
		Assert.assertEquals(0L, searchResult.getClassPK());

		assertEmptyCommentRelatedSearchResults(searchResult);
		assertEmptyFileEntryRelatedSearchResults(searchResult);
		assertEmptyVersions(searchResult);
	}

	private SearchResultManagerImpl _createSearchResultManagerImpl() {
		_searchResultManagerImpl = new SearchResultManagerImpl();

		ReflectionTestUtil.setFieldValue(
			_searchResultManagerImpl, "_classNameLocalService",
			classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_searchResultManagerImpl, "_summaryFactory",
			_createSummaryFactory());

		_searchResultManagerImpl.activate(bundleContext);

		return _searchResultManagerImpl;
	}

	private SummaryFactory _createSummaryFactory() {
		SummaryFactoryImpl summaryFactoryImpl = new SummaryFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			summaryFactoryImpl, "_indexerRegistry", _indexerRegistry);

		return summaryFactoryImpl;
	}

	@SuppressWarnings("rawtypes")
	private AssetRenderer _assetRenderer = Mockito.mock(AssetRenderer.class);

	private final AssetRendererFactory<?> _assetRendererFactory = Mockito.mock(
		AssetRendererFactory.class);
	private final Indexer<Object> _indexer = Mockito.mock(Indexer.class);
	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private SearchResultManagerImpl _searchResultManagerImpl;
	private final ServiceTrackerMap<String, AssetRendererFactory<?>>
		_serviceTrackerMap = Mockito.mock(ServiceTrackerMap.class);

}