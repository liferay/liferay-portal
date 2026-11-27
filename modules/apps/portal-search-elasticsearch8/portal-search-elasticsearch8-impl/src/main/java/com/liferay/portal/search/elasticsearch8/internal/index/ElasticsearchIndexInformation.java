/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = IndexInformation.class)
public class ElasticsearchIndexInformation implements IndexInformation {

	@Override
	public String getCompanyIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId);
	}

	@Override
	public String getFieldMappings(String indexName) {
		ElasticsearchIndicesClient elasticsearchIndicesClient =
			_getElasticsearchIndicesClient();

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			GetIndexResponse getIndexResponse = elasticsearchIndicesClient.get(
				GetIndexRequest.of(
					getIndexRequest -> getIndexRequest.index(indexName)));

			Map<String, IndexState> indexStates = getIndexResponse.result();

			for (Map.Entry<String, IndexState> entry : indexStates.entrySet()) {
				IndexState indexState = entry.getValue();

				jsonObject.put(
					_getIndexName(entry.getKey(), indexState, indexName),
					JSONUtil.put(
						"mappings",
						_jsonFactory.createJSONObject(
							JsonpUtil.toString(indexState.mappings()))));
			}

			return jsonObject.toString(3);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public String[] getIndexNames() {
		ElasticsearchIndicesClient elasticsearchIndicesClient =
			_getElasticsearchIndicesClient();

		try {
			GetIndexResponse getIndexResponse = elasticsearchIndicesClient.get(
				GetIndexRequest.of(
					getIndexRequest -> getIndexRequest.index(StringPool.STAR)));

			Map<String, IndexState> indexStates = getIndexResponse.result();

			Set<String> indexNames = indexStates.keySet();

			return indexNames.toArray(new String[0]);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private ElasticsearchIndicesClient _getElasticsearchIndicesClient() {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(null, true);

		return elasticsearchClient.indices();
	}

	private String _getIndexName(
		String indexName, IndexState indexState, String requestIndexName) {

		Map<String, Alias> aliases = indexState.aliases();

		if (aliases.containsKey(requestIndexName)) {
			return requestIndexName;
		}

		return indexName;
	}

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

}