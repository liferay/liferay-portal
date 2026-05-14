/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index.reindexer;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.metrics.integration.helper.IndexerHelper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.background.task.WorkflowMetricsReindexStatusMessageSender;
import com.liferay.portal.workflow.metrics.search.index.NodeWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = {IndexReindexer.class, WorkflowMetricsReindexer.class})
public class NodeWorkflowMetricsReindexer
	implements IndexReindexer, WorkflowMetricsReindexer {

	@Override
	public String getIndexNameSuffix() {
		return WorkflowMetricsIndexNameConstants.SUFFIX_NODE;
	}

	@Override
	public String getKey() {
		return "node";
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #reindex(long, ExecutionMode)}
	 */
	@Deprecated
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

		if (!_searchCapabilities.isWorkflowMetricsSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		Date date = null;

		if (_isExecuteSyncReindex(executionMode)) {
			date = new Date();

			Thread.sleep(1000);
		}
		else {
			WorkflowMetricsIndex workflowMetricsIndex =
				WorkflowMetricsIndex.toWorkflowMetricsIndex(getKey());

			workflowMetricsIndex.removeIndex(
				_searchCapabilities, _searchEngineAdapter, _indexNameBuilder,
				companyId);

			workflowMetricsIndex.createIndex(
				_searchCapabilities, _searchEngineAdapter, _indexNameBuilder,
				companyId);
		}

		_reindexIndexWithKaleoNode(companyId);
		_reindexIndexWithKaleoTask(companyId);

		if (_isExecuteSyncReindex(executionMode)) {
			SyncReindexManager syncReindexManager =
				_syncReindexManagerSnapshot.get();

			syncReindexManager.deleteStaleDocuments(
				WorkflowMetricsIndex.getIndexName(
					_indexNameBuilder,
					WorkflowMetricsIndexNameConstants.SUFFIX_NODE, companyId),
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

	private void _reindexIndexWithKaleoNode(long companyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_kaleoNodeLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));

				Property typeProperty = PropertyFactoryUtil.forName("type");

				dynamicQuery.add(typeProperty.eq(NodeType.STATE.name()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoNode kaleoNode) -> {
				KaleoDefinitionVersion kaleoDefinitionVersion =
					_kaleoDefinitionVersionLocalService.
						fetchKaleoDefinitionVersion(
							kaleoNode.getKaleoDefinitionVersionId());

				if (Objects.isNull(kaleoDefinitionVersion)) {
					return;
				}

				_nodeWorkflowMetricsIndexer.addNode(
					_indexerHelper.createAddNodeRequest(
						kaleoDefinitionVersion, kaleoNode));
			});

		actionableDynamicQuery.performActions();
	}

	private void _reindexIndexWithKaleoTask(long companyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_kaleoTaskLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});

		long total = actionableDynamicQuery.performCount();

		AtomicInteger atomicCounter = new AtomicInteger(0);

		actionableDynamicQuery.setPerformActionMethod(
			(KaleoTask kaleoTask) -> {
				KaleoDefinitionVersion kaleoDefinitionVersion =
					_kaleoDefinitionVersionLocalService.
						fetchKaleoDefinitionVersion(
							kaleoTask.getKaleoDefinitionVersionId());

				if (Objects.isNull(kaleoDefinitionVersion)) {
					return;
				}

				_nodeWorkflowMetricsIndexer.addNode(
					_indexerHelper.createAddNodeRequest(
						kaleoDefinitionVersion, kaleoTask));

				_workflowMetricsReindexStatusMessageSender.sendStatusMessage(
					atomicCounter.incrementAndGet(), total, "node");
			});

		actionableDynamicQuery.performActions();
	}

	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			NodeWorkflowMetricsReindexer.class, SyncReindexManager.class, null,
			true);

	@Reference
	private IndexerHelper _indexerHelper;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private NodeWorkflowMetricsIndexer _nodeWorkflowMetricsIndexer;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private WorkflowMetricsReindexStatusMessageSender
		_workflowMetricsReindexStatusMessageSender;

}