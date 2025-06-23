/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import java.util.Arrays;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRequestExecutor {

	public CreateSnapshotRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public CreateSnapshotResponse execute(
		CreateSnapshotRequest createSnapshotRequest) {

		org.opensearch.client.opensearch.snapshot.CreateSnapshotResponse
			openSearchCreateSnapshotResponse = _getCreateSnapshotResponse(
				createSnapshotRequest,
				createCreateSnapshotRequest(createSnapshotRequest));

		return new CreateSnapshotResponse(
			SnapshotInfoConverter.convert(
				openSearchCreateSnapshotResponse.snapshot()));
	}

	protected org.opensearch.client.opensearch.snapshot.CreateSnapshotRequest
		createCreateSnapshotRequest(
			CreateSnapshotRequest createSnapshotRequest) {

		org.opensearch.client.opensearch.snapshot.CreateSnapshotRequest.Builder
			builder =
				new org.opensearch.client.opensearch.snapshot.
					CreateSnapshotRequest.Builder();

		if (ArrayUtil.isNotEmpty(createSnapshotRequest.getIndexNames())) {
			builder.indices(
				Arrays.asList(createSnapshotRequest.getIndexNames()));
		}

		builder.repository(createSnapshotRequest.getRepositoryName());
		builder.snapshot(createSnapshotRequest.getSnapshotName());
		builder.waitForCompletion(createSnapshotRequest.isWaitForCompletion());

		return builder.build();
	}

	private org.opensearch.client.opensearch.snapshot.CreateSnapshotResponse
		_getCreateSnapshotResponse(
			CreateSnapshotRequest createSnapshotRequest,
			org.opensearch.client.opensearch.snapshot.CreateSnapshotRequest
				openSearchCreateSnapshotRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				createSnapshotRequest.getConnectionId(),
				createSnapshotRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.create(
				openSearchCreateSnapshotRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}