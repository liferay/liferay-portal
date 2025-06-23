/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.helper.SearchLogHelperUtil;
import com.liferay.portal.search.elasticsearch7.internal.util.ClassLoaderUtil;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;

import java.io.IOException;

import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author Michael C. Han
 */
public class CreateIndexRequestExecutor {

	public CreateIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public CreateIndexResponse execute(CreateIndexRequest createIndexRequest) {
		org.elasticsearch.client.indices.CreateIndexRequest
			elasticsearchCreateIndexRequest = createCreateIndexRequest(
				createIndexRequest);

		org.elasticsearch.client.indices.CreateIndexResponse
			elasticsearchCreateIndexResponse = _getCreateIndexResponse(
				elasticsearchCreateIndexRequest, createIndexRequest);

		SearchLogHelperUtil.logActionResponse(
			_log, elasticsearchCreateIndexResponse);

		return new CreateIndexResponse(
			elasticsearchCreateIndexResponse.isAcknowledged(),
			elasticsearchCreateIndexResponse.index());
	}

	protected org.elasticsearch.client.indices.CreateIndexRequest
		createCreateIndexRequest(CreateIndexRequest createIndexRequest) {

		org.elasticsearch.client.indices.CreateIndexRequest
			elasticsearchCreateIndexRequest =
				new org.elasticsearch.client.indices.CreateIndexRequest(
					createIndexRequest.getIndexName());

		if (createIndexRequest.getMappings() != null) {
			ClassLoaderUtil.getWithContextClassLoader(
				() -> elasticsearchCreateIndexRequest.mapping(
					createIndexRequest.getMappings(), XContentType.JSON),
				getClass());
		}

		if (createIndexRequest.getSettings() != null) {
			ClassLoaderUtil.getWithContextClassLoader(
				() -> elasticsearchCreateIndexRequest.settings(
					createIndexRequest.getSettings(), XContentType.JSON),
				getClass());
		}

		if (createIndexRequest.getSource() != null) {
			ClassLoaderUtil.getWithContextClassLoader(
				() -> elasticsearchCreateIndexRequest.source(
					createIndexRequest.getSource(), XContentType.JSON),
				getClass());
		}

		return elasticsearchCreateIndexRequest;
	}

	private org.elasticsearch.client.indices.CreateIndexResponse
		_getCreateIndexResponse(
			org.elasticsearch.client.indices.CreateIndexRequest
				elasticsearchCreateIndexRequest,
			CreateIndexRequest createIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				createIndexRequest.getConnectionId(),
				createIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.create(
				elasticsearchCreateIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreateIndexRequestExecutor.class);

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}