/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.GetIndexRequest;
import org.opensearch.client.opensearch.indices.GetIndexResponse;
import org.opensearch.client.opensearch.indices.IndexState;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class GetIndexIndexRequestExecutor {

	public GetIndexIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public GetIndexIndexResponse execute(
		GetIndexIndexRequest getIndexIndexRequest) {

		GetIndexIndexResponse getIndexIndexResponse =
			new GetIndexIndexResponse();

		GetIndexResponse getIndexResponse = _getGetIndexResponse(
			getIndexIndexRequest, createGetIndexRequest(getIndexIndexRequest));

		Map<String, IndexState> indexStates = getIndexResponse.result();

		getIndexIndexResponse.setIndexNames(
			ArrayUtil.toStringArray(indexStates.keySet()));

		_translateResponse(getIndexIndexResponse, indexStates);

		return getIndexIndexResponse;
	}

	protected GetIndexRequest createGetIndexRequest(
		GetIndexIndexRequest getIndexIndexRequest) {

		return GetIndexRequest.of(
			getIndexRequest -> getIndexRequest.index(
				ListUtil.fromArray(getIndexIndexRequest.getIndexNames())));
	}

	private GetIndexResponse _getGetIndexResponse(
		GetIndexIndexRequest getIndexIndexRequest,
		GetIndexRequest getIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				getIndexIndexRequest.getConnectionId(),
				getIndexIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.get(getIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _translateResponse(
		GetIndexIndexResponse getIndexIndexResponse,
		Map<String, IndexState> indexStates) {

		Map<String, String> settings = new HashMap<>();

		Map<String, String> indexMappings = new HashMap<>();

		for (Map.Entry<String, IndexState> entry : indexStates.entrySet()) {
			IndexState indexState = entry.getValue();

			indexMappings.put(
				entry.getKey(), JsonpUtil.toString(indexState.mappings()));
			settings.put(
				entry.getKey(), JsonpUtil.toString(indexState.settings()));
		}

		getIndexIndexResponse.setIndexMappings(indexMappings);

		getIndexIndexResponse.setSettings(settings);
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}