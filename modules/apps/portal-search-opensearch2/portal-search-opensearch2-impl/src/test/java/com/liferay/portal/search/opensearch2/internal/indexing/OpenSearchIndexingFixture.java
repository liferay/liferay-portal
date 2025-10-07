/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.indexing;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderFactoryImpl;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConfiguration;
import com.liferay.portal.search.opensearch2.internal.OpenSearchIndexSearcher;
import com.liferay.portal.search.opensearch2.internal.OpenSearchIndexWriter;
import com.liferay.portal.search.opensearch2.internal.OpenSearchQuerySuggester;
import com.liferay.portal.search.opensearch2.internal.OpenSearchSpellCheckIndexWriter;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.search.opensearch2.internal.connection.IndexCreator;
import com.liferay.portal.search.opensearch2.internal.connection.IndexName;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.TestOpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.opensearch2.internal.facet.FacetProcessor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.OpenSearchEngineAdapterFixture;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.util.LocalizationImpl;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;

import org.mockito.Mockito;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class OpenSearchIndexingFixture implements IndexingFixture {

	public OpenSearchIndexingFixture() {
		_companyId = RandomTestUtil.randomLong();
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	public OpenSearchConnectionManager getOpenSearchConnectionManager() {
		return _testOpenSearchConnectionManager;
	}

	@Override
	public SearchEngineAdapter getSearchEngineAdapter() {
		return _searchEngineAdapter;
	}

	@Override
	public boolean isSearchEngineAvailable() {
		return true;
	}

	public void setIndexCreationHelper(
		IndexCreationHelper indexCreationHelper) {

		_indexCreationHelper = indexCreationHelper;
	}

	@Override
	public void setUp() throws Exception {
		OpenSearchEngineAdapterFixture openSearchEngineAdapterFixture =
			_createOpenSearchEngineAdapterFixture(
				_testOpenSearchConnectionManager, _facetProcessor);

		openSearchEngineAdapterFixture.setUp();

		_searchEngineAdapter =
			openSearchEngineAdapterFixture.getSearchEngineAdapter();

		Localization localization = new LocalizationImpl();

		_indexNameBuilder = _createIndexNameBuilder();

		_indexSearcher = _createIndexSearcher(
			_indexNameBuilder, localization, _testOpenSearchConnectionManager,
			_searchEngineAdapter);

		_indexWriter = _createIndexWriter(
			_indexNameBuilder, localization, _testOpenSearchConnectionManager,
			_searchEngineAdapter);

		if (_useLiferayMappings) {
			_createIndex(_indexNameBuilder);
		}
	}

	@Override
	public void tearDown() throws Exception {
		if (_useLiferayMappings) {
			_deleteIndex();
		}
	}

	protected static OpenSearchConfigurationWrapper
		createOpenSearchConfigurationWrapper(
			Map<String, Object> configurationProperties) {

		return new OpenSearchConfigurationWrapperImpl() {
			{
				if (configurationProperties == null) {
					setOpenSearchConfiguration(
						ConfigurableUtil.createConfigurable(
							OpenSearchConfiguration.class,
							Collections.emptyMap()));
				}
				else {
					setOpenSearchConfiguration(
						ConfigurableUtil.createConfigurable(
							OpenSearchConfiguration.class,
							configurationProperties));
				}
			}
		};
	}

	protected void setFacetProcessor(
		FacetProcessor<SearchRequest.Builder> facetProcessor) {

		_facetProcessor = facetProcessor;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	protected void setTestOpenSearchConnectionManager(
		TestOpenSearchConnectionManager testOpenSearchConnectionManager) {

		_testOpenSearchConnectionManager = testOpenSearchConnectionManager;
	}

	protected void setUseLiferayMappings(boolean useLiferayMappings) {
		_useLiferayMappings = useLiferayMappings;
	}

	private void _createIndex(IndexNameBuilder indexNameBuilder) {
		IndexCreator indexCreator = new IndexCreator() {
			{
				setIndexCreationHelper(_indexCreationHelper);
				setLiferayMappingsAddedToIndex(_liferayMappingsAddedToIndex);
				setOpenSearchConnectionManager(
					_testOpenSearchConnectionManager);
				setSearchEngineInformation(_createSearchEngineInformation());
			}
		};

		indexCreator.createIndex(
			new IndexName(indexNameBuilder.getIndexName(_companyId)));
	}

	private IndexNameBuilder _createIndexNameBuilder() {
		IndexNameBuilder indexNameBuilder = Mockito.mock(
			IndexNameBuilder.class);

		Mockito.when(
			indexNameBuilder.getIndexName(Mockito.anyLong())
		).then(
			invocation -> String.valueOf(invocation.getArgument(0, Long.class))
		);

		return indexNameBuilder;
	}

	private OpenSearchIndexSearcher _createIndexSearcher(
		IndexNameBuilder indexNameBuilder, Localization localization,
		TestOpenSearchConnectionManager openSearchFixture,
		SearchEngineAdapter searchEngineAdapter) {

		OpenSearchIndexSearcher openSearchIndexSearcher =
			new OpenSearchIndexSearcher();

		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_openSearchConfigurationWrapper",
			createOpenSearchConfigurationWrapper(
				openSearchFixture.getOpenSearchConfigurationProperties()));

		_setUpIndexSearchLimit();

		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_querySuggester",
			_createOpenSearchQuerySuggester(
				indexNameBuilder, localization, searchEngineAdapter));
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_searchRequestBuilderFactory",
			new SearchRequestBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_searchResponseBuilderFactory",
			new SearchResponseBuilderFactoryImpl());

		return openSearchIndexSearcher;
	}

	private IndexWriter _createIndexWriter(
		IndexNameBuilder indexNameBuilder, Localization localization,
		TestOpenSearchConnectionManager openSearchFixture,
		SearchEngineAdapter searchEngineAdapter) {

		OpenSearchIndexWriter openSearchIndexWriter =
			new OpenSearchIndexWriter();

		ReflectionTestUtil.setFieldValue(
			openSearchIndexWriter, "_companyLocalService",
			Mockito.mock(CompanyLocalService.class));
		ReflectionTestUtil.setFieldValue(
			openSearchIndexWriter, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexWriter, "_openSearchConfigurationWrapper",
			createOpenSearchConfigurationWrapper(
				openSearchFixture.getOpenSearchConfigurationProperties()));
		ReflectionTestUtil.setFieldValue(
			openSearchIndexWriter, "_searchEngineAdapter", searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexWriter, "_spellCheckIndexWriter",
			_createOpenSearchSpellCheckIndexWriter(
				indexNameBuilder, localization, searchEngineAdapter));

		return openSearchIndexWriter;
	}

	private OpenSearchEngineAdapterFixture
		_createOpenSearchEngineAdapterFixture(
			OpenSearchConnectionManager openSearchConnectionManager,
			FacetProcessor<SearchRequest.Builder> facetProcessor) {

		return new OpenSearchEngineAdapterFixture() {
			{
				setFacetProcessor(facetProcessor);
				setOpenSearchConnectionManager(openSearchConnectionManager);
			}
		};
	}

	private QuerySuggester _createOpenSearchQuerySuggester(
		IndexNameBuilder indexNameBuilder, Localization localization,
		SearchEngineAdapter searchEngineAdapter) {

		OpenSearchQuerySuggester openSearchQuerySuggester =
			new OpenSearchQuerySuggester() {
				{
					setLocalization(localization);
				}
			};

		ReflectionTestUtil.setFieldValue(
			openSearchQuerySuggester, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchQuerySuggester, "_searchEngineAdapter",
			searchEngineAdapter);

		return openSearchQuerySuggester;
	}

	private OpenSearchSpellCheckIndexWriter
		_createOpenSearchSpellCheckIndexWriter(
			IndexNameBuilder indexNameBuilder, Localization localization,
			SearchEngineAdapter searchEngineAdapter) {

		OpenSearchSpellCheckIndexWriter openSearchSpellCheckIndexWriter =
			new OpenSearchSpellCheckIndexWriter() {
				{
					setLocalization(localization);
				}
			};

		ReflectionTestUtil.setFieldValue(
			openSearchSpellCheckIndexWriter, "_indexNameBuilder",
			indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchSpellCheckIndexWriter, "_searchEngineAdapter",
			searchEngineAdapter);

		return openSearchSpellCheckIndexWriter;
	}

	private SearchEngineInformation _createSearchEngineInformation() {
		SearchEngineInformation searchEngineInformation = Mockito.mock(
			SearchEngineInformation.class);

		Mockito.when(
			searchEngineInformation.getEmbeddingVectorDimensions()
		).thenReturn(
			new int[] {256}
		);

		return searchEngineInformation;
	}

	private void _deleteIndex() {
		OpenSearchClient openSearchClient =
			_testOpenSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			openSearchIndicesClient.delete(
				DeleteIndexRequest.of(
					deleteIndexRequest -> deleteIndexRequest.index(
						_indexNameBuilder.getIndexName(_companyId))));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setUpIndexSearchLimit() {
		PropsUtil.set(PropsKeys.INDEX_SEARCH_LIMIT, "20");
	}

	private final long _companyId;
	private FacetProcessor<SearchRequest.Builder> _facetProcessor;
	private IndexCreationHelper _indexCreationHelper;
	private IndexNameBuilder _indexNameBuilder;
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;
	private boolean _liferayMappingsAddedToIndex;
	private SearchEngineAdapter _searchEngineAdapter;
	private TestOpenSearchConnectionManager _testOpenSearchConnectionManager;
	private boolean _useLiferayMappings;

}