/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.index;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.json.JsonpMapper;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch8.internal.index.constants.IndexMappingsConstants;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.ResourceUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class MappingsHelper {

	public MappingsHelper(
		ElasticsearchIndicesClient elasticsearchIndicesClient, String indexName,
		JSONFactory jsonFactory, JsonpMapper jsonpMapper,
		String overrideMappings,
		SearchEngineInformation searchEngineInformation) {

		_elasticsearchIndicesClient = elasticsearchIndicesClient;
		_indexName = indexName;
		_jsonFactory = jsonFactory;
		_jsonpMapper = jsonpMapper;
		_overrideMappings = overrideMappings;
		_searchEngineInformation = searchEngineInformation;
	}

	public void putDefaultOrOverrideMappings() {
		_putMappings(_getDefaultOrOverrideMappingsJSONObject());
	}

	public void putMappings(String source) {
		if (Validator.isNotNull(_overrideMappings)) {
			return;
		}

		_putMappings(
			_getMappingsJSONObjectWithMergedDynamicTemplates(
				_getCurrentMappings(_indexName), source));
	}

	public void setDefaultOrOverrideMappings(
		CreateIndexRequest.Builder builder) {

		String mappings = String.valueOf(
			_getDefaultOrOverrideMappingsJSONObject());

		try (InputStream inputStream = new ByteArrayInputStream(
				mappings.getBytes(StandardCharsets.UTF_8))) {

			JsonProvider jsonProvider = _jsonpMapper.jsonProvider();

			builder.mappings(
				TypeMapping._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), _jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _addTextEmbeddingDynamicTemplates(String mappings) {
		JSONObject jsonObject = _createJSONObject(mappings);

		JSONArray jsonArray = jsonObject.getJSONArray("dynamic_templates");

		for (int dimension :
				_searchEngineInformation.getEmbeddingVectorDimensions()) {

			jsonArray.put(
				JSONUtil.put(
					"template_text_embedding_" + dimension,
					JSONUtil.put(
						"mapping",
						JSONUtil.put(
							"dims", dimension
						).put(
							"type", "dense_vector"
						)
					).put(
						"path_match",
						StringBundler.concat(
							"text_embedding_", dimension, StringPool.STAR)
					)));
		}

		return jsonObject.toString();
	}

	private JSONObject _createJSONObject(String mappings) {
		try {
			return _jsonFactory.createJSONObject(mappings);
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}
	}

	private String _getCurrentMappings(String indexName) {
		if (Validator.isNotNull(_overrideMappings)) {
			return StringPool.BLANK;
		}

		try {
			GetMappingResponse getMappingResponse =
				_elasticsearchIndicesClient.getMapping(
					GetMappingRequest.of(
						getMappingRequest -> getMappingRequest.index(
							indexName)));

			Map<String, IndexMappingRecord> indexMappingRecords =
				getMappingResponse.result();

			IndexMappingRecord indexMappingRecord = indexMappingRecords.get(
				indexName);

			return JsonpUtil.toString(indexMappingRecord.mappings());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private JSONObject _getDefaultOrOverrideMappingsJSONObject() {
		if (Validator.isNotNull(_overrideMappings)) {
			return _removeLegacyDocumentType(_overrideMappings);
		}

		String defaultMappings = _addTextEmbeddingDynamicTemplates(
			ResourceUtil.getResourceAsString(
				getClass(), IndexMappingsConstants.INDEX_MAPPINGS_FILE_NAME));

		return _getMappingsJSONObjectWithMergedDynamicTemplates(
			StringPool.BLANK, defaultMappings);
	}

	private JSONObject _getMappingsJSONObjectWithMergedDynamicTemplates(
		String currentMappings, String putMappings) {

		JSONObject currentMappingsJSONObject = _removeLegacyDocumentType(
			currentMappings);
		JSONObject putMappingsJSONObject = _removeLegacyDocumentType(
			putMappings);

		putMappingsJSONObject.put(
			"dynamic_templates",
			_mergeDynamicTemplates(
				currentMappingsJSONObject.getJSONArray("dynamic_templates"),
				putMappingsJSONObject.getJSONArray("dynamic_templates")));

		return putMappingsJSONObject;
	}

	private JSONArray _mergeDynamicTemplates(
		JSONArray currentDynamicTemplatesJSONArray,
		JSONArray putDynamicTemplatesJSONArray) {

		if (putDynamicTemplatesJSONArray == null) {
			return currentDynamicTemplatesJSONArray;
		}

		LinkedHashMap<String, JSONObject> linkedHashMap = new LinkedHashMap<>();

		_putAll(linkedHashMap, putDynamicTemplatesJSONArray);

		_putAll(linkedHashMap, currentDynamicTemplatesJSONArray);

		JSONArray mergedDynamicTemplatesJSONArray =
			_jsonFactory.createJSONArray();

		JSONObject defaultTemplateJSONObject = null;

		for (Map.Entry<String, JSONObject> entry : linkedHashMap.entrySet()) {
			String key = entry.getKey();

			if (key.equals("template_")) {
				defaultTemplateJSONObject = entry.getValue();
			}
			else {
				mergedDynamicTemplatesJSONArray.put(entry.getValue());
			}
		}

		if (defaultTemplateJSONObject != null) {
			mergedDynamicTemplatesJSONArray.put(defaultTemplateJSONObject);
		}

		return mergedDynamicTemplatesJSONArray;
	}

	private void _putAll(Map<String, JSONObject> map, JSONArray jsonArray) {
		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray namesJSONArray = jsonObject.names();

			String name = (String)namesJSONArray.get(0);

			map.put(name, jsonObject);
		}
	}

	private void _putMappings(JSONObject mappingsJSONObject) {
		String mappings = String.valueOf(mappingsJSONObject);

		try (InputStream inputStream = new ByteArrayInputStream(
				mappings.getBytes(StandardCharsets.UTF_8))) {

			JsonProvider jsonProvider = _jsonpMapper.jsonProvider();

			PutMappingRequest.Builder builder = new PutMappingRequest.Builder(
			).index(
				_indexName
			).withJson(
				jsonProvider.createParser(inputStream), _jsonpMapper
			);

			PutMappingResponse putMappingResponse =
				_elasticsearchIndicesClient.putMapping(builder.build());

			JsonpUtil.logInfoResponse(putMappingResponse, _log);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"The attempted mappings update for index ", _indexName,
						" is not compatible with its current mappings. Please ",
						"recreate the index or modify the attempted updates."),
					exception);
			}
		}
	}

	private JSONObject _removeLegacyDocumentType(String json) {
		JSONObject jsonObject = _createJSONObject(json);

		if (jsonObject.has(
				IndexMappingsConstants.LEGACY_LIFERAY_DOCUMENT_TYPE)) {

			return jsonObject.getJSONObject(
				IndexMappingsConstants.LEGACY_LIFERAY_DOCUMENT_TYPE);
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(MappingsHelper.class);

	private final ElasticsearchIndicesClient _elasticsearchIndicesClient;
	private final String _indexName;
	private final JSONFactory _jsonFactory;
	private final JsonpMapper _jsonpMapper;
	private final String _overrideMappings;
	private final SearchEngineInformation _searchEngineInformation;

}