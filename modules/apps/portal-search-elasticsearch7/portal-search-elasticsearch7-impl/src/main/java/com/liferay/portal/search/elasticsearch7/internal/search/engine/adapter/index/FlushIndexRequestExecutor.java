/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.FlushIndexRequest;
import com.liferay.portal.search.engine.adapter.index.FlushIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndexRequestShardFailure;

import java.io.IOException;

import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

/**
 * @author Michael C. Han
 */
public class FlushIndexRequestExecutor {

	public FlushIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public FlushIndexResponse execute(FlushIndexRequest flushIndexRequest) {
		FlushRequest flushRequest = createFlushRequest(flushIndexRequest);

		FlushResponse flushResponse = _getFlushResponse(
			flushRequest, flushIndexRequest);

		FlushIndexResponse flushIndexResponse = new FlushIndexResponse();

		flushIndexResponse.setFailedShards(flushResponse.getFailedShards());
		flushIndexResponse.setSuccessfulShards(
			flushResponse.getSuccessfulShards());
		flushIndexResponse.setTotalShards(flushResponse.getTotalShards());

		RestStatus restStatus = flushResponse.getStatus();

		flushIndexResponse.setRestStatus(restStatus.getStatus());

		ShardOperationFailedException[] shardOperationFailedExceptions =
			flushResponse.getShardFailures();

		if (ArrayUtil.isNotEmpty(shardOperationFailedExceptions)) {
			for (ShardOperationFailedException shardOperationFailedException :
					shardOperationFailedExceptions) {

				IndexRequestShardFailure indexRequestShardFailure =
					IndexRequestShardFailureTranslatorUtil.translate(
						shardOperationFailedException);

				flushIndexResponse.addIndexRequestShardFailure(
					indexRequestShardFailure);
			}
		}

		return flushIndexResponse;
	}

	protected FlushRequest createFlushRequest(
		FlushIndexRequest flushIndexRequest) {

		FlushRequest flushRequest = new FlushRequest();

		flushRequest.force(flushIndexRequest.isForce());
		flushRequest.indices(flushIndexRequest.getIndexNames());
		flushRequest.waitIfOngoing(flushIndexRequest.isWaitIfOngoing());

		return flushRequest;
	}

	private FlushResponse _getFlushResponse(
		FlushRequest flushRequest, FlushIndexRequest flushIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				flushIndexRequest.getConnectionId(),
				flushIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.flush(flushRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}