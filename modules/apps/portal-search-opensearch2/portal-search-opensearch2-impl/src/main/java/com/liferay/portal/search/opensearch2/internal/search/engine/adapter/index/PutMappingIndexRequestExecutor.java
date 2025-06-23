/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.IndexUtil;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.DynamicTemplate;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.PutMappingRequest;
import org.opensearch.client.opensearch.indices.PutMappingResponse;

/**
 * @author Dylan Rebelak
 */
public class PutMappingIndexRequestExecutor {

	public PutMappingIndexRequestExecutor(
		JSONFactory jsonFactory,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_jsonFactory = jsonFactory;
		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public PutMappingIndexResponse execute(
		PutMappingIndexRequest putMappingIndexRequest) {

		PutMappingResponse putMappingResponse = getPutMappingResponse(
			putMappingIndexRequest,
			createPutMappingRequest(putMappingIndexRequest));

		return new PutMappingIndexResponse(putMappingResponse.acknowledged());
	}

	protected PutMappingRequest createPutMappingRequest(
		PutMappingIndexRequest putMappingIndexRequest) {

		PutMappingRequest.Builder builder = new PutMappingRequest.Builder();

		try {
			JSONObject mappingJSONObject = _jsonFactory.createJSONObject(
				putMappingIndexRequest.getMapping());

			List<Map<String, DynamicTemplate>> dynamicTemplates =
				IndexUtil.getDynamicTemplatesMap(mappingJSONObject);

			if (dynamicTemplates != null) {
				builder.dynamicTemplates(dynamicTemplates);
			}

			builder.index(
				ListUtil.fromArray(putMappingIndexRequest.getIndexNames()));

			Map<String, Property> properties = IndexUtil.getPropertiesMap(
				mappingJSONObject);

			if (properties != null) {
				builder.properties(properties);
			}

			return builder.build();
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}
	}

	protected PutMappingResponse getPutMappingResponse(
		PutMappingIndexRequest putMappingIndexRequest,
		PutMappingRequest putMappingRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				putMappingIndexRequest.getConnectionId(),
				putMappingIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.putMapping(putMappingRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final JSONFactory _jsonFactory;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}