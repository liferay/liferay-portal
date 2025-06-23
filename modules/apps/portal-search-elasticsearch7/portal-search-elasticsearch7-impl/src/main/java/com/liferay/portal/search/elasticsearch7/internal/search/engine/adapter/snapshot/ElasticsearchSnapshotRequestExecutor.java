/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = SnapshotRequestExecutor.class
)
public class ElasticsearchSnapshotRequestExecutor
	implements SnapshotRequestExecutor {

	@Override
	public CreateSnapshotRepositoryResponse executeSnapshotRequest(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		return createSnapshotRepositoryRequestExecutor.execute(
			createSnapshotRepositoryRequest);
	}

	@Override
	public CreateSnapshotResponse executeSnapshotRequest(
		CreateSnapshotRequest createSnapshotRequest) {

		return createSnapshotRequestExecutor.execute(createSnapshotRequest);
	}

	@Override
	public DeleteSnapshotResponse executeSnapshotRequest(
		DeleteSnapshotRequest deleteSnapshotRequest) {

		return deleteSnapshotRequestExecutor.execute(deleteSnapshotRequest);
	}

	@Override
	public GetSnapshotRepositoriesResponse executeSnapshotRequest(
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest) {

		return getSnapshotRepositoriesRequestExecutor.execute(
			getSnapshotRepositoriesRequest);
	}

	@Override
	public GetSnapshotsResponse executeSnapshotRequest(
		GetSnapshotsRequest getSnapshotsRequest) {

		return getSnapshotsRequestExecutor.execute(getSnapshotsRequest);
	}

	@Override
	public RestoreSnapshotResponse executeSnapshotRequest(
		RestoreSnapshotRequest restoreSnapshotRequest) {

		return restoreSnapshotRequestExecutor.execute(restoreSnapshotRequest);
	}

	@Activate
	protected void activate() {
		createSnapshotRepositoryRequestExecutor =
			new CreateSnapshotRepositoryRequestExecutor(
				_elasticsearchClientResolver);
		createSnapshotRequestExecutor = new CreateSnapshotRequestExecutor(
			_elasticsearchClientResolver);
		deleteSnapshotRequestExecutor = new DeleteSnapshotRequestExecutor(
			_elasticsearchClientResolver);
		getSnapshotRepositoriesRequestExecutor =
			new GetSnapshotRepositoriesRequestExecutor(
				_elasticsearchClientResolver);
		getSnapshotsRequestExecutor = new GetSnapshotsRequestExecutor(
			_elasticsearchClientResolver);
		restoreSnapshotRequestExecutor = new RestoreSnapshotRequestExecutor(
			_elasticsearchClientResolver);
	}

	protected CreateSnapshotRepositoryRequestExecutor
		createSnapshotRepositoryRequestExecutor;
	protected CreateSnapshotRequestExecutor createSnapshotRequestExecutor;
	protected DeleteSnapshotRequestExecutor deleteSnapshotRequestExecutor;
	protected GetSnapshotRepositoriesRequestExecutor
		getSnapshotRepositoriesRequestExecutor;
	protected GetSnapshotsRequestExecutor getSnapshotsRequestExecutor;
	protected RestoreSnapshotRequestExecutor restoreSnapshotRequestExecutor;

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

}