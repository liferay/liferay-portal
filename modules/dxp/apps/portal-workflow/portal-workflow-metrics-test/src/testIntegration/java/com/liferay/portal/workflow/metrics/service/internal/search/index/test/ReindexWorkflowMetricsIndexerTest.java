/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.internal.search.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexerRegistry;
import com.liferay.portal.workflow.metrics.service.util.BaseWorkflowMetricsIndexerTestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rodrigo Guedes de Souza
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class ReindexWorkflowMetricsIndexerTest
	extends BaseWorkflowMetricsIndexerTestCase {

	@Test
	public void testReindexPopulatesDocuments() throws Exception {
		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			addKaleoTaskInstanceToken("Review");

		long kaleoDefinitionId = kaleoTaskInstanceToken.getKaleoDefinitionId();
		long kaleoTaskInstanceTokenId =
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId();

		_assertInstanceReindexPopulatesDocuments(
			IndexReindexer.ExecutionMode.FULL, kaleoDefinitionId,
			kaleoTaskInstanceTokenId);
		_assertInstanceReindexPopulatesDocuments(
			IndexReindexer.ExecutionMode.SYNC, kaleoDefinitionId,
			kaleoTaskInstanceTokenId);

		_assertTaskReindexPopulatesTemplates(
			IndexReindexer.ExecutionMode.FULL, kaleoDefinitionId);
		_assertTaskReindexPopulatesTemplates(
			IndexReindexer.ExecutionMode.SYNC, kaleoDefinitionId);
	}

	@Test
	public void testReindexRemovesOrphanDocument() throws Exception {
		_assertReindexRemovesOrphanDocument(IndexReindexer.ExecutionMode.FULL);
		_assertReindexRemovesOrphanDocument(IndexReindexer.ExecutionMode.SYNC);
	}

	private void _assertInstanceReindexPopulatesDocuments(
			IndexReindexer.ExecutionMode executionMode, long kaleoDefinitionId,
			long kaleoTaskInstanceTokenId)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String instanceIndexName = _getIndexName(
			companyId, WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE);

		Query instanceTemplateQuery = _getTemplateQuery(
			"instanceId", kaleoDefinitionId);

		long instanceTemplatesCount = _getCount(
			instanceIndexName, instanceTemplateQuery);

		Assert.assertTrue(instanceTemplatesCount > 0);

		String taskIndexName = _getIndexName(
			companyId, WorkflowMetricsIndexNameConstants.SUFFIX_TASK);

		Query taskTemplateQuery = _getTemplateQuery(
			"taskId", kaleoDefinitionId);

		long taskTemplatesCount = _getCount(taskIndexName, taskTemplateQuery);

		Assert.assertTrue(taskTemplatesCount > 0);

		_deleteDocuments(instanceIndexName, instanceTemplateQuery);

		Query tasksQuery = _getTasksQuery(kaleoTaskInstanceTokenId);

		_deleteDocuments(instanceIndexName, tasksQuery);

		_deleteDocuments(taskIndexName, taskTemplateQuery);

		Assert.assertEquals(
			0, _getCount(instanceIndexName, instanceTemplateQuery));
		Assert.assertEquals(0, _getCount(instanceIndexName, tasksQuery));
		Assert.assertEquals(0, _getCount(taskIndexName, taskTemplateQuery));

		_reindex(companyId, executionMode, "instance");

		Assert.assertEquals(
			instanceTemplatesCount,
			_getCount(instanceIndexName, instanceTemplateQuery));
		Assert.assertEquals(
			taskTemplatesCount, _getCount(taskIndexName, taskTemplateQuery));
		Assert.assertEquals(1, _getCount(instanceIndexName, tasksQuery));
	}

	private void _assertReindexRemovesOrphanDocument(
			IndexReindexer.ExecutionMode executionMode)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String processIndexName = _getIndexName(
			companyId, WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS);

		_indexProcessDocument(processIndexName, companyId);

		Assert.assertEquals(
			1, _getCount(processIndexName, _getProcessQuery(companyId)));

		_reindex(companyId, executionMode, "process");

		Assert.assertEquals(
			executionMode + " reindex must remove orphan documents", 0,
			_getCount(processIndexName, _getProcessQuery(companyId)));
	}

	private void _assertTaskReindexPopulatesTemplates(
			IndexReindexer.ExecutionMode executionMode, long kaleoDefinitionId)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String taskIndexName = _getIndexName(
			companyId, WorkflowMetricsIndexNameConstants.SUFFIX_TASK);

		Query taskTemplateQuery = _getTemplateQuery(
			"taskId", kaleoDefinitionId);

		long taskTemplatesCount = _getCount(taskIndexName, taskTemplateQuery);

		Assert.assertTrue(taskTemplatesCount > 0);

		_deleteDocuments(taskIndexName, taskTemplateQuery);

		Assert.assertEquals(0, _getCount(taskIndexName, taskTemplateQuery));

		_reindex(companyId, executionMode, "task");

		Assert.assertEquals(
			executionMode + " reindex must populate task's templates",
			taskTemplatesCount, _getCount(taskIndexName, taskTemplateQuery));
	}

	private void _deleteDocuments(String indexName, Query query) {
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(query, indexName);

		deleteByQueryDocumentRequest.setRefresh(true);
		deleteByQueryDocumentRequest.setWaitForCompletion(true);

		searchEngineAdapter.execute(deleteByQueryDocumentRequest);
	}

	private long _getCount(String indexName, Query query) {
		searchEngineAdapter.execute(new RefreshIndexRequest(indexName));

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(indexName);
		countSearchRequest.setQuery(query);

		CountSearchResponse countSearchResponse = searchEngineAdapter.execute(
			countSearchRequest);

		return countSearchResponse.getCount();
	}

	private String _getIndexName(long companyId, String suffix) {
		return _indexNameBuilder.getIndexName(companyId) + suffix;
	}

	private Query _getProcessQuery(long companyId) {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		return booleanQuery.addFilterQueryClauses(
			QueriesUtil.term("companyId", companyId),
			QueriesUtil.term("processId", _PROCESS_ID));
	}

	private Query _getTasksQuery(long taskId) {
		return QueriesUtil.nested(
			"tasks", QueriesUtil.term("tasks.taskId", taskId));
	}

	private Query _getTemplateQuery(String idFieldName, long processId) {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addMustQueryClauses(
			QueriesUtil.term(idFieldName, 0L),
			QueriesUtil.term("deleted", false),
			QueriesUtil.term("processId", processId));

		return booleanQuery;
	}

	private void _indexProcessDocument(String indexName, long companyId) {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue(
			"deleted", false
		).setLong(
			"companyId", companyId
		).setLong(
			"processId", _PROCESS_ID
		).setString(
			"uid", "WorkflowMetricsProcess" + _PROCESS_ID
		);

		Document document = documentBuilder.build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			indexName, document.getString("uid"), document);

		indexDocumentRequest.setRefresh(true);

		searchEngineAdapter.execute(indexDocumentRequest);
	}

	private void _reindex(
			long companyId, IndexReindexer.ExecutionMode executionMode,
			String indexEntityName)
		throws Exception {

		if (executionMode == IndexReindexer.ExecutionMode.SYNC) {
			Thread.sleep(1000);
		}

		WorkflowMetricsReindexer workflowMetricsReindexer =
			_workflowMetricsReindexerRegistry.getWorkflowMetricsReindexer(
				indexEntityName);

		IndexReindexer indexReindexer =
			(IndexReindexer)workflowMetricsReindexer;

		indexReindexer.reindex(companyId, executionMode);
	}

	private static final long _PROCESS_ID = RandomTestUtil.randomLong();

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private WorkflowMetricsReindexerRegistry _workflowMetricsReindexerRegistry;

}