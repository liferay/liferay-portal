/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index.reindexer;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;

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

		WorkflowMetricsIndex workflowMetricsIndex =
			WorkflowMetricsIndex.toWorkflowMetricsIndex(getKey());

		workflowMetricsIndex.removeIndex(
			searchCapabilities, searchEngineAdapter, indexNameBuilder,
			companyId);

		workflowMetricsIndex.createIndex(
			searchCapabilities, searchEngineAdapter, indexNameBuilder,
			companyId);

		reindexEntities(companyId, executionMode);
	}

	protected abstract void reindexEntities(
			long companyId, ExecutionMode executionMode)
		throws Exception;

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

}