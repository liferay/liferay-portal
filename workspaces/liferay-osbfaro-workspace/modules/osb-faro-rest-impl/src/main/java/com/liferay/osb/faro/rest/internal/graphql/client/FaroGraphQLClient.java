/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.internal.graphql.model.GraphQLError;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(service = FaroGraphQLClient.class)
public class FaroGraphQLClient {

	public <T> T execute(
			Class<T> dataType, FaroProject faroProject, String operationName,
			Map<String, Object> variables)
		throws Exception {

		GraphQLRequest graphQLRequest = new GraphQLRequest();

		graphQLRequest.setOperationName(operationName);
		graphQLRequest.setQuery(_loadQuery(operationName));
		graphQLRequest.setVariables(
			(variables == null) ? Collections.emptyMap() : variables);

		@SuppressWarnings("unchecked")
		Map<String, Object> response = _contactsEngineClient.post(
			faroProject, Collections.emptyMap(), _GRAPHQL_PATH,
			Collections.emptyMap(), graphQLRequest, Map.class);

		if (response == null) {
			throw new GraphQLException(Collections.emptyList(), operationName);
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> errors =
			(List<Map<String, Object>>)response.get("errors");

		if ((errors != null) && !errors.isEmpty()) {
			List<GraphQLError> graphQLErrors = _objectMapper.convertValue(
				errors,
				new TypeReference<List<GraphQLError>>() {
				});

			throw new GraphQLException(graphQLErrors, operationName);
		}

		return _objectMapper.convertValue(response.get("data"), dataType);
	}

	private String _loadQuery(String operationName) throws Exception {
		String query = _queryCache.get(operationName);

		if (query != null) {
			return query;
		}

		String resourcePath = "/graphql/" + operationName + ".graphql";

		Class<FaroGraphQLClient> clazz = FaroGraphQLClient.class;

		try (InputStream inputStream = clazz.getResourceAsStream(
				resourcePath)) {

			if (inputStream == null) {
				throw new Exception(
					"Missing GraphQL query resource: " + resourcePath);
			}

			byte[] bytes = inputStream.readAllBytes();

			query = new String(bytes, StandardCharsets.UTF_8);

			if (query.isEmpty()) {
				throw new Exception(
					"Empty GraphQL query resource: " + resourcePath);
			}

			_queryCache.putIfAbsent(operationName, query);

			return query;
		}
	}

	private static final String _GRAPHQL_PATH = "/graphql";

	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final ConcurrentMap<String, String> _queryCache =
		new ConcurrentHashMap<>();

	@Reference
	private ContactsEngineClient _contactsEngineClient;

}