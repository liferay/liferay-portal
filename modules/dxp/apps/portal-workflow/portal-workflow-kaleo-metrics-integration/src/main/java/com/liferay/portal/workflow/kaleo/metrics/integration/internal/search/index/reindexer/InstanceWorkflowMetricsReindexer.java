/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.internal.search.index.reindexer;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.workflow.kaleo.metrics.integration.internal.helper.IndexerHelper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.background.task.WorkflowMetricsReindexStatusMessageSender;
import com.liferay.portal.workflow.metrics.search.index.InstanceWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = IndexReindexer.class)
public class InstanceWorkflowMetricsReindexer implements IndexReindexer { //rename class

	public String getKey() {
		return "instance";
	}

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!_searchCapabilities.isResultRankingsSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		WorkflowMetricsIndex workflowMetricsIndex =
				WorkflowMetricsIndex.toWorkflowMetricsIndex(getKey());

		Date date = null;

		if (_isExecuteSyncReindex(executionMode)) {
			date = new Date();

			Thread.sleep(1000);
		}
		else {
			workflowMetricsIndex.removeIndex(
				_searchCapabilities, _searchEngineAdapter,
				_indexNameBuilder, companyId);

			workflowMetricsIndex.createIndex(
				_searchCapabilities, _searchEngineAdapter,
				_indexNameBuilder, companyId);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_kaleoInstanceLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});

		long total = actionableDynamicQuery.performCount();

		AtomicInteger atomicCounter = new AtomicInteger(0);

		actionableDynamicQuery.setPerformActionMethod(
			(KaleoInstance kaleoInstance) -> {
				KaleoDefinitionVersion kaleoDefinitionVersion =
					_kaleoDefinitionVersionLocalService.
						fetchKaleoDefinitionVersion(
							kaleoInstance.getKaleoDefinitionVersionId());

				if (Objects.isNull(kaleoDefinitionVersion)) {
					return;
				}

				_instanceWorkflowMetricsIndexer.addInstance(
					_indexerHelper.createAssetTitleLocalizationMap(
						kaleoInstance.getClassName(),
						kaleoInstance.getClassPK(), kaleoInstance.getGroupId()),
					_indexerHelper.createAssetTypeLocalizationMap(
						kaleoInstance.getClassName(),
						kaleoInstance.getGroupId()),
					kaleoInstance.getClassName(), kaleoInstance.getClassPK(),
					companyId, kaleoInstance.getCompletionDate(),
					kaleoInstance.getCreateDate(),
					kaleoInstance.getKaleoInstanceId(),
					kaleoInstance.getModifiedDate(),
					kaleoInstance.getKaleoDefinitionId(),
					kaleoDefinitionVersion.getVersion(),
					kaleoInstance.getUserId(), kaleoInstance.getUserName());

				_workflowMetricsReindexStatusMessageSender.sendStatusMessage(
					atomicCounter.incrementAndGet(), total, "instance");
			});

		actionableDynamicQuery.performActions();

		if (_isExecuteSyncReindex(executionMode)) {
			SyncReindexManager syncReindexManager =
				_syncReindexManagerSnapshot.get();

			syncReindexManager.deleteStaleDocuments(
				WorkflowMetricsIndex.getIndexName(
					_indexNameBuilder,
					WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
					companyId),
				date, Collections.emptySet());
		}
	}

	private boolean _isExecuteSyncReindex(ExecutionMode executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) &&
			executionMode.equals(ExecutionMode.SYNC)) {

			return true;
		}

		return false;
	}

	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			InstanceWorkflowMetricsReindexer.class, SyncReindexManager.class,
		null, true);

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private IndexerHelper _indexerHelper;

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private WorkflowMetricsReindexStatusMessageSender
		_workflowMetricsReindexStatusMessageSender;

}