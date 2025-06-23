/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class CloseIndexRequestExecutor {

	public CloseIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public CloseIndexResponse execute(CloseIndexRequest closeIndexRequest) {
		org.opensearch.client.opensearch.indices.CloseIndexResponse
			closeIndexResponse = getCloseIndexResponse(
				createCloseIndexRequest(closeIndexRequest), closeIndexRequest);

		return new CloseIndexResponse(closeIndexResponse.acknowledged());
	}

	protected org.opensearch.client.opensearch.indices.CloseIndexRequest
		createCloseIndexRequest(CloseIndexRequest closeIndexRequest) {

		org.opensearch.client.opensearch.indices.CloseIndexRequest.Builder
			builder =
				new org.opensearch.client.opensearch.indices.CloseIndexRequest.
					Builder();

		IndicesOptions indicesOptions = closeIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			builder.allowNoIndices(indicesOptions.isAllowNoIndices());
			builder.ignoreUnavailable(indicesOptions.isIgnoreUnavailable());
		}

		builder.index(ListUtil.fromArray(closeIndexRequest.getIndexNames()));

		if (closeIndexRequest.getTimeout() > 0) {
			Time time = Time.of(
				openSearchTime -> openSearchTime.time(
					closeIndexRequest.getTimeout() +
						TimeUnit.Milliseconds.jsonValue()));

			builder.masterTimeout(time);
			builder.timeout(time);
		}

		return builder.build();
	}

	protected org.opensearch.client.opensearch.indices.CloseIndexResponse
		getCloseIndexResponse(
			org.opensearch.client.opensearch.indices.CloseIndexRequest
				openSearchCloseIndexRequest,
			CloseIndexRequest closeIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				closeIndexRequest.getConnectionId(),
				closeIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.close(openSearchCloseIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}