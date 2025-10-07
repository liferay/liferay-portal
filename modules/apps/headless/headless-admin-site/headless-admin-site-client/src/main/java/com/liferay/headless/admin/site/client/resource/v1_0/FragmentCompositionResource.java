/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.resource.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentComposition;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentCompositionSerDes;

import jakarta.annotation.Generated;

import java.net.URL;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public interface FragmentCompositionResource {

	public static Builder builder() {
		return new Builder();
	}

	public void deleteSiteFragmentComposition(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse deleteSiteFragmentCompositionHttpResponse(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode)
		throws Exception;

	public FragmentComposition getSiteFragmentComposition(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse getSiteFragmentCompositionHttpResponse(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode)
		throws Exception;

	public Page<FragmentComposition> getSiteFragmentCompositionsPage(
			String siteExternalReferenceCode, String search,
			String filterString, Pagination pagination, String sortString)
		throws Exception;

	public HttpInvoker.HttpResponse getSiteFragmentCompositionsPageHttpResponse(
			String siteExternalReferenceCode, String search,
			String filterString, Pagination pagination, String sortString)
		throws Exception;

	public FragmentComposition patchSiteFragmentComposition(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode,
			FragmentComposition fragmentComposition)
		throws Exception;

	public HttpInvoker.HttpResponse patchSiteFragmentCompositionHttpResponse(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode,
			FragmentComposition fragmentComposition)
		throws Exception;

	public FragmentComposition postSiteFragmentComposition(
			String siteExternalReferenceCode, Boolean saveInlineContent,
			Boolean saveMapping, FragmentComposition fragmentComposition)
		throws Exception;

	public HttpInvoker.HttpResponse postSiteFragmentCompositionHttpResponse(
			String siteExternalReferenceCode, Boolean saveInlineContent,
			Boolean saveMapping, FragmentComposition fragmentComposition)
		throws Exception;

	public void postSiteFragmentCompositionBatch(
			String siteExternalReferenceCode, Boolean saveInlineContent,
			Boolean saveMapping, String callbackURL, Object object)
		throws Exception;

	public HttpInvoker.HttpResponse
			postSiteFragmentCompositionBatchHttpResponse(
				String siteExternalReferenceCode, Boolean saveInlineContent,
				Boolean saveMapping, String callbackURL, Object object)
		throws Exception;

	public void postSiteFragmentCompositionsPageExportBatch(
			String siteExternalReferenceCode, String search,
			String filterString, String sortString, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public HttpInvoker.HttpResponse
			postSiteFragmentCompositionsPageExportBatchHttpResponse(
				String siteExternalReferenceCode, String search,
				String filterString, String sortString, String callbackURL,
				String contentType, String fieldNames)
		throws Exception;

	public FragmentComposition putSiteFragmentComposition(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode,
			FragmentComposition fragmentComposition)
		throws Exception;

	public HttpInvoker.HttpResponse putSiteFragmentCompositionHttpResponse(
			String siteExternalReferenceCode,
			String fragmentCompositionExternalReferenceCode,
			FragmentComposition fragmentComposition)
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

		public FragmentCompositionResource build() {
			return new FragmentCompositionResourceImpl(this);
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

	public static class FragmentCompositionResourceImpl
		implements FragmentCompositionResource {

		public void deleteSiteFragmentComposition(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteSiteFragmentCompositionHttpResponse(
					siteExternalReferenceCode,
					fragmentCompositionExternalReferenceCode);

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
				deleteSiteFragmentCompositionHttpResponse(
					String siteExternalReferenceCode,
					String fragmentCompositionExternalReferenceCode)
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
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/{fragmentCompositionExternalReferenceCode}");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);
			httpInvoker.path(
				"fragmentCompositionExternalReferenceCode",
				fragmentCompositionExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public FragmentComposition getSiteFragmentComposition(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getSiteFragmentCompositionHttpResponse(
					siteExternalReferenceCode,
					fragmentCompositionExternalReferenceCode);

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
				return FragmentCompositionSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getSiteFragmentCompositionHttpResponse(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode)
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
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/{fragmentCompositionExternalReferenceCode}");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);
			httpInvoker.path(
				"fragmentCompositionExternalReferenceCode",
				fragmentCompositionExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<FragmentComposition> getSiteFragmentCompositionsPage(
				String siteExternalReferenceCode, String search,
				String filterString, Pagination pagination, String sortString)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getSiteFragmentCompositionsPageHttpResponse(
					siteExternalReferenceCode, search, filterString, pagination,
					sortString);

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
				return Page.of(content, FragmentCompositionSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getSiteFragmentCompositionsPageHttpResponse(
					String siteExternalReferenceCode, String search,
					String filterString, Pagination pagination,
					String sortString)
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

			if (search != null) {
				httpInvoker.parameter("search", String.valueOf(search));
			}

			if (filterString != null) {
				httpInvoker.parameter("filter", filterString);
			}

			if (pagination != null) {
				httpInvoker.parameter(
					"page", String.valueOf(pagination.getPage()));
				httpInvoker.parameter(
					"pageSize", String.valueOf(pagination.getPageSize()));
			}

			if (sortString != null) {
				httpInvoker.parameter("sort", sortString);
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public FragmentComposition patchSiteFragmentComposition(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode,
				FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				patchSiteFragmentCompositionHttpResponse(
					siteExternalReferenceCode,
					fragmentCompositionExternalReferenceCode,
					fragmentComposition);

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
				return FragmentCompositionSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				patchSiteFragmentCompositionHttpResponse(
					String siteExternalReferenceCode,
					String fragmentCompositionExternalReferenceCode,
					FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				fragmentComposition.toString(), "application/json");

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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.PATCH);

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/{fragmentCompositionExternalReferenceCode}");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);
			httpInvoker.path(
				"fragmentCompositionExternalReferenceCode",
				fragmentCompositionExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public FragmentComposition postSiteFragmentComposition(
				String siteExternalReferenceCode, Boolean saveInlineContent,
				Boolean saveMapping, FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postSiteFragmentCompositionHttpResponse(
					siteExternalReferenceCode, saveInlineContent, saveMapping,
					fragmentComposition);

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
				return FragmentCompositionSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse postSiteFragmentCompositionHttpResponse(
				String siteExternalReferenceCode, Boolean saveInlineContent,
				Boolean saveMapping, FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				fragmentComposition.toString(), "application/json");

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

			if (saveInlineContent != null) {
				httpInvoker.parameter(
					"saveInlineContent", String.valueOf(saveInlineContent));
			}

			if (saveMapping != null) {
				httpInvoker.parameter(
					"saveMapping", String.valueOf(saveMapping));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void postSiteFragmentCompositionBatch(
				String siteExternalReferenceCode, Boolean saveInlineContent,
				Boolean saveMapping, String callbackURL, Object object)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postSiteFragmentCompositionBatchHttpResponse(
					siteExternalReferenceCode, saveInlineContent, saveMapping,
					callbackURL, object);

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
				postSiteFragmentCompositionBatchHttpResponse(
					String siteExternalReferenceCode, Boolean saveInlineContent,
					Boolean saveMapping, String callbackURL, Object object)
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

			if (saveInlineContent != null) {
				httpInvoker.parameter(
					"saveInlineContent", String.valueOf(saveInlineContent));
			}

			if (saveMapping != null) {
				httpInvoker.parameter(
					"saveMapping", String.valueOf(saveMapping));
			}

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/batch");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void postSiteFragmentCompositionsPageExportBatch(
				String siteExternalReferenceCode, String search,
				String filterString, String sortString, String callbackURL,
				String contentType, String fieldNames)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postSiteFragmentCompositionsPageExportBatchHttpResponse(
					siteExternalReferenceCode, search, filterString, sortString,
					callbackURL, contentType, fieldNames);

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
				postSiteFragmentCompositionsPageExportBatchHttpResponse(
					String siteExternalReferenceCode, String search,
					String filterString, String sortString, String callbackURL,
					String contentType, String fieldNames)
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

			if (search != null) {
				httpInvoker.parameter("search", String.valueOf(search));
			}

			if (filterString != null) {
				httpInvoker.parameter("filter", filterString);
			}

			if (sortString != null) {
				httpInvoker.parameter("sort", sortString);
			}

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
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/export-batch");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public FragmentComposition putSiteFragmentComposition(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode,
				FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putSiteFragmentCompositionHttpResponse(
					siteExternalReferenceCode,
					fragmentCompositionExternalReferenceCode,
					fragmentComposition);

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
				return FragmentCompositionSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse putSiteFragmentCompositionHttpResponse(
				String siteExternalReferenceCode,
				String fragmentCompositionExternalReferenceCode,
				FragmentComposition fragmentComposition)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				fragmentComposition.toString(), "application/json");

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
						"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/fragment-compositions/{fragmentCompositionExternalReferenceCode}");

			httpInvoker.path(
				"siteExternalReferenceCode", siteExternalReferenceCode);
			httpInvoker.path(
				"fragmentCompositionExternalReferenceCode",
				fragmentCompositionExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private FragmentCompositionResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			FragmentCompositionResource.class.getName());

		private Builder _builder;

	}

}