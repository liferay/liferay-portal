/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ccr;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.ccr.PutFollowCCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.PutFollowCCRResponse;

import java.io.IOException;

import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.CcrClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ccr.PutFollowRequest;
import org.elasticsearch.client.ccr.PutFollowResponse;

/**
 * @author Bryan Engler
 */
public class PutFollowCCRRequestExecutor {

	public PutFollowCCRRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public PutFollowCCRResponse execute(
		PutFollowCCRRequest putFollowCCRRequest) {

		PutFollowRequest putFollowRequest = _createPutFollowRequest(
			putFollowCCRRequest);

		PutFollowResponse putFollowResponse = _getPutFollowResponse(
			putFollowRequest, putFollowCCRRequest);

		return new PutFollowCCRResponse(
			putFollowResponse.isFollowIndexCreated(),
			putFollowResponse.isIndexFollowingStarted());
	}

	private PutFollowRequest _createPutFollowRequest(
		PutFollowCCRRequest putFollowCCRRequest) {

		if (putFollowCCRRequest.getWaitForActiveShards() != 0) {
			return new PutFollowRequest(
				putFollowCCRRequest.getRemoteClusterAlias(),
				putFollowCCRRequest.getLeaderIndexName(),
				putFollowCCRRequest.getFollowerIndexName(),
				ActiveShardCount.from(
					putFollowCCRRequest.getWaitForActiveShards()));
		}

		return new PutFollowRequest(
			putFollowCCRRequest.getRemoteClusterAlias(),
			putFollowCCRRequest.getLeaderIndexName(),
			putFollowCCRRequest.getFollowerIndexName());
	}

	private PutFollowResponse _getPutFollowResponse(
		PutFollowRequest putFollowRequest,
		PutFollowCCRRequest putFollowCCRRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				putFollowCCRRequest.getConnectionId(),
				putFollowCCRRequest.isPreferLocalCluster());

		CcrClient ccrClient = restHighLevelClient.ccr();

		try {
			return ccrClient.putFollow(
				putFollowRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}