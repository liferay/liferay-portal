/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsResponse;
import co.elastic.clients.json.JsonpMapper;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexResponse;

import jakarta.json.spi.JsonProvider;

import java.io.IOException;

/**
 * @author Michael C. Han
 */
public class UpdateIndexSettingsIndexRequestExecutor {

	public UpdateIndexSettingsIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public UpdateIndexSettingsIndexResponse execute(
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		PutIndicesSettingsResponse putIndicesSettingsResponse =
			_getPutIndicesSettingsResponse(
				createPutIndicesSettingsRequest(
					updateIndexSettingsIndexRequest),
				updateIndexSettingsIndexRequest);

		return new UpdateIndexSettingsIndexResponse(
			putIndicesSettingsResponse.acknowledged());
	}

	protected PutIndicesSettingsRequest createPutIndicesSettingsRequest(
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		PutIndicesSettingsRequest.Builder builder =
			new PutIndicesSettingsRequest.Builder();

		IndicesOptions indicesOptions =
			updateIndexSettingsIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			builder.allowNoIndices(indicesOptions.isAllowNoIndices());
			builder.ignoreUnavailable(indicesOptions.isIgnoreUnavailable());
		}

		builder.index(
			ListUtil.fromArray(
				updateIndexSettingsIndexRequest.getIndexNames()));

		JsonpMapper jsonpMapper = _elasticsearchClientResolver.getJsonpMapper(
			updateIndexSettingsIndexRequest.getConnectionId());

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		builder.settings(
			IndexSettings._DESERIALIZER.deserialize(
				jsonProvider.createParser(
					new UnsyncStringReader(
						updateIndexSettingsIndexRequest.getSettings())),
				jsonpMapper));

		return builder.build();
	}

	private PutIndicesSettingsResponse _getPutIndicesSettingsResponse(
		PutIndicesSettingsRequest putIndicesSettingsRequest,
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(
				updateIndexSettingsIndexRequest.getConnectionId(),
				updateIndexSettingsIndexRequest.isPreferLocalCluster());

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		try {
			return elasticsearchIndicesClient.putSettings(
				putIndicesSettingsRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}