/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Michael C. Han
 */
public class DeleteIndexRequestExecutor {

	public DeleteIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public DeleteIndexResponse execute(DeleteIndexRequest deleteIndexRequest) {
		org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
			elasticsearchDeleteIndexRequest = createDeleteIndexRequest(
				deleteIndexRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			elasticsearchDeleteIndexRequest, deleteIndexRequest);

		return new DeleteIndexResponse(acknowledgedResponse.isAcknowledged());
	}

	protected org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
		createDeleteIndexRequest(DeleteIndexRequest deleteIndexRequest) {

		org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
			elasticsearchDeleteIndexRequest =
				new org.elasticsearch.action.admin.indices.delete.
					DeleteIndexRequest(deleteIndexRequest.getIndexNames());

		IndicesOptions indicesOptions = IndicesOptionsTranslatorUtil.translate(
			deleteIndexRequest.getIndicesOptions());

		elasticsearchDeleteIndexRequest.indicesOptions(indicesOptions);

		return elasticsearchDeleteIndexRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
			elasticsearchDeleteIndexRequest,
		DeleteIndexRequest deleteIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				deleteIndexRequest.getConnectionId(),
				deleteIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.delete(
				elasticsearchDeleteIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}