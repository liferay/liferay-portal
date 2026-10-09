/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.openapi.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.internal.vulcan.openapi.contributor.util.OpenAPIContributorUtil;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResource;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResourceProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(service = OpenAPIContributor.class)
public class RelatedObjectEntryOpenAPIContributor
	extends BaseOpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext)
		throws Exception {

		if (openAPIContext == null) {
			return;
		}

		Map<ObjectDefinition, SystemObjectDefinitionManager>
			systemObjectDefinitionManagerMap = new HashMap<>();

		for (ObjectDefinition systemObjectDefinition :
				_objectDefinitionLocalService.getSystemObjectDefinitions()) {

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						systemObjectDefinition.getName());

			if (systemObjectDefinitionManager == null) {
				continue;
			}

			JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
				systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

			String path = openAPIContext.getPath();

			if (!path.contains(
					jaxRsApplicationDescriptor.getApplicationPath())) {

				continue;
			}

			systemObjectDefinitionManagerMap.put(
				systemObjectDefinition, systemObjectDefinitionManager);
		}

		for (Map.Entry<ObjectDefinition, SystemObjectDefinitionManager> entry :
				systemObjectDefinitionManagerMap.entrySet()) {

			ObjectDefinition systemObjectDefinition = entry.getKey();

			for (ObjectRelationship systemObjectRelationship :
					_objectRelationshipLocalService.getObjectRelationships(
						systemObjectDefinition.getObjectDefinitionId())) {

				_contribute(
					openAPI, systemObjectDefinition, entry.getValue(),
					systemObjectRelationship, openAPIContext.getVersion());
			}
		}
	}

	@Activate
	protected void activate() {
		init(_dtoConverterRegistry, _systemObjectDefinitionManagerRegistry);
	}

	private void _contribute(
			OpenAPI openAPI, ObjectDefinition systemObjectDefinition,
			SystemObjectDefinitionManager systemObjectDefinitionManager,
			ObjectRelationship systemObjectRelationship, String version)
		throws Exception {

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				systemObjectDefinition, systemObjectRelationship);

		if (!relatedObjectDefinition.isActive()) {
			return;
		}

		Paths paths = openAPI.getPaths();

		String relatedSchemaName = getSchemaName(relatedObjectDefinition);

		ObjectEntryOpenAPIResource objectEntryOpenAPIResource =
			_objectEntryOpenAPIResourceProvider.getObjectEntryOpenAPIResource(
				relatedObjectDefinition);

		Map<String, Schema> relatedSchemas =
			objectEntryOpenAPIResource.getSchemas();

		OpenAPIContributorUtil.copySchemas(
			relatedSchemaName, relatedSchemas,
			relatedObjectDefinition.isUnmodifiableSystemObject(), openAPI);

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			systemObjectDefinitionManager.getJaxRsApplicationDescriptor();
		String schemaName = getSchemaName(systemObjectDefinition);

		String name = StringBundler.concat(
			StringPool.SLASH, version, StringPool.SLASH,
			jaxRsApplicationDescriptor.getPath(), StringPool.SLASH,
			_getIdParameterTemplate(schemaName), StringPool.SLASH,
			systemObjectRelationship.getName());

		Schema idSchema = new StringSchema();

		PathItem pathItem = paths.get(
			StringBundler.concat(
				StringPool.SLASH, version, StringPool.SLASH,
				jaxRsApplicationDescriptor.getPath(), StringPool.SLASH,
				_getIdParameterTemplate(schemaName)));

		Operation getOperation = (pathItem != null) ? pathItem.getGet() : null;

		if ((getOperation != null) && (getOperation.getParameters() != null)) {
			String idParameterName = _getIdParameterName(schemaName);

			for (Parameter parameter : getOperation.getParameters()) {
				if (Objects.equals(idParameterName, parameter.getName())) {
					idSchema = parameter.getSchema();

					break;
				}
			}
		}

		Schema finalIdSchema = idSchema;

		paths.addPathItem(
			name,
			new PathItem() {
				{
					get(
						_getGetOperation(
							finalIdSchema, systemObjectRelationship,
							relatedSchemaName, schemaName));
				}
			});
		paths.addPathItem(
			StringBundler.concat(
				name, StringPool.SLASH,
				_getIdParameterTemplate(
					relatedObjectDefinition.getShortName())),
			new PathItem() {
				{
					delete(
						_getDeleteOperation(
							finalIdSchema, systemObjectRelationship,
							relatedSchemaName, schemaName));
					put(
						_getPutOperation(
							finalIdSchema, systemObjectRelationship,
							relatedSchemaName, schemaName));
				}
			});
	}

	private Content _getContent(String schemaName) {
		Content content = new Content();

		MediaType mediaType = new MediaType();

		if (schemaName != null) {
			Schema schema = new Schema();

			schema.set$ref(schemaName);

			mediaType.setSchema(schema);
		}

		content.addMediaType("application/json", mediaType);
		content.addMediaType("application/xml", mediaType);

		return content;
	}

	private Operation _getDeleteOperation(
		Schema idSchema, ObjectRelationship objectRelationship,
		String relatedSchemaName, String schemaName) {

		return new Operation() {
			{
				operationId(
					_getOperationId(
						"delete", objectRelationship.getName(), schemaName));
				parameters(
					Arrays.asList(
						new Parameter() {
							{
								in("path");
								name(_getIdParameterName(schemaName));
								required(true);
								schema(idSchema);
							}
						},
						new Parameter() {
							{
								in("path");
								name(_getIdParameterName(relatedSchemaName));
								required(true);
								schema(idSchema);
							}
						}));
				responses(
					new ApiResponses() {
						{
							setDefault(
								new ApiResponse() {
									{
										setContent(_getContent(null));
										setDescription("default response");
									}
								});
						}
					});
				tags(Arrays.asList(schemaName));
			}
		};
	}

	private Operation _getGetOperation(
		Schema idSchema, ObjectRelationship objectRelationship,
		String relatedSchemaName, String schemaName) {

		String parameterName = _getIdParameterName(schemaName);

		return new Operation() {
			{
				operationId(
					_getOperationId(
						"get", objectRelationship.getName(), schemaName));
				parameters(
					Arrays.asList(
						new Parameter() {
							{
								in("path");
								name(parameterName);
								required(true);
								schema(idSchema);
							}
						}));
				responses(
					new ApiResponses() {
						{
							setDefault(
								new ApiResponse() {
									{
										setContent(
											_getContent(
												OpenAPIContributorUtil.
													getPageSchemaName(
														relatedSchemaName)));
										setDescription("default response");
									}
								});
						}
					});
				tags(Arrays.asList(schemaName));
			}
		};
	}

	private String _getIdParameterName(String name) {
		return StringUtil.lowerCaseFirstLetter(name) + "Id";
	}

	private String _getIdParameterTemplate(String name) {
		return StringPool.OPEN_CURLY_BRACE + _getIdParameterName(name) +
			StringPool.CLOSE_CURLY_BRACE;
	}

	private String _getOperationId(
		String method, String objectRelationshipName,
		String systemObjectDefinitionName) {

		String sufix = "";

		if (StringUtil.equals(method, "get")) {
			sufix = "Page";
		}

		return StringBundler.concat(
			method, systemObjectDefinitionName,
			StringUtil.upperCaseFirstLetter(objectRelationshipName), sufix);
	}

	private Operation _getPutOperation(
		Schema idSchema, ObjectRelationship objectRelationship,
		String relatedSchemaName, String schemaName) {

		return new Operation() {
			{
				operationId(
					_getOperationId(
						"put", objectRelationship.getName(), schemaName));
				parameters(
					Arrays.asList(
						new Parameter() {
							{
								in("path");
								name(_getIdParameterName(schemaName));
								required(true);
								schema(idSchema);
							}
						},
						new Parameter() {
							{
								in("path");
								name(_getIdParameterName(relatedSchemaName));
								required(true);
								schema(idSchema);
							}
						}));
				responses(
					new ApiResponses() {
						{
							setDefault(
								new ApiResponse() {
									{
										setContent(
											_getContent(relatedSchemaName));
										setDescription("default response");
									}
								});
						}
					});
				tags(Arrays.asList(schemaName));
			}
		};
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryOpenAPIResourceProvider
		_objectEntryOpenAPIResourceProvider;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}