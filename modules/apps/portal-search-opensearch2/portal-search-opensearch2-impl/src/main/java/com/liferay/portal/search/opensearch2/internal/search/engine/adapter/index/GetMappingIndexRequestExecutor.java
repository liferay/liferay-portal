/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.GetMappingRequest;
import org.opensearch.client.opensearch.indices.GetMappingResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.get_mapping.IndexMappingRecord;

/**
 * @author Dylan Rebelak
 */
public class GetMappingIndexRequestExecutor {

	public GetMappingIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public GetMappingIndexResponse execute(
		GetMappingIndexRequest getMappingIndexRequest) {

		GetMappingResponse getMappingResponse = _getGetMappingResponse(
			getMappingIndexRequest,
			createGetMappingRequest(getMappingIndexRequest));

		Map<String, IndexMappingRecord> indexMappingRecords =
			getMappingResponse.result();

		Map<String, String> indexMappings = new HashMap<>();

		for (String indexName : getMappingIndexRequest.getIndexNames()) {
			IndexMappingRecord indexMappingRecord = indexMappingRecords.get(
				indexName);

			indexMappings.put(
				indexName, JsonpUtil.toString(indexMappingRecord.mappings()));
		}

		return new GetMappingIndexResponse(indexMappings);
	}

	protected GetMappingRequest createGetMappingRequest(
		GetMappingIndexRequest getMappingIndexRequest) {

		return GetMappingRequest.of(
			getMappingRequest -> getMappingRequest.index(
				ListUtil.fromArray(getMappingIndexRequest.getIndexNames())));
	}

	private GetMappingResponse _getGetMappingResponse(
		GetMappingIndexRequest getMappingIndexRequest,
		GetMappingRequest getMappingRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				getMappingIndexRequest.getConnectionId(),
				getMappingIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.getMapping(getMappingRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}