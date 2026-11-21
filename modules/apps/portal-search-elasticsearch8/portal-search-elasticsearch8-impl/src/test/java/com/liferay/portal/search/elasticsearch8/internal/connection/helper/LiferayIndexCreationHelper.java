/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection.helper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.index.MappingsHelper;
import com.liferay.portal.search.elasticsearch8.internal.index.constants.IndexSettingsConstants;
import com.liferay.portal.search.elasticsearch8.internal.settings.SettingsHelper;
import com.liferay.portal.search.elasticsearch8.internal.util.ResourceUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;

/**
 * @author André de Oliveira
 */
public class LiferayIndexCreationHelper implements IndexCreationHelper {

	public LiferayIndexCreationHelper(
		ElasticsearchClientResolver elasticsearchClientResolver,
		SearchEngineInformation searchEngineInformation) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
		_searchEngineInformation = searchEngineInformation;
	}

	@Override
	public void contribute(CreateIndexRequest.Builder builder) {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient();

		MappingsHelper mappingsHelper = new MappingsHelper(
			elasticsearchClient.indices(), null, new JSONFactoryImpl(),
			_elasticsearchClientResolver.getJsonpMapper(null), null,
			_searchEngineInformation);

		mappingsHelper.setDefaultOrOverrideMappings(builder);
	}

	@Override
	public void contributeIndexSettings(SettingsHelper settingsHelper) {
		settingsHelper.loadFromSource(
			ResourceUtil.getResourceAsString(
				getClass(), IndexSettingsConstants.INDEX_SETTINGS_FILE_NAME));
	}

	@Override
	public void whenIndexCreated(String indexName) {
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;
	private final SearchEngineInformation _searchEngineInformation;

}