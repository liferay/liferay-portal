/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.indexing;

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
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchIndexSearcher;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchIndexWriter;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchQuerySuggester;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchSpellCheckIndexWriter;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexCreator;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch7.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ElasticsearchEngineAdapterFixture;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderFactoryImpl;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.util.LocalizationImpl;

import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ElasticsearchIndexingFixture implements IndexingFixture {

	public ElasticsearchIndexingFixture() {
		_companyId = RandomTestUtil.randomLong();
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	public ElasticsearchClientResolver getElasticsearchClientResolver() {
		return _elasticsearchFixture;
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
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
		_elasticsearchFixture.setUp();

		ElasticsearchEngineAdapterFixture elasticsearchEngineAdapterFixture =
			_createElasticsearchEngineAdapterFixture(
				_elasticsearchFixture, _facetProcessor);

		elasticsearchEngineAdapterFixture.setUp();

		SearchEngineAdapter searchEngineAdapter =
			elasticsearchEngineAdapterFixture.getSearchEngineAdapter();

		IndexNameBuilder indexNameBuilder = _createIndexNameBuilder();

		Localization localization = new LocalizationImpl();

		ElasticsearchIndexSearcher elasticsearchIndexSearcher =
			_createIndexSearcher(
				_elasticsearchFixture, indexNameBuilder, localization,
				searchEngineAdapter);

		IndexWriter indexWriter = _createIndexWriter(
			_elasticsearchFixture, indexNameBuilder, localization,
			searchEngineAdapter);

		_indexSearcher = elasticsearchIndexSearcher;
		_indexWriter = indexWriter;
		_searchEngineAdapter = searchEngineAdapter;

		_createIndex(indexNameBuilder);
	}

	@Override
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	protected static ElasticsearchConfigurationWrapper
		createElasticsearchConfigurationWrapper(
			Map<String, Object> properties) {

		return new ElasticsearchConfigurationWrapper() {
			{
				setElasticsearchConfiguration(
					ConfigurableUtil.createConfigurable(
						ElasticsearchConfiguration.class, properties));
			}
		};
	}

	protected void setElasticsearchFixture(
		ElasticsearchFixture elasticsearchFixture) {

		_elasticsearchFixture = elasticsearchFixture;
	}

	protected void setFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		_facetProcessor = facetProcessor;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	private ElasticsearchEngineAdapterFixture
		_createElasticsearchEngineAdapterFixture(
			ElasticsearchClientResolver elasticsearchClientResolver,
			FacetProcessor<SearchRequestBuilder> facetProcessor) {

		return new ElasticsearchEngineAdapterFixture() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setFacetProcessor(facetProcessor);
			}
		};
	}

	private QuerySuggester _createElasticsearchQuerySuggester(
		IndexNameBuilder indexNameBuilder, Localization localization,
		SearchEngineAdapter searchEngineAdapter) {

		ElasticsearchQuerySuggester elasticsearchQuerySuggester =
			new ElasticsearchQuerySuggester() {
				{
					setLocalization(localization);
				}
			};

		ReflectionTestUtil.setFieldValue(
			elasticsearchQuerySuggester, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchQuerySuggester, "_searchEngineAdapter",
			searchEngineAdapter);

		return elasticsearchQuerySuggester;
	}

	private ElasticsearchSpellCheckIndexWriter
		_createElasticsearchSpellCheckIndexWriter(
			IndexNameBuilder indexNameBuilder, Localization localization,
			SearchEngineAdapter searchEngineAdapter) {

		ElasticsearchSpellCheckIndexWriter elasticsearchSpellCheckIndexWriter =
			new ElasticsearchSpellCheckIndexWriter() {
				{
					setLocalization(localization);
				}
			};

		ReflectionTestUtil.setFieldValue(
			elasticsearchSpellCheckIndexWriter, "_indexNameBuilder",
			indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchSpellCheckIndexWriter, "_searchEngineAdapter",
			searchEngineAdapter);

		return elasticsearchSpellCheckIndexWriter;
	}

	private void _createIndex(IndexNameBuilder indexNameBuilder) {
		IndexCreator indexCreator = new IndexCreator() {
			{
				setElasticsearchClientResolver(_elasticsearchFixture);
				setIndexCreationHelper(_indexCreationHelper);
				setLiferayMappingsAddedToIndex(_liferayMappingsAddedToIndex);
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

	private ElasticsearchIndexSearcher _createIndexSearcher(
		ElasticsearchFixture elasticsearchFixture,
		IndexNameBuilder indexNameBuilder, Localization localization,
		SearchEngineAdapter searchEngineAdapter) {

		ElasticsearchIndexSearcher elasticsearchIndexSearcher =
			new ElasticsearchIndexSearcher();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_elasticsearchConfigurationWrapper",
			createElasticsearchConfigurationWrapper(
				elasticsearchFixture.
					getElasticsearchConfigurationProperties()));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_indexNameBuilder", indexNameBuilder);

		_setUpIndexSearchLimit();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_querySuggester",
			_createElasticsearchQuerySuggester(
				indexNameBuilder, localization, searchEngineAdapter));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchRequestBuilderFactory",
			new SearchRequestBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchResponseBuilderFactory",
			new SearchResponseBuilderFactoryImpl());

		return elasticsearchIndexSearcher;
	}

	private IndexWriter _createIndexWriter(
		ElasticsearchFixture elasticsearchFixture,
		IndexNameBuilder indexNameBuilder, Localization localization,
		SearchEngineAdapter searchEngineAdapter) {

		ElasticsearchIndexWriter elasticsearchIndexWriter =
			new ElasticsearchIndexWriter();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "_companyLocalService",
			Mockito.mock(CompanyLocalService.class));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "_elasticsearchConfigurationWrapper",
			createElasticsearchConfigurationWrapper(
				elasticsearchFixture.
					getElasticsearchConfigurationProperties()));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "_searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "_spellCheckIndexWriter",
			_createElasticsearchSpellCheckIndexWriter(
				indexNameBuilder, localization, searchEngineAdapter));

		return elasticsearchIndexWriter;
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

	private void _setUpIndexSearchLimit() {
		PropsUtil.set(PropsKeys.INDEX_SEARCH_LIMIT, "20");
	}

	private final long _companyId;
	private ElasticsearchFixture _elasticsearchFixture;
	private FacetProcessor<SearchRequestBuilder> _facetProcessor;
	private IndexCreationHelper _indexCreationHelper;
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;
	private boolean _liferayMappingsAddedToIndex;
	private SearchEngineAdapter _searchEngineAdapter;

}