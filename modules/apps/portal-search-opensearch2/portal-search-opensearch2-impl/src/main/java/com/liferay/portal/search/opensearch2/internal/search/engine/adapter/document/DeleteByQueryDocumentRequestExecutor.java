/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.legacy.query.OpenSearchQueryVisitor;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Conflicts;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.DeleteByQueryRequest;
import org.opensearch.client.opensearch.core.DeleteByQueryResponse;

/**
 * @author Dylan Rebelak
 */
public class DeleteByQueryDocumentRequestExecutor {

	public DeleteByQueryDocumentRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
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

		if (deleteByQueryDocumentRequest.isProceedOnConflicts()) {
			builder.conflicts(Conflicts.Proceed);
		}

		builder.index(
			ListUtil.fromArray(deleteByQueryDocumentRequest.getIndexNames()));

		if (deleteByQueryDocumentRequest.getPortalSearchQuery() != null) {
			builder.query(
				new Query(
					com.liferay.portal.search.opensearch2.internal.query.
						OpenSearchQueryVisitor.INSTANCE.translate(
							deleteByQueryDocumentRequest.
								getPortalSearchQuery())));
		}
		else {
			builder.query(
				new Query(
					OpenSearchQueryVisitor.INSTANCE.translate(
						deleteByQueryDocumentRequest.getQuery())));
		}

		builder.refresh(deleteByQueryDocumentRequest.isRefresh());

		return builder.build();
	}

	private DeleteByQueryResponse _getDeleteByQueryResponse(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest,
		DeleteByQueryRequest deleteByQueryRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				deleteByQueryDocumentRequest.getConnectionId(),
				deleteByQueryDocumentRequest.isPreferLocalCluster());

		try {
			return openSearchClient.deleteByQuery(deleteByQueryRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}