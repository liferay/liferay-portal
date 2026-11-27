/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.Alias;
import org.opensearch.client.opensearch.indices.GetIndexRequest;
import org.opensearch.client.opensearch.indices.GetIndexResponse;
import org.opensearch.client.opensearch.indices.IndexState;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunen
 */
@Component(service = IndexInformation.class)
public class OpenSearchIndexInformation implements IndexInformation {

	@Override
	public String getCompanyIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId);
	}

	@Override
	public String getFieldMappings(String indexName) {
		OpenSearchIndicesClient openSearchIndicesClient =
			_getOpenSearchIndicesClient();

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			GetIndexResponse getIndexResponse = openSearchIndicesClient.get(
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
		OpenSearchIndicesClient openSearchIndicesClient =
			_getOpenSearchIndicesClient();

		try {
			GetIndexResponse getIndexResponse = openSearchIndicesClient.get(
				GetIndexRequest.of(
					getIndexRequest -> getIndexRequest.index("*")));

			Map<String, IndexState> indexStates = getIndexResponse.result();

			Set<String> indexNames = indexStates.keySet();

			return indexNames.toArray(new String[0]);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getIndexName(
		String indexName, IndexState indexState, String requestIndexName) {

		Map<String, Alias> aliases = indexState.aliases();

		if (aliases.containsKey(requestIndexName)) {
			return requestIndexName;
		}

		return indexName;
	}

	private OpenSearchIndicesClient _getOpenSearchIndicesClient() {
		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(null, true);

		return openSearchClient.indices();
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

}