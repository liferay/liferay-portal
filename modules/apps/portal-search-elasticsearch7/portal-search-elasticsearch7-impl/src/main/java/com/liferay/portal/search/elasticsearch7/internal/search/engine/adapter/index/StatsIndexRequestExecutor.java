/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.StatsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.StatsIndexResponse;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EntityUtils;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Felipe Lorenz
 */
public class StatsIndexRequestExecutor {

	public StatsIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		JSONFactory jsonFactory) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
		_jsonFactory = jsonFactory;
	}

	public StatsIndexResponse execute(StatsIndexRequest statsIndexRequest) {
		Request request = getElasticsearchIndexRequest(statsIndexRequest);

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				statsIndexRequest.getConnectionId(),
				statsIndexRequest.isPreferLocalCluster());

		RestClient restClient = restHighLevelClient.getLowLevelClient();

		try {
			Response response = restClient.performRequest(request);

			JSONObject responseJSONObject = _jsonFactory.createJSONObject(
				EntityUtils.toString(response.getEntity()));

			JSONObject indicesJSONObject = responseJSONObject.getJSONObject(
				"indices");

			Map<String, Long> indexSizes = new HashMap<>();
			long sizeOfLargestIndex = 0;

			for (String indexName : indicesJSONObject.keySet()) {
				JSONObject indexJSONObject = indicesJSONObject.getJSONObject(
					indexName);

				JSONObject totalJSONObject = indexJSONObject.getJSONObject(
					"total");

				JSONObject storeJSONObject = totalJSONObject.getJSONObject(
					"store");

				long indexSize = storeJSONObject.getLong("size_in_bytes");

				if (indexSize > sizeOfLargestIndex) {
					sizeOfLargestIndex = indexSize;
				}

				indexSizes.put(indexName, indexSize);
			}

			return new StatsIndexResponse(indexSizes, sizeOfLargestIndex);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected Request getElasticsearchIndexRequest(
		StatsIndexRequest statsIndexRequest) {

		String indexes = "_all";

		if (ArrayUtil.isNotEmpty(statsIndexRequest.getIndexNames())) {
			indexes = StringUtil.merge(statsIndexRequest.getIndexNames());
		}

		String endpoint = "/" + indexes + "/_stats";

		return new Request("GET", endpoint);
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;
	private final JSONFactory _jsonFactory;

}