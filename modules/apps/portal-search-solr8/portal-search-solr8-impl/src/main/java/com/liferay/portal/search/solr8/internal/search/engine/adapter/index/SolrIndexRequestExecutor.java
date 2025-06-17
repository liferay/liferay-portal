/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexRequest;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexResponse;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;
import com.liferay.portal.search.engine.adapter.index.FlushIndexRequest;
import com.liferay.portal.search.engine.adapter.index.FlushIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndexRequestExecutor;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;
import com.liferay.portal.search.engine.adapter.index.StatsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.StatsIndexResponse;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "search.engine.impl=Solr", service = IndexRequestExecutor.class
)
public class SolrIndexRequestExecutor implements IndexRequestExecutor {

	@Override
	public AnalyzeIndexResponse executeIndexRequest(
		AnalyzeIndexRequest analyzeIndexRequest) {

		return _analyzeIndexRequestExecutor.execute(analyzeIndexRequest);
	}

	@Override
	public CloseIndexResponse executeIndexRequest(
		CloseIndexRequest closeIndexRequest) {

		return _closeIndexRequestExecutor.execute(closeIndexRequest);
	}

	@Override
	public CreateIndexResponse executeIndexRequest(
		CreateIndexRequest createIndexRequest) {

		return _createIndexRequestExecutor.execute(createIndexRequest);
	}

	@Override
	public DeleteIndexResponse executeIndexRequest(
		DeleteIndexRequest deleteIndexRequest) {

		return _deleteIndexRequestExecutor.execute(deleteIndexRequest);
	}

	@Override
	public FlushIndexResponse executeIndexRequest(
		FlushIndexRequest flushIndexRequest) {

		return _flushIndexRequestExecutor.execute(flushIndexRequest);
	}

	@Override
	public GetFieldMappingIndexResponse executeIndexRequest(
		GetFieldMappingIndexRequest getFieldMappingIndexRequest) {

		return _getFieldMappingIndexRequestExecutor.execute(
			getFieldMappingIndexRequest);
	}

	@Override
	public GetIndexIndexResponse executeIndexRequest(
		GetIndexIndexRequest getIndexIndexRequest) {

		return _getIndexIndexRequestExecutor.execute(getIndexIndexRequest);
	}

	@Override
	public GetMappingIndexResponse executeIndexRequest(
		GetMappingIndexRequest getMappingIndexRequest) {

		return _getMappingIndexRequestExecutor.execute(getMappingIndexRequest);
	}

	@Override
	public IndicesExistsIndexResponse executeIndexRequest(
		IndicesExistsIndexRequest indicesExistsIndexRequest) {

		return _indicesExistsIndexRequestExecutor.execute(
			indicesExistsIndexRequest);
	}

	@Override
	public OpenIndexResponse executeIndexRequest(
		OpenIndexRequest openIndexRequest) {

		return _openIndexRequestExecutor.execute(openIndexRequest);
	}

	@Override
	public PutMappingIndexResponse executeIndexRequest(
		PutMappingIndexRequest putMappingIndexRequest) {

		return _putMappingIndexRequestExecutor.execute(putMappingIndexRequest);
	}

	@Override
	public RefreshIndexResponse executeIndexRequest(
		RefreshIndexRequest refreshIndexRequest) {

		return _refreshIndexRequestExecutor.execute(refreshIndexRequest);
	}

	@Override
	public StatsIndexResponse executeIndexRequest(
		StatsIndexRequest statsIndexRequest) {

		return _statsIndexRequestExecutor.execute(statsIndexRequest);
	}

	@Override
	public UpdateIndexSettingsIndexResponse executeIndexRequest(
		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest) {

		return _updateIndexSettingsIndexRequestExecutor.execute(
			updateIndexSettingsIndexRequest);
	}

	@Reference
	private AnalyzeIndexRequestExecutor _analyzeIndexRequestExecutor;

	@Reference
	private CloseIndexRequestExecutor _closeIndexRequestExecutor;

	@Reference
	private CreateIndexRequestExecutor _createIndexRequestExecutor;

	@Reference
	private DeleteIndexRequestExecutor _deleteIndexRequestExecutor;

	@Reference
	private FlushIndexRequestExecutor _flushIndexRequestExecutor;

	@Reference
	private GetFieldMappingIndexRequestExecutor
		_getFieldMappingIndexRequestExecutor;

	@Reference
	private GetIndexIndexRequestExecutor _getIndexIndexRequestExecutor;

	@Reference
	private GetMappingIndexRequestExecutor _getMappingIndexRequestExecutor;

	@Reference
	private IndicesExistsIndexRequestExecutor
		_indicesExistsIndexRequestExecutor;

	@Reference
	private OpenIndexRequestExecutor _openIndexRequestExecutor;

	@Reference
	private PutMappingIndexRequestExecutor _putMappingIndexRequestExecutor;

	@Reference
	private RefreshIndexRequestExecutor _refreshIndexRequestExecutor;

	private final StatsIndexRequestExecutor _statsIndexRequestExecutor =
		new StatsIndexRequestExecutor();

	@Reference
	private UpdateIndexSettingsIndexRequestExecutor
		_updateIndexSettingsIndexRequestExecutor;

}