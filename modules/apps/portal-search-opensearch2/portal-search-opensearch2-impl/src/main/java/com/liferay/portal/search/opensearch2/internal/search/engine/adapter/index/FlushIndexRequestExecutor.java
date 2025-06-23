/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.FlushIndexRequest;
import com.liferay.portal.search.engine.adapter.index.FlushIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.ShardStatistics;
import org.opensearch.client.opensearch.indices.FlushRequest;
import org.opensearch.client.opensearch.indices.FlushResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class FlushIndexRequestExecutor {

	public FlushIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public FlushIndexResponse execute(FlushIndexRequest flushIndexRequest) {
		FlushResponse flushResponse = _getFlushResponse(
			flushIndexRequest, createFlushRequest(flushIndexRequest));

		FlushIndexResponse flushIndexResponse = new FlushIndexResponse();

		ShardStatistics shardStatistics = flushResponse.shards();

		ListUtil.isNotEmptyForEach(
			shardStatistics.failures(),
			shardFailure -> flushIndexResponse.addIndexRequestShardFailure(
				IndexRequestShardFailureTranslatorUtil.translate(
					shardFailure)));

		flushIndexResponse.setFailedShards(
			ConversionUtil.toInt(shardStatistics.failed()));
		flushIndexResponse.setSuccessfulShards(
			ConversionUtil.toInt(shardStatistics.successful()));
		flushIndexResponse.setTotalShards(
			ConversionUtil.toInt(shardStatistics.total()));

		return flushIndexResponse;
	}

	protected FlushRequest createFlushRequest(
		FlushIndexRequest flushIndexRequest) {

		return FlushRequest.of(
			flushRequest -> flushRequest.force(
				flushIndexRequest.isForce()
			).index(
				ListUtil.fromArray(flushIndexRequest.getIndexNames())
			).waitIfOngoing(
				flushIndexRequest.isWaitIfOngoing()
			));
	}

	private FlushResponse _getFlushResponse(
		FlushIndexRequest flushIndexRequest, FlushRequest flushRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				flushIndexRequest.getConnectionId(),
				flushIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.flush(flushRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}