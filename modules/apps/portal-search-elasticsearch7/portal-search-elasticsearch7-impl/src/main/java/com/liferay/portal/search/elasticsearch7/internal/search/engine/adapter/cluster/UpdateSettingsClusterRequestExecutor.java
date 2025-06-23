/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterResponse;

import java.io.IOException;

import java.util.Map;

import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

/**
 * @author Bryan Engler
 */
public class UpdateSettingsClusterRequestExecutor {

	public UpdateSettingsClusterRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public UpdateSettingsClusterResponse execute(
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		ClusterUpdateSettingsRequest clusterUpdateSettingsRequest =
			_createClusterUpdateSettingsRequest(updateSettingsClusterRequest);

		ClusterUpdateSettingsResponse clusterUpdateSettingsResponse =
			_getClusterUpdateSettingsResponse(
				clusterUpdateSettingsRequest, updateSettingsClusterRequest);

		Settings persistentSettings =
			clusterUpdateSettingsResponse.getPersistentSettings();
		Settings transientSettings =
			clusterUpdateSettingsResponse.getTransientSettings();

		return new UpdateSettingsClusterResponse(
			persistentSettings.toString(), transientSettings.toString());
	}

	private ClusterUpdateSettingsRequest _createClusterUpdateSettingsRequest(
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		ClusterUpdateSettingsRequest clusterUpdateSettingsRequest =
			new ClusterUpdateSettingsRequest();

		Settings.Builder persistentSettingsBuilder = Settings.builder();

		Map<String, String> persistentSettings =
			updateSettingsClusterRequest.getPersistentSettings();

		for (Map.Entry<String, String> entry : persistentSettings.entrySet()) {
			persistentSettingsBuilder.put(entry.getKey(), entry.getValue());
		}

		clusterUpdateSettingsRequest.persistentSettings(
			persistentSettingsBuilder);

		Settings.Builder transientSettingsBuilder = Settings.builder();

		Map<String, String> transientSettings =
			updateSettingsClusterRequest.getTransientSettings();

		for (Map.Entry<String, String> entry : transientSettings.entrySet()) {
			transientSettingsBuilder.put(entry.getKey(), entry.getValue());
		}

		clusterUpdateSettingsRequest.transientSettings(
			transientSettingsBuilder);

		return clusterUpdateSettingsRequest;
	}

	private ClusterUpdateSettingsResponse _getClusterUpdateSettingsResponse(
		ClusterUpdateSettingsRequest clusterUpdateSettingsRequest,
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				updateSettingsClusterRequest.getConnectionId(),
				updateSettingsClusterRequest.isPreferLocalCluster());

		ClusterClient clusterClient = restHighLevelClient.cluster();

		try {
			return clusterClient.putSettings(
				clusterUpdateSettingsRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}