/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.FieldMapping;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.indices.GetFieldMappingRequest;
import org.opensearch.client.opensearch.indices.GetFieldMappingResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.get_field_mapping.TypeFieldMappings;

/**
 * @author Dylan Rebelak
 */
public class GetFieldMappingIndexRequestExecutor {

	public GetFieldMappingIndexRequestExecutor(
		JSONFactory jsonFactory,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_jsonFactory = jsonFactory;
		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public GetFieldMappingIndexResponse execute(
		GetFieldMappingIndexRequest getFieldMappingIndexRequest) {

		Map<String, String> fieldMappingsMap = new HashMap<>();

		GetFieldMappingResponse getFieldMappingResponse =
			_getGetFieldMappingsResponse(
				getFieldMappingIndexRequest,
				createGetFieldMappingRequest(getFieldMappingIndexRequest));

		Map<String, TypeFieldMappings> typeFieldMappingsMap =
			getFieldMappingResponse.result();

		for (Map.Entry<String, TypeFieldMappings> entry :
				typeFieldMappingsMap.entrySet()) {

			TypeFieldMappings typeFieldMappings = entry.getValue();

			fieldMappingsMap.put(
				entry.getKey(),
				_getFieldMappings(typeFieldMappings.mappings()));
		}

		return new GetFieldMappingIndexResponse(fieldMappingsMap);
	}

	protected GetFieldMappingRequest createGetFieldMappingRequest(
		GetFieldMappingIndexRequest getFieldMappingIndexRequest) {

		return GetFieldMappingRequest.of(
			getFieldMappingRequest -> getFieldMappingRequest.fields(
				ListUtil.fromArray(getFieldMappingIndexRequest.getFields())
			).index(
				ListUtil.fromArray(getFieldMappingIndexRequest.getIndexNames())
			));
	}

	private String _getFieldMappings(Map<String, FieldMapping> mappings) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Map.Entry<String, FieldMapping> entry1 : mappings.entrySet()) {
			FieldMapping fieldMapping = entry1.getValue();

			Map<String, Property> properties = fieldMapping.mapping();

			for (Map.Entry<String, Property> entry2 : properties.entrySet()) {
				Property property = entry2.getValue();

				try {
					jsonObject.put(
						entry1.getKey(),
						JSONUtil.put(
							entry2.getKey(),
							_jsonFactory.createJSONObject(
								JsonpUtil.toString(property))));
				}
				catch (JSONException jsonException) {
					throw new RuntimeException(jsonException);
				}
			}
		}

		return jsonObject.toString();
	}

	private GetFieldMappingResponse _getGetFieldMappingsResponse(
		GetFieldMappingIndexRequest getFieldMappingIndexRequest,
		GetFieldMappingRequest getFieldMappingRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				getFieldMappingIndexRequest.getConnectionId(),
				getFieldMappingIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.getFieldMapping(
				getFieldMappingRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final JSONFactory _jsonFactory;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}