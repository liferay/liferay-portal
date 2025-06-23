/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotResponse;

import java.io.IOException;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SnapshotClient;

/**
 * @author Michael C. Han
 */
public class DeleteSnapshotRequestExecutor {

	public DeleteSnapshotRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public DeleteSnapshotResponse execute(
		DeleteSnapshotRequest deleteSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.delete.
			DeleteSnapshotRequest elasticsearchDeleteSnapshotRequest =
				createDeleteSnapshotRequest(deleteSnapshotRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			elasticsearchDeleteSnapshotRequest, deleteSnapshotRequest);

		return new DeleteSnapshotResponse(
			acknowledgedResponse.isAcknowledged());
	}

	protected org.elasticsearch.action.admin.cluster.snapshots.delete.
		DeleteSnapshotRequest createDeleteSnapshotRequest(
			DeleteSnapshotRequest deleteSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.delete.
			DeleteSnapshotRequest elasticsearchDeleteSnapshotRequest =
				new org.elasticsearch.action.admin.cluster.snapshots.delete.
					DeleteSnapshotRequest();

		elasticsearchDeleteSnapshotRequest.repository(
			deleteSnapshotRequest.getRepositoryName());
		elasticsearchDeleteSnapshotRequest.snapshots(
			deleteSnapshotRequest.getSnapshotName());

		return elasticsearchDeleteSnapshotRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		org.elasticsearch.action.admin.cluster.snapshots.delete.
			DeleteSnapshotRequest elasticsearchDeleteSnapshotRequest,
		DeleteSnapshotRequest deleteSnapshotRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				deleteSnapshotRequest.getConnectionId(),
				deleteSnapshotRequest.isPreferLocalCluster());

		SnapshotClient snapshotClient = restHighLevelClient.snapshot();

		try {
			return snapshotClient.delete(
				elasticsearchDeleteSnapshotRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}