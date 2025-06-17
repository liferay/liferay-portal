/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.index.StatsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.StatsIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.StoreStats;
import org.opensearch.client.opensearch.indices.IndicesStatsRequest;
import org.opensearch.client.opensearch.indices.IndicesStatsResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.stats.IndexStats;
import org.opensearch.client.opensearch.indices.stats.IndicesStats;

/**
 * @author Felipe Lorenz
 */
public class StatsIndexRequestExecutor {

	public StatsIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public StatsIndexResponse execute(StatsIndexRequest statsIndexRequest) {
		try {
			IndicesStatsResponse indicesStatsResponse =
				_getIndicesStatsResponse(
					_createIndicesStatsRequest(statsIndexRequest),
					statsIndexRequest);

			Map<String, Long> indexSizes = new HashMap<>();

			Map<String, IndicesStats> indicesStatsMap =
				indicesStatsResponse.indices();

			long sizeOfLargestIndex = 0;

			for (Map.Entry<String, IndicesStats> entry :
					indicesStatsMap.entrySet()) {

				IndicesStats indicesStats = entry.getValue();

				IndexStats indexStats = indicesStats.total();

				StoreStats storeStats = indexStats.store();

				long indexSize = storeStats.sizeInBytes();

				if (indexSize > sizeOfLargestIndex) {
					sizeOfLargestIndex = indexSize;
				}

				indexSizes.put(entry.getKey(), indexSize);
			}

			return new StatsIndexResponse(indexSizes, sizeOfLargestIndex);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private IndicesStatsRequest _createIndicesStatsRequest(
		StatsIndexRequest statsIndexRequest) {

		IndicesStatsRequest.Builder builder = new IndicesStatsRequest.Builder();

		if (ArrayUtil.isNotEmpty(statsIndexRequest.getIndexNames())) {
			builder.index(Arrays.asList(statsIndexRequest.getIndexNames()));
		}
		else {
			builder.index("_all");
		}

		return builder.build();
	}

	private IndicesStatsResponse _getIndicesStatsResponse(
		IndicesStatsRequest indicesStatsRequest,
		StatsIndexRequest statsIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				statsIndexRequest.getConnectionId(),
				statsIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.stats(indicesStatsRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}