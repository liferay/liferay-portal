/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.script.ScriptBuilder;
import com.liferay.portal.search.script.ScriptType;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.workflow.metrics.internal.search.constants.WorkflowMetricsIndexTypeConstants;
import com.liferay.portal.workflow.metrics.internal.sla.processor.WorkflowMetricsSLAInstanceResult;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = SLAInstanceResultWorkflowMetricsIndexer.class)
public class SLAInstanceResultWorkflowMetricsIndexer
	extends BaseSLAWorkflowMetricsIndexer {

	public void blockDocuments(
		long companyId, long processId, long slaDefinitionId) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term("instanceCompleted", Boolean.TRUE));

		updateDocuments(
			companyId,
			HashMapBuilder.<String, Object>put(
				"blocked", Boolean.TRUE
			).build(),
			booleanQuery.addMustQueryClauses(
				QueriesUtil.term("companyId", companyId),
				QueriesUtil.term("processId", processId),
				QueriesUtil.term("slaDefinitionId", slaDefinitionId)));
	}

	public Document createDefaultDocument(long companyId, long processId) {
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult =
			new WorkflowMetricsSLAInstanceResult();

		workflowMetricsSLAInstanceResult.setCompanyId(companyId);
		workflowMetricsSLAInstanceResult.setProcessId(processId);

		return createDocument(workflowMetricsSLAInstanceResult);
	}

	public Document createDocument(
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue(
			"active", true
		).setValue(
			"blocked", false
		).setLong(
			"companyId", workflowMetricsSLAInstanceResult.getCompanyId()
		);

		if (workflowMetricsSLAInstanceResult.getCompletionLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"completionDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getCompletionLocalDateTime()));
		}

		documentBuilder.setValue(
			"deleted", false
		).setLong(
			"elapsedTime", workflowMetricsSLAInstanceResult.getElapsedTime()
		).setValue(
			"instanceCompleted",
			workflowMetricsSLAInstanceResult.getCompletionLocalDateTime() !=
				null
		).setLong(
			"instanceId", workflowMetricsSLAInstanceResult.getInstanceId()
		);

		if (workflowMetricsSLAInstanceResult.getModifiedLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"modifiedDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getModifiedLocalDateTime()));
		}

		documentBuilder.setValue(
			"onTime", workflowMetricsSLAInstanceResult.isOnTime());

		if (workflowMetricsSLAInstanceResult.getOverdueLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"overdueDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getOverdueLocalDateTime()));
		}

		documentBuilder.setLong(
			"processId", workflowMetricsSLAInstanceResult.getProcessId()
		).setLong(
			"remainingTime", workflowMetricsSLAInstanceResult.getRemainingTime()
		).setLong(
			"slaDefinitionId",
			workflowMetricsSLAInstanceResult.getSLADefinitionId()
		);

		WorkflowMetricsSLAStatus workflowMetricsSLAStatus =
			workflowMetricsSLAInstanceResult.getWorkflowMetricsSLAStatus();

		if (workflowMetricsSLAStatus != null) {
			documentBuilder.setString(
				"status", workflowMetricsSLAStatus.name());
		}

		documentBuilder.setString(
			"uid",
			digest(
				workflowMetricsSLAInstanceResult.getCompanyId(),
				workflowMetricsSLAInstanceResult.getInstanceId(),
				workflowMetricsSLAInstanceResult.getProcessId(),
				workflowMetricsSLAInstanceResult.getSLADefinitionId()));

		return documentBuilder.build();
	}

	@Override
	public void deleteDocuments(
		long companyId, long processId, long slaDefinitionId) {

		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		super.deleteDocuments(companyId, processId, slaDefinitionId);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		BooleanQuery filterBooleanQuery = QueriesUtil.booleanQuery();

		filterBooleanQuery.addMustNotQueryClauses(
			QueriesUtil.term("instanceCompleted", Boolean.TRUE));

		filterBooleanQuery.addMustQueryClauses(
			QueriesUtil.term("completed", false),
			QueriesUtil.term("processId", processId),
			QueriesUtil.nested(
				"slaResults",
				QueriesUtil.term(
					"slaResults.slaDefinitionId", slaDefinitionId)));

		booleanQuery.addFilterQueryClauses(filterBooleanQuery);

		ScriptBuilder scriptBuilder = Scripts.INSTANCE.builder();

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				booleanQuery,
				scriptBuilder.idOrCode(
					StringUtil.read(
						getClass(),
						"dependencies/workflow-metrics-delete-sla-result-" +
							"script.painless")
				).language(
					"painless"
				).putParameter(
					"slaDefinitionId", slaDefinitionId
				).scriptType(
					ScriptType.INLINE
				).build(),
				WorkflowMetricsIndex.getIndexName(
					_indexNameBuilder,
					WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
					companyId));

		if (PortalRunMode.isTestMode()) {
			updateByQueryDocumentRequest.setRefresh(true);
		}

		searchEngineAdapter.execute(updateByQueryDocumentRequest);
	}

	@Override
	public String getIndexName(long companyId) {
		return WorkflowMetricsIndex.getIndexName(
			_indexNameBuilder,
			WorkflowMetricsIndexNameConstants.SUFFIX_SLA_INSTANCE_RESULT,
			companyId);
	}

	@Override
	public String getIndexType() {
		return WorkflowMetricsIndexTypeConstants.SLA_INSTANCE_RESULT_TYPE;
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

}