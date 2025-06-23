/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author Dylan Rebelak
 */
public class PutMappingIndexRequestExecutor {

	public PutMappingIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public PutMappingIndexResponse execute(
		PutMappingIndexRequest putMappingIndexRequest) {

		PutMappingRequest putMappingRequest = createPutMappingRequest(
			putMappingIndexRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			putMappingRequest, putMappingIndexRequest);

		return new PutMappingIndexResponse(
			acknowledgedResponse.isAcknowledged());
	}

	protected PutMappingRequest createPutMappingRequest(
		PutMappingIndexRequest putMappingIndexRequest) {

		PutMappingRequest putMappingRequest = new PutMappingRequest(
			putMappingIndexRequest.getIndexNames());

		putMappingRequest.source(
			putMappingIndexRequest.getMapping(), XContentType.JSON);

		return putMappingRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		PutMappingRequest putMappingRequest,
		PutMappingIndexRequest putMappingIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				putMappingIndexRequest.getConnectionId(),
				putMappingIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.putMapping(
				putMappingRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}