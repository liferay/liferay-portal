/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster.ClusterRequestExecutorFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.DocumentRequestExecutorFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.IndexRequestExecutorFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search.SearchRequestExecutorFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot.SnapshotRequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;

import org.elasticsearch.action.search.SearchRequestBuilder;

/**
 * @author Michael C. Han
 */
public class ElasticsearchEngineAdapterFixture {

	public SearchEngineAdapter getSearchEngineAdapter() {
		return _searchEngineAdapter;
	}

	public void setUp() {
		_searchEngineAdapter = createSearchEngineAdapter(
			_elasticsearchClientResolver, _facetProcessor);
	}

	public void tearDown() {
		_searchRequestExecutorFixture.tearDown();
	}

	protected static SearchEngineAdapter createSearchEngineAdapter(
		ElasticsearchClientResolver elasticsearchClientResolver,
		FacetProcessor<?> facetProcessor) {

		ClusterRequestExecutorFixture clusterRequestExecutorFixture =
			new ClusterRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		DocumentRequestExecutorFixture documentRequestExecutorFixture =
			new DocumentRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		IndexRequestExecutorFixture indexRequestExecutorFixture =
			new IndexRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		_searchRequestExecutorFixture = new SearchRequestExecutorFixture() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setFacetProcessor(facetProcessor);
			}
		};

		SnapshotRequestExecutorFixture snapshotRequestExecutorFixture =
			new SnapshotRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		clusterRequestExecutorFixture.setUp();
		documentRequestExecutorFixture.setUp();
		indexRequestExecutorFixture.setUp();
		_searchRequestExecutorFixture.setUp();
		snapshotRequestExecutorFixture.setUp();

		SearchEngineAdapter searchEngineAdapter =
			new ElasticsearchSearchEngineAdapterImpl() {
				{
					setThrowOriginalExceptions(true);
				}
			};

		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_clusterRequestExecutor",
			clusterRequestExecutorFixture.getClusterRequestExecutor());
		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_documentRequestExecutor",
			documentRequestExecutorFixture.getDocumentRequestExecutor());
		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_indexRequestExecutor",
			indexRequestExecutorFixture.getIndexRequestExecutor());
		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_searchRequestExecutor",
			_searchRequestExecutorFixture.getSearchRequestExecutor());
		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_snapshotRequestExecutor",
			snapshotRequestExecutorFixture.getSnapshotRequestExecutor());

		return searchEngineAdapter;
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	protected void setFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		_facetProcessor = facetProcessor;
	}

	private static SearchRequestExecutorFixture _searchRequestExecutorFixture;

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private FacetProcessor<SearchRequestBuilder> _facetProcessor;
	private SearchEngineAdapter _searchEngineAdapter;

}