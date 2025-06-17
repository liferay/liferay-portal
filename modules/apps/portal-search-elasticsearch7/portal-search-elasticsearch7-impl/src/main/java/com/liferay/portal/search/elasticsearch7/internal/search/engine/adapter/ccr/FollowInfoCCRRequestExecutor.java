/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ccr;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.ccr.FollowInfoCCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.FollowInfoCCRResponse;
import com.liferay.portal.search.engine.adapter.ccr.FollowInfoStatus;

import java.io.IOException;

import java.util.List;

import org.elasticsearch.client.CcrClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ccr.FollowInfoRequest;
import org.elasticsearch.client.ccr.FollowInfoResponse;

/**
 * @author Bryan Engler
 */
public class FollowInfoCCRRequestExecutor {

	public FollowInfoCCRRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public FollowInfoCCRResponse execute(
		FollowInfoCCRRequest followInfoCCRRequest) {

		FollowInfoRequest followInfoRequest = _createFollowInfoRequest(
			followInfoCCRRequest);

		FollowInfoResponse followInfoResponse = _getFollowInfoResponse(
			followInfoRequest, followInfoCCRRequest);

		List<FollowInfoResponse.FollowerInfo> followerInfos =
			followInfoResponse.getInfos();

		FollowInfoResponse.FollowerInfo followerInfo = followerInfos.get(0);

		FollowInfoResponse.Status status = followerInfo.getStatus();

		if (status == FollowInfoResponse.Status.ACTIVE) {
			return new FollowInfoCCRResponse(FollowInfoStatus.ACTIVE);
		}

		return new FollowInfoCCRResponse(FollowInfoStatus.PAUSED);
	}

	private FollowInfoRequest _createFollowInfoRequest(
		FollowInfoCCRRequest followInfoCCRRequest) {

		return new FollowInfoRequest(followInfoCCRRequest.getIndexName());
	}

	private FollowInfoResponse _getFollowInfoResponse(
		FollowInfoRequest followInfoRequest,
		FollowInfoCCRRequest followInfoCCRRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				followInfoCCRRequest.getConnectionId(),
				followInfoCCRRequest.isPreferLocalCluster());

		CcrClient ccrClient = restHighLevelClient.ccr();

		try {
			return ccrClient.getFollowInfo(
				followInfoRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}