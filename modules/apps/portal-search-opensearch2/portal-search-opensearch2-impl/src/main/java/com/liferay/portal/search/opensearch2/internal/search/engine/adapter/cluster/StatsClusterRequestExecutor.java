/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.Arrays;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cluster.ClusterStatsRequest;
import org.opensearch.client.opensearch.cluster.ClusterStatsResponse;
import org.opensearch.client.opensearch.cluster.OpenSearchClusterClient;
import org.opensearch.client.opensearch.cluster.stats.ClusterFileSystem;
import org.opensearch.client.opensearch.cluster.stats.ClusterNodes;

/**
 * @author Dylan Rebelak
 */
public class StatsClusterRequestExecutor {

	public StatsClusterRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public StatsClusterResponse execute(
		StatsClusterRequest statsClusterRequest) {

		try {
			ClusterStatsResponse clusterStatsResponse =
				_getClusterStatsResponse(
					_createClusterStatsRequest(statsClusterRequest),
					statsClusterRequest);

			ClusterNodes clusterNodes = clusterStatsResponse.nodes();

			ClusterFileSystem clusterFileSystem = clusterNodes.fs();

			long availableInBytes = clusterFileSystem.availableInBytes();
			long totalInBytes = clusterFileSystem.totalInBytes();

			return new StatsClusterResponse(
				availableInBytes,
				ClusterHealthStatusTranslatorUtil.translate(
					clusterStatsResponse.status()),
				JsonpUtil.toString(clusterStatsResponse),
				totalInBytes - availableInBytes);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private ClusterStatsRequest _createClusterStatsRequest(
		StatsClusterRequest statsClusterRequest) {

		ClusterStatsRequest.Builder builder = new ClusterStatsRequest.Builder();

		if (ArrayUtil.isNotEmpty(statsClusterRequest.getNodeIds())) {
			builder.nodeId(Arrays.asList(statsClusterRequest.getNodeIds()));
		}

		return builder.build();
	}

	private ClusterStatsResponse _getClusterStatsResponse(
		ClusterStatsRequest clusterStatsRequest,
		StatsClusterRequest statsClusterRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				statsClusterRequest.getConnectionId(),
				statsClusterRequest.isPreferLocalCluster());

		OpenSearchClusterClient openSearchClusterClient =
			openSearchClient.cluster();

		try {
			return openSearchClusterClient.stats(clusterStatsRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}