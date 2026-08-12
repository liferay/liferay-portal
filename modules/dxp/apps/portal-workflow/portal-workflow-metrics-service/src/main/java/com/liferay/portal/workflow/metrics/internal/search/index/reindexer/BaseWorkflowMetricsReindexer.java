/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index.reindexer;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;

import java.util.Collections;
import java.util.Date;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Felipe Lorenz
 */
public abstract class BaseWorkflowMetricsReindexer
	implements IndexReindexer, WorkflowMetricsReindexer {

	@Override
	public void reindex(long companyId) throws PortalException {
		try {
			reindex(companyId, ExecutionMode.FULL);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!searchCapabilities.isWorkflowMetricsSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		WorkflowMetricsIndex.createMissingIndexes(
			searchCapabilities, searchEngineAdapter, indexNameBuilder,
			companyId);

		Date date = null;

		WorkflowMetricsIndex workflowMetricsIndex =
			WorkflowMetricsIndex.toWorkflowMetricsIndex(getKey());

		if (isExecuteSyncReindex(executionMode)) {
			date = new Date();

			Thread.sleep(1000);
		}
		else {
			workflowMetricsIndex.removeIndex(
				searchCapabilities, searchEngineAdapter, indexNameBuilder,
				companyId);

			workflowMetricsIndex.createIndex(
				searchCapabilities, searchEngineAdapter, indexNameBuilder,
				companyId);
		}

		reindexEntities(companyId);

		if (isExecuteSyncReindex(executionMode)) {
			SyncReindexManager syncReindexManager =
				_syncReindexManagerSnapshot.get();

			syncReindexManager.deleteStaleDocuments(
				WorkflowMetricsIndex.getIndexName(
					indexNameBuilder, workflowMetricsIndex.getIndexNameSuffix(),
					companyId),
				date, Collections.emptySet());
		}
	}

	protected boolean isExecuteSyncReindex(ExecutionMode executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) &&
			executionMode.equals(ExecutionMode.SYNC)) {

			return true;
		}

		return false;
	}

	protected abstract void reindexEntities(long companyId) throws Exception;

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			BaseWorkflowMetricsReindexer.class, SyncReindexManager.class, null,
			true);

}