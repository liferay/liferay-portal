/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequestExecutor;

/**
 * @author Michael C. Han
 */
public class SnapshotRequestExecutorFixture {

	public SnapshotRequestExecutor getSnapshotRequestExecutor() {
		return _snapshotRequestExecutor;
	}

	public void setUp() {
		_snapshotRequestExecutor = new ElasticsearchSnapshotRequestExecutor() {
			{
				createSnapshotRepositoryRequestExecutor =
					new CreateSnapshotRepositoryRequestExecutor(
						_elasticsearchClientResolver);
				createSnapshotRequestExecutor =
					new CreateSnapshotRequestExecutor(
						_elasticsearchClientResolver);
				deleteSnapshotRequestExecutor =
					new DeleteSnapshotRequestExecutor(
						_elasticsearchClientResolver);
				getSnapshotRepositoriesRequestExecutor =
					new GetSnapshotRepositoriesRequestExecutor(
						_elasticsearchClientResolver);
				getSnapshotsRequestExecutor = new GetSnapshotsRequestExecutor(
					_elasticsearchClientResolver);
				restoreSnapshotRequestExecutor =
					new RestoreSnapshotRequestExecutor(
						_elasticsearchClientResolver);
			}
		};
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private SnapshotRequestExecutor _snapshotRequestExecutor;

}