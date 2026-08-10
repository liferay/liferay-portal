/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.resource.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.problem.Problem;
import com.liferay.exportimport.rest.client.serdes.v1_0.ExportPreviewSerDes;

import jakarta.annotation.Generated;

import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public interface ExportPreviewResource {

	public static Builder builder() {
		return new Builder();
	}

	public ExportPreview getAssetLibraryExportPreview(
			String assetLibraryExternalReferenceCode, java.util.Date endDate,
			java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse getAssetLibraryExportPreviewHttpResponse(
			String assetLibraryExternalReferenceCode, java.util.Date endDate,
			java.util.Date startDate)
		throws Exception;

	public ExportPreview getAssetLibraryPortletExportPreview(
			String assetLibraryExternalReferenceCode, String portletId,
			java.util.Date endDate, Long plid, java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse
			getAssetLibraryPortletExportPreviewHttpResponse(
				String assetLibraryExternalReferenceCode, String portletId,
				java.util.Date endDate, Long plid, java.util.Date startDate)
		throws Exception;

	public ExportPreview getExportPreview(
			java.util.Date endDate, java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse getExportPreviewHttpResponse(
			java.util.Date endDate, java.util.Date startDate)
		throws Exception;

	public ExportPreview getPortletExportPreview(
			String portletId, java.util.Date endDate, Long plid,
			java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse getPortletExportPreviewHttpResponse(
			String portletId, java.util.Date endDate, Long plid,
			java.util.Date startDate)
		throws Exception;

	public ExportPreview getSiteExportPreview(
			String siteExternalReferenceCode, java.util.Date endDate,
			java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse getSiteExportPreviewHttpResponse(
			String siteExternalReferenceCode, java.util.Date endDate,
			java.util.Date startDate)
		throws Exception;

	public ExportPreview getSitePortletExportPreview(
			String siteExternalReferenceCode, String portletId,
			java.util.Date endDate, Long plid, java.util.Date startDate)
		throws Exception;

	public HttpInvoker.HttpResponse getSitePortletExportPreviewHttpResponse(
			String siteExternalReferenceCode, String portletId,
			java.util.Date endDate, Long plid, java.util.Date startDate)
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

		public ExportPreviewResource build() {
			return new ExportPreviewResourceImpl(this);
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

	public static class ExportPreviewResourceImpl
		implements ExportPreviewResource {

		public ExportPreview getAssetLibraryExportPreview(
				String assetLibraryExternalReferenceCode,
				java.util.Date endDate, java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryExportPreviewHttpResponse(
					assetLibraryExternalReferenceCode, endDate, startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryExportPreviewHttpResponse(
					String assetLibraryExternalReferenceCode,
					java.util.Date endDate, java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/export-preview");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ExportPreview getAssetLibraryPortletExportPreview(
				String assetLibraryExternalReferenceCode, String portletId,
				java.util.Date endDate, Long plid, java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getAssetLibraryPortletExportPreviewHttpResponse(
					assetLibraryExternalReferenceCode, portletId, endDate, plid,
					startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getAssetLibraryPortletExportPreviewHttpResponse(
					String assetLibraryExternalReferenceCode, String portletId,
					java.util.Date endDate, Long plid, java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (plid != null) {
				httpInvoker.parameter("plid", String.valueOf(plid));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/portlets/{portletId}/export-preview");

			httpInvoker.path(
				"assetLibraryExternalReferenceCode",
				assetLibraryExternalReferenceCode);
			httpInvoker.path("portletId", portletId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ExportPreview getExportPreview(
				java.util.Date endDate, java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getExportPreviewHttpResponse(endDate, startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getExportPreviewHttpResponse(
				java.util.Date endDate, java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/export-preview");

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ExportPreview getPortletExportPreview(
				String portletId, java.util.Date endDate, Long plid,
				java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getPortletExportPreviewHttpResponse(
					portletId, endDate, plid, startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getPortletExportPreviewHttpResponse(
				String portletId, java.util.Date endDate, Long plid,
				java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (plid != null) {
				httpInvoker.parameter("plid", String.valueOf(plid));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/portlets/{portletId}/export-preview");

			httpInvoker.path("portletId", portletId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ExportPreview getSiteExportPreview(
				String siteExternalReferenceCode, java.util.Date endDate,
				java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getSiteExportPreviewHttpResponse(
					siteExternalReferenceCode, endDate, startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getSiteExportPreviewHttpResponse(
				String siteExternalReferenceCode, java.util.Date endDate,
				java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/sites/{siteExternalReferenceCode}/export-preview");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ExportPreview getSitePortletExportPreview(
				String siteExternalReferenceCode, String portletId,
				java.util.Date endDate, Long plid, java.util.Date startDate)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getSitePortletExportPreviewHttpResponse(
					siteExternalReferenceCode, portletId, endDate, plid,
					startDate);

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
				return ExportPreviewSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getSitePortletExportPreviewHttpResponse(
				String siteExternalReferenceCode, String portletId,
				java.util.Date endDate, Long plid, java.util.Date startDate)
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

			DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssXX");

			if (endDate != null) {
				httpInvoker.parameter(
					"endDate", liferayToJSONDateFormat.format(endDate));
			}

			if (plid != null) {
				httpInvoker.parameter("plid", String.valueOf(plid));
			}

			if (startDate != null) {
				httpInvoker.parameter(
					"startDate", liferayToJSONDateFormat.format(startDate));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/export-import/v1.0/sites/{siteExternalReferenceCode}/portlets/{portletId}/export-preview");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);
			httpInvoker.path("portletId", portletId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private ExportPreviewResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			ExportPreviewResource.class.getName());

		private Builder _builder;

	}

}
// LIFERAY-REST-BUILDER-HASH:-2119587010