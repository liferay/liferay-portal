/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;

import java.io.IOException;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;

/**
 * @author Michael C. Han
 */
public class OpenIndexRequestExecutor {

	public OpenIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public OpenIndexResponse execute(OpenIndexRequest openIndexRequest) {
		org.elasticsearch.action.admin.indices.open.OpenIndexRequest
			elasticsearchOpenIndexRequest = createOpenIndexRequest(
				openIndexRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			elasticsearchOpenIndexRequest, openIndexRequest);

		return new OpenIndexResponse(acknowledgedResponse.isAcknowledged());
	}

	protected org.elasticsearch.action.admin.indices.open.OpenIndexRequest
		createOpenIndexRequest(OpenIndexRequest openIndexRequest) {

		org.elasticsearch.action.admin.indices.open.OpenIndexRequest
			elasticsearchOpenIndexRequest =
				new org.elasticsearch.action.admin.indices.open.
					OpenIndexRequest();

		elasticsearchOpenIndexRequest.indices(openIndexRequest.getIndexNames());

		IndicesOptions indicesOptions = openIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			elasticsearchOpenIndexRequest.indicesOptions(
				IndicesOptionsTranslatorUtil.translate(indicesOptions));
		}

		if (openIndexRequest.getTimeout() > 0) {
			TimeValue timeValue = TimeValue.timeValueMillis(
				openIndexRequest.getTimeout());

			elasticsearchOpenIndexRequest.masterNodeTimeout(timeValue);
			elasticsearchOpenIndexRequest.timeout(timeValue);
		}

		if (openIndexRequest.getWaitForActiveShards() > 0) {
			elasticsearchOpenIndexRequest.waitForActiveShards(
				openIndexRequest.getWaitForActiveShards());
		}

		return elasticsearchOpenIndexRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		org.elasticsearch.action.admin.indices.open.OpenIndexRequest
			elasticsearchOpenIndexRequest,
		OpenIndexRequest openIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				openIndexRequest.getConnectionId(),
				openIndexRequest.isPreferLocalCluster());

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.open(
				elasticsearchOpenIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}