/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.resource.v1_0;

import com.liferay.headless.asset.library.client.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.headless.asset.library.client.serdes.v1_0.ConnectedSiteSerDes;

import jakarta.annotation.Generated;

import java.net.URL;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public interface ConnectedSiteResource {

	public static Builder builder() {
		return new Builder();
	}

	public void
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode)
		throws Exception;

	public void deleteAssetLibraryConnectedSite(
			Long assetLibraryId, Long connectedSiteId)
		throws Exception;

	public HttpInvoker.HttpResponse deleteAssetLibraryConnectedSiteHttpResponse(
			Long assetLibraryId, Long connectedSiteId)
		throws Exception;

	public ConnectedSite
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode)
		throws Exception;

	public Page<ConnectedSite>
			getAssetLibraryByExternalReferenceCodeConnectedSitesPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryByExternalReferenceCodeConnectedSitesPageHttpResponse(
				String externalReferenceCode, Pagination pagination)
		throws Exception;

	public ConnectedSite getAssetLibraryConnectedSite(
			Long assetLibraryId, Long connectedSiteId)
		throws Exception;

	public HttpInvoker.HttpResponse getAssetLibraryConnectedSiteHttpResponse(
			Long assetLibraryId, Long connectedSiteId)
		throws Exception;

	public Page<ConnectedSite> getAssetLibraryConnectedSitesPage(
			Long assetLibraryId, Pagination pagination)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryConnectedSitesPageHttpResponse(
				Long assetLibraryId, Pagination pagination)
		throws Exception;

	public void postAssetLibraryConnectedSitesPageExportBatch(
			Long assetLibraryId, String callbackURL, String contentType,
			String fieldNames)
		throws Exception;

	public HttpInvoker.HttpResponse
			postAssetLibraryConnectedSitesPageExportBatchHttpResponse(
				Long assetLibraryId, String callbackURL, String contentType,
				String fieldNames)
		throws Exception;

	public ConnectedSite
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode,
				ConnectedSite connectedSite)
		throws Exception;

	public HttpInvoker.HttpResponse
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
				String assetLibraryExternalReferenceCode,
				String connectedSiteExternalReferenceCode,
				ConnectedSite connectedSite)
		throws Exception;

	public ConnectedSite putAssetLibraryConnectedSite(
			Long assetLibraryId, Long connectedSiteId,
			ConnectedSite connectedSite)
		throws Exception;

	public HttpInvoker.HttpResponse putAssetLibraryConnectedSiteHttpResponse(
			Long assetLibraryId, Long connectedSiteId,
			ConnectedSite connectedSite)
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

		public ConnectedSiteResource build() {
			return new ConnectedSiteResourceImpl(this);
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

	public static class ConnectedSiteResourceImpl
		implements ConnectedSiteResource {

		public void
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					assetLibraryExternalReferenceCode,
					connectedSiteExternalReferenceCode);

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
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode)
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
						"/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/{assetLibraryExternalReferenceCode}/connected-sites/by-external-reference-code/{connectedSiteExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"connectedSiteExternalReferenceCode",
				connectedSiteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void deleteAssetLibraryConnectedSite(
				Long assetLibraryId, Long connectedSiteId)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteAssetLibraryConnectedSiteHttpResponse(
					assetLibraryId, connectedSiteId);

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
				deleteAssetLibraryConnectedSiteHttpResponse(
					Long assetLibraryId, Long connectedSiteId)
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
						"/o/headless-asset-library/v1.0/asset-libraries/{assetLibraryId}/connected-sites/{connectedSiteId}");

			httpInvoker.path("assetLibraryId", assetLibraryId);
			httpInvoker.path("connectedSiteId", connectedSiteId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ConnectedSite
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					assetLibraryExternalReferenceCode,
					connectedSiteExternalReferenceCode);

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
				return ConnectedSiteSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode)
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
						"/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/{assetLibraryExternalReferenceCode}/connected-sites/by-external-reference-code/{connectedSiteExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"connectedSiteExternalReferenceCode",
				connectedSiteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<ConnectedSite>
				getAssetLibraryByExternalReferenceCodeConnectedSitesPage(
					String externalReferenceCode, Pagination pagination)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryByExternalReferenceCodeConnectedSitesPageHttpResponse(
					externalReferenceCode, pagination);

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
				return Page.of(content, ConnectedSiteSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryByExternalReferenceCodeConnectedSitesPageHttpResponse(
					String externalReferenceCode, Pagination pagination)
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

			if (pagination != null) {
				httpInvoker.parameter(
					"page", String.valueOf(pagination.getPage()));
				httpInvoker.parameter(
					"pageSize", String.valueOf(pagination.getPageSize()));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/{externalReferenceCode}/connected-sites");

			httpInvoker.path("externalReferenceCode", externalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ConnectedSite getAssetLibraryConnectedSite(
				Long assetLibraryId, Long connectedSiteId)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryConnectedSiteHttpResponse(
					assetLibraryId, connectedSiteId);

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
				return ConnectedSiteSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryConnectedSiteHttpResponse(
					Long assetLibraryId, Long connectedSiteId)
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
						"/o/headless-asset-library/v1.0/asset-libraries/{assetLibraryId}/connected-sites/{connectedSiteId}");

			httpInvoker.path("assetLibraryId", assetLibraryId);
			httpInvoker.path("connectedSiteId", connectedSiteId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<ConnectedSite> getAssetLibraryConnectedSitesPage(
				Long assetLibraryId, Pagination pagination)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryConnectedSitesPageHttpResponse(
					assetLibraryId, pagination);

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
				return Page.of(content, ConnectedSiteSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryConnectedSitesPageHttpResponse(
					Long assetLibraryId, Pagination pagination)
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

			if (pagination != null) {
				httpInvoker.parameter(
					"page", String.valueOf(pagination.getPage()));
				httpInvoker.parameter(
					"pageSize", String.valueOf(pagination.getPageSize()));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-asset-library/v1.0/asset-libraries/{assetLibraryId}/connected-sites");

			httpInvoker.path("assetLibraryId", assetLibraryId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void postAssetLibraryConnectedSitesPageExportBatch(
				Long assetLibraryId, String callbackURL, String contentType,
				String fieldNames)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postAssetLibraryConnectedSitesPageExportBatchHttpResponse(
					assetLibraryId, callbackURL, contentType, fieldNames);

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
				postAssetLibraryConnectedSitesPageExportBatchHttpResponse(
					Long assetLibraryId, String callbackURL, String contentType,
					String fieldNames)
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
						"/o/headless-asset-library/v1.0/asset-libraries/{assetLibraryId}/connected-sites/export-batch");

			httpInvoker.path("assetLibraryId", assetLibraryId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ConnectedSite
				putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode,
					ConnectedSite connectedSite)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					assetLibraryExternalReferenceCode,
					connectedSiteExternalReferenceCode, connectedSite);

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
				return ConnectedSiteSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCodeHttpResponse(
					String assetLibraryExternalReferenceCode,
					String connectedSiteExternalReferenceCode,
					ConnectedSite connectedSite)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(connectedSite.toString(), "application/json");

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
						"/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/{assetLibraryExternalReferenceCode}/connected-sites/by-external-reference-code/{connectedSiteExternalReferenceCode}");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path(
				"connectedSiteExternalReferenceCode",
				connectedSiteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ConnectedSite putAssetLibraryConnectedSite(
				Long assetLibraryId, Long connectedSiteId,
				ConnectedSite connectedSite)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putAssetLibraryConnectedSiteHttpResponse(
					assetLibraryId, connectedSiteId, connectedSite);

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
				return ConnectedSiteSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				putAssetLibraryConnectedSiteHttpResponse(
					Long assetLibraryId, Long connectedSiteId,
					ConnectedSite connectedSite)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(connectedSite.toString(), "application/json");

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
						"/o/headless-asset-library/v1.0/asset-libraries/{assetLibraryId}/connected-sites/{connectedSiteId}");

			httpInvoker.path("assetLibraryId", assetLibraryId);
			httpInvoker.path("connectedSiteId", connectedSiteId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private ConnectedSiteResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			ConnectedSiteResource.class.getName());

		private Builder _builder;

	}

}