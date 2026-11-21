/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.opensearch2.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.opensearch2.internal.connection.helper.LiferayIndexCreationHelper;
import com.liferay.portal.search.opensearch2.internal.settings.SettingsHelper;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import org.mockito.Mockito;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.transport.endpoints.BooleanResponse;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class IndexCreator {

	public Index createIndex(IndexName indexName) {
		OpenSearchIndicesClient openSearchIndicesClient = _getIndicesClient();

		String name = indexName.getName();

		deleteIndex(name, openSearchIndicesClient);

		CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder();

		builder.index(name);

		IndexCreationHelper indexCreationHelper = _getIndexCreationHelper();

		indexCreationHelper.contribute(builder);

		SettingsHelper settingsHelper = new SettingsHelper();

		settingsHelper.put("index.number_of_replicas", "0");
		settingsHelper.put("index.number_of_shards", "1");

		indexCreationHelper.contributeIndexSettings(settingsHelper);

		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
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
			openSearchIndicesClient.create(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		indexCreationHelper.whenIndexCreated(name);

		return new Index(indexName);
	}

	public void deleteIndex(IndexName indexName) {
		deleteIndex(indexName.getName(), _getIndicesClient());
	}

	protected void deleteIndex(
		String name, OpenSearchIndicesClient openSearchIndicesClient) {

		try {
			BooleanResponse booleanResponse = openSearchIndicesClient.exists(
				ExistsRequest.of(existRequest -> existRequest.index(name)));

			if (booleanResponse.value()) {
				openSearchIndicesClient.delete(
					DeleteIndexRequest.of(
						deleteIndexRequest -> deleteIndexRequest.index(name)));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected void setIndexCreationHelper(
		IndexCreationHelper indexCreationHelper) {

		_indexCreationHelper = indexCreationHelper;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	protected void setOpenSearchConnectionManager(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	protected void setSearchEngineInformation(
		SearchEngineInformation searchEngineInformation) {

		_searchEngineInformation = searchEngineInformation;
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
				_openSearchConnectionManager, _searchEngineInformation);

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

	private final OpenSearchIndicesClient _getIndicesClient() {
		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		return openSearchClient.indices();
	}

	private IndexCreationHelper _indexCreationHelper;
	private boolean _liferayMappingsAddedToIndex;
	private OpenSearchConnectionManager _openSearchConnectionManager;
	private SearchEngineInformation _searchEngineInformation;

}