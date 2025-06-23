/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;

import java.io.IOException;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;

/**
 * @author Michael C. Han
 */
public class CloseIndexRequestExecutor {

	public CloseIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public CloseIndexResponse execute(CloseIndexRequest closeIndexRequest) {
		org.elasticsearch.client.indices.CloseIndexRequest
			elasticsearchCloseIndexRequest = createCloseIndexRequest(
				closeIndexRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			elasticsearchCloseIndexRequest, closeIndexRequest);

		return new CloseIndexResponse(acknowledgedResponse.isAcknowledged());
	}

	protected org.elasticsearch.client.indices.CloseIndexRequest
		createCloseIndexRequest(CloseIndexRequest closeIndexRequest) {

		org.elasticsearch.client.indices.CloseIndexRequest
			elasticsearchCloseIndexRequest =
				new org.elasticsearch.client.indices.CloseIndexRequest(
					closeIndexRequest.getIndexNames());

		IndicesOptions indicesOptions = closeIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			elasticsearchCloseIndexRequest.indicesOptions(
				IndicesOptionsTranslatorUtil.translate(indicesOptions));
		}

		if (closeIndexRequest.getTimeout() > 0) {
			TimeValue timeValue = TimeValue.timeValueMillis(
				closeIndexRequest.getTimeout());

			elasticsearchCloseIndexRequest.setMasterTimeout(timeValue);
			elasticsearchCloseIndexRequest.setTimeout(timeValue);
		}

		return elasticsearchCloseIndexRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		org.elasticsearch.client.indices.CloseIndexRequest
			elasticsearchCloseIndexRequest,
		CloseIndexRequest closeIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				closeIndexRequest.getConnectionId(),
				closeIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.close(
				elasticsearchCloseIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}