/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.Arrays;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch.cluster.HealthRequest;
import org.opensearch.client.opensearch.cluster.HealthResponse;
import org.opensearch.client.opensearch.cluster.OpenSearchClusterClient;

/**
 * @author Dylan Rebelak
 */
public class HealthClusterRequestExecutor {

	public HealthClusterRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public HealthClusterResponse execute(
		HealthClusterRequest healthClusterRequest) {

		HealthResponse healthResponse = _getHealthResponse(
			healthClusterRequest, createHealthRequest(healthClusterRequest));

		return new HealthClusterResponse(
			ClusterHealthStatusTranslatorUtil.translate(
				healthResponse.status()),
			JsonpUtil.toString(healthResponse));
	}

	protected HealthRequest createHealthRequest(
		HealthClusterRequest healthClusterRequest) {

		HealthRequest.Builder builder = new HealthRequest.Builder();

		if (ArrayUtil.isNotEmpty(healthClusterRequest.getIndexNames())) {
			builder.index(Arrays.asList(healthClusterRequest.getIndexNames()));
		}

		if (healthClusterRequest.getTimeout() > 0) {
			Time time = Time.of(
				openSearchTime -> openSearchTime.time(
					healthClusterRequest.getTimeout() +
						TimeUnit.Milliseconds.jsonValue()));

			builder.masterTimeout(time);
			builder.timeout(time);
		}

		if (healthClusterRequest.getWaitForClusterHealthStatus() != null) {
			builder.waitForStatus(
				ClusterHealthStatusTranslatorUtil.translate(
					healthClusterRequest.getWaitForClusterHealthStatus()));
		}

		return builder.build();
	}

	private HealthResponse _getHealthResponse(
		HealthClusterRequest healthClusterRequest,
		HealthRequest healthRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				healthClusterRequest.getConnectionId(),
				healthClusterRequest.isPreferLocalCluster());

		OpenSearchClusterClient openSearchClusterClient =
			openSearchClient.cluster();

		try {
			return openSearchClusterClient.health(healthRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}