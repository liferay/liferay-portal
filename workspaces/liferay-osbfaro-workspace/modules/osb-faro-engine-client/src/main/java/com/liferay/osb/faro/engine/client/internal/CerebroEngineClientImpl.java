/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.internal;

import com.liferay.osb.faro.engine.client.CerebroEngineClient;
import com.liferay.osb.faro.engine.client.http.client.AuthenticationClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.AuthorClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Marcellus Tavares
 */
@Component(service = CerebroEngineClient.class)
public class CerebroEngineClientImpl implements CerebroEngineClient {

	@Override
	public String getSiteMetrics(
			String channelId, FaroProject faroProject, String interval,
			int rangeKey)
		throws Exception {

		GraphQLRequest graphQLRequest = new GraphQLRequest();

		graphQLRequest.setOperationName("SiteMetrics");

		StringBundler sb = new StringBundler(10);

		sb.append("query SiteMetrics($channelId: String $interval: String! ");
		sb.append("$rangeKey: Int) {site(channelId: $channelId interval: ");
		sb.append("$interval rangeKey: $rangeKey) {bounceRateMetric {trend {");
		sb.append("percentage trendClassification} previousValue value} ");
		sb.append("sessionDurationMetric {trend {percentage ");
		sb.append("trendClassification} previousValue value} ");
		sb.append("sessionsPerVisitorMetric {trend {percentage ");
		sb.append("trendClassification} previousValue value} visitorsMetric {");
		sb.append("trend {percentage trendClassification} previousValue ");
		sb.append("value}}}");

		graphQLRequest.setQuery(sb.toString());

		graphQLRequest.setVariables(
			HashMapBuilder.<String, Object>put(
				"channelId", channelId
			).put(
				"interval", interval
			).put(
				"rangeKey", rangeKey
			).build());

		ResponseEntity<String> responseEntity = _getResponseEntity(
			faroProject, graphQLRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			responseEntity.getBody());

		JSONObject dataJSONObject = jsonObject.getJSONObject("data");

		JSONObject siteJSONObject = dataJSONObject.getJSONObject("site");

		return siteJSONObject.toString();
	}

	@Override
	public boolean isCustomEventsLimitReached(FaroProject faroProject)
		throws Exception {

		GraphQLRequest graphQLRequest = new GraphQLRequest();

		graphQLRequest.setQuery("{customEventLimitReached}");

		ResponseEntity<String> responseEntity = _getResponseEntity(
			faroProject, graphQLRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			responseEntity.getBody());

		JSONObject dataJSONObject = jsonObject.getJSONObject("data");

		return dataJSONObject.getBoolean("customEventLimitReached");
	}

	@Override
	public void updateTimeZone(FaroProject faroProject) throws Exception {
		_getResponseEntity(
			faroProject,
			_getTimeZoneGraphQLRequestHttpEntity(faroProject.getTimeZoneId()));
	}

	private ResponseEntity<String> _getResponseEntity(
			FaroProject faroProject, GraphQLRequest graphQLRequest)
		throws Exception {

		RestTemplate restTemplate = new RestTemplate();

		restTemplate.setInterceptors(
			Arrays.asList(
				new AuthenticationClientHttpRequestInterceptor(faroProject),
				new AuthorClientHttpRequestInterceptor()));

		return restTemplate.exchange(
			EngineServiceURLUtil.getBackendURL(faroProject, "/graphql"),
			HttpMethod.POST, new HttpEntity<>(graphQLRequest), String.class);
	}

	private GraphQLRequest _getTimeZoneGraphQLRequestHttpEntity(String value) {
		GraphQLRequest graphQLRequest = new GraphQLRequest();

		graphQLRequest.setOperationName("TimeZone");
		graphQLRequest.setQuery(
			"mutation TimeZone($value: String!) {timeZone(value: $value)}");
		graphQLRequest.setVariables(Collections.singletonMap("value", value));

		return graphQLRequest;
	}

	@Reference
	private JSONFactory _jsonFactory;

}