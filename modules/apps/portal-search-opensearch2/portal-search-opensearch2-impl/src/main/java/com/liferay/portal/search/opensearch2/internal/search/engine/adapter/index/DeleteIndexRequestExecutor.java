/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class DeleteIndexRequestExecutor {

	public DeleteIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public DeleteIndexResponse execute(DeleteIndexRequest deleteIndexRequest) {
		org.opensearch.client.opensearch.indices.DeleteIndexResponse
			deleteIndexResponse = getDeleteIndexResponse(
				deleteIndexRequest,
				createDeleteIndexRequest(deleteIndexRequest));

		return new DeleteIndexResponse(deleteIndexResponse.acknowledged());
	}

	protected org.opensearch.client.opensearch.indices.DeleteIndexRequest
		createDeleteIndexRequest(DeleteIndexRequest deleteIndexRequest) {

		org.opensearch.client.opensearch.indices.DeleteIndexRequest.Builder
			builder =
				new org.opensearch.client.opensearch.indices.DeleteIndexRequest.
					Builder();

		IndicesOptions indicesOptions = deleteIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			builder.allowNoIndices(indicesOptions.isAllowNoIndices());
			builder.ignoreUnavailable(indicesOptions.isIgnoreUnavailable());
		}

		builder.index(ListUtil.fromArray(deleteIndexRequest.getIndexNames()));

		return builder.build();
	}

	protected org.opensearch.client.opensearch.indices.DeleteIndexResponse
		getDeleteIndexResponse(
			DeleteIndexRequest deleteIndexRequest,
			org.opensearch.client.opensearch.indices.DeleteIndexRequest
				openSearchDeleteIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				deleteIndexRequest.getConnectionId(),
				deleteIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.delete(openSearchDeleteIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}