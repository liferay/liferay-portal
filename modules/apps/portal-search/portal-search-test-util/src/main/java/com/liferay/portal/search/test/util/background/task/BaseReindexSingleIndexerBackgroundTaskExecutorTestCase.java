/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.background.task;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.internal.SearchEngineHelperImpl;
import com.liferay.portal.search.internal.background.task.ReindexSingleIndexerBackgroundTaskExecutor;
import com.liferay.portal.search.test.util.search.engine.SearchEngineFixture;

import java.io.Serializable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Adam Brandizzi
 */
public abstract class BaseReindexSingleIndexerBackgroundTaskExecutorTestCase {

	@Before
	public void setUp() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		setUpBackgroundTask(companyId);

		setUpIndexerRegistry();

		SearchEngineFixture searchEngineFixture = getSearchEngineFixture();

		searchEngineFixture.setUp();

		SearchEngineHelper searchEngineHelper = new SearchEngineHelperImpl();

		ReflectionTestUtil.setFieldValue(
			searchEngineHelper, "_searchEngine",
			searchEngineFixture.getSearchEngine());

		_companyId = companyId;
		_searchEngineFixture = searchEngineFixture;
		_searchEngineHelper = searchEngineHelper;
	}

	@After
	public void tearDown() throws Exception {
		if (_searchEngineFixture != null) {
			_searchEngineFixture.tearDown();
		}

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Test
	public void testFieldMappings() throws Exception {
		ReindexSingleIndexerBackgroundTaskExecutor
			reindexSingleIndexerBackgroundTaskExecutor =
				getReindexSingleIndexerBackgroundTaskExecutor();

		reindexSingleIndexerBackgroundTaskExecutor.execute(_backgroundTask);

		assertFieldType(Field.ENTRY_CLASS_NAME, "keyword");
	}

	protected abstract void assertFieldType(String fieldName, String fieldType)
		throws Exception;

	protected String getIndexName() {
		IndexNameBuilder indexNameBuilder =
			_searchEngineFixture.getIndexNameBuilder();

		return indexNameBuilder.getIndexName(_companyId);
	}

	protected ReindexSingleIndexerBackgroundTaskExecutor
		getReindexSingleIndexerBackgroundTaskExecutor() {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			IndexWriterHelper.class, _indexWriterHelper, null);

		return new ReindexSingleIndexerBackgroundTaskExecutor() {
			{
				indexerRegistry = _indexerRegistry;
				reindexStatusMessageSender = _reindexStatusMessageSender;
				searchEngineHelper = _searchEngineHelper;
				systemIndexers = _systemIndexers;
			}
		};
	}

	protected abstract SearchEngineFixture getSearchEngineFixture();

	protected void setUpBackgroundTask(long companyId) {
		Mockito.when(
			_backgroundTask.getTaskContextMap()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				ReindexBackgroundTaskConstants.CLASS_NAME,
				RandomTestUtil.randomString()
			).put(
				ReindexBackgroundTaskConstants.COMPANY_IDS,
				new long[] {companyId}
			).build()
		);
	}

	protected void setUpIndexerRegistry() {
		Mockito.when(
			_indexerRegistry.getIndexer(Mockito.anyString())
		).thenReturn(
			_indexer
		);
	}

	private final BackgroundTask _backgroundTask = Mockito.mock(
		BackgroundTask.class);
	private long _companyId;
	private final Indexer<Object> _indexer = Mockito.mock(Indexer.class);
	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private final IndexWriterHelper _indexWriterHelper = Mockito.mock(
		IndexWriterHelper.class);
	private final ReindexStatusMessageSender _reindexStatusMessageSender =
		Mockito.mock(ReindexStatusMessageSender.class);
	private SearchEngineFixture _searchEngineFixture;
	private SearchEngineHelper _searchEngineHelper;
	private ServiceRegistration<IndexWriterHelper> _serviceRegistration;
	private final ServiceTrackerList<Indexer<?>> _systemIndexers = Mockito.mock(
		ServiceTrackerList.class);

}