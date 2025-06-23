/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.ShardStatistics;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.RefreshRequest;
import org.opensearch.client.opensearch.indices.RefreshResponse;

/**
 * @author Michael C. Han
 */
public class RefreshIndexRequestExecutor {

	public RefreshIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public RefreshIndexResponse execute(
		RefreshIndexRequest refreshIndexRequest) {

		RefreshIndexResponse refreshIndexResponse = new RefreshIndexResponse();

		RefreshResponse refreshResponse = _getRefreshResponse(
			refreshIndexRequest, createRefreshRequest(refreshIndexRequest));

		ShardStatistics shardStatistics = refreshResponse.shards();

		ListUtil.isNotEmptyForEach(
			shardStatistics.failures(),
			shardFailure -> refreshIndexResponse.addIndexRequestShardFailure(
				IndexRequestShardFailureTranslatorUtil.translate(
					shardFailure)));

		refreshIndexResponse.setFailedShards(
			ConversionUtil.toInt(shardStatistics.failed()));
		refreshIndexResponse.setSuccessfulShards(
			ConversionUtil.toInt(shardStatistics.successful()));
		refreshIndexResponse.setTotalShards(
			ConversionUtil.toInt(shardStatistics.total()));

		return refreshIndexResponse;
	}

	protected RefreshRequest createRefreshRequest(
		RefreshIndexRequest refreshIndexRequest) {

		return RefreshRequest.of(
			refreshRequest -> refreshRequest.index(
				ListUtil.fromArray(refreshIndexRequest.getIndexNames())));
	}

	private RefreshResponse _getRefreshResponse(
		RefreshIndexRequest refreshIndexRequest,
		RefreshRequest refreshRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				refreshIndexRequest.getConnectionId(),
				refreshIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.refresh(refreshRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}