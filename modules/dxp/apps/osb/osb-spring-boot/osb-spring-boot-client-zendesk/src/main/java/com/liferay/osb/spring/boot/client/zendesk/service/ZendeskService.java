/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.spring.boot.client.zendesk.service;

import com.liferay.osb.spring.boot.client.zendesk.model.ZendeskOrganization;
import com.liferay.osb.spring.boot.client.zendesk.model.ZendeskTicket;
import com.liferay.osb.spring.boot.client.zendesk.model.ZendeskUser;
import com.liferay.osb.spring.boot.client.zendesk.search.SearchHits;
import com.liferay.osb.spring.boot.client.zendesk.search.ZendeskTicketQuery;
import com.liferay.petra.string.StringPool;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Amos Fong
 */
@Component
public class ZendeskService {

	public void addAgentZendeskTicketComment(
			String htmlBody, long zendeskTicketId, long zendeskUserId)
		throws Exception {

		WebClient.create(
			_zendeskURL
		).put(
		).uri(
			"/api/v2/tickets/" + zendeskTicketId + ".json"
		).accept(
			MediaType.APPLICATION_JSON
		).contentType(
			MediaType.APPLICATION_JSON
		).header(
			HttpHeaders.AUTHORIZATION, _zendeskAuthorization
		).body(
			BodyInserters.fromValue(
				new JSONObject(
				).put(
					"ticket",
					new JSONObject(
					).put(
						"comment",
						new JSONObject(
						).put(
							"author_id", zendeskUserId
						).put(
							"html_body", htmlBody
						).put(
							"public", true
						)
					)
				).toString())
		).retrieve(
		).bodyToMono(
			String.class
		).block();
	}

	public void addEndUserZendeskTicketComment(
			String emailAddress, String htmlBody, long zendeskTicketId)
		throws Exception {

		WebClient.create(
			_zendeskURL
		).put(
		).uri(
			"/api/v2/requests/" + zendeskTicketId + ".json"
		).accept(
			MediaType.APPLICATION_JSON
		).contentType(
			MediaType.APPLICATION_JSON
		).header(
			HttpHeaders.AUTHORIZATION, _getAuthorization(emailAddress)
		).body(
			BodyInserters.fromValue(
				new JSONObject(
				).put(
					"request",
					new JSONObject(
					).put(
						"comment",
						new JSONObject(
						).put(
							"html_body", htmlBody
						)
					)
				).toString())
		).retrieve(
		).bodyToMono(
			String.class
		).block();
	}

	public ZendeskUser fetchZendeskUser(String emailAddress) throws Exception {
		JSONObject jsonObject = new JSONObject(
			WebClient.create(
				_zendeskURL
			).get(
			).uri(
				"/api/v2/users/search.json?query=" + emailAddress
			).accept(
				MediaType.APPLICATION_JSON
			).header(
				HttpHeaders.AUTHORIZATION, _zendeskAuthorization
			).retrieve(
			).bodyToMono(
				String.class
			).block());

		JSONArray jsonArray = jsonObject.getJSONArray("users");

		if (jsonArray.length() <= 0) {
			return null;
		}

		return new ZendeskUser(jsonArray.getJSONObject(0));
	}

	public ZendeskOrganization getZendeskOrganization(
			long zendeskOrganizationId)
		throws Exception {

		JSONObject jsonObject = new JSONObject(
			WebClient.create(
				_zendeskURL
			).get(
			).uri(
				"/api/v2/organizations/" + zendeskOrganizationId + ".json"
			).accept(
				MediaType.APPLICATION_JSON
			).header(
				HttpHeaders.AUTHORIZATION, _zendeskAuthorization
			).retrieve(
			).bodyToMono(
				String.class
			).block());

		return new ZendeskOrganization(
			jsonObject.getJSONObject("organization"));
	}

	public ZendeskTicket getZendeskTicket(long zendeskTicketId)
		throws Exception {

		JSONObject jsonObject = new JSONObject(
			WebClient.create(
				_zendeskURL
			).get(
			).uri(
				"/api/v2/tickets/" + zendeskTicketId + ".json"
			).accept(
				MediaType.APPLICATION_JSON
			).header(
				HttpHeaders.AUTHORIZATION, _zendeskAuthorization
			).retrieve(
			).bodyToMono(
				String.class
			).block());

		return new ZendeskTicket(
			jsonObject.getJSONObject("ticket"), _zendeskURL);
	}

	@PostConstruct
	public void init() throws Exception {
		_zendeskAuthorization = _getAuthorization(_zendeskAPIEmailAddress);
	}

	public SearchHits<ZendeskTicket> search(
			ZendeskTicketQuery zendeskTicketQuery)
		throws Exception {

		JSONObject jsonObject = new JSONObject(
			WebClient.create(
				_zendeskURL + "/api/v2/search.json"
			).get(
			).uri(
				uriBuilder -> {
					Map<String, String> parameters =
						zendeskTicketQuery.getParameters();

					for (Map.Entry<String, String> entry :
							parameters.entrySet()) {

						uriBuilder.queryParam(entry.getKey(), entry.getValue());
					}

					return uriBuilder.build();
				}
			).accept(
				MediaType.APPLICATION_JSON
			).header(
				HttpHeaders.AUTHORIZATION, _zendeskAuthorization
			).retrieve(
			).bodyToMono(
				String.class
			).block());

		return toSearchHits(jsonObject);
	}

	public void updateZendeskOrganization(
			long zendeskOrganizationId, String businessEvents)
		throws Exception {

		WebClient.create(
			_zendeskURL
		).put(
		).uri(
			"/api/v2/organizations/" + zendeskOrganizationId + ".json"
		).accept(
			MediaType.APPLICATION_JSON
		).contentType(
			MediaType.APPLICATION_JSON
		).header(
			HttpHeaders.AUTHORIZATION, _zendeskAuthorization
		).body(
			BodyInserters.fromValue(
				new JSONObject(
				).put(
					"organization",
					new JSONObject(
					).put(
						"organization_fields",
						new JSONObject(
						).put(
							"business_events", businessEvents
						)
					)
				).toString())
		).retrieve(
		).bodyToMono(
			String.class
		).block();
	}

	public void updateZendeskTicket(
			long zendeskTicketId, long zendeskOrganizationId, long requesterId,
			String status, Map<Long, String> customFields, Set<String> tags)
		throws Exception {

		WebClient.create(
			_zendeskURL
		).put(
		).uri(
			"/api/v2/tickets/" + zendeskTicketId + ".json"
		).accept(
			MediaType.APPLICATION_JSON
		).contentType(
			MediaType.APPLICATION_JSON
		).header(
			HttpHeaders.AUTHORIZATION, _zendeskAuthorization
		).body(
			BodyInserters.fromValue(
				new JSONObject(
				).put(
					"ticket",
					new JSONObject(
					).put(
						"custom_fields",
						_transformToCustomFieldsJSONArray(customFields)
					).put(
						"organization_id", zendeskOrganizationId
					).put(
						"requester_id", requesterId
					).put(
						"status", status
					).put(
						"tags", _transformToTagsJSONArray(tags)
					)
				).toString())
		).retrieve(
		).bodyToMono(
			String.class
		).block();
	}

	protected SearchHits<ZendeskTicket> toSearchHits(JSONObject jsonObject) {
		SearchHits<ZendeskTicket> searchHits = new SearchHits<>();

		searchHits.setCount(jsonObject.getInt("count"));

		String nextPageURL = jsonObject.optString("next_page");

		if (!nextPageURL.equals(StringPool.BLANK)) {
			String page = _getParameter(nextPageURL, "page");

			searchHits.setNextPage(Integer.valueOf(page));
		}

		List<ZendeskTicket> zendeskTickets = new ArrayList<>();

		JSONArray jsonArray = jsonObject.getJSONArray("results");

		for (int i = 0; i < jsonArray.length(); i++) {
			zendeskTickets.add(
				new ZendeskTicket(jsonArray.getJSONObject(i), _zendeskURL));
		}

		searchHits.setResults(zendeskTickets);

		return searchHits;
	}

	private String _getAuthorization(String emailAddress) throws Exception {
		Base64.Encoder encoder = Base64.getEncoder();

		String zendeskCredentials = emailAddress + "/token:" + _zendeskAPIToken;

		String encodedZendeskCredentials = new String(
			encoder.encode(zendeskCredentials.getBytes(StringPool.UTF8)),
			StringPool.UTF8);

		return "Basic " + encodedZendeskCredentials;
	}

	private String _getParameter(String url, String name) {
		int x = url.indexOf(StringPool.QUESTION);

		int y = url.indexOf(name + StringPool.EQUAL, x);

		if (y < 0) {
			return StringPool.BLANK;
		}

		int z = url.indexOf(StringPool.AMPERSAND, y);

		if (z < 0) {
			return url.substring(y + name.length() + 1);
		}

		return url.substring(y + name.length() + 1, z);
	}

	private JSONArray _transformToCustomFieldsJSONArray(
		Map<Long, String> customFields) {

		JSONArray jsonArray = new JSONArray();

		for (Map.Entry<Long, String> entry : customFields.entrySet()) {
			jsonArray.put(
				new JSONObject(
				).put(
					"id", entry.getKey()
				).put(
					"value", entry.getValue()
				));
		}

		return jsonArray;
	}

	private JSONArray _transformToTagsJSONArray(Set<String> tags) {
		JSONArray jsonArray = new JSONArray();

		for (String tag : tags) {
			jsonArray.put(tag);
		}

		return jsonArray;
	}

	@Value("${liferay.osb.spring.boot.client.zendesk.api.email.address}")
	private String _zendeskAPIEmailAddress;

	@Value("${liferay.osb.spring.boot.client.zendesk.api.token}")
	private String _zendeskAPIToken;

	private String _zendeskAuthorization;

	@Value("${liferay.osb.spring.boot.client.zendesk.url}")
	private String _zendeskURL;

}