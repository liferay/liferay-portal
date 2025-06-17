/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.search.ClearScrollRequest;
import com.liferay.portal.search.engine.adapter.search.ClearScrollResponse;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Gustavo Lima
 */
public class ClearScrollRequestExecutor {

	public ClearScrollRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public ClearScrollResponse execute(ClearScrollRequest clearScrollRequest) {
		org.elasticsearch.action.search.ClearScrollRequest
			elasticsearchClearScrollRequest = createClearScrollRequest(
				clearScrollRequest);

		org.elasticsearch.action.search.ClearScrollResponse
			clearScrollResponse = getClearScrollResponse(
				clearScrollRequest, elasticsearchClearScrollRequest);

		return new ClearScrollResponse(clearScrollResponse.getNumFreed());
	}

	protected org.elasticsearch.action.search.ClearScrollRequest
		createClearScrollRequest(ClearScrollRequest clearScrollRequest) {

		org.elasticsearch.action.search.ClearScrollRequest
			elasticsearchClearScrollRequest =
				new org.elasticsearch.action.search.ClearScrollRequest();

		elasticsearchClearScrollRequest.addScrollId(
			clearScrollRequest.getScrollId());

		return elasticsearchClearScrollRequest;
	}

	protected org.elasticsearch.action.search.ClearScrollResponse
		getClearScrollResponse(
			ClearScrollRequest clearScrollRequest,
			org.elasticsearch.action.search.ClearScrollRequest
				elasticsearchClearScrollRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				clearScrollRequest.getConnectionId(),
				clearScrollRequest.isPreferLocalCluster());

		try {
			return restHighLevelClient.clearScroll(
				elasticsearchClearScrollRequest, RequestOptions.DEFAULT);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception.getMessage(), exception);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}