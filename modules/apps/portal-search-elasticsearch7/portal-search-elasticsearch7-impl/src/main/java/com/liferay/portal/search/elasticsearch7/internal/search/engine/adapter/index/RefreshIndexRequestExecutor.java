/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndexRequestShardFailure;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Michael C. Han
 */
public class RefreshIndexRequestExecutor {

	public RefreshIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public RefreshIndexResponse execute(
		RefreshIndexRequest refreshIndexRequest) {

		RefreshRequest refreshRequest = createRefreshRequest(
			refreshIndexRequest);

		RefreshResponse refreshResponse = _getRefreshResponse(
			refreshRequest, refreshIndexRequest);

		RefreshIndexResponse refreshIndexResponse = new RefreshIndexResponse();

		refreshIndexResponse.setFailedShards(refreshResponse.getFailedShards());
		refreshIndexResponse.setSuccessfulShards(
			refreshResponse.getSuccessfulShards());
		refreshIndexResponse.setTotalShards(refreshResponse.getTotalShards());

		ShardOperationFailedException[] shardOperationFailedExceptions =
			refreshResponse.getShardFailures();

		if (ArrayUtil.isNotEmpty(shardOperationFailedExceptions)) {
			for (ShardOperationFailedException shardOperationFailedException :
					shardOperationFailedExceptions) {

				IndexRequestShardFailure indexRequestShardFailure =
					IndexRequestShardFailureTranslatorUtil.translate(
						shardOperationFailedException);

				refreshIndexResponse.addIndexRequestShardFailure(
					indexRequestShardFailure);
			}
		}

		return refreshIndexResponse;
	}

	protected RefreshRequest createRefreshRequest(
		RefreshIndexRequest refreshIndexRequest) {

		return new RefreshRequest(refreshIndexRequest.getIndexNames());
	}

	private RefreshResponse _getRefreshResponse(
		RefreshRequest refreshRequest,
		RefreshIndexRequest refreshIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				refreshIndexRequest.getConnectionId(),
				refreshIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.refresh(
				refreshRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}