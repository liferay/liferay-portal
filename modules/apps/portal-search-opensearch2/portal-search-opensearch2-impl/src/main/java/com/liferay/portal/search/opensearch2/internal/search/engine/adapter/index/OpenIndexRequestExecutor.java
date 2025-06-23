/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch._types.WaitForActiveShards;
import org.opensearch.client.opensearch.indices.OpenRequest;
import org.opensearch.client.opensearch.indices.OpenResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class OpenIndexRequestExecutor {

	public OpenIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public OpenIndexResponse execute(OpenIndexRequest openIndexRequest) {
		OpenResponse openResponse = _getOpenResponse(
			openIndexRequest, createOpenRequest(openIndexRequest));

		return new OpenIndexResponse(openResponse.acknowledged());
	}

	protected OpenRequest createOpenRequest(OpenIndexRequest openIndexRequest) {
		OpenRequest.Builder builder = new OpenRequest.Builder();

		IndicesOptions indicesOptions = openIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			builder.allowNoIndices(indicesOptions.isAllowNoIndices());
			builder.ignoreUnavailable(indicesOptions.isIgnoreUnavailable());
		}

		builder.index(ListUtil.fromArray(openIndexRequest.getIndexNames()));

		if (openIndexRequest.getTimeout() > 0) {
			Time time = Time.of(
				openSearchTime -> openSearchTime.time(
					openIndexRequest.getTimeout() +
						TimeUnit.Milliseconds.jsonValue()));

			builder.masterTimeout(time);
			builder.timeout(time);
		}

		if (openIndexRequest.getWaitForActiveShards() > 0) {
			builder.waitForActiveShards(
				WaitForActiveShards.of(
					waitForActiveShards -> waitForActiveShards.count(
						openIndexRequest.getWaitForActiveShards())));
		}

		return builder.build();
	}

	private OpenResponse _getOpenResponse(
		OpenIndexRequest openIndexRequest, OpenRequest openRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				openIndexRequest.getConnectionId(),
				openIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.open(openRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}