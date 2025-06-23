/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotDetails;

import java.io.IOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SnapshotClient;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRequestExecutor {

	public CreateSnapshotRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public CreateSnapshotResponse execute(
		CreateSnapshotRequest createSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.create.
			CreateSnapshotRequest elasticsearchCreateSnapshotRequest =
				createCreateSnapshotRequest(createSnapshotRequest);

		org.elasticsearch.action.admin.cluster.snapshots.create.
			CreateSnapshotResponse elasticsearchCreateSnapshotResponse =
				_getCreateSnapshotResponse(
					elasticsearchCreateSnapshotRequest, createSnapshotRequest);

		SnapshotDetails snapshotDetails = SnapshotInfoConverter.convert(
			elasticsearchCreateSnapshotResponse.getSnapshotInfo());

		return new CreateSnapshotResponse(snapshotDetails);
	}

	protected org.elasticsearch.action.admin.cluster.snapshots.create.
		CreateSnapshotRequest createCreateSnapshotRequest(
			CreateSnapshotRequest createSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.create.
			CreateSnapshotRequest elasticsearchCreateSnapshotRequest =
				new org.elasticsearch.action.admin.cluster.snapshots.create.
					CreateSnapshotRequest();

		if (ArrayUtil.isNotEmpty(createSnapshotRequest.getIndexNames())) {
			elasticsearchCreateSnapshotRequest.indices(
				createSnapshotRequest.getIndexNames());
		}

		elasticsearchCreateSnapshotRequest.repository(
			createSnapshotRequest.getRepositoryName());
		elasticsearchCreateSnapshotRequest.snapshot(
			createSnapshotRequest.getSnapshotName());
		elasticsearchCreateSnapshotRequest.waitForCompletion(
			createSnapshotRequest.isWaitForCompletion());

		return elasticsearchCreateSnapshotRequest;
	}

	private org.elasticsearch.action.admin.cluster.snapshots.create.
		CreateSnapshotResponse _getCreateSnapshotResponse(
			org.elasticsearch.action.admin.cluster.snapshots.create.
				CreateSnapshotRequest elasticsearchCreateSnapshotRequest,
			CreateSnapshotRequest createSnapshotRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				createSnapshotRequest.getConnectionId(),
				createSnapshotRequest.isPreferLocalCluster());

		SnapshotClient snapshotClient = restHighLevelClient.snapshot();

		try {
			return snapshotClient.create(
				elasticsearchCreateSnapshotRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}