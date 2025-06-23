/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Michael C. Han
 */
public class IndicesExistsIndexRequestExecutor {

	public IndicesExistsIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public IndicesExistsIndexResponse execute(
		IndicesExistsIndexRequest indicesExistsIndexRequest) {

		return new IndicesExistsIndexResponse(
			_indicesExists(
				createGetIndexRequest(indicesExistsIndexRequest),
				indicesExistsIndexRequest));
	}

	protected GetIndexRequest createGetIndexRequest(
		IndicesExistsIndexRequest indicesExistsIndexRequest) {

		GetIndexRequest getIndexRequest = new GetIndexRequest();

		getIndexRequest.indices(indicesExistsIndexRequest.getIndexNames());

		return getIndexRequest;
	}

	private boolean _indicesExists(
		GetIndexRequest getIndexRequest,
		IndicesExistsIndexRequest indicesExistsIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				indicesExistsIndexRequest.getConnectionId(),
				indicesExistsIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.exists(
				getIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}