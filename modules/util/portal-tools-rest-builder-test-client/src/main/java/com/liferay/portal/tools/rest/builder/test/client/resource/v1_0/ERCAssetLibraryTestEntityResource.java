/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.permission.Permission;
import com.liferay.portal.tools.rest.builder.test.client.problem.Problem;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ERCAssetLibraryTestEntitySerDes;

import jakarta.annotation.Generated;

import java.net.URL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public interface ERCAssetLibraryTestEntityResource {

	public static Builder builder() {
		return new Builder();
	}

	public void deleteAssetLibraryERCAssetLibraryTestEntity(
			String assetLibraryExternalReferenceCode,
			String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			deleteAssetLibraryERCAssetLibraryTestEntityHttpResponse(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception;

	public Page<ERCAssetLibraryTestEntity>
			getAssetLibraryERCAssetLibraryTestEntitiesPage(
				String assetLibraryExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryERCAssetLibraryTestEntitiesPageHttpResponse(
				String assetLibraryExternalReferenceCode)
		throws Exception;

	public ERCAssetLibraryTestEntity getAssetLibraryERCAssetLibraryTestEntity(
			String assetLibraryExternalReferenceCode,
			String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception;

	public Page<Permission>
			getAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				String roleNames)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				String roleNames)
		throws Exception;

	public void postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch(
			String assetLibraryExternalReferenceCode, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public HttpInvoker.HttpResponse
			postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatchHttpResponse(
				String assetLibraryExternalReferenceCode, String callbackURL,
				String contentType, String fieldNames)
		throws Exception;

	public ERCAssetLibraryTestEntity postAssetLibraryERCAssetLibraryTestEntity(
			String assetLibraryExternalReferenceCode,
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception;

	public HttpInvoker.HttpResponse
			postAssetLibraryERCAssetLibraryTestEntityHttpResponse(
				String assetLibraryExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception;

	public void postAssetLibraryERCAssetLibraryTestEntityBatch(
			String assetLibraryExternalReferenceCode, String callbackURL,
			Object object)
		throws Exception;

	public HttpInvoker.HttpResponse
			postAssetLibraryERCAssetLibraryTestEntityBatchHttpResponse(
				String assetLibraryExternalReferenceCode, String callbackURL,
				Object object)
		throws Exception;

	public ERCAssetLibraryTestEntity putAssetLibraryERCAssetLibraryTestEntity(
			String assetLibraryExternalReferenceCode,
			String ercAssetLibraryTestEntityExternalReferenceCode,
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception;

	public HttpInvoker.HttpResponse
			putAssetLibraryERCAssetLibraryTestEntityHttpResponse(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception;

	public Page<Permission>
			putAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				Permission[] permissions)
		throws Exception;

	public HttpInvoker.HttpResponse
			putAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				Permission[] permissions)
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

		public ERCAssetLibraryTestEntityResource build() {
			return new ERCAssetLibraryTestEntityResourceImpl(this);
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

	public static class ERCAssetLibraryTestEntityResourceImpl
		implements ERCAssetLibraryTestEntityResource {

		public void deleteAssetLibraryERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntityExternalReferenceCode);

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
				return;
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				deleteAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode)
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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.DELETE);

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"ercAssetLibraryTestEntityExternalReferenceCode",
				ercAssetLibraryTestEntityExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<ERCAssetLibraryTestEntity>
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					String assetLibraryExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryERCAssetLibraryTestEntitiesPageHttpResponse(
					assetLibraryExternalReferenceCode);

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
				return Page.of(content, ERCAssetLibraryTestEntitySerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryERCAssetLibraryTestEntitiesPageHttpResponse(
					String assetLibraryExternalReferenceCode)
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

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ERCAssetLibraryTestEntity
				getAssetLibraryERCAssetLibraryTestEntity(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntityExternalReferenceCode);

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
				return ERCAssetLibraryTestEntitySerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode)
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

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"ercAssetLibraryTestEntityExternalReferenceCode",
				ercAssetLibraryTestEntityExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<Permission>
				getAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					String roleNames)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntityExternalReferenceCode, roleNames);

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
				return Page.of(content, Permission::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					String roleNames)
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

			if (roleNames != null) {
				httpInvoker.parameter("roleNames", String.valueOf(roleNames));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}/permissions");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"ercAssetLibraryTestEntityExternalReferenceCode",
				ercAssetLibraryTestEntityExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch(
				String assetLibraryExternalReferenceCode, String callbackURL,
				String contentType, String fieldNames)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatchHttpResponse(
					assetLibraryExternalReferenceCode, callbackURL, contentType,
					fieldNames);

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
		}

		public HttpInvoker.HttpResponse
				postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatchHttpResponse(
					String assetLibraryExternalReferenceCode,
					String callbackURL, String contentType, String fieldNames)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body("[]", "application/json");

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

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (contentType != null) {
				httpInvoker.parameter(
					"contentType", String.valueOf(contentType));
			}

			if (fieldNames != null) {
				httpInvoker.parameter("fieldNames", String.valueOf(fieldNames));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/export-batch");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ERCAssetLibraryTestEntity
				postAssetLibraryERCAssetLibraryTestEntity(
					String assetLibraryExternalReferenceCode,
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntity);

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
				return ERCAssetLibraryTestEntitySerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				postAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					String assetLibraryExternalReferenceCode,
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				ercAssetLibraryTestEntity.toString(), "application/json");

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

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void postAssetLibraryERCAssetLibraryTestEntityBatch(
				String assetLibraryExternalReferenceCode, String callbackURL,
				Object object)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postAssetLibraryERCAssetLibraryTestEntityBatchHttpResponse(
					assetLibraryExternalReferenceCode, callbackURL, object);

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
		}

		public HttpInvoker.HttpResponse
				postAssetLibraryERCAssetLibraryTestEntityBatchHttpResponse(
					String assetLibraryExternalReferenceCode,
					String callbackURL, Object object)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(object.toString(), "application/json");

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

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/batch");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ERCAssetLibraryTestEntity
				putAssetLibraryERCAssetLibraryTestEntity(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntityExternalReferenceCode,
					ercAssetLibraryTestEntity);

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
				return ERCAssetLibraryTestEntitySerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				putAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				ercAssetLibraryTestEntity.toString(), "application/json");

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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.PUT);

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"ercAssetLibraryTestEntityExternalReferenceCode",
				ercAssetLibraryTestEntityExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<Permission>
				putAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					Permission[] permissions)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					assetLibraryExternalReferenceCode,
					ercAssetLibraryTestEntityExternalReferenceCode,
					permissions);

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
				return Page.of(content, Permission::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				putAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					String assetLibraryExternalReferenceCode,
					String ercAssetLibraryTestEntityExternalReferenceCode,
					Permission[] permissions)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (Permission permissionValue : permissions) {
				values.add(String.valueOf(permissionValue));
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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.PUT);

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}/permissions");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"ercAssetLibraryTestEntityExternalReferenceCode",
				ercAssetLibraryTestEntityExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private ERCAssetLibraryTestEntityResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			ERCAssetLibraryTestEntityResource.class.getName());

		private Builder _builder;

	}

}