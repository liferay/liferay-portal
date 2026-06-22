/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 * @author Joshua Cords
 * @author Tibor Lipusz
 */
public class RankingIndexCreatorUtil {

	public static void create(
		SearchEngineAdapter searchEngineAdapter,
		RankingIndexName rankingIndexName) {

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			rankingIndexName.getIndexName());

		createIndexRequest.setMappings(_readJSON(_INDEX_MAPPINGS_FILE_NAME));
		createIndexRequest.setSettings(_readJSON(_INDEX_SETTINGS_FILE_NAME));

		searchEngineAdapter.execute(createIndexRequest);
	}

	public static void createIfNotExists(
		SearchEngineAdapter searchEngineAdapter,
		RankingIndexName rankingIndexName) {

		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(rankingIndexName.getIndexName());

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			searchEngineAdapter.execute(indicesExistsIndexRequest);

		if (!indicesExistsIndexResponse.isExists()) {
			create(searchEngineAdapter, rankingIndexName);
		}
	}

	public static void deleteIfExists(
		SearchEngineAdapter searchEngineAdapter,
		RankingIndexName rankingIndexName) {

		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(rankingIndexName.getIndexName());

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			searchEngineAdapter.execute(indicesExistsIndexRequest);

		if (indicesExistsIndexResponse.isExists()) {
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
				rankingIndexName.getIndexName());

			searchEngineAdapter.execute(deleteIndexRequest);
		}
	}

	private static String _readJSON(String fileName) {
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.read(
					RankingIndexCreatorUtil.class,
					"/META-INF/search/" + fileName));

			return jsonObject.toString();
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);
		}

		return null;
	}

	private static final String _INDEX_MAPPINGS_FILE_NAME =
		"liferay-search-tuning-rankings-mappings.json";

	private static final String _INDEX_SETTINGS_FILE_NAME =
		"liferay-search-tuning-rankings-settings.json";

	private static final Log _log = LogFactoryUtil.getLog(
		RankingIndexCreatorUtil.class);

}