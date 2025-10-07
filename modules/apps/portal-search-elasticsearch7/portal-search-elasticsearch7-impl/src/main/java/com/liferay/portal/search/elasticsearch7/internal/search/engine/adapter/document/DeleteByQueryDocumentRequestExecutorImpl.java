/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.QueryTranslator;

import java.io.IOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(service = DeleteByQueryDocumentRequestExecutor.class)
public class DeleteByQueryDocumentRequestExecutorImpl
	implements DeleteByQueryDocumentRequestExecutor {

	@Override
	public DeleteByQueryDocumentResponse execute(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		DeleteByQueryRequest deleteByQueryRequest = createDeleteByQueryRequest(
			deleteByQueryDocumentRequest);

		BulkByScrollResponse bulkByScrollResponse = getBulkByScrollResponse(
			deleteByQueryRequest, deleteByQueryDocumentRequest);

		TimeValue timeValue = bulkByScrollResponse.getTook();

		return new DeleteByQueryDocumentResponse(
			bulkByScrollResponse.getDeleted(), timeValue.getMillis());
	}

	@Activate
	protected void activate() {
		_legacyQueryTranslator = new ElasticsearchQueryTranslator(
			_indexNameBuilder);
	}

	protected DeleteByQueryRequest createDeleteByQueryRequest(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();

		deleteByQueryRequest.indices(
			deleteByQueryDocumentRequest.getIndexNames());

		if (deleteByQueryDocumentRequest.getPortalSearchQuery() != null) {
			QueryBuilder queryBuilder = _queryTranslator.translate(
				deleteByQueryDocumentRequest.getPortalSearchQuery());

			deleteByQueryRequest.setQuery(queryBuilder);
		}
		else {
			@SuppressWarnings("deprecation")
			QueryBuilder queryBuilder = _legacyQueryTranslator.translate(
				deleteByQueryDocumentRequest.getQuery(), null);

			deleteByQueryRequest.setQuery(queryBuilder);
		}

		deleteByQueryRequest.setRefresh(
			deleteByQueryDocumentRequest.isRefresh());

		return deleteByQueryRequest;
	}

	protected BulkByScrollResponse getBulkByScrollResponse(
		DeleteByQueryRequest deleteByQueryRequest,
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				deleteByQueryDocumentRequest.getConnectionId(),
				deleteByQueryDocumentRequest.isPreferLocalCluster());

		try {
			return restHighLevelClient.deleteByQuery(
				deleteByQueryRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private com.liferay.portal.kernel.search.query.QueryTranslator<QueryBuilder>
		_legacyQueryTranslator;
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new com.liferay.portal.search.elasticsearch7.internal.query.
			ElasticsearchQueryTranslator();

}