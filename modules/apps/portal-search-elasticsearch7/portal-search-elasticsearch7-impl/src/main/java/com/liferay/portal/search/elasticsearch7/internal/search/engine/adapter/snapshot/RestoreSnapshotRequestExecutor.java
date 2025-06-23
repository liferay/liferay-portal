/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotResponse;

import java.io.IOException;

import java.util.List;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SnapshotClient;
import org.elasticsearch.snapshots.RestoreInfo;

/**
 * @author Michael C. Han
 */
public class RestoreSnapshotRequestExecutor {

	public RestoreSnapshotRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public RestoreSnapshotResponse execute(
		RestoreSnapshotRequest restoreSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.restore.
			RestoreSnapshotRequest elasticsearchRestoreSnapshotRequest =
				createRestoreSnapshotRequest(restoreSnapshotRequest);

		org.elasticsearch.action.admin.cluster.snapshots.restore.
			RestoreSnapshotResponse elasticsearchRestoreSnapshotResponse =
				_getRestoreSnapshotResponse(
					elasticsearchRestoreSnapshotRequest,
					restoreSnapshotRequest);

		RestoreInfo restoreInfo =
			elasticsearchRestoreSnapshotResponse.getRestoreInfo();

		List<String> indexNames = restoreInfo.indices();

		return new RestoreSnapshotResponse(
			restoreInfo.name(), indexNames.toArray(new String[0]),
			restoreInfo.totalShards(), restoreInfo.failedShards());
	}

	protected org.elasticsearch.action.admin.cluster.snapshots.restore.
		RestoreSnapshotRequest createRestoreSnapshotRequest(
			RestoreSnapshotRequest restoreSnapshotRequest) {

		org.elasticsearch.action.admin.cluster.snapshots.restore.
			RestoreSnapshotRequest elasticsearchRestoreSnapshotRequest =
				new org.elasticsearch.action.admin.cluster.snapshots.restore.
					RestoreSnapshotRequest();

		elasticsearchRestoreSnapshotRequest.includeAliases(
			restoreSnapshotRequest.isIncludeAliases());
		elasticsearchRestoreSnapshotRequest.indices(
			restoreSnapshotRequest.getIndexNames());
		elasticsearchRestoreSnapshotRequest.partial(
			restoreSnapshotRequest.isPartialRestore());

		if (Validator.isNotNull(
				restoreSnapshotRequest.getRenameReplacement())) {

			elasticsearchRestoreSnapshotRequest.renameReplacement(
				restoreSnapshotRequest.getRenameReplacement());
		}

		if (Validator.isNotNull(restoreSnapshotRequest.getRenamePattern())) {
			elasticsearchRestoreSnapshotRequest.renamePattern(
				restoreSnapshotRequest.getRenamePattern());
		}

		elasticsearchRestoreSnapshotRequest.repository(
			restoreSnapshotRequest.getRepositoryName());
		elasticsearchRestoreSnapshotRequest.includeGlobalState(
			restoreSnapshotRequest.isRestoreGlobalState());
		elasticsearchRestoreSnapshotRequest.snapshot(
			restoreSnapshotRequest.getSnapshotName());
		elasticsearchRestoreSnapshotRequest.waitForCompletion(
			restoreSnapshotRequest.isWaitForCompletion());

		return elasticsearchRestoreSnapshotRequest;
	}

	private org.elasticsearch.action.admin.cluster.snapshots.restore.
		RestoreSnapshotResponse _getRestoreSnapshotResponse(
			org.elasticsearch.action.admin.cluster.snapshots.restore.
				RestoreSnapshotRequest elasticsearchRestoreSnapshotRequest,
			RestoreSnapshotRequest restoreSnapshotRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				restoreSnapshotRequest.getConnectionId(),
				restoreSnapshotRequest.isPreferLocalCluster());

		SnapshotClient snapshotClient = restHighLevelClient.snapshot();

		try {
			return snapshotClient.restore(
				elasticsearchRestoreSnapshotRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}