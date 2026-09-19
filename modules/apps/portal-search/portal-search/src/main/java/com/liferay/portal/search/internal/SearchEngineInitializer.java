/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.index.ConcurrentReindexManager;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Brian Wing Shun Chan
 */
public class SearchEngineInitializer implements Runnable {

	public SearchEngineInitializer(
			long companyId, ConcurrentReindexManager concurrentReindexManager,
			String executionMode, ExecutorService executorService,
			IndexNameBuilder indexNameBuilder, List<Indexer<?>> indexers,
			JSONFactory jsonFactory, SearchEngineAdapter searchEngineAdapter,
			SyncReindexManager syncReindexManager)
		throws Exception {

		_companyId = companyId;
		_concurrentReindexManager = concurrentReindexManager;
		_executionMode = executionMode;
		_executorService = executorService;
		_indexNameBuilder = indexNameBuilder;
		_indexers = indexers;
		_jsonFactory = jsonFactory;
		_searchEngineAdapter = searchEngineAdapter;
		_syncReindexManager = syncReindexManager;

		if (_log.isInfoEnabled()) {
			_log.info("Reindexing started");
		}

		_stopWatch = new StopWatch();

		_stopWatch.start();

		SafeCloseable bulkLoadSafeCloseable = null;
		Date date = null;
		boolean fullMode = false;

		if (_isExecuteConcurrentReindex()) {
			SearchEngineHelperUtil.initialize(_companyId);

			_concurrentReindexManager.createNextIndex(_companyId);
		}
		else if (_isExecuteSyncReindex()) {
			date = new Date();

			Thread.sleep(1000);
		}
		else {
			fullMode = true;

			SearchEngineHelperUtil.removeCompany(_companyId);

			SearchEngineHelperUtil.initialize(_companyId);

			bulkLoadSafeCloseable = _configureIndexForBulkLoad();
		}

		_bulkLoadSafeCloseable = bulkLoadSafeCloseable;
		_date = date;
		_fullMode = fullMode;
		_sharedReindexCacheMap = new ConcurrentHashMap<>();
	}

	public void complete() {
		try {
			if (_isExecuteConcurrentReindex()) {
				_concurrentReindexManager.replaceCurrentIndexWithNextIndex(
					_companyId);
			}
			else if (_isExecuteSyncReindex()) {
				Set<String> indexerClassNames = new HashSet<>();

				for (Indexer<?> indexer : _indexers) {
					indexerClassNames.add(indexer.getClassName());
				}

				_syncReindexManager.deleteStaleDocuments(
					_companyId, _date, indexerClassNames);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Reindexing completed in " +
						(_stopWatch.getTime() / Time.SECOND) + " seconds");
			}
		}
		catch (Exception exception) {
			if (_isExecuteConcurrentReindex()) {
				_concurrentReindexManager.deleteNextIndex(_companyId);
			}

			_log.error("Unable to reindex", exception);

			if (_log.isInfoEnabled()) {
				_log.info("Unable to reindex");
			}
		}
		finally {
			if (_bulkLoadSafeCloseable != null) {
				try {
					_bulkLoadSafeCloseable.close();
				}
				catch (Exception exception) {
					_log.error("Unable to restore index settings", exception);
				}
			}
		}

		_finished = true;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Map<Indexer<?>, Long> getReindexEntryCounts() {
		Map<Indexer<?>, Future<Long>> futures = new HashMap<>();

		for (Indexer<?> indexer : _indexers) {
			futures.put(
				indexer,
				_executorService.submit(
					() -> {
						try (SafeCloseable safeCloseable1 =
								CompanyThreadLocal.
									setCompanyIdWithSafeCloseable(_companyId);
							SafeCloseable safeCloseable2 =
								ReindexCacheThreadLocal.openReindexMode(
									_fullMode, _sharedReindexCacheMap)) {

							return indexer.getReindexEntryCount(_companyId);
						}
					}));
		}

		Map<Indexer<?>, Long> reindexEntryCounts = new HashMap<>();

		for (Map.Entry<Indexer<?>, Future<Long>> entry : futures.entrySet()) {
			Indexer<?> indexer = entry.getKey();
			Future<Long> future = entry.getValue();

			try {
				long count = future.get();

				if (count != 0) {
					reindexEntryCounts.put(indexer, count);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to get reindex entry count for " +
						indexer.getClassName(),
					exception);
			}
		}

		return reindexEntryCounts;
	}

	public void halt() {
	}

	public boolean isFinished() {
		return _finished;
	}

	public void reindex() {
		reindex(0);
	}

	public void reindex(int delay) {
		_reindex(delay);
	}

	public void reindexIndexer(Indexer<?> indexer) throws Exception {
		try (SafeCloseable safeCloseable1 =
				ReindexCacheThreadLocal.openReindexMode(
					_fullMode, _sharedReindexCacheMap);
			SafeCloseable safeCloseable2 = SearchContext.openBatchMode(false)) {

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			if (_log.isInfoEnabled()) {
				_log.info(
					"Reindexing of " + indexer.getClassName() +
						" entities started");
			}

			indexer.reindexCompany(_companyId);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Reindexing of ", indexer.getClassName(),
						" entities completed in ",
						stopWatch.getTime() / Time.SECOND, " seconds"));
			}
		}
	}

	@Override
	public void run() {
		reindex(PropsValues.INDEX_ON_STARTUP_DELAY);
	}

	private SafeCloseable _configureIndexForBulkLoad() throws Exception {
		String indexName = _indexNameBuilder.getIndexName(_companyId);

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(new GetIndexIndexRequest(indexName));

		Map<String, String> settingsMap = getIndexIndexResponse.getSettings();

		JSONObject indexSettingsJSONObject = _jsonFactory.createJSONObject(
			settingsMap.get(indexName)
		).getJSONObject(
			"index"
		);

		String originalNumberOfReplicas = indexSettingsJSONObject.getString(
			"number_of_replicas");
		String originalRefreshInterval = GetterUtil.getString(
			indexSettingsJSONObject.get("refresh_interval"), "1s");

		_updateIndexSettings(indexName, _INDEX_SETTINGS_BULK_LOAD);

		return () -> _updateIndexSettings(
			indexName,
			JSONUtil.put(
				"index",
				JSONUtil.put(
					"number_of_replicas", originalNumberOfReplicas
				).put(
					"refresh_interval", originalRefreshInterval
				)
			).toString());
	}

	private boolean _isExecuteConcurrentReindex() {
		if ((_concurrentReindexManager != null) && (_executionMode != null) &&
			_executionMode.equals("concurrent") &&
			(_companyId != CompanyConstants.SYSTEM)) {

			return true;
		}

		return false;
	}

	private boolean _isExecuteSyncReindex() {
		if ((_syncReindexManager != null) && (_executionMode != null) &&
			_executionMode.equals("sync")) {

			return true;
		}

		return false;
	}

	private void _reindex(int delay) {
		if (IndexWriterHelperUtil.isIndexReadOnly()) {
			return;
		}

		if (delay < 0) {
			delay = 0;
		}

		try {
			if (delay > 0) {
				Thread.sleep(Time.SECOND * delay);
			}
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(interruptedException);
			}
		}

		try {
			if (_indexers.size() == 1) {
				reindexIndexer(_indexers.get(0));
			}
			else {
				long backgroundTaskId =
					BackgroundTaskThreadLocal.getBackgroundTaskId();
				List<FutureTask<Void>> futureTasks = new ArrayList<>();

				for (Indexer<?> indexer : _indexers) {
					FutureTask<Void> futureTask = new FutureTask<>(
						new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								try (SafeCloseable safeCloseable1 =
										BackgroundTaskThreadLocal.
											setBackgroundTaskIdWithSafeCloseable(
												backgroundTaskId)) {

									reindexIndexer(indexer);

									return null;
								}
							}

						});

					_executorService.submit(futureTask);

					futureTasks.add(futureTask);
				}

				for (FutureTask<Void> futureTask : futureTasks) {
					futureTask.get();
				}
			}

			complete();
		}
		catch (Exception exception) {
			if (_isExecuteConcurrentReindex()) {
				_concurrentReindexManager.deleteNextIndex(_companyId);
			}

			_log.error("Error encountered while reindexing", exception);

			if (_log.isInfoEnabled()) {
				_log.info("Reindexing failed");
			}

			_finished = true;
		}
	}

	private void _updateIndexSettings(String indexName, String settings) {
		try {
			UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest =
				new UpdateIndexSettingsIndexRequest(indexName);

			updateIndexSettingsIndexRequest.setSettings(settings);

			_searchEngineAdapter.execute(updateIndexSettingsIndexRequest);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Updated index settings for ", indexName, " with ",
						settings));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final String _INDEX_SETTINGS_BULK_LOAD = JSONUtil.put(
		"index",
		JSONUtil.put(
			"number_of_replicas", "0"
		).put(
			"refresh_interval", "-1"
		)
	).toString();

	private static final Log _log = LogFactoryUtil.getLog(
		SearchEngineInitializer.class);

	private final SafeCloseable _bulkLoadSafeCloseable;
	private final long _companyId;
	private final ConcurrentReindexManager _concurrentReindexManager;
	private final Date _date;
	private final String _executionMode;
	private final ExecutorService _executorService;
	private boolean _finished;
	private final boolean _fullMode;
	private final IndexNameBuilder _indexNameBuilder;
	private final List<Indexer<?>> _indexers;
	private final JSONFactory _jsonFactory;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final Map<String, Object> _sharedReindexCacheMap;
	private final StopWatch _stopWatch;
	private final SyncReindexManager _syncReindexManager;

}