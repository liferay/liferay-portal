/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class GetIndexIndexRequestExecutor {

	public GetIndexIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
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

		_translateResponse(
			getIndexIndexResponse, indexStates,
			getIndexIndexRequest.getIndexNames());

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

		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(
				getIndexIndexRequest.getConnectionId(),
				getIndexIndexRequest.isPreferLocalCluster());

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		try {
			return elasticsearchIndicesClient.get(getIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getIndexName(
		String indexName, IndexState indexState, String[] requestIndexNames) {

		Map<String, Alias> aliases = indexState.aliases();

		for (String requestIndexName : requestIndexNames) {
			if (aliases.containsKey(requestIndexName)) {
				return requestIndexName;
			}
		}

		return indexName;
	}

	private void _translateResponse(
		GetIndexIndexResponse getIndexIndexResponse,
		Map<String, IndexState> indexStates, String[] requestIndexNames) {

		Map<String, String> settings = new HashMap<>();

		Map<String, String> indexMappings = new HashMap<>();

		for (Map.Entry<String, IndexState> entry : indexStates.entrySet()) {
			IndexState indexState = entry.getValue();

			String indexName = _getIndexName(
				entry.getKey(), indexState, requestIndexNames);

			indexMappings.put(
				indexName, JsonpUtil.toString(indexState.mappings()));
			settings.put(indexName, JsonpUtil.toString(indexState.settings()));
		}

		getIndexIndexResponse.setIndexMappings(indexMappings);

		getIndexIndexResponse.setSettings(settings);
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}