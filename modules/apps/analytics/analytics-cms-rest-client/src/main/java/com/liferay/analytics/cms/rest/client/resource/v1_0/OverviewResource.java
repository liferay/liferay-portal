/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.resource.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Overview;
import com.liferay.analytics.cms.rest.client.http.HttpInvoker;
import com.liferay.analytics.cms.rest.client.problem.Problem;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.OverviewSerDes;

import jakarta.annotation.Generated;

import java.net.URL;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public interface OverviewResource {

	public static Builder builder() {
		return new Builder();
	}

	public Overview getContentOverview(
			Long depotEntryId, String languageId, String rangeEnd,
			Integer rangeKey, String rangeStart)
		throws Exception;

	public HttpInvoker.HttpResponse getContentOverviewHttpResponse(
			Long depotEntryId, String languageId, String rangeEnd,
			Integer rangeKey, String rangeStart)
		throws Exception;

	public Overview getFileOverview(
			Long depotEntryId, String languageId, String rangeEnd,
			Integer rangeKey, String rangeStart)
		throws Exception;

	public HttpInvoker.HttpResponse getFileOverviewHttpResponse(
			Long depotEntryId, String languageId, String rangeEnd,
			Integer rangeKey, String rangeStart)
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

		public OverviewResource build() {
			return new OverviewResourceImpl(this);
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

	public static class OverviewResourceImpl implements OverviewResource {

		public Overview getContentOverview(
				Long depotEntryId, String languageId, String rangeEnd,
				Integer rangeKey, String rangeStart)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getContentOverviewHttpResponse(
					depotEntryId, languageId, rangeEnd, rangeKey, rangeStart);

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
				return OverviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getContentOverviewHttpResponse(
				Long depotEntryId, String languageId, String rangeEnd,
				Integer rangeKey, String rangeStart)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);

			if (depotEntryId != null) {
				httpInvoker.parameter(
					"depotEntryId", String.valueOf(depotEntryId));
			}

			if (languageId != null) {
				httpInvoker.parameter("languageId", String.valueOf(languageId));
			}

			if (rangeEnd != null) {
				httpInvoker.parameter("rangeEnd", String.valueOf(rangeEnd));
			}

			if (rangeKey != null) {
				httpInvoker.parameter("rangeKey", String.valueOf(rangeKey));
			}

			if (rangeStart != null) {
				httpInvoker.parameter("rangeStart", String.valueOf(rangeStart));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/analytics-cms-rest/v1.0/content-overview");

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Overview getFileOverview(
				Long depotEntryId, String languageId, String rangeEnd,
				Integer rangeKey, String rangeStart)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse = getFileOverviewHttpResponse(
				depotEntryId, languageId, rangeEnd, rangeKey, rangeStart);

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
				return OverviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getFileOverviewHttpResponse(
				Long depotEntryId, String languageId, String rangeEnd,
				Integer rangeKey, String rangeStart)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);

			if (depotEntryId != null) {
				httpInvoker.parameter(
					"depotEntryId", String.valueOf(depotEntryId));
			}

			if (languageId != null) {
				httpInvoker.parameter("languageId", String.valueOf(languageId));
			}

			if (rangeEnd != null) {
				httpInvoker.parameter("rangeEnd", String.valueOf(rangeEnd));
			}

			if (rangeKey != null) {
				httpInvoker.parameter("rangeKey", String.valueOf(rangeKey));
			}

			if (rangeStart != null) {
				httpInvoker.parameter("rangeStart", String.valueOf(rangeStart));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/analytics-cms-rest/v1.0/file-overview");

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private OverviewResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			OverviewResource.class.getName());

		private Builder _builder;

	}

}