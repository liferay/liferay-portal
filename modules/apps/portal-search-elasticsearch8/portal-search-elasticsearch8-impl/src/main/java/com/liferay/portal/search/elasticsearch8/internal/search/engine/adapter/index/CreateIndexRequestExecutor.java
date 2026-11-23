/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonpMapper;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;

import jakarta.json.spi.JsonProvider;

import java.io.IOException;

/**
 * @author Michael C. Han
 */
public class CreateIndexRequestExecutor {

	public CreateIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public CreateIndexResponse execute(CreateIndexRequest createIndexRequest) {
		co.elastic.clients.elasticsearch.indices.CreateIndexResponse
			createIndexResponse = _getCreateIndexResponse(
				createIndexRequest,
				createCreateIndexRequest(createIndexRequest));

		JsonpUtil.logInfoResponse(createIndexResponse, _log);

		return new CreateIndexResponse(
			createIndexResponse.acknowledged(), createIndexResponse.index());
	}

	protected co.elastic.clients.elasticsearch.indices.CreateIndexRequest
		createCreateIndexRequest(CreateIndexRequest createIndexRequest) {

		co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder
			builder =
				new co.elastic.clients.elasticsearch.indices.CreateIndexRequest.
					Builder();

		builder.index(createIndexRequest.getIndexName());

		JsonpMapper jsonpMapper = _elasticsearchClientResolver.getJsonpMapper(
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

	private co.elastic.clients.elasticsearch.indices.CreateIndexResponse
		_getCreateIndexResponse(
			CreateIndexRequest createIndexRequest,
			co.elastic.clients.elasticsearch.indices.CreateIndexRequest
				elasticsearchCreateIndexRequest) {

		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(
				createIndexRequest.getConnectionId(),
				createIndexRequest.isPreferLocalCluster());

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		try {
			return elasticsearchIndicesClient.create(
				elasticsearchCreateIndexRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setMappings(
		co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String mappings) {

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		builder.mappings(
			TypeMapping._DESERIALIZER.deserialize(
				jsonProvider.createParser(new UnsyncStringReader(mappings)),
				jsonpMapper));
	}

	private void _setSettings(
		co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String settings) {

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		builder.settings(
			IndexSettings._DESERIALIZER.deserialize(
				jsonProvider.createParser(new UnsyncStringReader(settings)),
				jsonpMapper));
	}

	private void _setSource(
		co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder
			builder,
		JsonpMapper jsonpMapper, String source) {

		JSONObject jsonObject;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(source);
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

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}