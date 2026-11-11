/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.recommendation.search;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.QueriesUtil;

/**
 * @author Riccardo Ferrari
 */
public class RecommendationIndexer {

	public RecommendationIndexer(
		String name, IndexNameBuilder indexNameBuilder,
		SearchCapabilities searchCapabilities,
		SearchEngineAdapter searchEngineAdapter) {

		_name = name;
		_indexNameBuilder = indexNameBuilder;
		_searchCapabilities = searchCapabilities;
		_searchEngineAdapter = searchEngineAdapter;
	}

	public void createIndex(long companyId) {
		if (!_searchCapabilities.isAnalyticsSupported()) {
			return;
		}

		String indexName = getIndexName(companyId);

		if (_indexExists(indexName)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Index " + indexName + " already exist");
			}

			return;
		}

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			indexName);

		createIndexRequest.setMappings(_readJSON(_getIndexMappingFileName()));
		createIndexRequest.setSettings(_readJSON("settings.json"));

		_searchEngineAdapter.execute(createIndexRequest);

		if (_log.isDebugEnabled()) {
			_log.debug("Index " + indexName + " created successfully");
		}
	}

	public void dropIndex(long companyId) {
		if (!_searchCapabilities.isAnalyticsSupported()) {
			return;
		}

		String indexName = getIndexName(companyId);

		if (!_indexExists(indexName)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Index " + indexName + " does not exist");
			}

			return;
		}

		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			indexName);

		_searchEngineAdapter.execute(deleteIndexRequest);

		if (_log.isDebugEnabled()) {
			_log.debug("Index " + indexName + " dropped successfully");
		}
	}

	public String getIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId) + "-" + _name;
	}

	public boolean isEmpty(long companyId) {
		if (!_searchCapabilities.isAnalyticsSupported()) {
			return false;
		}

		String indexName = getIndexName(companyId);

		if (!_indexExists(indexName)) {
			return false;
		}

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(indexName);
		countSearchRequest.setQuery(QueriesUtil.matchAll());

		CountSearchResponse countSearchResponse = _searchEngineAdapter.execute(
			countSearchRequest);

		if (countSearchResponse.getCount() == 0) {
			return true;
		}

		return false;
	}

	private String _getIndexMappingFileName() {
		return _name.concat("-mappings.json");
	}

	private boolean _indexExists(String indexName) {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(indexName);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	private String _readJSON(String fileName) {
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.read(getClass(), "/META-INF/search/" + fileName));

			return jsonObject.toString();
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);

			throw new IllegalStateException("Unable to read file " + fileName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RecommendationIndexer.class);

	private final IndexNameBuilder _indexNameBuilder;
	private final String _name;
	private final SearchCapabilities _searchCapabilities;
	private final SearchEngineAdapter _searchEngineAdapter;

}