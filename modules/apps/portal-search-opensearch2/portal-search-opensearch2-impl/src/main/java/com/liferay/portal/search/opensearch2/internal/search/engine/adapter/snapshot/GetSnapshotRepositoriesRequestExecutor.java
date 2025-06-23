/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesResponse;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRepositoryDetails;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.snapshot.GetRepositoryRequest;
import org.opensearch.client.opensearch.snapshot.GetRepositoryResponse;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;

/**
 * @author Michael C. Han
 */
public class GetSnapshotRepositoriesRequestExecutor {

	public GetSnapshotRepositoriesRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public GetSnapshotRepositoriesResponse execute(
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest) {

		GetSnapshotRepositoriesResponse getSnapshotRepositoriesResponse =
			new GetSnapshotRepositoriesResponse();

		GetRepositoryResponse getRepositoryResponse = _getGetRepositoryResponse(
			createGetRepositoryRequest(getSnapshotRepositoriesRequest),
			getSnapshotRepositoriesRequest);

		MapUtil.isNotEmptyForEach(
			getRepositoryResponse.result(),
			(name, repository) ->
				getSnapshotRepositoriesResponse.addSnapshotRepositoryMetadata(
					new SnapshotRepositoryDetails(
						name, repository.type(),
						JsonpUtil.toString(repository.settings()))));

		return getSnapshotRepositoriesResponse;
	}

	protected GetRepositoryRequest createGetRepositoryRequest(
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest) {

		return GetRepositoryRequest.of(
			getRepositoryRequest -> getRepositoryRequest.name(
				ListUtil.fromArray(
					getSnapshotRepositoriesRequest.getRepositoryNames())));
	}

	private GetRepositoryResponse _getGetRepositoryResponse(
		GetRepositoryRequest getRepositoryRequest,
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				getSnapshotRepositoriesRequest.getConnectionId(),
				getSnapshotRepositoriesRequest.isPreferLocalCluster());

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		try {
			return openSearchSnapshotClient.getRepository(getRepositoryRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}