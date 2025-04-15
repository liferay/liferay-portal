/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.client.resource.v1_0;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.problem.Problem;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ImportTaskSerDes;

import jakarta.annotation.Generated;

import java.io.File;

import java.net.URL;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
public interface ImportTaskResource {

	public static Builder builder() {
		return new Builder();
	}

	public ImportTask getImportTaskByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getImportTaskByExternalReferenceCodeHttpResponse(
				String externalReferenceCode)
		throws Exception;

	public void getImportTaskByExternalReferenceCodeContent(
			String externalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getImportTaskByExternalReferenceCodeContentHttpResponse(
				String externalReferenceCode)
		throws Exception;

	public void getImportTaskByExternalReferenceCodeFailedItemReport(
			String externalReferenceCode)
		throws Exception;

	public HttpInvoker.HttpResponse
			getImportTaskByExternalReferenceCodeFailedItemReportHttpResponse(
				String externalReferenceCode)
		throws Exception;

	public ImportTask deleteImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName, Object object)
		throws Exception;

	public HttpInvoker.HttpResponse deleteImportTaskHttpResponse(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName, Object object)
		throws Exception;

	public ImportTask deleteFormDataImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			ImportTask importTask, Map<String, File> multipartFiles)
		throws Exception;

	public HttpInvoker.HttpResponse deleteFormDataImportTaskHttpResponse(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			ImportTask importTask, Map<String, File> multipartFiles)
		throws Exception;

	public ImportTask postImportTask(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, Object object)
		throws Exception;

	public HttpInvoker.HttpResponse postImportTaskHttpResponse(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, Object object)
		throws Exception;

	public ImportTask postFormDataImportTask(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, ImportTask importTask,
			Map<String, File> multipartFiles)
		throws Exception;

	public HttpInvoker.HttpResponse postFormDataImportTaskHttpResponse(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, ImportTask importTask,
			Map<String, File> multipartFiles)
		throws Exception;

	public ImportTask putImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, Object object)
		throws Exception;

	public HttpInvoker.HttpResponse putImportTaskHttpResponse(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, Object object)
		throws Exception;

	public ImportTask putFormDataImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, ImportTask importTask,
			Map<String, File> multipartFiles)
		throws Exception;

	public HttpInvoker.HttpResponse putFormDataImportTaskHttpResponse(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, ImportTask importTask,
			Map<String, File> multipartFiles)
		throws Exception;

	public ImportTask getImportTask(Long importTaskId) throws Exception;

	public HttpInvoker.HttpResponse getImportTaskHttpResponse(Long importTaskId)
		throws Exception;

	public void getImportTaskContent(Long importTaskId) throws Exception;

	public HttpInvoker.HttpResponse getImportTaskContentHttpResponse(
			Long importTaskId)
		throws Exception;

	public void getImportTaskFailedItemReport(Long importTaskId)
		throws Exception;

	public HttpInvoker.HttpResponse getImportTaskFailedItemReportHttpResponse(
			Long importTaskId)
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

		public ImportTaskResource build() {
			return new ImportTaskResourceImpl(this);
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

	public static class ImportTaskResourceImpl implements ImportTaskResource {

		public ImportTask getImportTaskByExternalReferenceCode(
				String externalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getImportTaskByExternalReferenceCodeHttpResponse(
					externalReferenceCode);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				getImportTaskByExternalReferenceCodeHttpResponse(
					String externalReferenceCode)
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
						"/o/headless-batch-engine/v1.0/import-task/by-external-reference-code/{externalReferenceCode}");

			httpInvoker.path("externalReferenceCode", externalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void getImportTaskByExternalReferenceCodeContent(
				String externalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getImportTaskByExternalReferenceCodeContentHttpResponse(
					externalReferenceCode);

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
				getImportTaskByExternalReferenceCodeContentHttpResponse(
					String externalReferenceCode)
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
						"/o/headless-batch-engine/v1.0/import-task/by-external-reference-code/{externalReferenceCode}/content");

			httpInvoker.path("externalReferenceCode", externalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void getImportTaskByExternalReferenceCodeFailedItemReport(
				String externalReferenceCode)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getImportTaskByExternalReferenceCodeFailedItemReportHttpResponse(
					externalReferenceCode);

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
				getImportTaskByExternalReferenceCodeFailedItemReportHttpResponse(
					String externalReferenceCode)
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
						"/o/headless-batch-engine/v1.0/import-task/by-external-reference-code/{externalReferenceCode}/failed-items/report");

			httpInvoker.path("externalReferenceCode", externalReferenceCode);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask deleteImportTask(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, Object object)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteImportTaskHttpResponse(
					className, callbackURL, externalReferenceCode,
					importStrategy, taskItemDelegateName, object);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse deleteImportTaskHttpResponse(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, Object object)
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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.DELETE);

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask deleteFormDataImportTask(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, ImportTask importTask,
				Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				deleteFormDataImportTaskHttpResponse(
					className, callbackURL, externalReferenceCode,
					importStrategy, taskItemDelegateName, importTask,
					multipartFiles);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse deleteFormDataImportTaskHttpResponse(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, ImportTask importTask,
				Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.multipart();

			httpInvoker.part("importTask", ImportTaskSerDes.toJSON(importTask));

			for (Map.Entry<String, File> entry : multipartFiles.entrySet()) {
				httpInvoker.part(entry.getKey(), entry.getValue());
			}

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

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask postImportTask(
				String className, String batchExternalReferenceCode,
				String batchRestrictFields, String callbackURL,
				String createStrategy, String externalReferenceCode,
				String fieldNameMapping, String importStrategy,
				String taskItemDelegateName, Object object)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse = postImportTaskHttpResponse(
				className, batchExternalReferenceCode, batchRestrictFields,
				callbackURL, createStrategy, externalReferenceCode,
				fieldNameMapping, importStrategy, taskItemDelegateName, object);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse postImportTaskHttpResponse(
				String className, String batchExternalReferenceCode,
				String batchRestrictFields, String callbackURL,
				String createStrategy, String externalReferenceCode,
				String fieldNameMapping, String importStrategy,
				String taskItemDelegateName, Object object)
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

			if (batchExternalReferenceCode != null) {
				httpInvoker.parameter(
					"batchExternalReferenceCode",
					String.valueOf(batchExternalReferenceCode));
			}

			if (batchRestrictFields != null) {
				httpInvoker.parameter(
					"batchRestrictFields", String.valueOf(batchRestrictFields));
			}

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (createStrategy != null) {
				httpInvoker.parameter(
					"createStrategy", String.valueOf(createStrategy));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (fieldNameMapping != null) {
				httpInvoker.parameter(
					"fieldNameMapping", String.valueOf(fieldNameMapping));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask postFormDataImportTask(
				String className, String batchExternalReferenceCode,
				String batchRestrictFields, String callbackURL,
				String createStrategy, String externalReferenceCode,
				String fieldNameMapping, String importStrategy,
				String taskItemDelegateName, ImportTask importTask,
				Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postFormDataImportTaskHttpResponse(
					className, batchExternalReferenceCode, batchRestrictFields,
					callbackURL, createStrategy, externalReferenceCode,
					fieldNameMapping, importStrategy, taskItemDelegateName,
					importTask, multipartFiles);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse postFormDataImportTaskHttpResponse(
				String className, String batchExternalReferenceCode,
				String batchRestrictFields, String callbackURL,
				String createStrategy, String externalReferenceCode,
				String fieldNameMapping, String importStrategy,
				String taskItemDelegateName, ImportTask importTask,
				Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.multipart();

			httpInvoker.part("importTask", ImportTaskSerDes.toJSON(importTask));

			for (Map.Entry<String, File> entry : multipartFiles.entrySet()) {
				httpInvoker.part(entry.getKey(), entry.getValue());
			}

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

			if (batchExternalReferenceCode != null) {
				httpInvoker.parameter(
					"batchExternalReferenceCode",
					String.valueOf(batchExternalReferenceCode));
			}

			if (batchRestrictFields != null) {
				httpInvoker.parameter(
					"batchRestrictFields", String.valueOf(batchRestrictFields));
			}

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (createStrategy != null) {
				httpInvoker.parameter(
					"createStrategy", String.valueOf(createStrategy));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (fieldNameMapping != null) {
				httpInvoker.parameter(
					"fieldNameMapping", String.valueOf(fieldNameMapping));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask putImportTask(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, String updateStrategy,
				Object object)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse = putImportTaskHttpResponse(
				className, callbackURL, externalReferenceCode, importStrategy,
				taskItemDelegateName, updateStrategy, object);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse putImportTaskHttpResponse(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, String updateStrategy,
				Object object)
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

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.PUT);

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			if (updateStrategy != null) {
				httpInvoker.parameter(
					"updateStrategy", String.valueOf(updateStrategy));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask putFormDataImportTask(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, String updateStrategy,
				ImportTask importTask, Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				putFormDataImportTaskHttpResponse(
					className, callbackURL, externalReferenceCode,
					importStrategy, taskItemDelegateName, updateStrategy,
					importTask, multipartFiles);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse putFormDataImportTaskHttpResponse(
				String className, String callbackURL,
				String externalReferenceCode, String importStrategy,
				String taskItemDelegateName, String updateStrategy,
				ImportTask importTask, Map<String, File> multipartFiles)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.multipart();

			httpInvoker.part("importTask", ImportTaskSerDes.toJSON(importTask));

			for (Map.Entry<String, File> entry : multipartFiles.entrySet()) {
				httpInvoker.part(entry.getKey(), entry.getValue());
			}

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

			if (callbackURL != null) {
				httpInvoker.parameter(
					"callbackURL", String.valueOf(callbackURL));
			}

			if (externalReferenceCode != null) {
				httpInvoker.parameter(
					"externalReferenceCode",
					String.valueOf(externalReferenceCode));
			}

			if (importStrategy != null) {
				httpInvoker.parameter(
					"importStrategy", String.valueOf(importStrategy));
			}

			if (taskItemDelegateName != null) {
				httpInvoker.parameter(
					"taskItemDelegateName",
					String.valueOf(taskItemDelegateName));
			}

			if (updateStrategy != null) {
				httpInvoker.parameter(
					"updateStrategy", String.valueOf(updateStrategy));
			}

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port + _builder._contextPath +
						"/o/headless-batch-engine/v1.0/import-task/{className}");

			httpInvoker.path("className", className);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public ImportTask getImportTask(Long importTaskId) throws Exception {
			HttpInvoker.HttpResponse httpResponse = getImportTaskHttpResponse(
				importTaskId);

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
				return ImportTaskSerDes.toDTO(content);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse getImportTaskHttpResponse(
				Long importTaskId)
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
						"/o/headless-batch-engine/v1.0/import-task/{importTaskId}");

			httpInvoker.path("importTaskId", importTaskId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void getImportTaskContent(Long importTaskId) throws Exception {
			HttpInvoker.HttpResponse httpResponse =
				getImportTaskContentHttpResponse(importTaskId);

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

		public HttpInvoker.HttpResponse getImportTaskContentHttpResponse(
				Long importTaskId)
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
						"/o/headless-batch-engine/v1.0/import-task/{importTaskId}/content");

			httpInvoker.path("importTaskId", importTaskId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		public void getImportTaskFailedItemReport(Long importTaskId)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				getImportTaskFailedItemReportHttpResponse(importTaskId);

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
				getImportTaskFailedItemReportHttpResponse(Long importTaskId)
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
						"/o/headless-batch-engine/v1.0/import-task/{importTaskId}/failed-items/report");

			httpInvoker.path("importTaskId", importTaskId);

			if ((_builder._login != null) && (_builder._password != null)) {
				httpInvoker.userNameAndPassword(
					_builder._login + ":" + _builder._password);
			}

			return httpInvoker.invoke();
		}

		private ImportTaskResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			ImportTaskResource.class.getName());

		private Builder _builder;

	}

}