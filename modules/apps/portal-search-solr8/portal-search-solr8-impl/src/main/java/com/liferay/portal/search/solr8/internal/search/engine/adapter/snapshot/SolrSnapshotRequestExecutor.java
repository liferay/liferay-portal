/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryResponse;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesResponse;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsResponse;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequestExecutor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bryan Engler
 */
@Component(
	property = "search.engine.impl=Solr",
	service = SnapshotRequestExecutor.class
)
public class SolrSnapshotRequestExecutor implements SnapshotRequestExecutor {

	@Override
	public CreateSnapshotRepositoryResponse executeSnapshotRequest(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		return _createSnapshotRepositoryRequestExecutor.execute(
			createSnapshotRepositoryRequest);
	}

	@Override
	public CreateSnapshotResponse executeSnapshotRequest(
		CreateSnapshotRequest createSnapshotRequest) {

		return _createSnapshotRequestExecutor.execute(createSnapshotRequest);
	}

	@Override
	public DeleteSnapshotResponse executeSnapshotRequest(
		DeleteSnapshotRequest deleteSnapshotRequest) {

		return _deleteSnapshotRequestExecutor.execute(deleteSnapshotRequest);
	}

	@Override
	public GetSnapshotRepositoriesResponse executeSnapshotRequest(
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest) {

		return _getSnapshotRepositoriesRequestExecutor.execute(
			getSnapshotRepositoriesRequest);
	}

	@Override
	public GetSnapshotsResponse executeSnapshotRequest(
		GetSnapshotsRequest getSnapshotsRequest) {

		return _getSnapshotsRequestExecutor.execute(getSnapshotsRequest);
	}

	@Override
	public RestoreSnapshotResponse executeSnapshotRequest(
		RestoreSnapshotRequest restoreSnapshotRequest) {

		return _restoreSnapshotRequestExecutor.execute(restoreSnapshotRequest);
	}

	private final CreateSnapshotRepositoryRequestExecutor
		_createSnapshotRepositoryRequestExecutor =
			new CreateSnapshotRepositoryRequestExecutor();
	private final CreateSnapshotRequestExecutor _createSnapshotRequestExecutor =
		new CreateSnapshotRequestExecutor();
	private final DeleteSnapshotRequestExecutor _deleteSnapshotRequestExecutor =
		new DeleteSnapshotRequestExecutor();
	private final GetSnapshotRepositoriesRequestExecutor
		_getSnapshotRepositoriesRequestExecutor =
			new GetSnapshotRepositoriesRequestExecutor();
	private final GetSnapshotsRequestExecutor _getSnapshotsRequestExecutor =
		new GetSnapshotsRequestExecutor();
	private final RestoreSnapshotRequestExecutor
		_restoreSnapshotRequestExecutor = new RestoreSnapshotRequestExecutor();

}