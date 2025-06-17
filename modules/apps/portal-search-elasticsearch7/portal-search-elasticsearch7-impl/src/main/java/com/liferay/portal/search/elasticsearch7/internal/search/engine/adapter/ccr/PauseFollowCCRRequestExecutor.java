/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ccr;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.ccr.PauseFollowCCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.PauseFollowCCRResponse;

import java.io.IOException;

import org.elasticsearch.client.CcrClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ccr.PauseFollowRequest;
import org.elasticsearch.client.core.AcknowledgedResponse;

/**
 * @author Bryan Engler
 */
public class PauseFollowCCRRequestExecutor {

	public PauseFollowCCRRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public PauseFollowCCRResponse execute(
		PauseFollowCCRRequest pauseFollowCCRRequest) {

		PauseFollowRequest pauseFollowRequest = _createPauseFollowRequest(
			pauseFollowCCRRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			pauseFollowRequest, pauseFollowCCRRequest);

		return new PauseFollowCCRResponse(
			acknowledgedResponse.isAcknowledged());
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		PauseFollowRequest pauseFollowRequest,
		PauseFollowCCRRequest pauseFollowCCRRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				pauseFollowCCRRequest.getConnectionId(),
				pauseFollowCCRRequest.isPreferLocalCluster());

		CcrClient ccrClient = restHighLevelClient.ccr();

		try {
			return ccrClient.pauseFollow(
				pauseFollowRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private PauseFollowRequest _createPauseFollowRequest(
		PauseFollowCCRRequest pauseFollowCCRRequest) {

		return new PauseFollowRequest(pauseFollowCCRRequest.getIndexName());
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}