/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ExpandWildcard;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;

import com.liferay.portal.search.elasticsearch8.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.elasticsearch8.internal.connection.helper.LiferayIndexCreationHelper;
import com.liferay.portal.search.elasticsearch8.internal.settings.SettingsHelper;
import com.liferay.portal.search.engine.SearchEngineInformation;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class IndexCreator {

	public Index createIndex(IndexName indexName) {
		ElasticsearchIndicesClient elasticsearchIndicesClient =
			_getElasticsearchIndicesClient();

		String name = indexName.getName();

		deleteIndex(elasticsearchIndicesClient, name);

		CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder();

		builder.index(name);

		IndexCreationHelper indexCreationHelper = _getIndexCreationHelper();

		indexCreationHelper.contribute(builder);

		SettingsHelper settingsHelper = new SettingsHelper();

		settingsHelper.put("index.number_of_replicas", "0");
		settingsHelper.put("index.number_of_shards", "1");

		indexCreationHelper.contributeIndexSettings(settingsHelper);

		JsonpMapper jsonpMapper = _elasticsearchClientResolver.getJsonpMapper(
			null);

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		String settings = String.valueOf(
			settingsHelper.getSettingsJSONObject());

		try (InputStream inputStream = new ByteArrayInputStream(
				settings.getBytes(StandardCharsets.UTF_8))) {

			builder.settings(
				IndexSettings._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		try {
			elasticsearchIndicesClient.create(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		indexCreationHelper.whenIndexCreated(name);

		return new Index(indexName);
	}

	public void deleteIndex(IndexName indexName) {
		deleteIndex(_getElasticsearchIndicesClient(), indexName.getName());
	}

	protected void deleteIndex(
		ElasticsearchIndicesClient elasticsearchIndicesClient, String name) {

		try {
			BooleanResponse booleanResponse = elasticsearchIndicesClient.exists(
				ExistsRequest.of(existRequest -> existRequest.index(name)));

			if (booleanResponse.value()) {
				elasticsearchIndicesClient.delete(
					DeleteIndexRequest.of(
						deleteIndexRequest -> deleteIndexRequest.allowNoIndices(
							true
						).expandWildcards(
							ExpandWildcard.Open
						).index(
							name
						)));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	protected void setIndexCreationHelper(
		IndexCreationHelper indexCreationHelper) {

		_indexCreationHelper = indexCreationHelper;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	protected void setSearchEngineInformation(
		SearchEngineInformation searchEngineInformation) {

		_searchEngineInformation = searchEngineInformation;
	}

	private final ElasticsearchIndicesClient _getElasticsearchIndicesClient() {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient();

		return elasticsearchClient.indices();
	}

	private IndexCreationHelper _getIndexCreationHelper() {
		if (!_liferayMappingsAddedToIndex) {
			if (_indexCreationHelper != null) {
				return _indexCreationHelper;
			}

			return Mockito.mock(IndexCreationHelper.class);
		}

		LiferayIndexCreationHelper liferayIndexCreationHelper =
			new LiferayIndexCreationHelper(
				_elasticsearchClientResolver, _searchEngineInformation);

		if (_indexCreationHelper == null) {
			return liferayIndexCreationHelper;
		}

		return new IndexCreationHelper() {

			@Override
			public void contribute(
				CreateIndexRequest.Builder createIndexRequestBuilder) {

				_indexCreationHelper.contribute(createIndexRequestBuilder);

				liferayIndexCreationHelper.contribute(
					createIndexRequestBuilder);
			}

			@Override
			public void contributeIndexSettings(SettingsHelper settingsHelper) {
				_indexCreationHelper.contributeIndexSettings(settingsHelper);

				liferayIndexCreationHelper.contributeIndexSettings(
					settingsHelper);
			}

			@Override
			public void whenIndexCreated(String indexName) {
				_indexCreationHelper.whenIndexCreated(indexName);

				liferayIndexCreationHelper.whenIndexCreated(indexName);
			}

		};
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private IndexCreationHelper _indexCreationHelper;
	private boolean _liferayMappingsAddedToIndex;
	private SearchEngineInformation _searchEngineInformation;

}