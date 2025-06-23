/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.core.TimeValue;

/**
 * @author Dylan Rebelak
 */
public class HealthClusterRequestExecutor {

	public HealthClusterRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public HealthClusterResponse execute(
		HealthClusterRequest healthClusterRequest) {

		ClusterHealthRequest clusterHealthRequest = createClusterHealthRequest(
			healthClusterRequest);

		ClusterHealthResponse clusterHealthResponse = _getClusterHealthResponse(
			clusterHealthRequest, healthClusterRequest);

		ClusterHealthStatus clusterHealthStatus =
			clusterHealthResponse.getStatus();

		return new HealthClusterResponse(
			ClusterHealthStatusTranslatorUtil.translate(clusterHealthStatus),
			clusterHealthResponse.toString());
	}

	protected ClusterHealthRequest createClusterHealthRequest(
		HealthClusterRequest healthClusterRequest) {

		ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest();

		if (ArrayUtil.isNotEmpty(healthClusterRequest.getIndexNames())) {
			clusterHealthRequest.indices(healthClusterRequest.getIndexNames());
		}

		long timeout = healthClusterRequest.getTimeout();

		if (timeout > 0) {
			clusterHealthRequest.masterNodeTimeout(
				TimeValue.timeValueMillis(timeout));
			clusterHealthRequest.timeout(TimeValue.timeValueMillis(timeout));
		}

		if (healthClusterRequest.getWaitForClusterHealthStatus() != null) {
			clusterHealthRequest.waitForStatus(
				ClusterHealthStatusTranslatorUtil.translate(
					healthClusterRequest.getWaitForClusterHealthStatus()));
		}

		return clusterHealthRequest;
	}

	private ClusterHealthResponse _getClusterHealthResponse(
		ClusterHealthRequest clusterHealthRequest,
		HealthClusterRequest healthClusterRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				healthClusterRequest.getConnectionId(),
				healthClusterRequest.isPreferLocalCluster());

		ClusterClient clusterClient = restHighLevelClient.cluster();

		try {
			return clusterClient.health(
				clusterHealthRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}