/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;

/**
 * @author Michael C. Han
 */
public class DeleteSnapshotRequestExecutor {

	public DeleteSnapshotRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public DeleteSnapshotResponse execute(
		DeleteSnapshotRequest deleteSnapshotRequest) {

		org.opensearch.client.opensearch.snapshot.DeleteSnapshotResponse
			deleteSnapshotResponse = getDeleteSnapshotResponse(
				deleteSnapshotRequest,
				createDeleteSnapshotRequest(deleteSnapshotRequest));

		return new DeleteSnapshotResponse(
			deleteSnapshotResponse.acknowledged());
	}

	protected org.opensearch.client.opensearch.snapshot.DeleteSnapshotRequest
		createDeleteSnapshotRequest(
			DeleteSnapshotRequest deleteSnapshotRequest) {

		return org.opensearch.client.opensearch.snapshot.DeleteSnapshotRequest.
			of(
				openSearchDeleteSnapshotRequest ->
					openSearchDeleteSnapshotRequest.repository(
						deleteSnapshotRequest.getRepositoryName()
					).snapshot(
						deleteSnapshotRequest.getSnapshotName()
					));
	}

	protected org.opensearch.client.opensearch.snapshot.DeleteSnapshotResponse
		getDeleteSnapshotResponse(
			DeleteSnapshotRequest deleteSnapshotRequest,
			org.opensearch.client.opensearch.snapshot.DeleteSnapshotRequest
				openSearchDeleteSnapshotRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				deleteSnapshotRequest.getConnectionId(),
				deleteSnapshotRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.delete(
				openSearchDeleteSnapshotRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}