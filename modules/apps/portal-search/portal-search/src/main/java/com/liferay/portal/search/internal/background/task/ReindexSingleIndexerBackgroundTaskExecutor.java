/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;
import com.liferay.portal.search.index.SyncReindexManager;

import java.util.Collections;
import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrew Betts
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.portal.search.internal.background.task.ReindexSingleIndexerBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class ReindexSingleIndexerBackgroundTaskExecutor
	extends BaseReindexBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		systemIndexers = ServiceTrackerListFactory.open(
			bundleContext, (Class<Indexer<?>>)(Class<?>)Indexer.class,
			"(system.index=true)");
	}

	@Deactivate
	protected void deactivate() {
		if (systemIndexers != null) {
			systemIndexers.close();
		}
	}

	@Override
	protected void reindex(
			String className, long[] companyIds, String executionMode)
		throws Exception {

		Indexer<?> indexer = indexerRegistry.getIndexer(className);

		if (indexer == null) {
			return;
		}

		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		boolean systemIndexer = _isSystemIndexer(indexer);

		for (long companyId : companyIds) {
			if (((companyId == CompanyConstants.SYSTEM) && !systemIndexer) ||
				((companyId != CompanyConstants.SYSTEM) && systemIndexer)) {

				continue;
			}

			reindexStatusMessageSender.sendStatusMessage(
				ReindexBackgroundTaskConstants.SINGLE_START, companyId,
				companyIds);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Start reindexing company ", companyId,
						" for class name ", className, " with execution mode ",
						executionMode));
			}

			try (SafeCloseable safeCloseable =
					ReindexCacheThreadLocal.openReindexMode()) {

				searchEngine.initialize(companyId);

				Date date = null;

				if (_isExecuteSyncReindex(executionMode)) {
					date = new Date();

					Thread.sleep(1000);
				}
				else {
					IndexWriterHelper indexWriterHelper =
						_indexWriterHelperSnapshot.get();

					indexWriterHelper.deleteEntityDocuments(
						companyId, className, true);
				}

				indexer.reindex(new String[] {String.valueOf(companyId)});

				if (_isExecuteSyncReindex(executionMode)) {
					SyncReindexManager syncReindexManager =
						_syncReindexManagerSnapshot.get();

					syncReindexManager.deleteStaleDocuments(
						companyId, date, Collections.singleton(className));
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
			finally {
				reindexStatusMessageSender.sendStatusMessage(
					ReindexBackgroundTaskConstants.SINGLE_END, companyId,
					companyIds);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Finished reindexing company ", companyId,
							" for class name ", className,
							" with execution mode ", executionMode));
				}
			}
		}
	}

	@Reference
	protected IndexerRegistry indexerRegistry;

	@Reference
	protected ReindexStatusMessageSender reindexStatusMessageSender;

	@Reference
	protected SearchEngineHelper searchEngineHelper;

	protected ServiceTrackerList<Indexer<?>> systemIndexers;

	private boolean _isExecuteSyncReindex(String executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) && executionMode.equals("sync")) {

			return true;
		}

		return false;
	}

	private boolean _isSystemIndexer(Indexer<?> indexer) {
		if (systemIndexers.size() > 0) {
			for (Indexer<?> systemIndexer : systemIndexers) {
				if (indexer.equals(systemIndexer)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexSingleIndexerBackgroundTaskExecutor.class);

	private static final Snapshot<IndexWriterHelper>
		_indexWriterHelperSnapshot = new Snapshot<>(
			ReindexSingleIndexerBackgroundTaskExecutor.class,
			IndexWriterHelper.class);
	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			ReindexSingleIndexerBackgroundTaskExecutor.class,
			SyncReindexManager.class, null, true);

}