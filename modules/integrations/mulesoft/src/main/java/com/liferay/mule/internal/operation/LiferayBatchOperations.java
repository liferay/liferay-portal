/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.operation;

import com.fasterxml.jackson.databind.JsonNode;

import com.liferay.mule.internal.connection.LiferayConnection;
import com.liferay.mule.internal.connection.ResourceContext;
import com.liferay.mule.internal.error.LiferayError;
import com.liferay.mule.internal.error.LiferayResponseValidator;
import com.liferay.mule.internal.error.provider.LiferayResponseErrorProvider;
import com.liferay.mule.internal.metadata.input.BatchImportInputTypeResolver;
import com.liferay.mule.internal.metadata.key.ClassNameTypeKeysResolver;
import com.liferay.mule.internal.metadata.output.BatchExportOutputTypeResolver;
import com.liferay.mule.internal.util.JsonNodeReader;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
@Throws(LiferayResponseErrorProvider.class)
public class LiferayBatchOperations {

	@DisplayName("Batch - Export Records")
	@MediaType(MediaType.APPLICATION_JSON)
	@OutputResolver(output = BatchExportOutputTypeResolver.class)
	public Result<InputStream, Void> executeExportTask(
			@Connection LiferayConnection connection,
			@MetadataKeyId(ClassNameTypeKeysResolver.class) String className,
			@Optional String siteId,
			@Optional @Summary("Comma-separated list") String fieldNames,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		long connectionTimeoutMillis = connectionTimeoutTimeUnit.toMillis(
			connectionTimeout);

		String exportTaskId = submitExportTask(
			className, connection, fieldNames, siteId, connectionTimeoutMillis);

		logger.info("Started batch export task with ID {}", exportTaskId);

		try {
			while (true) {
				JsonNode exportTaskJsonNode = getExportTaskJsonNode(
					connection, exportTaskId, connectionTimeoutMillis);

				String exportTaskStatus = exportTaskJsonNode.get(
					"executeStatus"
				).asText();

				if (exportTaskStatus.equalsIgnoreCase("completed")) {
					break;
				}
				else if (exportTaskStatus.equalsIgnoreCase("failed")) {
					String errorMessage = exportTaskJsonNode.get(
						"errorMessage"
					).asText();

					logger.error(
						"Batch export task with ID {} failed with message: {}",
						exportTaskId, errorMessage);

					throw new ModuleException(
						exportTaskJsonNode.get(
							"errorMessage"
						).asText(),
						LiferayError.BATCH_EXPORT_FAILED);
				}

				Thread.sleep(1000);
			}
		}
		catch (InterruptedException interruptedException) {
			throw new ModuleException(
				LiferayError.EXECUTION, interruptedException);
		}

		logger.info(
			"Batch export with ID {} finished successfully", exportTaskId);

		return getExportTaskResult(
			getExportTaskContentZipInputStream(
				connection, exportTaskId, connectionTimeoutMillis));
	}

	@DisplayName("Batch - Import Records - Create")
	public void executeImportCreateTask(
			@Connection LiferayConnection connection,
			@MetadataKeyId(ClassNameTypeKeysResolver.class) String className,
			@NullSafe @Optional Map<String, String> fieldNameMappings,
			@Content @DisplayName("Records")
			@TypeResolver(value = BatchImportInputTypeResolver.class)
			InputStream inputStream,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		long connectionTimeoutMillis = connectionTimeoutTimeUnit.toMillis(
			connectionTimeout);

		String importTaskId = submitImportCreateTask(
			connection, inputStream, className, fieldNameMappings,
			connectionTimeoutMillis);

		logger.info("Started batch create task with ID {}", importTaskId);

		checkImportTaskExecutionResult(
			connection, importTaskId, connectionTimeoutMillis);

		logger.info(
			"Batch create task with ID {} finished successfully", importTaskId);
	}

	@DisplayName("Batch - Import Records - Delete")
	public void executeImportDeleteTask(
			@Connection LiferayConnection connection,
			@MetadataKeyId(ClassNameTypeKeysResolver.class) String className,
			@Content @DisplayName("Records")
			@TypeResolver(value = BatchImportInputTypeResolver.class)
			InputStream inputStream,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		long connectionTimeoutMillis = connectionTimeoutTimeUnit.toMillis(
			connectionTimeout);

		String importTaskId = submitImportDeleteTask(
			connection, inputStream, className, connectionTimeoutMillis);

		logger.info("Started batch delete task with ID {}", importTaskId);

		checkImportTaskExecutionResult(
			connection, importTaskId, connectionTimeoutMillis);

		logger.info(
			"Batch delete task with ID {} finished successfully", importTaskId);
	}

	@DisplayName("Batch - Import Records - Update")
	public void executeImportUpdateTask(
			@Connection LiferayConnection connection,
			@MetadataKeyId(ClassNameTypeKeysResolver.class) String className,
			@Content @DisplayName("Records")
			@TypeResolver(value = BatchImportInputTypeResolver.class)
			InputStream inputStream,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		long connectionTimeoutMillis = connectionTimeoutTimeUnit.toMillis(
			connectionTimeout);

		String importTaskId = submitImportUpdateTask(
			connection, inputStream, className, connectionTimeoutMillis);

		logger.info("Started batch update task with ID {}", importTaskId);

		checkImportTaskExecutionResult(
			connection, importTaskId, connectionTimeoutMillis);

		logger.info(
			"Batch update task with ID {} finished successfully", importTaskId);
	}

	private void checkImportTaskExecutionResult(
			LiferayConnection connection, String importTaskId,
			long connectionTimeoutMillis)
		throws ModuleException {

		try {
			while (true) {
				JsonNode importTaskJsonNode = getImportTaskJsonNode(
					connection, importTaskId, connectionTimeoutMillis);

				String importTaskStatus = importTaskJsonNode.get(
					"executeStatus"
				).asText();

				if (importTaskStatus.equalsIgnoreCase("completed")) {
					break;
				}
				else if (importTaskStatus.equalsIgnoreCase("failed")) {
					String errorMessage = importTaskJsonNode.get(
						"errorMessage"
					).asText();

					logger.error(
						"Batch import task with ID {} failed with message: {}",
						importTaskId, errorMessage);

					throw new ModuleException(
						errorMessage, LiferayError.BATCH_IMPORT_FAILED);
				}

				Thread.sleep(1000);
			}
		}
		catch (InterruptedException interruptedException) {
			throw new ModuleException(
				LiferayError.EXECUTION, interruptedException);
		}
	}

	private ZipInputStream getExportTaskContentZipInputStream(
			LiferayConnection connection, String exportTaskId,
			long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("exportTaskId", exportTaskId);

		HttpResponse httpResponse = connection.get(
			builder.connectionTimeout(
				connectionTimeout
			).endpoint(
				"/v1.0/export-task/{exportTaskId}/content"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		HttpEntity httpEntity = httpResponse.getEntity();

		return new ZipInputStream(httpEntity.getContent());
	}

	private JsonNode getExportTaskJsonNode(
			LiferayConnection connection, String exportTaskId,
			long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("exportTaskId", exportTaskId);

		HttpResponse httpResponse = connection.get(
			builder.connectionTimeout(
				connectionTimeout
			).endpoint(
				"/v1.0/export-task/{exportTaskId}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return jsonNodeReader.fromHttpResponse(httpResponse);
	}

	private Result<InputStream, Void> getExportTaskResult(
			ZipInputStream zipInputStream)
		throws ModuleException {

		try {
			zipInputStream.getNextEntry();

			return Result.<InputStream, Void>builder(
			).output(
				zipInputStream
			).build();
		}
		catch (IOException ioException) {
			throw new ModuleException(LiferayError.EXECUTION, ioException);
		}
	}

	private JsonNode getImportTaskJsonNode(
			LiferayConnection connection, String importTaskId,
			long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("importTaskId", importTaskId);

		HttpResponse httpResponse = connection.get(
			builder.connectionTimeout(
				connectionTimeout
			).endpoint(
				"/v1.0/import-task/{importTaskId}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return jsonNodeReader.fromHttpResponse(httpResponse);
	}

	private String submitExportTask(
			String className, LiferayConnection connection, String fieldNames,
			String siteId, long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("className", className);
		pathParams.put("contentType", BatchExportContentType.JSON.toString());

		MultiMap<String, String> queryParams = new MultiMap<>();

		if (fieldNames != null) {
			queryParams.put("fieldNames", fieldNames);
		}

		if (siteId != null) {
			queryParams.put("siteId", siteId);
		}

		HttpResponse httpResponse = connection.post(
			builder.connectionTimeout(
				connectionTimeout
			).endpoint(
				"/v1.0/export-task/{className}/{contentType}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		JsonNode payloadJsonNode = jsonNodeReader.fromHttpResponse(
			httpResponse);

		JsonNode idJsonNode = payloadJsonNode.get("id");

		return String.valueOf(idJsonNode.longValue());
	}

	private String submitImportCreateTask(
			LiferayConnection connection, InputStream inputStream,
			String className, Map<String, String> fieldNameMappings,
			long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("className", className);

		MultiMap<String, String> queryParams = new MultiMap<>();

		if (!fieldNameMappings.isEmpty()) {
			StringBuilder sb = new StringBuilder();

			for (Map.Entry<String, String> entry :
					fieldNameMappings.entrySet()) {

				sb.append(entry.getKey() + "=" + entry.getValue());
				sb.append(",");
			}

			sb.setLength(sb.length() - 1);

			queryParams.put("fieldNameMappings", sb.toString());
		}

		HttpResponse httpResponse = connection.post(
			builder.bytes(
				IOUtils.toByteArray(inputStream)
			).connectionTimeout(
				connectionTimeout
			).contentType(
				"multipart/form-data"
			).endpoint(
				"/v1.0/import-task/{className}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		JsonNode payloadJsonNode = jsonNodeReader.fromHttpResponse(
			httpResponse);

		JsonNode idJsonNode = payloadJsonNode.get("id");

		return String.valueOf(idJsonNode.longValue());
	}

	private String submitImportDeleteTask(
			LiferayConnection connection, InputStream inputStream,
			String className, long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("className", className);

		HttpResponse httpResponse = connection.delete(
			builder.bytes(
				IOUtils.toByteArray(inputStream)
			).connectionTimeout(
				connectionTimeout
			).contentType(
				"multipart/form-data"
			).endpoint(
				"/v1.0/import-task/{className}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		JsonNode payloadJsonNode = jsonNodeReader.fromHttpResponse(
			httpResponse);

		JsonNode idJsonNode = payloadJsonNode.get("id");

		return String.valueOf(idJsonNode.longValue());
	}

	private String submitImportUpdateTask(
			LiferayConnection connection, InputStream inputStream,
			String className, long connectionTimeout)
		throws ModuleException {

		ResourceContext.Builder builder = new ResourceContext.Builder();

		Map<String, String> pathParams = new HashMap<>();

		pathParams.put("className", className);

		HttpResponse httpResponse = connection.put(
			builder.bytes(
				IOUtils.toByteArray(inputStream)
			).connectionTimeout(
				connectionTimeout
			).contentType(
				"multipart/form-data"
			).endpoint(
				"/v1.0/import-task/{className}"
			).jaxRSAppBase(
				"/headless-batch-engine"
			).pathParams(
				pathParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		JsonNode payloadJsonNode = jsonNodeReader.fromHttpResponse(
			httpResponse);

		JsonNode idJsonNode = payloadJsonNode.get("id");

		return String.valueOf(idJsonNode.longValue());
	}

	private static final Logger logger = LoggerFactory.getLogger(
		LiferayBatchOperations.class);

	private final JsonNodeReader jsonNodeReader = new JsonNodeReader();
	private final LiferayResponseValidator liferayResponseValidator =
		new LiferayResponseValidator();

}