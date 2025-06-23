/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.snapshot.CreateRepositoryRequest;
import org.opensearch.client.opensearch.snapshot.CreateRepositoryResponse;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRepositoryRequestExecutor {

	public CreateSnapshotRepositoryRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public CreateSnapshotRepositoryResponse execute(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		CreateRepositoryResponse createRepositoryResponse =
			getCreateRepositoryResponse(
				createCreateRepositoryRequest(createSnapshotRepositoryRequest),
				createSnapshotRepositoryRequest);

		return new CreateSnapshotRepositoryResponse(
			createRepositoryResponse.acknowledged());
	}

	protected CreateRepositoryRequest createCreateRepositoryRequest(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		return CreateRepositoryRequest.of(
			createRepositoryRequest -> createRepositoryRequest.name(
				createSnapshotRepositoryRequest.getName()
			).settings(
				settings -> settings.compress(
					createSnapshotRepositoryRequest.isCompress()
				).location(
					createSnapshotRepositoryRequest.getLocation()
				)
			).type(
				createSnapshotRepositoryRequest.getType()
			).verify(
				createSnapshotRepositoryRequest.isVerify()
			));
	}

	protected CreateRepositoryResponse getCreateRepositoryResponse(
		CreateRepositoryRequest createRepositoryRequest,
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				createSnapshotRepositoryRequest.getConnectionId(),
				createSnapshotRepositoryRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.createRepository(
				createRepositoryRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}