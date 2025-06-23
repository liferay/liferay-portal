/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

/**
 * @author Michael C. Han
 */
public class SnapshotRequestExecutorFixture {

	public SnapshotRequestExecutor getSnapshotRequestExecutor() {
		return _snapshotRequestExecutor;
	}

	public void setUp() {
		_snapshotRequestExecutor = new OpenSearchSnapshotRequestExecutor() {
			{
				createSnapshotRepositoryRequestExecutor =
					new CreateSnapshotRepositoryRequestExecutor(
						_openSearchConnectionManager);
				createSnapshotRequestExecutor =
					new CreateSnapshotRequestExecutor(
						_openSearchConnectionManager);
				deleteSnapshotRequestExecutor =
					new DeleteSnapshotRequestExecutor(
						_openSearchConnectionManager);
				getSnapshotRepositoriesRequestExecutor =
					new GetSnapshotRepositoriesRequestExecutor(
						_openSearchConnectionManager);
				getSnapshotsRequestExecutor = new GetSnapshotsRequestExecutor(
					_openSearchConnectionManager);
				restoreSnapshotRequestExecutor =
					new RestoreSnapshotRequestExecutor(
						_openSearchConnectionManager);
			}
		};
	}

	protected void setOpenSearchConnectionManager(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	private OpenSearchConnectionManager _openSearchConnectionManager;
	private SnapshotRequestExecutor _snapshotRequestExecutor;

}