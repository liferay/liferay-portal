/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.index.ConcurrentReindexManager;
import com.liferay.portal.search.index.SyncReindexManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.framework.BundleContext;

/**
 * @author Brian Wing Shun Chan
 */
public class SearchEngineInitializer implements Runnable {

	public SearchEngineInitializer(
		BundleContext bundleContext, long companyId,
		ConcurrentReindexManager concurrentReindexManager, String executionMode,
		PortalExecutorManager portalExecutorManager,
		SyncReindexManager syncReindexManager) {

		_bundleContext = bundleContext;
		_companyId = companyId;
		_concurrentReindexManager = concurrentReindexManager;
		_executionMode = executionMode;
		_portalExecutorManager = portalExecutorManager;
		_syncReindexManager = syncReindexManager;
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

	@Override
	public void run() {
		reindex(PropsValues.INDEX_ON_STARTUP_DELAY);
	}

	protected void reindex(Indexer<?> indexer) throws Exception {
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		if (_log.isInfoEnabled()) {
			_log.info(
				"Reindexing of " + indexer.getClassName() +
					" entities started");
		}

		indexer.reindex(new String[] {String.valueOf(_companyId)});

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Reindexing of ", indexer.getClassName(),
					" entities completed in ",
					stopWatch.getTime() / Time.SECOND, " seconds"));
		}
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

		if (_log.isInfoEnabled()) {
			_log.info("Reindexing started");
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

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				SearchEngineInitializer.class.getName());

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			Date date = null;

			if (_isExecuteConcurrentReindex()) {
				SearchEngineHelperUtil.initialize(_companyId);

				_concurrentReindexManager.createNextIndex(_companyId);
			}
			else if (_isExecuteSyncReindex()) {
				date = new Date();

				Thread.sleep(1000);
			}
			else {
				SearchEngineHelperUtil.removeCompany(_companyId);

				SearchEngineHelperUtil.initialize(_companyId);
			}

			long backgroundTaskId =
				BackgroundTaskThreadLocal.getBackgroundTaskId();
			List<FutureTask<Void>> futureTasks = new ArrayList<>();

			if (_companyId == CompanyConstants.SYSTEM) {
				_indexers = ServiceTrackerListFactory.open(
					_bundleContext, (Class<Indexer<?>>)(Class<?>)Indexer.class,
					"(system.index=true)");
			}
			else {
				_indexers = ServiceTrackerListFactory.open(
					_bundleContext, (Class<Indexer<?>>)(Class<?>)Indexer.class,
					"(!(system.index=true))");
			}

			Set<String> indexerClassNames = new HashSet<>();

			for (Indexer<?> indexer : _indexers) {
				indexerClassNames.add(indexer.getClassName());

				FutureTask<Void> futureTask = new FutureTask<>(
					new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							try (SafeCloseable safeCloseable =
									BackgroundTaskThreadLocal.
										setBackgroundTaskIdWithSafeCloseable(
											backgroundTaskId)) {

								reindex(indexer);

								return null;
							}
						}

					});

				executorService.submit(futureTask);

				futureTasks.add(futureTask);
			}

			_indexers.close();

			for (FutureTask<Void> futureTask : futureTasks) {
				futureTask.get();
			}

			if (_isExecuteConcurrentReindex()) {
				_concurrentReindexManager.replaceCurrentIndexWithNextIndex(
					_companyId);
			}
			else if (_isExecuteSyncReindex()) {
				_syncReindexManager.deleteStaleDocuments(
					_companyId, date, indexerClassNames);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Reindexing completed in " +
						(stopWatch.getTime() / Time.SECOND) + " seconds");
			}
		}
		catch (Exception exception) {
			if (_isExecuteConcurrentReindex()) {
				_concurrentReindexManager.deleteNextIndex(_companyId);
			}

			_log.error("Error encountered while reindexing", exception);

			if (_log.isInfoEnabled()) {
				_log.info("Reindexing failed");
			}
		}

		_finished = true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchEngineInitializer.class);

	private final BundleContext _bundleContext;
	private final long _companyId;
	private final ConcurrentReindexManager _concurrentReindexManager;
	private final String _executionMode;
	private boolean _finished;
	private ServiceTrackerList<Indexer<?>> _indexers;
	private final PortalExecutorManager _portalExecutorManager;
	private final SyncReindexManager _syncReindexManager;

}