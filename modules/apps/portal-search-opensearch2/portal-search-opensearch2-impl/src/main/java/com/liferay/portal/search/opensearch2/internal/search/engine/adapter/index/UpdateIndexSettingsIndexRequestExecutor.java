/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;

/**
 * @author Michael C. Han
 */
public class UpdateIndexSettingsIndexRequestExecutor {

	public UpdateIndexSettingsIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
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

		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
			updateIndexSettingsIndexRequest.getConnectionId());

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		String settings = updateIndexSettingsIndexRequest.getSettings();

		try (InputStream inputStream = new ByteArrayInputStream(
				settings.getBytes(StandardCharsets.UTF_8))) {

			builder.settings(
				IndexSettings._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return builder.build();
	}

	private PutIndicesSettingsResponse _getPutIndicesSettingsResponse(
		PutIndicesSettingsRequest putIndicesSettingsRequest,
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				updateIndexSettingsIndexRequest.getConnectionId(),
				updateIndexSettingsIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.putSettings(
				putIndicesSettingsRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}