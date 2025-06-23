/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterResponse;

import java.io.IOException;

import org.apache.http.util.EntityUtils;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Dylan Rebelak
 */
public class StateClusterRequestExecutor {

	public StateClusterRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public StateClusterResponse execute(
		StateClusterRequest stateClusterRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				stateClusterRequest.getConnectionId(),
				stateClusterRequest.isPreferLocalCluster());

		RestClient restClient = restHighLevelClient.getLowLevelClient();

		String endpoint = "/_cluster/state";

		Request request = new Request("GET", endpoint);

		try {
			Response response = restClient.performRequest(request);

			String responseBody = EntityUtils.toString(response.getEntity());

			return new StateClusterResponse(responseBody);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}