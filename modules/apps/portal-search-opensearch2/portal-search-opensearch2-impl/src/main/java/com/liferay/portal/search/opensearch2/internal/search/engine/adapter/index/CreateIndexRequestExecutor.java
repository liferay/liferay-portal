/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 */
public class CreateIndexRequestExecutor {

	public CreateIndexRequestExecutor(
		JSONFactory jsonFactory,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_jsonFactory = jsonFactory;
		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public CreateIndexResponse execute(CreateIndexRequest createIndexRequest) {
		org.opensearch.client.opensearch.indices.CreateIndexResponse
			createIndexResponse = _getCreateIndexResponse(
				createIndexRequest,
				createCreateIndexRequest(createIndexRequest));

		JsonpUtil.logInfoResponse(createIndexResponse, _log);

		return new CreateIndexResponse(
			createIndexResponse.acknowledged(), createIndexResponse.index());
	}

	protected org.opensearch.client.opensearch.indices.CreateIndexRequest
		createCreateIndexRequest(CreateIndexRequest createIndexRequest) {

		org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder
			builder =
				new org.opensearch.client.opensearch.indices.CreateIndexRequest.
					Builder();

		builder.index(createIndexRequest.getIndexName());

		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
			createIndexRequest.getConnectionId());

		if (!Validator.isBlank(createIndexRequest.getMappings())) {
			_setMappings(
				builder, jsonpMapper, createIndexRequest.getMappings());
		}

		if (!Validator.isBlank(createIndexRequest.getSettings())) {
			_setSettings(
				builder, jsonpMapper, createIndexRequest.getSettings());
		}

		if (!Validator.isBlank(createIndexRequest.getSource())) {
			_setSource(builder, jsonpMapper, createIndexRequest.getSource());
		}

		return builder.build();
	}

	private org.opensearch.client.opensearch.indices.CreateIndexResponse
		_getCreateIndexResponse(
			CreateIndexRequest createIndexRequest,
			org.opensearch.client.opensearch.indices.CreateIndexRequest
				openSearchCreateIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				createIndexRequest.getConnectionId(),
				createIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.create(openSearchCreateIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setMappings(
		org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String mappings) {

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		try (InputStream inputStream = new ByteArrayInputStream(
				mappings.getBytes(StandardCharsets.UTF_8))) {

			builder.mappings(
				TypeMapping._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setSettings(
		org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String settings) {

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		try (InputStream inputStream = new ByteArrayInputStream(
				settings.getBytes(StandardCharsets.UTF_8))) {

			builder.settings(
				IndexSettings._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setSource(
		org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String source) {

		JSONObject jsonObject;

		try {
			jsonObject = _jsonFactory.createJSONObject(source);
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}

		JSONObject mappingsJSONObject = jsonObject.getJSONObject("mappings");

		if (mappingsJSONObject != null) {
			_setMappings(builder, jsonpMapper, mappingsJSONObject.toString());
		}

		JSONObject settingsJSONObject = jsonObject.getJSONObject("settings");

		if (settingsJSONObject != null) {
			_setSettings(builder, jsonpMapper, settingsJSONObject.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreateIndexRequestExecutor.class);

	private final JSONFactory _jsonFactory;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}