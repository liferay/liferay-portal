/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.compress.CompressedXContent;
import org.elasticsearch.common.settings.Settings;

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

		GetIndexRequest getIndexRequest = createGetIndexRequest(
			getIndexIndexRequest);

		GetIndexResponse getIndexResponse = _getGetIndexResponse(
			getIndexRequest, getIndexIndexRequest);

		GetIndexIndexResponse getIndexIndexResponse =
			new GetIndexIndexResponse();

		getIndexIndexResponse.setIndexNames(getIndexResponse.getIndices());

		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetadata>>
			indicesMappings = getIndexResponse.getMappings();

		getIndexIndexResponse.setMappings(_convertMappings(indicesMappings));

		ImmutableOpenMap<String, Settings> indicesSettings =
			getIndexResponse.getSettings();

		getIndexIndexResponse.setSettings(_convertSettings(indicesSettings));

		return getIndexIndexResponse;
	}

	protected GetIndexRequest createGetIndexRequest(
		GetIndexIndexRequest getIndexIndexRequest) {

		GetIndexRequest getIndexRequest = new GetIndexRequest();

		getIndexRequest.indices(getIndexIndexRequest.getIndexNames());

		return getIndexRequest;
	}

	private Map<String, Map<String, String>> _convertMappings(
		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetadata>>
			indicesMappings) {

		Iterator
			<ObjectObjectCursor
				<String, ImmutableOpenMap<String, MappingMetadata>>> iterator =
					indicesMappings.iterator();

		Map<String, Map<String, String>> indexMappings = new HashMap<>();

		while (iterator.hasNext()) {
			ObjectObjectCursor
				<String, ImmutableOpenMap<String, MappingMetadata>>
					objectObjectCursor = iterator.next();

			ImmutableOpenMap<String, MappingMetadata> typeMappingsData =
				objectObjectCursor.value;

			Map<String, String> indiceTypeMappings = new HashMap<>();

			indexMappings.put(objectObjectCursor.key, indiceTypeMappings);

			Iterator<ObjectObjectCursor<String, MappingMetadata>>
				typeMappingsIterator = typeMappingsData.iterator();

			while (typeMappingsIterator.hasNext()) {
				ObjectObjectCursor<String, MappingMetadata>
					typeMappingsObjectObjectCursor =
						typeMappingsIterator.next();

				MappingMetadata mappingMetadata =
					typeMappingsObjectObjectCursor.value;

				CompressedXContent mappingContent = mappingMetadata.source();

				indiceTypeMappings.put(
					typeMappingsObjectObjectCursor.key,
					mappingContent.toString());
			}
		}

		return indexMappings;
	}

	private Map<String, String> _convertSettings(
		ImmutableOpenMap<String, Settings> indicesSettings) {

		Iterator<ObjectObjectCursor<String, Settings>> iterator =
			indicesSettings.iterator();

		Map<String, String> indicesSettingsMap = new HashMap<>();

		while (iterator.hasNext()) {
			ObjectObjectCursor<String, Settings> objectObjectCursor =
				iterator.next();

			Settings settings = objectObjectCursor.value;

			indicesSettingsMap.put(objectObjectCursor.key, settings.toString());
		}

		return indicesSettingsMap;
	}

	private GetIndexResponse _getGetIndexResponse(
		GetIndexRequest getIndexRequest,
		GetIndexIndexRequest getIndexIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				getIndexIndexRequest.getConnectionId(),
				getIndexIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.get(getIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}