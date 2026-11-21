/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

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
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.opensearch2.internal.index.constants.IndexMappingsConstants;
import com.liferay.portal.search.opensearch2.internal.util.IndexUtil;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.opensearch2.internal.util.ResourceUtil;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch._types.mapping.DynamicTemplate;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.GetMappingRequest;
import org.opensearch.client.opensearch.indices.GetMappingResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.PutMappingRequest;
import org.opensearch.client.opensearch.indices.PutMappingResponse;
import org.opensearch.client.opensearch.indices.get_mapping.IndexMappingRecord;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class MappingsHelper {

	public MappingsHelper(
		String indexName, JSONFactory jsonFactory,
		OpenSearchIndicesClient openSearchIndicesClient,
		String overrideMappings,
		SearchEngineInformation searchEngineInformation) {

		_indexName = indexName;
		_jsonFactory = jsonFactory;
		_openSearchIndicesClient = openSearchIndicesClient;
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
		CreateIndexRequest.Builder builder, JsonpMapper jsonpMapper) {

		String mappings = String.valueOf(
			_getDefaultOrOverrideMappingsJSONObject());

		try (InputStream inputStream = new ByteArrayInputStream(
				mappings.getBytes(StandardCharsets.UTF_8))) {

			JsonProvider jsonProvider = jsonpMapper.jsonProvider();

			builder.mappings(
				TypeMapping._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
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
							"dimension", dimension
						).put(
							"type", "knn_vector"
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
				_openSearchIndicesClient.getMapping(
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
		PutMappingRequest.Builder builder = new PutMappingRequest.Builder(
		).index(
			_indexName
		);

		List<Map<String, DynamicTemplate>> dynamicTemplates =
			IndexUtil.getDynamicTemplatesMap(mappingsJSONObject);

		if (dynamicTemplates != null) {
			builder.dynamicTemplates(dynamicTemplates);
		}

		Map<String, Property> properties = IndexUtil.getPropertiesMap(
			mappingsJSONObject);

		if (properties != null) {
			builder.properties(properties);
		}

		try {
			PutMappingResponse putMappingResponse =
				_openSearchIndicesClient.putMapping(builder.build());

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

	private final String _indexName;
	private final JSONFactory _jsonFactory;
	private final OpenSearchIndicesClient _openSearchIndicesClient;
	private final String _overrideMappings;
	private final SearchEngineInformation _searchEngineInformation;

}