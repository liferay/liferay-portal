/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.workflow.metrics.internal.search.constants.WorkflowMetricsIndexTypeConstants;
import com.liferay.portal.workflow.metrics.internal.search.index.util.WorkflowMetricsIndexerUtil;
import com.liferay.portal.workflow.metrics.model.AddNodeRequest;
import com.liferay.portal.workflow.metrics.model.DeleteNodeRequest;
import com.liferay.portal.workflow.metrics.search.index.NodeWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = NodeWorkflowMetricsIndexer.class)
public class NodeWorkflowMetricsIndexerImpl
	extends BaseWorkflowMetricsIndexer implements NodeWorkflowMetricsIndexer {

	@Override
	public Document addNode(AddNodeRequest addNodeRequest) {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		Document document = documentBuilder.setLong(
			"companyId", addNodeRequest.getCompanyId()
		).setDate(
			"createDate", getDate(addNodeRequest.getCreateDate())
		).setValue(
			"deleted", false
		).setValue(
			"initial", addNodeRequest.getInitial()
		).setDate(
			"modifiedDate", getDate(addNodeRequest.getModifiedDate())
		).setString(
			"name", addNodeRequest.getName()
		).setString(
			Field.getSortableFieldName("name"),
			StringUtil.toLowerCase(addNodeRequest.getName())
		).setLong(
			"nodeId", addNodeRequest.getNodeId()
		).setLong(
			"processId", addNodeRequest.getProcessId()
		).setValue(
			"terminal", addNodeRequest.getTerminal()
		).setString(
			"type", addNodeRequest.getType()
		).setString(
			"uid",
			digest(addNodeRequest.getCompanyId(), addNodeRequest.getNodeId())
		).setString(
			"version", addNodeRequest.getProcessVersion()
		).build();

		workflowMetricsPortalExecutor.execute(() -> addDocument(document));

		return document;
	}

	@Override
	public void deleteNode(DeleteNodeRequest deleteNodeRequest) {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", deleteNodeRequest.getCompanyId()
		).setLong(
			"nodeId", deleteNodeRequest.getNodeId()
		).setString(
			"uid",
			digest(
				deleteNodeRequest.getCompanyId(), deleteNodeRequest.getNodeId())
		);

		workflowMetricsPortalExecutor.execute(
			() -> deleteDocument(documentBuilder));
	}

	@Override
	public String getIndexName(long companyId) {
		return WorkflowMetricsIndex.getIndexName(
			_indexNameBuilder, WorkflowMetricsIndexNameConstants.SUFFIX_NODE,
			companyId);
	}

	@Override
	public String getIndexType() {
		return WorkflowMetricsIndexTypeConstants.NODE_TYPE;
	}

	@Override
	protected void addDocument(Document document) {
		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		super.addDocument(document);

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		if (Objects.equals(document.getString("type"), "TASK")) {
			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					_slaTaskResultWorkflowMetricsIndexer.getIndexName(
						document.getLong("companyId")),
					_slaTaskResultWorkflowMetricsIndexer.createDefaultDocument(
						document.getLong("companyId"),
						document.getLong("nodeId"),
						document.getLong("processId"),
						document.getString("name"))));

			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					WorkflowMetricsIndex.getIndexName(
						_indexNameBuilder,
						WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
						document.getLong("companyId")),
					_createWorkflowMetricsTaskDocument(
						document.getLong("companyId"),
						document.getLong("processId"),
						document.getLong("nodeId"), document.getString("name"),
						document.getString("version"))));
		}

		bulkDocumentRequest.addBulkableDocumentRequest(
			new IndexDocumentRequest(
				WorkflowMetricsIndex.getIndexName(
					_indexNameBuilder,
					WorkflowMetricsIndexNameConstants.SUFFIX_NODE,
					document.getLong("companyId")),
				document));

		if (PortalRunMode.isTestMode()) {
			bulkDocumentRequest.setRefresh(true);
		}

		searchEngineAdapter.execute(bulkDocumentRequest);
	}

	private Document _createWorkflowMetricsTaskDocument(
		long companyId, long processId, long nodeId, String name,
		String processVersion) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", companyId
		).setValue(
			"completed", false
		).setValue(
			"deleted", false
		).setValue(
			"instanceCompleted", false
		).setLong(
			"instanceId", 0L
		).setString(
			"name", name
		).setString(
			Field.getSortableFieldName("name"), StringUtil.toLowerCase(name)
		).setLong(
			"nodeId", nodeId
		).setLong(
			"processId", processId
		).setLong(
			"taskId", 0L
		).setString(
			"uid",
			WorkflowMetricsIndexerUtil.digest(
				WorkflowMetricsIndexTypeConstants.TASK_TYPE, companyId,
				processId, processVersion, nodeId)
		).setString(
			"version", processVersion
		);

		return documentBuilder.build();
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

}