/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.ConcurrentReindexManager;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.internal.SearchEngineInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrew Betts
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.portal.search.internal.background.task.ReindexPortalBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class ReindexPortalBackgroundTaskExecutor
	extends BaseReindexBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, (Class<Indexer<?>>)(Class<?>)Indexer.class, null,
			(serviceReference, emitter) -> emitter.emit(
				GetterUtil.getBoolean(
					serviceReference.getProperty("system.index"))));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void reindex(
			String className, long[] companyIds, String executionMode)
		throws Exception {

		if (IndexWriterHelperUtil.isIndexReadOnly()) {
			return;
		}

		long backgroundTaskId = BackgroundTaskThreadLocal.getBackgroundTaskId();
		ExecutorService executorService =
			_searchEngineHelper.getDocumentsProducerExecutorService();

		try (SafeCloseable safeCloseable1 = SearchContext.openBatchMode()) {
			List<SearchEngineInitializer> searchEngineInitializers =
				_prepareSearchEngineInitializers(
					backgroundTaskId, companyIds, executionMode,
					executorService);

			for (Future<Void> future :
					executorService.invokeAll(
						_sortReindexCallables(
							backgroundTaskId, searchEngineInitializers))) {

				future.get();
			}

			_completeSearchEngineInitializers(
				companyIds, executionMode, searchEngineInitializers);
		}
	}

	private void _completeSearchEngineInitializers(
		long[] companyIds, String executionMode,
		List<SearchEngineInitializer> searchEngineInitializers) {

		for (SearchEngineInitializer searchEngineInitializer :
				searchEngineInitializers) {

			long companyId = searchEngineInitializer.getCompanyId();

			try {
				_companyLocalService.forEachCompanyId(
					curCompanyId -> searchEngineInitializer.complete(),
					new long[] {companyId});
			}
			finally {
				ReindexStatusMessageSenderUtil.sendStatusMessage(
					ReindexBackgroundTaskConstants.PORTAL_END, companyId,
					companyIds);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Finished reindexing company ", companyId,
							" with execution mode ", executionMode));
				}
			}
		}
	}

	private SearchEngineInitializer _prepareSearchEngineInitializer(
			long backgroundTaskId, long companyId, long[] companyIds,
			String executionMode, ExecutorService executorService,
			List<Indexer<?>> indexers)
		throws Exception {

		try (SafeCloseable safeCloseable =
				BackgroundTaskThreadLocal.setBackgroundTaskIdWithSafeCloseable(
					backgroundTaskId)) {

			AtomicReference<SearchEngineInitializer>
				searchEngineInitializerAtomicReference =
					new AtomicReference<>();

			_companyLocalService.forEachCompanyId(
				curCompanyId -> {
					ReindexStatusMessageSenderUtil.sendStatusMessage(
						ReindexBackgroundTaskConstants.PORTAL_START, companyId,
						companyIds);

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Start reindexing company ", companyId,
								" with execution mode ", executionMode));
					}

					searchEngineInitializerAtomicReference.set(
						new SearchEngineInitializer(
							companyId, _concurrentReindexManagerSnapshot.get(),
							executionMode, executorService, _indexNameBuilder,
							indexers, _jsonFactory, _searchEngineAdapter,
							_syncReindexManagerSnapshot.get()));
				},
				new long[] {companyId});

			return searchEngineInitializerAtomicReference.get();
		}
	}

	private List<SearchEngineInitializer> _prepareSearchEngineInitializers(
			long backgroundTaskId, long[] companyIds, String executionMode,
			ExecutorService executorService)
		throws Exception {

		List<Callable<SearchEngineInitializer>> callables = new ArrayList<>();

		for (long companyId : companyIds) {
			List<Indexer<?>> indexers = _serviceTrackerMap.getService(
				companyId == CompanyConstants.SYSTEM);

			callables.add(
				() -> _prepareSearchEngineInitializer(
					backgroundTaskId, companyId, companyIds, executionMode,
					executorService, indexers));
		}

		return TransformUtil.unsafeTransform(
			executorService.invokeAll(callables), Future::get);
	}

	private List<Callable<Void>> _sortReindexCallables(
		long backgroundTaskId,
		List<SearchEngineInitializer> searchEngineInitializers) {

		List<Callable<Void>> callables = new ArrayList<>();
		Map<Callable<Void>, Long> callableCounts = new HashMap<>();
		List<Callable<Void>> smallCallables = new ArrayList<>();

		for (SearchEngineInitializer searchEngineInitializer :
				searchEngineInitializers) {

			Map<Indexer<?>, Long> reindexEntryCounts =
				searchEngineInitializer.getReindexEntryCounts();

			for (Map.Entry<Indexer<?>, Long> entry :
					reindexEntryCounts.entrySet()) {

				Indexer<?> indexer = entry.getKey();
				long count = entry.getValue();

				if (count <= _SMALL_INDEXER_THRESHOLD) {
					smallCallables.add(
						() -> {
							searchEngineInitializer.reindexIndexer(indexer);

							return null;
						});
				}
				else {
					Callable<Void> callable = () -> {
						try (SafeCloseable safeCloseable =
								BackgroundTaskThreadLocal.
									setBackgroundTaskIdWithSafeCloseable(
										backgroundTaskId)) {

							searchEngineInitializer.reindexIndexer(indexer);
						}

						return null;
					};

					callables.add(callable);
					callableCounts.put(callable, count);
				}
			}
		}

		callables.sort(
			(callable1, callable2) -> Long.compare(
				callableCounts.get(callable2), callableCounts.get(callable1)));

		if (!smallCallables.isEmpty()) {
			callables.add(
				() -> {
					try (SafeCloseable safeCloseable =
							BackgroundTaskThreadLocal.
								setBackgroundTaskIdWithSafeCloseable(
									backgroundTaskId)) {

						for (Callable<Void> callable : smallCallables) {
							callable.call();
						}
					}

					return null;
				});
		}

		return callables;
	}

	private static final int _SMALL_INDEXER_THRESHOLD = 200;

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexPortalBackgroundTaskExecutor.class);

	private static final Snapshot<ConcurrentReindexManager>
		_concurrentReindexManagerSnapshot = new Snapshot<>(
			ReindexPortalBackgroundTaskExecutor.class,
			ConcurrentReindexManager.class, null, true);
	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			ReindexPortalBackgroundTaskExecutor.class, SyncReindexManager.class,
			null, true);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SearchEngineHelper _searchEngineHelper;

	private ServiceTrackerMap<Boolean, List<Indexer<?>>> _serviceTrackerMap;

}