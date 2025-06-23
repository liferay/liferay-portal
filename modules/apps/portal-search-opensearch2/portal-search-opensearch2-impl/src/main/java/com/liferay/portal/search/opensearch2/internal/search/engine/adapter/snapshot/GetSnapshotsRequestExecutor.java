/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.snapshot.GetSnapshotRequest;
import org.opensearch.client.opensearch.snapshot.GetSnapshotResponse;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;
import org.opensearch.client.opensearch.snapshot.SnapshotInfo;

/**
 * @author Michael C. Han
 */
public class GetSnapshotsRequestExecutor {

	public GetSnapshotsRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public GetSnapshotsResponse execute(
		GetSnapshotsRequest getSnapshotsRequest) {

		GetSnapshotsResponse getSnapshotsResponse = new GetSnapshotsResponse();

		GetSnapshotResponse getSnapshotResponse = _getGetSnapshotResponse(
			createGetSnapshotRequest(getSnapshotsRequest), getSnapshotsRequest);

		List<SnapshotInfo> snapshotInfos = getSnapshotResponse.snapshots();

		snapshotInfos.forEach(
			snapshotInfo -> getSnapshotsResponse.addSnapshotInfo(
				SnapshotInfoConverter.convert(snapshotInfo)));

		return getSnapshotsResponse;
	}

	protected GetSnapshotRequest createGetSnapshotRequest(
		GetSnapshotsRequest getSnapshotsRequest) {

		return GetSnapshotRequest.of(
			getSnapshotRequest -> getSnapshotRequest.ignoreUnavailable(
				getSnapshotsRequest.isIgnoreUnavailable()
			).repository(
				getSnapshotsRequest.getRepositoryName()
			).snapshot(
				ListUtil.fromArray(getSnapshotsRequest.getSnapshotNames())
			).verbose(
				getSnapshotsRequest.isVerbose()
			));
	}

	private GetSnapshotResponse _getGetSnapshotResponse(
		GetSnapshotRequest getSnapshotRequest,
		GetSnapshotsRequest getSnapshotsRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				getSnapshotsRequest.getConnectionId(),
				getSnapshotsRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.get(getSnapshotRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}