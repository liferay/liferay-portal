/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequestExecutor;
import com.liferay.portal.search.engine.adapter.cluster.ClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.UpdateSettingsClusterResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = ClusterRequestExecutor.class
)
public class ElasticsearchClusterRequestExecutor
	implements ClusterRequestExecutor {

	@Override
	public <T extends ClusterResponse> T execute(
		ClusterRequest<T> clusterRequest) {

		return clusterRequest.accept(this);
	}

	@Override
	public HealthClusterResponse executeClusterRequest(
		HealthClusterRequest healthClusterRequest) {

		return _healthClusterRequestExecutor.execute(healthClusterRequest);
	}

	@Override
	public StateClusterResponse executeClusterRequest(
		StateClusterRequest stateClusterRequest) {

		return _stateClusterRequestExecutor.execute(stateClusterRequest);
	}

	@Override
	public StatsClusterResponse executeClusterRequest(
		StatsClusterRequest statsClusterRequest) {

		return _statsClusterRequestExecutor.execute(statsClusterRequest);
	}

	@Override
	public UpdateSettingsClusterResponse executeClusterRequest(
		UpdateSettingsClusterRequest updateSettingsClusterRequest) {

		return _updateSettingsClusterRequestExecutor.execute(
			updateSettingsClusterRequest);
	}

	@Activate
	protected void activate() {
		_healthClusterRequestExecutor = new HealthClusterRequestExecutor(
			_elasticsearchClientResolver);
		_stateClusterRequestExecutor = new StateClusterRequestExecutor(
			_elasticsearchClientResolver);
		_statsClusterRequestExecutor = new StatsClusterRequestExecutor(
			_elasticsearchClientResolver, _jsonFactory);
		_updateSettingsClusterRequestExecutor =
			new UpdateSettingsClusterRequestExecutor(
				_elasticsearchClientResolver);
	}

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	private HealthClusterRequestExecutor _healthClusterRequestExecutor;

	@Reference
	private JSONFactory _jsonFactory;

	private StateClusterRequestExecutor _stateClusterRequestExecutor;
	private StatsClusterRequestExecutor _statsClusterRequestExecutor;
	private UpdateSettingsClusterRequestExecutor
		_updateSettingsClusterRequestExecutor;

}