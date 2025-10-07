/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.legacy.query.OpenSearchQueryTranslator;
import com.liferay.portal.search.query.QueryTranslator;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.DeleteByQueryRequest;
import org.opensearch.client.opensearch.core.DeleteByQueryResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 * @author Petteri Karttunen
 */
@Component(service = DeleteByQueryDocumentRequestExecutor.class)
public class DeleteByQueryDocumentRequestExecutorImpl
	implements DeleteByQueryDocumentRequestExecutor {

	@Override
	public DeleteByQueryDocumentResponse execute(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest) {

		DeleteByQueryResponse deleteByQueryResponse = _getDeleteByQueryResponse(
			deleteByQueryDocumentRequest,
			createDeleteByQueryRequest(deleteByQueryDocumentRequest));

		return new DeleteByQueryDocumentResponse(
			deleteByQueryResponse.total(), deleteByQueryResponse.took());
	}

	@Activate
	protected void activate() {
		_legacyQueryTranslator = new OpenSearchQueryTranslator(
			_indexNameBuilder);
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
					_queryTranslator.translate(
						deleteByQueryDocumentRequest.getPortalSearchQuery())));
		}
		else {
			builder.query(
				new Query(
					_legacyQueryTranslator.translate(
						deleteByQueryDocumentRequest.getQuery(), null)));
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

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private com.liferay.portal.kernel.search.query.QueryTranslator<QueryVariant>
		_legacyQueryTranslator;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

	private final QueryTranslator<QueryVariant> _queryTranslator =
		new com.liferay.portal.search.opensearch2.internal.query.
			OpenSearchQueryTranslator();

}