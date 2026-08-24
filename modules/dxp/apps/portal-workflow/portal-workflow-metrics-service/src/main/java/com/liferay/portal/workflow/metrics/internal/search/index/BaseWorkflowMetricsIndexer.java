/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.workflow.metrics.internal.petra.executor.WorkflowMetricsPortalExecutor;
import com.liferay.portal.workflow.metrics.internal.search.index.util.WorkflowMetricsIndexerUtil;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
public abstract class BaseWorkflowMetricsIndexer {

	public void addDocuments(List<Document> documents) {
		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		documents.forEach(
			document -> bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					getIndexName(document.getLong("companyId")),
					document.getString("uid"), document)));

		if (ListUtil.isNotEmpty(
				bulkDocumentRequest.getBulkableDocumentRequests())) {

			bulkDocumentRequest.setRefresh(
				IndexWriterHelperUtil.isIndexCommitImmediately());

			searchEngineAdapter.execute(bulkDocumentRequest);
		}
	}

	public void deleteDocument(DocumentBuilder documentBuilder) {
		documentBuilder.setValue("deleted", true);

		_updateDocument(documentBuilder.build());
	}

	public abstract String getIndexName(long companyId);

	public abstract String getIndexType();

	public void updateDocument(Document document) {
		_updateDocument(document);
	}

	protected void addDocument(Document document) {
		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			getIndexName(document.getLong("companyId")), document);

		indexDocumentRequest.setRefresh(
			IndexWriterHelperUtil.isIndexCommitImmediately());

		searchEngineAdapter.execute(indexDocumentRequest);
	}

	protected String digest(Serializable... parts) {
		return WorkflowMetricsIndexerUtil.digest(getIndexType(), parts);
	}

	protected String formatLocalDateTime(LocalDateTime localDateTime) {
		return _dateTimeFormatter.format(localDateTime);
	}

	protected String getDate(Date date) {
		try {
			return DateUtil.getDate(
				date, "yyyyMMddHHmmss", LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	protected void setLocalizedField(
		DocumentBuilder documentBuilder, String fieldName,
		Map<Locale, String> localizedMap) {

		for (Map.Entry<Locale, String> entry : localizedMap.entrySet()) {
			String localizedName = Field.getLocalizedName(
				entry.getKey(), fieldName);

			documentBuilder.setValue(
				localizedName, entry.getValue()
			).setValue(
				Field.getSortableFieldName(localizedName), entry.getValue()
			);
		}
	}

	protected void updateDocuments(
		long companyId, Map<String, Object> fieldsMap, Query filterQuery) {

		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(getIndexName(companyId));

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(filterQuery));

		searchSearchRequest.setSelectedFieldNames("uid");
		searchSearchRequest.setSize(10000);

		SearchSearchResponse searchSearchResponse = searchEngineAdapter.execute(
			searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		if (searchHits.getTotalHits() == 0) {
			return;
		}

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();
			DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

			documentBuilder.setString("uid", document.getString("uid"));

			fieldsMap.forEach(documentBuilder::setValue);

			UpdateDocumentRequest updateDocumentRequest =
				new UpdateDocumentRequest(
					getIndexName(companyId), document.getString("uid"),
					documentBuilder.build());

			updateDocumentRequest.setUpsert(true);

			bulkDocumentRequest.addBulkableDocumentRequest(
				updateDocumentRequest);
		}

		if (ListUtil.isNotEmpty(
				bulkDocumentRequest.getBulkableDocumentRequests())) {

			bulkDocumentRequest.setRefresh(
				IndexWriterHelperUtil.isIndexCommitImmediately());

			searchEngineAdapter.execute(bulkDocumentRequest);
		}
	}

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	@Reference
	protected WorkflowMetricsPortalExecutor workflowMetricsPortalExecutor;

	private void _updateDocument(Document document) {
		if (!searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			getIndexName(document.getLong("companyId")),
			document.getString("uid"), document);

		updateDocumentRequest.setRefresh(
			IndexWriterHelperUtil.isIndexCommitImmediately());

		updateDocumentRequest.setUpsert(true);

		searchEngineAdapter.execute(updateDocumentRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseWorkflowMetricsIndexer.class);

	private final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

}