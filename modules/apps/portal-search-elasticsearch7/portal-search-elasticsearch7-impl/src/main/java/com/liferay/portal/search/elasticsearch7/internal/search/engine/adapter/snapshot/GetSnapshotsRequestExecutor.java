/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsResponse;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotDetails;

import java.io.IOException;

import java.util.List;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SnapshotClient;
import org.elasticsearch.snapshots.SnapshotInfo;

/**
 * @author Michael C. Han
 */
public class GetSnapshotsRequestExecutor {

	public GetSnapshotsRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public GetSnapshotsResponse execute(
		GetSnapshotsRequest getSnapshotsRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest
			elasticsearchGetSnapshotsRequest = createGetSnapshotsRequest(
				getSnapshotsRequest);

		org.elasticsearch.action.admin.cluster.snapshots.get.
			GetSnapshotsResponse elasticsearchGetSnapshotsResponse =
				_getGetSnapshotsResponse(
					elasticsearchGetSnapshotsRequest, getSnapshotsRequest);

		GetSnapshotsResponse getSnapshotsResponse = new GetSnapshotsResponse();

		List<SnapshotInfo> snapshotInfos =
			elasticsearchGetSnapshotsResponse.getSnapshots();

		snapshotInfos.forEach(
			snapshotInfo -> {
				SnapshotDetails snapshotDetails = SnapshotInfoConverter.convert(
					snapshotInfo);

				getSnapshotsResponse.addSnapshotInfo(snapshotDetails);
			});

		return getSnapshotsResponse;
	}

	protected
		org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest
			createGetSnapshotsRequest(GetSnapshotsRequest getSnapshotsRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest
			elasticsearchGetSnapshotsRequest =
				new org.elasticsearch.action.admin.cluster.snapshots.get.
					GetSnapshotsRequest();

		elasticsearchGetSnapshotsRequest.ignoreUnavailable(
			getSnapshotsRequest.isIgnoreUnavailable());
		elasticsearchGetSnapshotsRequest.repository(
			getSnapshotsRequest.getRepositoryName());
		elasticsearchGetSnapshotsRequest.snapshots(
			getSnapshotsRequest.getSnapshotNames());
		elasticsearchGetSnapshotsRequest.verbose(
			getSnapshotsRequest.isVerbose());

		return elasticsearchGetSnapshotsRequest;
	}

	private
		org.elasticsearch.action.admin.cluster.snapshots.get.
			GetSnapshotsResponse _getGetSnapshotsResponse(
				org.elasticsearch.action.admin.cluster.snapshots.get.
					GetSnapshotsRequest elasticsearchGetSnapshotsRequest,
				GetSnapshotsRequest getSnapshotsRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				getSnapshotsRequest.getConnectionId(),
				getSnapshotsRequest.isPreferLocalCluster());

		SnapshotClient snapshotClient = restHighLevelClient.snapshot();

		try {
			return snapshotClient.get(
				elasticsearchGetSnapshotsRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}