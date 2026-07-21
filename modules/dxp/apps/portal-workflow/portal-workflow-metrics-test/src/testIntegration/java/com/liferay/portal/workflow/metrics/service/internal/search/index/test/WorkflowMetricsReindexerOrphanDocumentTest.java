/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.internal.search.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexerRegistry;
import com.liferay.portal.workflow.metrics.service.util.BaseWorkflowMetricsIndexerTestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rodrigo Guedes de Souza
 */
@RunWith(Arquillian.class)
public class WorkflowMetricsReindexerOrphanDocumentTest
	extends BaseWorkflowMetricsIndexerTestCase {

	@Test
	public void testFullReindexRemovesOrphanDocument() throws Exception {
		_assertReindexRemovesOrphanDocument(IndexReindexer.ExecutionMode.FULL);
	}

	@Test
	public void testSyncReindexRemovesOrphanDocument() throws Exception {
		_assertReindexRemovesOrphanDocument(IndexReindexer.ExecutionMode.SYNC);
	}

	private void _assertReindexRemovesOrphanDocument(
			IndexReindexer.ExecutionMode executionMode)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String processIndexName =
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS;

		WorkflowMetricsReindexer workflowMetricsReindexer =
			_workflowMetricsReindexerRegistry.getWorkflowMetricsReindexer(
				"process");

		workflowMetricsReindexer.reindex(companyId);

		_indexOrphanDocument(processIndexName, companyId);

		Assert.assertEquals(
			"Orphan document should be present right after seeding", 1,
			_countOrphanDocuments(processIndexName, companyId));

		if (executionMode == IndexReindexer.ExecutionMode.SYNC) {
			Thread.sleep(1100);
		}

		IndexReindexer indexReindexer =
			(IndexReindexer)workflowMetricsReindexer;

		indexReindexer.reindex(companyId, executionMode);

		Assert.assertEquals(
			executionMode + " reindex must remove the orphan document", 0,
			_countOrphanDocuments(processIndexName, companyId));
	}

	private long _countOrphanDocuments(String indexName, long companyId) {
		searchEngineAdapter.execute(new RefreshIndexRequest(indexName));

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(indexName);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		countSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				QueriesUtil.term("companyId", companyId),
				QueriesUtil.term("processId", _ORPHAN_DOCUMENT_PROCESS_ID)));

		CountSearchResponse countSearchResponse = searchEngineAdapter.execute(
			countSearchRequest);

		return countSearchResponse.getCount();
	}

	private void _indexOrphanDocument(String indexName, long companyId) {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue(
			"deleted", false
		).setLong(
			"companyId", companyId
		).setLong(
			"processId", _ORPHAN_DOCUMENT_PROCESS_ID
		).setString(
			"uid", "orphan-" + _ORPHAN_DOCUMENT_PROCESS_ID
		);

		Document document = documentBuilder.build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			indexName, document.getString("uid"), document);

		indexDocumentRequest.setRefresh(true);

		searchEngineAdapter.execute(indexDocumentRequest);
	}

	private static final long _ORPHAN_DOCUMENT_PROCESS_ID = 999999999L;

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private WorkflowMetricsReindexerRegistry _workflowMetricsReindexerRegistry;

}