/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.openapi.v1_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.internal.resource.v1_0.CollaboratorResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryRelatedObjectsResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.OpenAPIResourceImpl;
import com.liferay.object.rest.internal.vulcan.openapi.contributor.ObjectEntryOpenAPIContributor;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResource;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResourceProvider;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.vulcan.batch.engine.Field;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.openapi.DTOProperty;
import com.liferay.portal.vulcan.openapi.OpenAPISchemaFilter;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.util.OpenAPIUtil;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectEntryOpenAPIResourceImpl
	implements ObjectEntryOpenAPIResource {

	public ObjectEntryOpenAPIResourceImpl(
		BundleContext bundleContext, DTOConverterRegistry dtoConverterRegistry,
		ObjectActionLocalService objectActionLocalService,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryOpenAPIResourceProvider objectEntryOpenAPIResourceProvider,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		OpenAPIResource openAPIResource,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_bundleContext = bundleContext;
		_dtoConverterRegistry = dtoConverterRegistry;
		_objectActionLocalService = objectActionLocalService;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryOpenAPIResourceProvider =
			objectEntryOpenAPIResourceProvider;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_openAPIResource = openAPIResource;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
	}

	@Override
	public Map<String, Field> getFields(UriInfo uriInfo) throws Exception {
		DTOProperty objectEntryDTOProperty = _getObjectEntryDTOProperty(
			_objectDefinition);

		Response response = _getOpenAPI(
			true,
			_getOpenAPISchemaFilter(objectEntryDTOProperty, _objectDefinition),
			"json", uriInfo);

		Schema schema = _getObjectDefinitionSchema(
			(OpenAPI)response.getEntity());

		if (schema == null) {
			return Collections.emptyMap();
		}

		Map<String, Field> fields = new HashMap<>();

		Map<String, String> relationshipNames = new HashMap<>();

		for (DTOProperty dtoProperty :
				objectEntryDTOProperty.getDTOProperties()) {

			if (!(dtoProperty instanceof RelationshipDTOProperty)) {
				continue;
			}

			RelationshipDTOProperty relationshipDTOProperty =
				(RelationshipDTOProperty)dtoProperty;

			relationshipNames.put(
				relationshipDTOProperty.getName(),
				relationshipDTOProperty.getRelationshipName());
		}

		List<String> requiredPropertySchemaNames =
			_getRequiredPropertySchemaNames(schema);

		Map<String, Schema> properties = schema.getProperties();

		for (Map.Entry<String, Schema> schemaEntry : properties.entrySet()) {
			String propertyName = schemaEntry.getKey();
			Schema propertySchema = schemaEntry.getValue();

			fields.put(
				propertyName,
				Field.of(
					MapUtil.getString(relationshipNames, propertyName, null),
					propertySchema.getDescription(), propertyName,
					GetterUtil.getBoolean(propertySchema.getReadOnly()),
					_getRef(propertySchema),
					requiredPropertySchemaNames.contains(propertyName),
					propertySchema.getType(),
					OpenAPIUtil.getBatchUnsupportedFormats(
						propertySchema.getExtensions()),
					GetterUtil.getBoolean(propertySchema.getWriteOnly())));
		}

		return fields;
	}

	@Override
	public Response getOpenAPI(
			HttpServletRequest httpServletRequest, String type, UriInfo uriInfo)
		throws Exception {

		return _getOpenAPI(
			true,
			_getOpenAPISchemaFilter(
				_getObjectEntryDTOProperty(_objectDefinition),
				_objectDefinition),
			type, uriInfo);
	}

	@Override
	public Map<String, Schema> getSchemas() throws Exception {
		Response response = _getOpenAPI(
			false,
			_getOpenAPISchemaFilter(
				_getObjectEntryDTOProperty(_objectDefinition),
				_objectDefinition),
			"json", null);

		OpenAPI openAPI = (OpenAPI)response.getEntity();

		Components components = openAPI.getComponents();

		return components.getSchemas();
	}

	private List<DTOProperty> _getDTOProperties(
		Map<String, String> dtoPropertiesMap, Map<String, Object> extensions,
		ObjectField objectField, String type) {

		DTOProperty dtoProperty = new DTOProperty(
			HashMapBuilder.<String, Object>put(
				"x-parent-map", "properties"
			).putAll(
				extensions
			).build(),
			objectField.getName(), type);

		List<DTOProperty> dtoProperties = new ArrayList<>();

		for (Map.Entry<String, String> entry : dtoPropertiesMap.entrySet()) {
			dtoProperties.add(
				new DTOProperty(
					Collections.singletonMap("x-parent-map", "properties"),
					entry.getKey(), entry.getValue()));
		}

		dtoProperty.setDTOProperties(dtoProperties);

		dtoProperty.setRequired(objectField.isRequired());

		return ListUtil.fromArray(dtoProperty);
	}

	private List<DTOProperty> _getDTOProperties(
		Map<String, String> dtoPropertiesMap, ObjectField objectField,
		String type) {

		return _getDTOProperties(
			dtoPropertiesMap, Collections.emptyMap(), objectField, type);
	}

	private List<DTOProperty> _getDTOProperties(ObjectField objectField) {
		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return _getDTOProperties(
				HashMapBuilder.put(
					"externalReferenceCode", String.class.getSimpleName()
				).put(
					"name", String.class.getSimpleName()
				).put(
					"type", Assignee.Type.class.getSimpleName()
				).build(),
				objectField, Assignee.class.getSimpleName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			return _getDTOProperties(
				HashMapBuilder.put(
					"id", Long.class.getSimpleName()
				).put(
					"name", String.class.getSimpleName()
				).build(),
				objectField, FileEntry.class.getSimpleName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DATE) &&
				 _fieldNameMappings.containsKey(objectField.getName())) {

			return ListUtil.fromArray(
				new DTOProperty(
					null, _fieldNameMappings.get(objectField.getName()),
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME) {

					{
						setRequired(objectField.isRequired());
					}
				});
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			return _getDTOProperties(
				Collections.emptyMap(),
				Collections.singletonMap(
					"x-timeStorage",
					ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE,
						objectField)),
				objectField, objectField.getDBType());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return _getDTOProperties(objectField, String.class.getSimpleName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return _getDTOProperties(
				HashMapBuilder.put(
					"key", String.class.getSimpleName()
				).put(
					"name", String.class.getSimpleName()
				).build(),
				objectField, ListEntry.class.getSimpleName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL)) {

			return _getDTOProperties(objectField, Double.class.getSimpleName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP) &&
				 Objects.equals(
					 objectField.getRelationshipType(),
					 ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			String relationshipName = objectRelationship.getName();

			return Arrays.asList(
				new RelationshipDTOProperty(
					HashMapBuilder.<String, Object>put(
						"x-parent-map", "properties"
					).build(),
					objectField.getName(), relationshipName,
					objectField.getDBType()) {

					{
						setRequired(objectField.isRequired());
					}
				},
				new RelationshipDTOProperty(
					HashMapBuilder.<String, Object>put(
						"x-parent-map", "properties"
					).build(),
					relationshipName, relationshipName,
					String.class.getSimpleName()) {

					{
						setRequired(objectField.isRequired());
					}
				},
				new RelationshipDTOProperty(
					HashMapBuilder.<String, Object>put(
						"x-parent-map", "properties"
					).build(),
					ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
						objectField),
					relationshipName, String.class.getSimpleName()) {

					{
						setRequired(objectField.isRequired());
					}
				},
				new RelationshipDTOProperty(
					Collections.singletonMap("x-parent-map", "properties"),
					relationshipName + "ERC", relationshipName,
					String.class.getSimpleName()) {

					{
						setReadOnly(true);
					}
				});
		}

		return _getDTOProperties(objectField, objectField.getDBType());
	}

	private List<DTOProperty> _getDTOProperties(
		ObjectField objectField, String type) {

		return _getDTOProperties(
			Collections.emptyMap(), Collections.emptyMap(), objectField, type);
	}

	private Schema _getObjectDefinitionSchema(OpenAPI openAPI) {
		Components components = openAPI.getComponents();

		Map<String, Schema> schemas = components.getSchemas();

		return schemas.get(_objectDefinition.getShortName());
	}

	private DTOProperty _getObjectEntryDTOProperty(
		ObjectDefinition objectDefinition) {

		DTOProperty dtoProperty = new DTOProperty(
			new HashMap<>(), "ObjectEntry", "Object");

		List<DTOProperty> dtoProperties = new ArrayList<>();

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			dtoProperties.addAll(_getDTOProperties(objectField));

			if (objectField.isLocalized()) {
				dtoProperties.add(
					new DTOProperty(
						Collections.singletonMap("x-parent-map", "properties"),
						objectField.getI18nObjectFieldName(),
						Map.class.getSimpleName()) {

						{
							setRequired(objectField.isRequired());
						}
					});
			}
		}

		dtoProperty.setDTOProperties(dtoProperties);

		return dtoProperty;
	}

	private Response _getOpenAPI(
			boolean addRelatedSchemas, OpenAPISchemaFilter openAPISchemaFilter,
			String type, UriInfo uriInfo)
		throws Exception {

		return _openAPIResource.getOpenAPI(
			new ObjectEntryOpenAPIContributor(
				addRelatedSchemas, _bundleContext, _dtoConverterRegistry,
				_objectActionLocalService, _objectDefinition,
				_objectEntryOpenAPIResourceProvider, _objectFieldLocalService,
				_objectRelationshipLocalService, _openAPIResource,
				_systemObjectDefinitionManagerRegistry),
			openAPISchemaFilter,
			new HashSet<Class<?>>() {
				{
					add(CollaboratorResourceImpl.class);
					add(ObjectEntryRelatedObjectsResourceImpl.class);
					add(ObjectEntryResourceImpl.class);
					add(OpenAPIResourceImpl.class);
				}
			},
			type, uriInfo);
	}

	private OpenAPISchemaFilter _getOpenAPISchemaFilter(
		DTOProperty dtoProperty, ObjectDefinition objectDefinition) {

		OpenAPISchemaFilter openAPISchemaFilter = new OpenAPISchemaFilter();

		openAPISchemaFilter.setApplicationPath(
			objectDefinition.getRESTContextPath());

		DTOProperty pageDTOProperty = new DTOProperty(
			new HashMap<>(), "PageObject", "Object");

		pageDTOProperty.setDTOProperties(
			Arrays.asList(
				new DTOProperty(new HashMap<>(), "items", "Array") {
					{
						setDTOProperties(
							Arrays.asList(
								new DTOProperty(
									new HashMap<>(), "ObjectEntry", "Object")));
					}
				}));

		openAPISchemaFilter.setDTOProperties(
			Arrays.asList(dtoProperty, pageDTOProperty));

		openAPISchemaFilter.setSchemaMappings(
			TreeMapBuilder.<String, String>create(
				Collections.reverseOrder()
			).put(
				"ObjectEntry", objectDefinition.getShortName()
			).put(
				"PageObject", "Page" + objectDefinition.getShortName()
			).put(
				"PageObjectEntry", "Page" + objectDefinition.getShortName()
			).build());

		return openAPISchemaFilter;
	}

	private String _getRef(Schema schema) {
		if (schema instanceof ArraySchema) {
			ArraySchema arraySchema = (ArraySchema)schema;

			Schema itemsSchema = arraySchema.getItems();

			return itemsSchema.get$ref();
		}

		return schema.get$ref();
	}

	private List<String> _getRequiredPropertySchemaNames(Schema schema) {
		List<String> requiredPropertySchemaNames = schema.getRequired();

		if (requiredPropertySchemaNames == null) {
			requiredPropertySchemaNames = Collections.emptyList();
		}

		return requiredPropertySchemaNames;
	}

	private final BundleContext _bundleContext;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final Map<String, String> _fieldNameMappings = HashMapBuilder.put(
		"createDate", "dateCreated"
	).put(
		"modifiedDate", "dateModified"
	).build();
	private final ObjectActionLocalService _objectActionLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryOpenAPIResourceProvider
		_objectEntryOpenAPIResourceProvider;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final OpenAPIResource _openAPIResource;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private class RelationshipDTOProperty extends DTOProperty {

		public RelationshipDTOProperty(
			Map<String, Object> extensions, String name,
			String relationshipName, String type) {

			super(extensions, name, type);

			_relationshipName = relationshipName;
		}

		public String getRelationshipName() {
			return _relationshipName;
		}

		private final String _relationshipName;

	}

}