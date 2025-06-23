/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.ShardStatistics;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;
import org.opensearch.client.opensearch.snapshot.RestoreRequest;
import org.opensearch.client.opensearch.snapshot.RestoreResponse;
import org.opensearch.client.opensearch.snapshot.restore.SnapshotRestore;

/**
 * @author Michael C. Han
 */
public class RestoreSnapshotRequestExecutor {

	public RestoreSnapshotRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public RestoreSnapshotResponse execute(
		RestoreSnapshotRequest restoreSnapshotRequest) {

		RestoreResponse restoreResponse = _getRestoreResponse(
			createRestoreRequest(restoreSnapshotRequest),
			restoreSnapshotRequest);

		SnapshotRestore snapshotRestore = restoreResponse.snapshot();

		ShardStatistics shardStatistics = snapshotRestore.shards();

		return new RestoreSnapshotResponse(
			snapshotRestore.snapshot(),
			ArrayUtil.toStringArray(snapshotRestore.indices()),
			ConversionUtil.toInt(shardStatistics.total()),
			ConversionUtil.toInt(shardStatistics.failed()));
	}

	protected RestoreRequest createRestoreRequest(
		RestoreSnapshotRequest restoreSnapshotRequest) {

		RestoreRequest.Builder builder = new RestoreRequest.Builder();

		builder.includeAliases(restoreSnapshotRequest.isIncludeAliases());
		builder.includeGlobalState(
			restoreSnapshotRequest.isRestoreGlobalState());
		builder.indices(
			ListUtil.fromArray(restoreSnapshotRequest.getIndexNames()));
		builder.partial(restoreSnapshotRequest.isPartialRestore());

		SetterUtil.setNotBlankString(
			builder::renamePattern, restoreSnapshotRequest.getRenamePattern());
		SetterUtil.setNotBlankString(
			builder::renameReplacement,
			restoreSnapshotRequest.getRenameReplacement());

		builder.repository(restoreSnapshotRequest.getRepositoryName());
		builder.snapshot(restoreSnapshotRequest.getSnapshotName());
		builder.waitForCompletion(restoreSnapshotRequest.isWaitForCompletion());

		return builder.build();
	}

	private RestoreResponse _getRestoreResponse(
		RestoreRequest restoreRequest,
		RestoreSnapshotRequest restoreSnapshotRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				restoreSnapshotRequest.getConnectionId(),
				restoreSnapshotRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.restore(restoreRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}