/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.operation;

import static org.mule.runtime.http.api.HttpConstants.Method;

import com.liferay.mule.internal.connection.LiferayConnection;
import com.liferay.mule.internal.connection.ResourceContext;
import com.liferay.mule.internal.error.LiferayResponseValidator;
import com.liferay.mule.internal.error.provider.LiferayResponseErrorProvider;
import com.liferay.mule.internal.metadata.input.PatchEndpointInputTypeResolver;
import com.liferay.mule.internal.metadata.input.PostEndpointInputTypeResolver;
import com.liferay.mule.internal.metadata.key.DeleteEndpointTypeKeysResolver;
import com.liferay.mule.internal.metadata.key.GetEndpointTypeKeysResolver;
import com.liferay.mule.internal.metadata.key.PatchEndpointTypeKeysResolver;
import com.liferay.mule.internal.metadata.key.PostEndpointTypeKeysResolver;
import com.liferay.mule.internal.metadata.output.DeleteEndpointOutputTypeResolver;
import com.liferay.mule.internal.metadata.output.GetEndpointOutputTypeResolver;
import com.liferay.mule.internal.metadata.output.PatchEndpointOutputTypeResolver;
import com.liferay.mule.internal.metadata.output.PostEndpointOutputTypeResolver;

import java.io.InputStream;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class LiferayCRUDOperations {

	@DisplayName("Delete Record")
	@MediaType(MediaType.APPLICATION_JSON)
	@OutputResolver(output = DeleteEndpointOutputTypeResolver.class)
	public Result<String, Void> delete(
			@Connection LiferayConnection connection,
			@MetadataKeyId(DeleteEndpointTypeKeysResolver.class) String
				endpoint,
			@DisplayName("Path Parameters") @NullSafe @Optional Map
				<String, String> pathParams,
			@DisplayName("Query Parameters") @NullSafe @Optional MultiMap
				<String, String> queryParams,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		logEndpointParams(Method.DELETE, endpoint, pathParams, queryParams);

		ResourceContext.Builder builder = new ResourceContext.Builder();

		HttpResponse httpResponse = connection.delete(
			builder.connectionTimeout(
				connectionTimeoutTimeUnit.toMillis(connectionTimeout)
			).endpoint(
				endpoint
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return geResult(httpResponse);
	}

	@DisplayName("Get Records")
	@MediaType(MediaType.APPLICATION_JSON)
	@OutputResolver(output = GetEndpointOutputTypeResolver.class)
	public Result<String, Void> get(
			@Connection LiferayConnection connection,
			@MetadataKeyId(GetEndpointTypeKeysResolver.class) String endpoint,
			@DisplayName("Path Parameters") @NullSafe @Optional Map
				<String, String> pathParams,
			@DisplayName("Query Parameters") @NullSafe @Optional MultiMap
				<String, String> queryParams,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		logEndpointParams(Method.GET, endpoint, pathParams, queryParams);

		ResourceContext.Builder builder = new ResourceContext.Builder();

		HttpResponse httpResponse = connection.get(
			builder.connectionTimeout(
				connectionTimeoutTimeUnit.toMillis(connectionTimeout)
			).endpoint(
				endpoint
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return geResult(httpResponse);
	}

	@DisplayName("Update Record")
	@MediaType(MediaType.APPLICATION_JSON)
	@OutputResolver(output = PatchEndpointOutputTypeResolver.class)
	public Result<String, Void> patch(
			@Connection LiferayConnection connection,
			@MetadataKeyId(PatchEndpointTypeKeysResolver.class) String endpoint,
			@Content @DisplayName("Record")
			@TypeResolver(value = PatchEndpointInputTypeResolver.class)
			InputStream inputStream,
			@DisplayName("Path Parameters") @NullSafe @Optional Map
				<String, String> pathParams,
			@DisplayName("Query Parameters") @NullSafe @Optional MultiMap
				<String, String> queryParams,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		logEndpointParams(Method.PATCH, endpoint, pathParams, queryParams);

		ResourceContext.Builder builder = new ResourceContext.Builder();

		HttpResponse httpResponse = connection.patch(
			builder.connectionTimeout(
				connectionTimeoutTimeUnit.toMillis(connectionTimeout)
			).endpoint(
				endpoint
			).inputStream(
				inputStream
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return geResult(httpResponse);
	}

	@DisplayName("Create Record")
	@MediaType(MediaType.APPLICATION_JSON)
	@OutputResolver(output = PostEndpointOutputTypeResolver.class)
	public Result<String, Void> post(
			@Connection LiferayConnection connection,
			@MetadataKeyId(PostEndpointTypeKeysResolver.class) String endpoint,
			@Content @DisplayName("Record")
			@TypeResolver(value = PostEndpointInputTypeResolver.class)
			InputStream inputStream,
			@DisplayName("Path Parameters") @NullSafe @Optional Map
				<String, String> pathParams,
			@DisplayName("Query Parameters") @NullSafe @Optional MultiMap
				<String, String> queryParams,
			@ConfigOverride @DisplayName("Connection Timeout") @Optional
			@Placement(order = 1, tab = Placement.ADVANCED_TAB)
			@Summary("Socket connection timeout value")
			int connectionTimeout,
			@ConfigOverride @DisplayName("Connection Timeout Unit") @Optional
			@Placement(order = 2, tab = Placement.ADVANCED_TAB)
			@Summary("Time unit to be used in the timeout configurations")
			TimeUnit connectionTimeoutTimeUnit)
		throws ModuleException {

		logEndpointParams(Method.POST, endpoint, pathParams, queryParams);

		ResourceContext.Builder builder = new ResourceContext.Builder();

		HttpResponse httpResponse = connection.post(
			builder.connectionTimeout(
				connectionTimeoutTimeUnit.toMillis(connectionTimeout)
			).endpoint(
				endpoint
			).inputStream(
				inputStream
			).pathParams(
				pathParams
			).queryParams(
				queryParams
			).build());

		liferayResponseValidator.validate(httpResponse);

		return geResult(httpResponse);
	}

	private Result<String, Void> geResult(HttpResponse httpResponse) {
		String responseBody = getResponseBody(httpResponse);

		logger.debug(
			"Received response with status {} and message {}",
			httpResponse.getStatusCode(), responseBody);

		return Result.<String, Void>builder(
		).output(
			responseBody
		).build();
	}

	private String getResponseBody(HttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();

		return IOUtils.toString(httpEntity.getContent());
	}

	private void logEndpointParams(
		Method method, String endpoint, Map<String, String> pathParams,
		Map<String, String> queryParams) {

		logger.debug(
			"Send {} request to endpoint {}, with path parameters {} and " +
				"query parameters {}",
			method, endpoint, pathParams, queryParams);
	}

	private static final Logger logger = LoggerFactory.getLogger(
		LiferayCRUDOperations.class);

	private final LiferayResponseValidator liferayResponseValidator =
		new LiferayResponseValidator();

}