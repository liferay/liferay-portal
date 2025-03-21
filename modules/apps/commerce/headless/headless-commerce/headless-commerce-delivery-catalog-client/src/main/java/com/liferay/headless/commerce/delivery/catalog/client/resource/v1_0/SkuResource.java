/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.DDMOption;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.problem.Problem;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.SkuSerDes;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public interface SkuResource {

	public static Builder builder() {
		return new Builder();
	}

	public Page<Sku>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				String currencyCode, Pagination pagination)
		throws Exception;

	public HttpInvoker.HttpResponse
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPageHttpResponse(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				String currencyCode, Pagination pagination)
		throws Exception;

	public Sku
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				java.math.BigDecimal quantity, DDMOption[] ddmOptions)
		throws Exception;

	public HttpInvoker.HttpResponse
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuHttpResponse(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				java.math.BigDecimal quantity, DDMOption[] ddmOptions)
		throws Exception;

	public Sku
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String skuExternalReferenceCode, Long accountId,
				String currencyCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCodeHttpResponse(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String skuExternalReferenceCode, Long accountId,
				String currencyCode)
		throws Exception;

	public Sku
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				String currencyCode, java.math.BigDecimal quantity,
				String skuUnitOfMeasureKey, SkuOption[] skuOptions)
		throws Exception;

	public HttpInvoker.HttpResponse
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOptionHttpResponse(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				String currencyCode, java.math.BigDecimal quantity,
				String skuUnitOfMeasureKey, SkuOption[] skuOptions)
		throws Exception;

	public Page<Sku> getChannelProductSkusPage(
			Long channelId, Long productId, Long accountId, String currencyCode,
			Pagination pagination)
		throws Exception;

	public HttpInvoker.HttpResponse getChannelProductSkusPageHttpResponse(
			Long channelId, Long productId, Long accountId, String currencyCode,
			Pagination pagination)
		throws Exception;

	public Sku postChannelProductSku(
			Long channelId, Long productId, Long accountId,
			java.math.BigDecimal quantity, DDMOption[] ddmOptions)
		throws Exception;

	public HttpInvoker.HttpResponse postChannelProductSkuHttpResponse(
			Long channelId, Long productId, Long accountId,
			java.math.BigDecimal quantity, DDMOption[] ddmOptions)
		throws Exception;

	public Sku postChannelProductSkuBySkuOption(
			Long channelId, Long productId, Long accountId, String currencyCode,
			java.math.BigDecimal quantity, String skuUnitOfMeasureKey,
			SkuOption[] skuOptions)
		throws Exception;

	public HttpInvoker.HttpResponse
			postChannelProductSkuBySkuOptionHttpResponse(
				Long channelId, Long productId, Long accountId,
				String currencyCode, java.math.BigDecimal quantity,
				String skuUnitOfMeasureKey, SkuOption[] skuOptions)
		throws Exception;

	public Sku getChannelProductSku(
			Long channelId, Long productId, Long skuId, Long accountId,
			String currencyCode)
		throws Exception;

	public HttpInvoker.HttpResponse getChannelProductSkuHttpResponse(
			Long channelId, Long productId, Long skuId, Long accountId,
			String currencyCode)
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

		public SkuResource build() {
			return new SkuResourceImpl(this);
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

	public static class SkuResourceImpl implements SkuResource {

		public Page<Sku>
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					String currencyCode, Pagination pagination)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPageHttpResponse(
					channelExternalReferenceCode, productExternalReferenceCode,
					accountId, currencyCode, pagination);

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
				return Page.of(content, SkuSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPageHttpResponse(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					String currencyCode, Pagination pagination)
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			if (pagination != null) {
				httpInvoker.parameter(
					"page", String.valueOf(pagination.getPage()));
				httpInvoker.parameter(
					"pageSize", String.valueOf(pagination.getPageSize()));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus");

			httpInvoker.path(
				"channelExternalReferenceCode", channelExternalReferenceCode);
			httpInvoker.path(
				"productExternalReferenceCode", productExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					java.math.BigDecimal quantity, DDMOption[] ddmOptions)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuHttpResponse(
					channelExternalReferenceCode, productExternalReferenceCode,
					accountId, quantity, ddmOptions);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuHttpResponse(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					java.math.BigDecimal quantity, DDMOption[] ddmOptions)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (DDMOption ddmOptionValue : ddmOptions) {
				values.add(String.valueOf(ddmOptionValue));
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (quantity != null) {
				httpInvoker.parameter("quantity", String.valueOf(quantity));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus");

			httpInvoker.path(
				"channelExternalReferenceCode", channelExternalReferenceCode);
			httpInvoker.path(
				"productExternalReferenceCode", productExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
					String channelExternalReferenceCode,
					String productExternalReferenceCode,
					String skuExternalReferenceCode, Long accountId,
					String currencyCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCodeHttpResponse(
					channelExternalReferenceCode, productExternalReferenceCode,
					skuExternalReferenceCode, accountId, currencyCode);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCodeHttpResponse(
					String channelExternalReferenceCode,
					String productExternalReferenceCode,
					String skuExternalReferenceCode, Long accountId,
					String currencyCode)
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-externalReferenceCode/{skuExternalReferenceCode}");

			httpInvoker.path(
				"channelExternalReferenceCode", channelExternalReferenceCode);
			httpInvoker.path(
				"productExternalReferenceCode", productExternalReferenceCode);
			httpInvoker.path(
				"skuExternalReferenceCode", skuExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					String currencyCode, java.math.BigDecimal quantity,
					String skuUnitOfMeasureKey, SkuOption[] skuOptions)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOptionHttpResponse(
					channelExternalReferenceCode, productExternalReferenceCode,
					accountId, currencyCode, quantity, skuUnitOfMeasureKey,
					skuOptions);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOptionHttpResponse(
					String channelExternalReferenceCode,
					String productExternalReferenceCode, Long accountId,
					String currencyCode, java.math.BigDecimal quantity,
					String skuUnitOfMeasureKey, SkuOption[] skuOptions)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (SkuOption skuOptionValue : skuOptions) {
				values.add(String.valueOf(skuOptionValue));
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			if (quantity != null) {
				httpInvoker.parameter("quantity", String.valueOf(quantity));
			}

			if (skuUnitOfMeasureKey != null) {
				httpInvoker.parameter(
					"skuUnitOfMeasureKey", String.valueOf(skuUnitOfMeasureKey));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-sku-option");

			httpInvoker.path(
				"channelExternalReferenceCode", channelExternalReferenceCode);
			httpInvoker.path(
				"productExternalReferenceCode", productExternalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Page<Sku> getChannelProductSkusPage(
				Long channelId, Long productId, Long accountId,
				String currencyCode, Pagination pagination)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getChannelProductSkusPageHttpResponse(
					channelId, productId, accountId, currencyCode, pagination);

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
				return Page.of(content, SkuSerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getChannelProductSkusPageHttpResponse(
				Long channelId, Long productId, Long accountId,
				String currencyCode, Pagination pagination)
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			if (pagination != null) {
				httpInvoker.parameter(
					"page", String.valueOf(pagination.getPage()));
				httpInvoker.parameter(
					"pageSize", String.valueOf(pagination.getPageSize()));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus");

			httpInvoker.path("channelId", channelId);
			httpInvoker.path("productId", productId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku postChannelProductSku(
				Long channelId, Long productId, Long accountId,
				java.math.BigDecimal quantity, DDMOption[] ddmOptions)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postChannelProductSkuHttpResponse(
					channelId, productId, accountId, quantity, ddmOptions);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse postChannelProductSkuHttpResponse(
				Long channelId, Long productId, Long accountId,
				java.math.BigDecimal quantity, DDMOption[] ddmOptions)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (DDMOption ddmOptionValue : ddmOptions) {
				values.add(String.valueOf(ddmOptionValue));
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (quantity != null) {
				httpInvoker.parameter("quantity", String.valueOf(quantity));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus");

			httpInvoker.path("channelId", channelId);
			httpInvoker.path("productId", productId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku postChannelProductSkuBySkuOption(
				Long channelId, Long productId, Long accountId,
				String currencyCode, java.math.BigDecimal quantity,
				String skuUnitOfMeasureKey, SkuOption[] skuOptions)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postChannelProductSkuBySkuOptionHttpResponse(
					channelId, productId, accountId, currencyCode, quantity,
					skuUnitOfMeasureKey, skuOptions);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				postChannelProductSkuBySkuOptionHttpResponse(
					Long channelId, Long productId, Long accountId,
					String currencyCode, java.math.BigDecimal quantity,
					String skuUnitOfMeasureKey, SkuOption[] skuOptions)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			List<String> values = new ArrayList<>();

			for (SkuOption skuOptionValue : skuOptions) {
				values.add(String.valueOf(skuOptionValue));
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			if (quantity != null) {
				httpInvoker.parameter("quantity", String.valueOf(quantity));
			}

			if (skuUnitOfMeasureKey != null) {
				httpInvoker.parameter(
					"skuUnitOfMeasureKey", String.valueOf(skuUnitOfMeasureKey));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus/by-sku-option");

			httpInvoker.path("channelId", channelId);
			httpInvoker.path("productId", productId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public Sku getChannelProductSku(
				Long channelId, Long productId, Long skuId, Long accountId,
				String currencyCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getChannelProductSkuHttpResponse(
					channelId, productId, skuId, accountId, currencyCode);

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
				return SkuSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getChannelProductSkuHttpResponse(
				Long channelId, Long productId, Long skuId, Long accountId,
				String currencyCode)
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

			if (accountId != null) {
				httpInvoker.parameter("accountId", String.valueOf(accountId));
			}

			if (currencyCode != null) {
				httpInvoker.parameter(
					"currencyCode", String.valueOf(currencyCode));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus/{skuId}");

			httpInvoker.path("channelId", channelId);
			httpInvoker.path("productId", productId);
			httpInvoker.path("skuId", skuId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private SkuResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			SkuResource.class.getName());

		private Builder _builder;

	}

}