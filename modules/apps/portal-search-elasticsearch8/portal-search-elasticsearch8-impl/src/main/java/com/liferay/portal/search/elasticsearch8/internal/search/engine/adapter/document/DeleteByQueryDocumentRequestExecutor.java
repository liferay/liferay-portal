/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.legacy.query.ElasticsearchQueryVisitor;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;

import java.io.IOException;

/**
 * @author Dylan Rebelak
 */
public class DeleteByQueryDocumentRequestExecutor {

	public DeleteByQueryDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public DeleteByQueryDocumentResponse execute(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		DeleteByQueryResponse deleteByQueryResponse = _getDeleteByQueryResponse(
			deleteByQueryDocumentRequest,
			createDeleteByQueryRequest(deleteByQueryDocumentRequest));

		return new DeleteByQueryDocumentResponse(
			deleteByQueryResponse.total(), deleteByQueryResponse.took());
	}

	protected DeleteByQueryRequest createDeleteByQueryRequest(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		DeleteByQueryRequest.Builder builder =
			new DeleteByQueryRequest.Builder();

		builder.index(
			ListUtil.fromArray(deleteByQueryDocumentRequest.getIndexNames()));

		if (deleteByQueryDocumentRequest.getPortalSearchQuery() != null) {
			builder.query(
				new Query(
					com.liferay.portal.search.elasticsearch8.internal.query.
						ElasticsearchQueryVisitor.INSTANCE.translate(
							deleteByQueryDocumentRequest.
								getPortalSearchQuery())));
		}
		else {
			builder.query(
				new Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(
						deleteByQueryDocumentRequest.getQuery())));
		}

		if (deleteByQueryDocumentRequest.isProceedOnConflicts()) {
			builder.conflicts(Conflicts.Proceed);
		}

		builder.refresh(deleteByQueryDocumentRequest.isRefresh());

		return builder.build();
	}

	private DeleteByQueryResponse _getDeleteByQueryResponse(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest,
		DeleteByQueryRequest deleteByQueryRequest) {

		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(
				deleteByQueryDocumentRequest.getConnectionId(),
				deleteByQueryDocumentRequest.isPreferLocalCluster());

		try {
			return elasticsearchClient.deleteByQuery(deleteByQueryRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}