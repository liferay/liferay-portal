/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.resource.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorResults;
import com.liferay.portal.search.rest.client.http.HttpInvoker;
import com.liferay.portal.search.rest.client.pagination.Page;
import com.liferay.portal.search.rest.client.problem.Problem;
import com.liferay.portal.search.rest.client.serdes.v1_0.SuggestionsContributorResultsSerDes;

import java.net.URL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Generated;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public interface SuggestionResource {

	public static Builder builder() {
		return new Builder();
	}

	public Page<SuggestionsContributorResults> postSuggestionsPage(
			String currentURL, String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, Long plid, String scope,
			String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations)
		throws Exception;

	public HttpInvoker.HttpResponse postSuggestionsPageHttpResponse(
			String currentURL, String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, Long plid, String scope,
			String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations)
		throws Exception;

	public static class Builder {

		public Builder authentication(String login, String password) {
			_login = login;
			_password = password;

			return this;
		}

		public Builder bearerToken(String token) {
			return header("Authorization", "Bearer " + token);
		}

		public SuggestionResource build() {
			return new SuggestionResourceImpl(this);
		}

		public Builder contextPath(String contextPath) {
			_contextPath = contextPath;

			return this;
		}

		public Builder endpoint(String address, String scheme) {
			String[] addressParts = address.split(":");

			String host = addressParts[0];

			int port = 443;

			if (addressParts.length > 1) {
				String portString = addressParts[1];

				try {
					port = Integer.parseInt(portString);
				}
				catch (NumberFormatException numberFormatException) {
					throw new IllegalArgumentException(
						"Unable to parse port from " + portString);
				}
			}

			return endpoint(host, port, scheme);
		}

		public Builder endpoint(String host, int port, String scheme) {
			_host = host;
			_port = port;
			_scheme = scheme;

			return this;
		}

		public Builder endpoint(URL url) {
			return endpoint(url.getHost(), url.getPort(), url.getProtocol());
		}

		public Builder header(String key, String value) {
			_headers.put(key, value);

			return this;
		}

		public Builder locale(Locale locale) {
			_locale = locale;

			return this;
		}

		public Builder parameter(String key, String value) {
			_parameters.put(key, value);

			return this;
		}

		public Builder parameters(String... parameters) {
			if ((parameters.length % 2) != 0) {
				throw new IllegalArgumentException(
					"Parameters length is not an even number");
			}

			for (int i = 0; i < parameters.length; i += 2) {
				String parameterName = String.valueOf(parameters[i]);
				String parameterValue = String.valueOf(parameters[i + 1]);

				_parameters.put(parameterName, parameterValue);
			}

			return this;
		}

		private Builder() {
		}

		private String _contextPath = "";
		private Map<String, String> _headers = new LinkedHashMap<>();
		private String _host = "localhost";
		private Locale _locale;
		private String _login;
		private String _password;
		private Map<String, String> _parameters = new LinkedHashMap<>();
		private int _port = 8080;
		private String _scheme = "http";

	}

	public static class SuggestionResourceImpl implements SuggestionResource {

		public Page<SuggestionsContributorResults> postSuggestionsPage(
				String currentURL, String destinationFriendlyURL, Long groupId,
				String keywordsParameterName, Long plid, String scope,
				String search,
				SuggestionsContributorConfiguration[]
					suggestionsContributorConfigurations)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postSuggestionsPageHttpResponse(
					currentURL, destinationFriendlyURL, groupId,
					keywordsParameterName, plid, scope, search,
					suggestionsContributorConfigurations);

			String content = httpResponse.getContent();

			if ((httpResponse.getStatusCode() / 100) != 2) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response content: " + content);
				_logger.log(
					Level.WARNING,
					"HTTP response message: " + httpResponse.getMessage());
				_logger.log(
					Level.WARNING,
					"HTTP response status code: " +
						httpResponse.getStatusCode());

				Problem.ProblemException problemException = null;

				if (Objects.equals(
						httpResponse.getContentType(), "application/json")) {

					problemException = new Problem.ProblemException(
						Problem.toDTO(content));
				}
				else {
					_logger.log(
						Level.WARNING,
						"Unable to process content type: " +
							httpResponse.getContentType());

					Problem problem = new Problem();

					problem.setStatus(
						String.valueOf(httpResponse.getStatusCode()));

					problemException = new Problem.ProblemException(problem);
				}

				throw problemException;
			}
			else {
				_logger.fine("HTTP response content: " + content);
				_logger.fine(
					"HTTP response message: " + httpResponse.getMessage());
				_logger.fine(
					"HTTP response status code: " +
						httpResponse.getStatusCode());
			}

			try {
				return Page.of(
					content, SuggestionsContributorResultsSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse postSuggestionsPageHttpResponse(
				String currentURL, String destinationFriendlyURL, Long groupId,
				String keywordsParameterName, Long plid, String scope,
				String search,
				SuggestionsContributorConfiguration[]
					suggestionsContributorConfigurations)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (SuggestionsContributorConfiguration
					suggestionsContributorConfigurationValue :
						suggestionsContributorConfigurations) {

				values.add(
					String.valueOf(suggestionsContributorConfigurationValue));
			}

			httpInvoker.body(values.toString(), "application/json");

			if (_builder._locale != null) {
				httpInvoker.header(
					"Accept-Language", _builder._locale.toLanguageTag());
			}

			for (Map.Entry<String, String> entry :
					_builder._headers.entrySet()) {

				httpInvoker.header(entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, String> entry :
					_builder._parameters.entrySet()) {

				httpInvoker.parameter(entry.getKey(), entry.getValue());
			}

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);

			if (currentURL != null) {
				httpInvoker.parameter("currentURL", String.valueOf(currentURL));
			}

			if (destinationFriendlyURL != null) {
				httpInvoker.parameter(
					"destinationFriendlyURL",
					String.valueOf(destinationFriendlyURL));
			}

			if (groupId != null) {
				httpInvoker.parameter("groupId", String.valueOf(groupId));
			}

			if (keywordsParameterName != null) {
				httpInvoker.parameter(
					"keywordsParameterName",
					String.valueOf(keywordsParameterName));
			}

			if (plid != null) {
				httpInvoker.parameter("plid", String.valueOf(plid));
			}

			if (scope != null) {
				httpInvoker.parameter("scope", String.valueOf(scope));
			}

			if (search != null) {
				httpInvoker.parameter("search", String.valueOf(search));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/search/v1.0/suggestions");

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private SuggestionResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			SuggestionResource.class.getName());

		private Builder _builder;

	}

}