/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ccr;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.ccr.UnfollowCCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.UnfollowCCRResponse;

import java.io.IOException;

import org.elasticsearch.client.CcrClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ccr.UnfollowRequest;
import org.elasticsearch.client.core.AcknowledgedResponse;

/**
 * @author Bryan Engler
 */
public class UnfollowCCRRequestExecutor {

	public UnfollowCCRRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public UnfollowCCRResponse execute(UnfollowCCRRequest unfollowCCRRequest) {
		UnfollowRequest unfollowRequest = _createUnfollowRequest(
			unfollowCCRRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			unfollowRequest, unfollowCCRRequest);

		return new UnfollowCCRResponse(acknowledgedResponse.isAcknowledged());
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		UnfollowRequest unfollowRequest,
		UnfollowCCRRequest unfollowCCRRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				unfollowCCRRequest.getConnectionId(),
				unfollowCCRRequest.isPreferLocalCluster());

		CcrClient ccrClient = restHighLevelClient.ccr();

		try {
			return ccrClient.unfollow(unfollowRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private UnfollowRequest _createUnfollowRequest(
		UnfollowCCRRequest unfollowCCRRequest) {

		return new UnfollowRequest(unfollowCCRRequest.getIndexName());
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}