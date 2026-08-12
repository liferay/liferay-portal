/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.workflow.metrics.internal.search.constants.WorkflowMetricsIndexTypeConstants;
import com.liferay.portal.workflow.metrics.internal.sla.processor.WorkflowMetricsSLATaskResult;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = SLATaskResultWorkflowMetricsIndexer.class)
public class SLATaskResultWorkflowMetricsIndexer
	extends BaseSLAWorkflowMetricsIndexer {

	public Document createDefaultDocument(
		long companyId, long nodeId, long processId, String taskName) {

		WorkflowMetricsSLATaskResult workflowMetricsSLATaskResult =
			new WorkflowMetricsSLATaskResult();

		workflowMetricsSLATaskResult.setCompanyId(companyId);
		workflowMetricsSLATaskResult.setNodeId(nodeId);
		workflowMetricsSLATaskResult.setProcessId(processId);
		workflowMetricsSLATaskResult.setTaskName(taskName);

		return createDocument(workflowMetricsSLATaskResult);
	}

	public Document createDocument(
		WorkflowMetricsSLATaskResult workflowMetricsSLATaskResult) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue("active", true);

		if (workflowMetricsSLATaskResult.getAssigneeIds() != null) {
			documentBuilder.setLongs(
				"assigneeIds", workflowMetricsSLATaskResult.getAssigneeIds());
			documentBuilder.setString(
				"assigneeType", workflowMetricsSLATaskResult.getAssigneeType());
		}

		documentBuilder.setValue(
			"breached", workflowMetricsSLATaskResult.isBreached()
		).setLong(
			"companyId", workflowMetricsSLATaskResult.getCompanyId()
		);

		if (workflowMetricsSLATaskResult.getCompletionLocalDateTime() != null) {
			documentBuilder.setDate(
				"completionDate",
				formatLocalDateTime(
					workflowMetricsSLATaskResult.getCompletionLocalDateTime()));
		}

		if (workflowMetricsSLATaskResult.getCompletionUserId() != null) {
			documentBuilder.setLong(
				"completionUserId",
				workflowMetricsSLATaskResult.getCompletionUserId());
		}

		documentBuilder.setValue(
			"deleted", false
		).setValue(
			"instanceCompleted",
			workflowMetricsSLATaskResult.isInstanceCompleted()
		);

		if (workflowMetricsSLATaskResult.getInstanceCompletionLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"instanceCompletionDate",
				formatLocalDateTime(
					workflowMetricsSLATaskResult.
						getInstanceCompletionLocalDateTime()));
		}

		documentBuilder.setLong(
			"instanceId", workflowMetricsSLATaskResult.getInstanceId());

		if (workflowMetricsSLATaskResult.getModifiedLocalDateTime() != null) {
			documentBuilder.setDate(
				"modifiedDate",
				formatLocalDateTime(
					workflowMetricsSLATaskResult.getModifiedLocalDateTime()));
		}

		documentBuilder.setLong(
			"nodeId", workflowMetricsSLATaskResult.getNodeId()
		).setValue(
			"onTime", workflowMetricsSLATaskResult.isOnTime()
		).setLong(
			"processId", workflowMetricsSLATaskResult.getProcessId()
		).setLong(
			"slaDefinitionId", workflowMetricsSLATaskResult.getSLADefinitionId()
		);

		WorkflowMetricsSLAStatus workflowMetricsSLAStatus =
			workflowMetricsSLATaskResult.getWorkflowMetricsSLAStatus();

		if (workflowMetricsSLAStatus != null) {
			documentBuilder.setString(
				"status", workflowMetricsSLAStatus.name());
		}

		documentBuilder.setLong(
			"taskId", workflowMetricsSLATaskResult.getTaskId()
		).setString(
			"taskName", workflowMetricsSLATaskResult.getTaskName()
		).setString(
			"uid",
			digest(
				workflowMetricsSLATaskResult.getCompanyId(),
				workflowMetricsSLATaskResult.getInstanceId(),
				workflowMetricsSLATaskResult.getNodeId(),
				workflowMetricsSLATaskResult.getProcessId(),
				workflowMetricsSLATaskResult.getSLADefinitionId(),
				workflowMetricsSLATaskResult.getTaskId())
		);

		return documentBuilder.build();
	}

	@Override
	public String getIndexName(long companyId) {
		return WorkflowMetricsIndex.getIndexName(
			_indexNameBuilder,
			WorkflowMetricsIndexNameConstants.SUFFIX_SLA_TASK_RESULT,
			companyId);
	}

	@Override
	public String getIndexType() {
		return WorkflowMetricsIndexTypeConstants.SLA_TASK_RESULT_TYPE;
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

}