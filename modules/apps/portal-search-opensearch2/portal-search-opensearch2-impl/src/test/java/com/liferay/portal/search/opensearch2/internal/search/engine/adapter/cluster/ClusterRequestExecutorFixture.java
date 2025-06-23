/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

/**
 * @author Dylan Rebelak
 */
public class ClusterRequestExecutorFixture {

	public ClusterRequestExecutor getClusterRequestExecutor() {
		return _clusterRequestExecutor;
	}

	public void setUp() {
		_clusterRequestExecutor = new OpenSearchClusterRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_healthClusterRequestExecutor",
			new HealthClusterRequestExecutor(_openSearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_stateClusterRequestExecutor",
			new StateClusterRequestExecutor(_openSearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_statsClusterRequestExecutor",
			new StatsClusterRequestExecutor(_openSearchConnectionManager));
	}

	protected void setOpenSearchConnectionManager(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	private ClusterRequestExecutor _clusterRequestExecutor;
	private OpenSearchConnectionManager _openSearchConnectionManager;

}