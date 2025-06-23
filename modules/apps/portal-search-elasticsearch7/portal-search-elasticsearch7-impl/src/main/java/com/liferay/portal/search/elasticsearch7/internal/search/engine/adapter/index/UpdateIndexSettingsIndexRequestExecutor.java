/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;

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

		UpdateSettingsRequest updateSettingsRequest =
			createUpdateSettingsRequest(updateIndexSettingsIndexRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			updateSettingsRequest, updateIndexSettingsIndexRequest);

		return new UpdateIndexSettingsIndexResponse(
			acknowledgedResponse.isAcknowledged());
	}

	protected UpdateSettingsRequest createUpdateSettingsRequest(
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest(
			updateIndexSettingsIndexRequest.getIndexNames());

		updateSettingsRequest.settings(
			updateIndexSettingsIndexRequest.getSettings(), XContentType.JSON);

		IndicesOptions indicesOptions =
			updateIndexSettingsIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			updateSettingsRequest.indicesOptions(
				IndicesOptionsTranslatorUtil.translate(indicesOptions));
		}

		return updateSettingsRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		UpdateSettingsRequest updateSettingsRequest,
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				updateIndexSettingsIndexRequest.getConnectionId(),
				updateIndexSettingsIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.putSettings(
				updateSettingsRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}