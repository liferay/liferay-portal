/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.vulcan.openapi.contributor;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.application.provider.APIApplicationProvider;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.util.OpenAPIUtil;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = OpenAPIContributor.class)
public class APIApplicationOpenApiContributor implements OpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext)
		throws Exception {

		if (openAPIContext == null) {
			return;
		}

		APIApplication apiApplication = _fetchAPIApplication(openAPIContext);

		if (apiApplication == null) {
			return;
		}

		Components components = openAPI.getComponents();

		if (components == null) {
			components = new Components();

			openAPI.setComponents(components);
		}

		Map<String, Schema> schemas = components.getSchemas();

		if (schemas == null) {
			schemas = new TreeMap<>();

			components.setSchemas(schemas);
		}

		for (APIApplication.Schema schema : apiApplication.getSchemas()) {
			schemas.putAll(_toOpenAPISchemas(schema));
		}

		openAPI.setInfo(
			new Info() {
				{
					setDescription(
						"OpenAPI Specification of the " +
							apiApplication.getTitle() + " REST API");
					setLicense(
						new License() {
							{
								setName("Apache 2.0");
								setUrl(
									"http://www.apache.org/licenses" +
										"/LICENSE-2.0.html");
							}
						});
					setTitle(apiApplication.getTitle());
					setVersion(apiApplication.getVersion());
				}
			});

		Paths paths = new Paths();

		Paths oldPaths = openAPI.getPaths();

		if ((oldPaths != null) && oldPaths.containsKey("/openapi.{type}")) {
			paths.put("/openapi.{type}", oldPaths.get("/openapi.{type}"));
		}

		Set<String> schemasSet = new HashSet<>();

		for (APIApplication.Endpoint endpoint : apiApplication.getEndpoints()) {
			if (Validator.isNull(endpoint.getRequestSchema()) &&
				Objects.equals(Http.Method.POST, endpoint.getMethod())) {

				continue;
			}

			paths.compute(
				_formatPath(endpoint),
				(key, pathItem) -> {
					PathItem openAPIPathItem = _toOpenAPIPathItem(endpoint);

					if (pathItem == null) {
						return openAPIPathItem;
					}

					if (openAPIPathItem.getDelete() != null) {
						pathItem.setDelete(openAPIPathItem.getDelete());
					}

					if (openAPIPathItem.getGet() != null) {
						pathItem.setGet(openAPIPathItem.getGet());
					}

					if (openAPIPathItem.getPatch() != null) {
						pathItem.setPatch(openAPIPathItem.getPatch());
					}

					if (openAPIPathItem.getPost() != null) {
						pathItem.setPost(openAPIPathItem.getPost());
					}

					if (openAPIPathItem.getPut() != null) {
						pathItem.setPut(openAPIPathItem.getPut());
					}

					return pathItem;
				});

			APIApplication.Schema requestSchema = endpoint.getRequestSchema();

			if (requestSchema != null) {
				schemasSet.add(requestSchema.getName());
			}

			APIApplication.Schema responseSchema = endpoint.getResponseSchema();

			if (responseSchema == null) {
				continue;
			}

			if (Objects.equals(
					endpoint.getRetrieveType(),
					APIApplication.Endpoint.RetrieveType.COLLECTION)) {

				schemasSet.add("Page" + responseSchema.getName());
			}
			else {
				schemasSet.add(responseSchema.getName());
			}
		}

		components.setSchemas(_removedUnusedPageSchema(schemas, schemasSet));

		openAPI.setPaths(paths);
	}

	private void _addSchemas(
		Class<?> entityClass, Map<String, Schema> schemas) {

		if (!schemas.containsKey(entityClass.getSimpleName())) {
			schemas.putAll(_openAPIResource.getSchemas(entityClass));
		}
	}

	private APIApplication _fetchAPIApplication(OpenAPIContext openAPIContext)
		throws Exception {

		String path = openAPIContext.getPath();

		if (path.startsWith(HeadlessBuilderConstants.BASE_PATH)) {
			path = path.substring(4);
		}

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		return _apiApplicationProvider.fetchAPIApplication(
			path, CompanyThreadLocal.getCompanyId());
	}

	private String _formatPath(APIApplication.Endpoint endpoint) {
		String path = endpoint.getPath();

		if (!path.startsWith(StringPool.SLASH)) {
			path = StringPool.SLASH + path;
		}

		if (endpoint.getScope() == APIApplication.Endpoint.Scope.SITE) {
			return HeadlessBuilderConstants.BASE_PATH_SCOPES_SUFFIX + path;
		}

		return path;
	}

	private Schema _getPropertySchema(
		APIApplication.Property property, Map<String, Schema> schemas) {

		Schema schema = null;

		APIApplication.Property.Type type = property.getType();

		if (type == APIApplication.Property.Type.AGGREGATION) {
			schema = new StringSchema();
		}
		else if (type == APIApplication.Property.Type.ATTACHMENT) {
			_addSchemas(FileEntry.class, schemas);

			schema = new Schema() {
				{
					set$ref("FileEntry");
				}
			};
		}
		else if (type == APIApplication.Property.Type.BOOLEAN) {
			schema = new BooleanSchema();
		}
		else if (type == APIApplication.Property.Type.DATE) {
			schema = new DateSchema();
		}
		else if (type == APIApplication.Property.Type.DATE_TIME) {
			schema = new DateTimeSchema();
		}
		else if (type == APIApplication.Property.Type.DECIMAL) {
			schema = new NumberSchema() {
				{
					setFormat("double");
				}
			};
		}
		else if (type == APIApplication.Property.Type.INTEGER) {
			schema = new IntegerSchema();
		}
		else if (type == APIApplication.Property.Type.LONG_INTEGER) {
			schema = new IntegerSchema() {
				{
					setFormat("int64");
				}
			};
		}
		else if (type == APIApplication.Property.Type.LONG_TEXT) {
			schema = new StringSchema();
		}
		else if (type == APIApplication.Property.Type.MULTISELECT_PICKLIST) {
			_addSchemas(ListEntry.class, schemas);

			schema = new ArraySchema() {
				{
					setItems(
						new Schema() {
							{
								set$ref("ListEntry");
							}
						});
				}
			};
		}
		else if (type == APIApplication.Property.Type.RECORD) {
			schema = new ObjectSchema();
		}
		else if (type == APIApplication.Property.Type.PICKLIST) {
			_addSchemas(ListEntry.class, schemas);

			schema = new Schema() {
				{
					set$ref("ListEntry");
				}
			};
		}
		else if (type == APIApplication.Property.Type.PRECISION_DECIMAL) {
			schema = new NumberSchema() {
				{
					setFormat("double");
				}
			};
		}
		else if (type == APIApplication.Property.Type.RICH_TEXT) {
			schema = new StringSchema();
		}
		else if (type == APIApplication.Property.Type.TEXT) {
			schema = new StringSchema();
		}

		schema.setDescription(property.getDescription());
		schema.setName(property.getName());

		for (APIApplication.Property childProperty : property.getProperties()) {
			schema.setProperties(
				HashMapBuilder.put(
					childProperty.getName(),
					_getPropertySchema(childProperty, schemas)
				).putAll(
					schema.getProperties()
				).build());
		}

		return schema;
	}

	private Map<String, Schema> _removedUnusedPageSchema(
		Map<String, Schema> schemas, Set<String> schemasSet) {

		Map<String, Schema> schemasMap = HashMapBuilder.putAll(
			schemas
		).build();

		for (String pageSchemaName : schemas.keySet()) {
			if (!pageSchemaName.startsWith("Page") ||
				schemasSet.contains(pageSchemaName)) {

				continue;
			}

			schemasMap.remove(pageSchemaName);
		}

		return schemasMap;
	}

	private PathItem _toOpenAPIPathItem(APIApplication.Endpoint endpoint) {
		Operation operation = new Operation();

		String responseSchemaName = null;

		APIApplication.Schema responseSchema = endpoint.getResponseSchema();

		if (responseSchema != null) {
			responseSchemaName = responseSchema.getName();
		}

		Http.Method method = endpoint.getMethod();

		operation.setOperationId(
			OpenAPIUtil.getOperationId(
				method, _formatPath(endpoint), endpoint.getRetrieveType(),
				responseSchemaName));

		List<Parameter> parameters = new ArrayList<>();

		if (Objects.equals(
				endpoint.getScope(), APIApplication.Endpoint.Scope.SITE)) {

			parameters.add(
				new Parameter() {
					{
						setIn("path");
						setName("scopeKey");
						setRequired(true);
						setSchema(new StringSchema());
					}
				});
		}

		if (Objects.equals(method, Http.Method.GET)) {
			if (Objects.equals(
					endpoint.getRetrieveType(),
					APIApplication.Endpoint.RetrieveType.COLLECTION)) {

				parameters.add(
					new Parameter() {
						{
							setIn("query");
							setName("filter");
							setSchema(new StringSchema());
						}
					});

				parameters.add(
					new Parameter() {
						{
							setIn("query");
							setName("page");
							setSchema(new StringSchema());
						}
					});
				parameters.add(
					new Parameter() {
						{
							setIn("query");
							setName("pageSize");
							setSchema(new StringSchema());
						}
					});

				parameters.add(
					new Parameter() {
						{
							setIn("query");
							setName("sort");
							setSchema(new StringSchema());
						}
					});
			}

			if (Objects.equals(
					endpoint.getRetrieveType(),
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT)) {

				parameters.add(
					new Parameter() {
						{
							setIn("path");
							setName(
								OpenAPIUtil.getPathParameter(
									endpoint.getPath()));
							setRequired(true);
							setSchema(new StringSchema());
						}
					});
			}
		}

		if (ListUtil.isNotEmpty(parameters)) {
			operation.setParameters(parameters);
		}

		APIApplication.Schema requestSchema = endpoint.getRequestSchema();

		if (requestSchema != null) {
			MediaType mediaType = new MediaType() {
				{
					setSchema(
						new Schema() {
							{
								set$ref(requestSchema.getName());
							}
						});
				}
			};

			RequestBody requestBody = new RequestBody() {
				{
					setContent(
						new Content() {
							{
								put("application/json", mediaType);
								put("application/xml", mediaType);
							}
						});
					setDescription("default response");
				}
			};

			operation.setRequestBody(requestBody);

			operation.setTags(
				Collections.singletonList(requestSchema.getName()));
		}

		if (responseSchema != null) {
			MediaType mediaType = new MediaType() {
				{
					setSchema(
						new Schema() {
							{
								if (Objects.equals(
										endpoint.getRetrieveType(),
										APIApplication.Endpoint.RetrieveType.
											COLLECTION)) {

									set$ref("Page" + responseSchema.getName());
								}
								else {
									set$ref(responseSchema.getName());
								}
							}
						});
				}
			};

			ApiResponse apiResponse = new ApiResponse() {
				{
					setContent(
						new Content() {
							{
								put("application/json", mediaType);
								put("application/xml", mediaType);
							}
						});
					setDescription("default response");
				}
			};

			operation.setResponses(
				new ApiResponses() {
					{
						setDefault(apiResponse);
					}
				});

			operation.setTags(Arrays.asList(responseSchema.getName()));
		}
		else {
			operation.setResponses(
				new ApiResponses() {
					{
						setDefault(
							new ApiResponse() {
								{
									setDescription("default response");
								}
							});
					}
				});
		}

		return new PathItem() {
			{
				operation(
					PathItem.HttpMethod.valueOf(method.name()), operation);
			}
		};
	}

	private Map<String, Schema> _toOpenAPISchemas(
		APIApplication.Schema schema) {

		Map<String, Schema> schemas = new TreeMap<>();

		Map<String, Schema> properties = new TreeMap<>();

		for (APIApplication.Property property : schema.getProperties()) {
			properties.put(
				property.getName(), _getPropertySchema(property, schemas));
		}

		schemas.put(
			schema.getName(),
			new ObjectSchema() {
				{
					setDescription(schema.getDescription());
					setName(schema.getName());
					setProperties(properties);
				}
			});

		Map<String, Schema> pageSchemas = _openAPIResource.getSchemas(
			Page.class);

		Schema pageSchema = pageSchemas.remove("Page");

		Map<String, Schema> pageProperties = pageSchema.getProperties();

		ArraySchema itemsArraySchema = (ArraySchema)pageProperties.get("items");

		itemsArraySchema.setItems(
			new Schema() {
				{
					set$ref(schema.getName());
				}
			});

		schemas.put("Page" + schema.getName(), pageSchema);

		schemas.putAll(pageSchemas);

		return schemas;
	}

	@Reference
	private APIApplicationProvider _apiApplicationProvider;

	@Reference
	private OpenAPIResource _openAPIResource;

}