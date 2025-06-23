/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import java.util.Map;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cluster.OpenSearchClusterClient;
import org.opensearch.client.opensearch.cluster.PutClusterSettingsRequest;
import org.opensearch.client.opensearch.cluster.PutClusterSettingsResponse;

/**
 * @author Bryan Engler
 */
public class UpdateSettingsClusterRequestExecutor {

	public UpdateSettingsClusterRequestExecutor(
		JSONFactory jsonFactory,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_jsonFactory = jsonFactory;
		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public UpdateSettingsClusterResponse execute(
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		PutClusterSettingsResponse putClusterSettingsResponse =
			_getPutClusterSettingsResponse(
				_createPutClusterSettingsRequest(updateSettingsClusterRequest),
				updateSettingsClusterRequest);

		JSONObject persistentSettingsJSONObject = _jsonFactory.createJSONObject(
			putClusterSettingsResponse.persistent());

		JSONObject transientSettingsJSONObject = _jsonFactory.createJSONObject(
			putClusterSettingsResponse.transient_());

		return new UpdateSettingsClusterResponse(
			persistentSettingsJSONObject.toString(),
			transientSettingsJSONObject.toString());
	}

	private PutClusterSettingsRequest _createPutClusterSettingsRequest(
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		PutClusterSettingsRequest.Builder builder =
			new PutClusterSettingsRequest.Builder();

		Map<String, String> persistentSettings =
			updateSettingsClusterRequest.getPersistentSettings();

		for (Map.Entry<String, String> entry : persistentSettings.entrySet()) {
			builder.persistent(entry.getKey(), JsonData.of(entry.getValue()));
		}

		Map<String, String> transientSettings =
			updateSettingsClusterRequest.getTransientSettings();

		for (Map.Entry<String, String> entry : transientSettings.entrySet()) {
			builder.transient_(entry.getKey(), JsonData.of(entry.getValue()));
		}

		return builder.build();
	}

	private PutClusterSettingsResponse _getPutClusterSettingsResponse(
		PutClusterSettingsRequest putClusterSettingsRequest,
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				updateSettingsClusterRequest.getConnectionId(),
				updateSettingsClusterRequest.isPreferLocalCluster());

		OpenSearchClusterClient openSearchClusterClient =
			openSearchClient.cluster();

		try {
			return openSearchClusterClient.putSettings(
				putClusterSettingsRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final JSONFactory _jsonFactory;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}