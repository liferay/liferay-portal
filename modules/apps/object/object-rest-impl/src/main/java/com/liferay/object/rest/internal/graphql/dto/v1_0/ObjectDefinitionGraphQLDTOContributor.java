/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.graphql.dto.v1_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryRelatedObjectsResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;
import com.liferay.portal.vulcan.graphql.dto.GraphQLDTOContributor;
import com.liferay.portal.vulcan.graphql.dto.GraphQLDTOProperty;
import com.liferay.portal.vulcan.graphql.dto.v1_0.Creator;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;
import com.liferay.portal.vulcan.list.type.ListEntry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.sql.Blob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Javier de Arcos
 */
public class ObjectDefinitionGraphQLDTOContributor
	implements GraphQLDTOContributor<Map<String, Object>, Map<String, Object>> {

	public static ObjectDefinitionGraphQLDTOContributor of(
		EntityModelProvider entityModelProvider,
		ExtensionProviderRegistry extensionProviderRegistry,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryManager objectEntryManager,
		ObjectFieldLocalService objectFieldLocalService,
		List<ObjectField> objectFields,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		List<ObjectRelationship> objectRelationships,
		ObjectScopeProvider objectScopeProvider,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		List<GraphQLDTOProperty> graphQLDTOProperties = new ArrayList<>();

		graphQLDTOProperties.add(
			GraphQLDTOProperty.of(
				objectDefinition.getPKObjectFieldName(), true, Long.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("creator", true, Creator.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("dateCreated", true, Date.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("dateModified", true, Date.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("externalReferenceCode", String.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("status", true, String.class));
		graphQLDTOProperties.add(
			GraphQLDTOProperty.of("statusCode", Integer.class));

		List<GraphQLDTOProperty> relationshipGraphQLDTOProperties =
			new ArrayList<>();

		if (objectFields == null) {
			objectFields = objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());
		}

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				graphQLDTOProperties.add(
					GraphQLDTOProperty.of(
						objectField.getName(), FileEntry.class));
			}
			else if (objectField.getListTypeDefinitionId() != 0) {
				graphQLDTOProperties.add(
					GraphQLDTOProperty.of(
						objectField.getName(), ListEntry.class));
			}
			else if (Objects.equals(
						objectField.getRelationshipType(), "oneToMany")) {

				String objectFieldName = objectField.getName();

				String relationshipIdName =
					objectFieldName.split(StringPool.UNDERLINE)[1];

				graphQLDTOProperties.add(
					GraphQLDTOProperty.of(relationshipIdName, Long.class));

				String relationshipName = StringUtil.replaceLast(
					relationshipIdName, "Id", "");

				relationshipGraphQLDTOProperties.add(
					GraphQLDTOProperty.of(relationshipName, Map.class));
			}
			else {
				if (objectField.isLocalized()) {
					graphQLDTOProperties.add(
						GraphQLDTOProperty.of(
							objectField.getI18nObjectFieldName(), true,
							Map.class));
				}

				graphQLDTOProperties.add(
					GraphQLDTOProperty.of(
						objectField.getName(),
						_typedClasses.getOrDefault(
							objectField.getDBType(), Object.class)));
			}
		}

		if (objectRelationships == null) {
			objectRelationships =
				objectRelationshipLocalService.getObjectRelationships(
					objectDefinition.getObjectDefinitionId());
		}

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!Objects.equals(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

				continue;
			}

			graphQLDTOProperties.add(
				GraphQLDTOProperty.of(
					objectRelationship.getName(), Long.class));
			relationshipGraphQLDTOProperties.add(
				GraphQLDTOProperty.of(objectRelationship.getName(), Map.class));
		}

		return new ObjectDefinitionGraphQLDTOContributor(
			objectDefinition.getCompanyId(),
			entityModelProvider.getEntityModel(objectDefinition),
			extensionProviderRegistry, graphQLDTOProperties,
			StringUtil.removeSubstring(
				objectDefinition.getPKObjectFieldName(), "c_"),
			objectDefinition, objectDefinitionLocalService, objectEntryManager,
			objectRelationshipLocalService, objectScopeProvider,
			relationshipGraphQLDTOProperties, objectDefinition.getShortName(),
			systemObjectDefinitionManagerRegistry, objectDefinition.getName());
	}

	@Override
	public Map<String, Object> createDTO(
			Map<String, Object> dto, DTOConverterContext dtoConverterContext)
		throws Exception {

		return _toMap(
			_objectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition, _toObjectEntry(dto),
				(String)dtoConverterContext.getAttribute("scopeKey")));
	}

	@Override
	public boolean deleteDTO(long id) throws Exception {
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(_objectEntryManager);

		defaultObjectEntryManager.deleteObjectEntry(_objectDefinition, id);

		return true;
	}

	@Override
	public String getApplicationName() {
		return _objectDefinition.getOSGiJaxRsName();
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public Map<String, Object> getDTO(
			DTOConverterContext dtoConverterContext, long id)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(_objectEntryManager);

		return _toMap(
			defaultObjectEntryManager.getObjectEntry(
				dtoConverterContext, _objectDefinition, id));
	}

	@Override
	public Page<Map<String, Object>> getDTOs(
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			(Long)dtoConverterContext.getAttribute("companyId"),
			_objectDefinition,
			(String)dtoConverterContext.getAttribute("scopeKey"), aggregation,
			dtoConverterContext,
			(String)dtoConverterContext.getAttribute("filter"), pagination,
			search, sorts);

		return Page.of(
			page.getActions(), page.getFacets(),
			TransformUtil.transform(page.getItems(), this::_toMap), pagination,
			page.getTotalCount());
	}

	@Override
	public EntityModel getEntityModel() {
		return _entityModel;
	}

	@Override
	public List<GraphQLDTOProperty> getGraphQLDTOProperties() {
		return _graphQLDTOProperties;
	}

	@Override
	public String getIdName() {
		return _idName;
	}

	@Override
	public List<GraphQLDTOProperty> getRelationshipGraphQLDTOProperties() {
		return _relationshipGraphQLDTOProperties;
	}

	@Override
	public <T> T getRelationshipValue(
			DTOConverterContext dtoConverterContext, long id,
			Class<T> relationshipClass, String relationshipName)
		throws Exception {

		if (!Objects.equals(relationshipClass, Map.class)) {
			return null;
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(_objectEntryManager);

		ObjectEntry objectEntry = defaultObjectEntryManager.getObjectEntry(
			dtoConverterContext, _objectDefinition, id);

		Map<String, Object> properties = objectEntry.getProperties();

		String objectRelationshipObjectFieldName =
			_getObjectRelationshipObjectFieldName(properties, relationshipName);

		if (Validator.isNull(objectRelationshipObjectFieldName)) {
			Page<ObjectEntry> page =
				defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
					dtoConverterContext, _objectDefinition, id,
					relationshipName,
					Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS));

			return (T)TransformUtil.transform(
				page.getItems(), itemObjectEntry -> _toMap(itemObjectEntry));
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByObjectDefinitionId(
					_objectDefinition.getObjectDefinitionId(),
					relationshipName);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		long relatedObjectEntryId = MapUtil.getLong(
			properties, objectRelationshipObjectFieldName);

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			return (T)_toExtendedEntity(
				dtoConverterContext, objectDefinition, relatedObjectEntryId,
				systemObjectDefinitionManager);
		}

		return (T)_toMap(
			defaultObjectEntryManager.fetchObjectEntry(
				dtoConverterContext, objectDefinition, relatedObjectEntryId),
			objectRelationshipObjectFieldName);
	}

	@Override
	public Class<?> getResourceClass(Operation operation) {
		ObjectValuePair<Class<?>, String> objectValuePair =
			_objectValuePairs.get(operation);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getKey();
	}

	@Override
	public Method getResourceMethod(Operation operation) {
		ObjectValuePair<Class<?>, String> objectValuePair =
			_objectValuePairs.get(operation);

		if (objectValuePair == null) {
			return null;
		}

		Class<?> clazz = objectValuePair.getKey();

		String methodName = objectValuePair.getValue();

		for (Method method : clazz.getMethods()) {
			if (Objects.equals(method.getName(), methodName)) {
				return method;
			}
		}

		return null;
	}

	@Override
	public String getResourceName() {
		return _resourceName;
	}

	@Override
	public String getTypeName() {
		return _typeName;
	}

	@Override
	public boolean hasScope() {
		return _objectScopeProvider.isGroupAware();
	}

	@Override
	public Map<String, Object> updateDTO(
			Map<String, Object> dto, DTOConverterContext dtoConverterContext,
			long id)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(_objectEntryManager);

		return _toMap(
			defaultObjectEntryManager.updateObjectEntry(
				dtoConverterContext, _objectDefinition, id,
				_toObjectEntry(dto)));
	}

	private ObjectDefinitionGraphQLDTOContributor(
		long companyId, EntityModel entityModel,
		ExtensionProviderRegistry extensionProviderRegistry,
		List<GraphQLDTOProperty> graphQLDTOProperties, String idName,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryManager objectEntryManager,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProvider objectScopeProvider,
		List<GraphQLDTOProperty> relationshipGraphQLDTOProperties,
		String resourceName,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry,
		String typeName) {

		_companyId = companyId;
		_entityModel = entityModel;
		_extensionProviderRegistry = extensionProviderRegistry;
		_graphQLDTOProperties = graphQLDTOProperties;
		_idName = idName;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryManager = objectEntryManager;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProvider = objectScopeProvider;
		_relationshipGraphQLDTOProperties = relationshipGraphQLDTOProperties;
		_resourceName = resourceName;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
		_typeName = typeName;
	}

	private String _getObjectRelationshipObjectFieldName(
		Map<String, Object> properties, String relationshipName) {

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Matcher matcher = _relationshipIdNamePattern.matcher(
				entry.getKey());
			String key = entry.getKey();

			if (matcher.matches() && key.contains(relationshipName) &&
				(entry.getValue() instanceof Long)) {

				return key;
			}
		}

		return null;
	}

	private ExtendedEntity _toExtendedEntity(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long relatedObjectEntryId,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(_objectEntryManager);

		Object systemObjectEntry =
			defaultObjectEntryManager.getSystemObjectEntry(
				dtoConverterContext, objectDefinition, relatedObjectEntryId);

		Map<String, Serializable> nestedFieldsRelatedProperties = null;

		DTOConverter<BaseModel<?>, ?> dtoConverter =
			ObjectEntryDTOConverterUtil.getDTOConverter(
				dtoConverterContext.getDTOConverterRegistry(),
				systemObjectDefinitionManager);

		EntityExtensionHandler entityExtensionHandler =
			ExtensionUtil.getEntityExtensionHandler(
				dtoConverter.getExternalDTOClassName(),
				objectDefinition.getCompanyId(), _extensionProviderRegistry);

		if (entityExtensionHandler != null) {
			nestedFieldsRelatedProperties =
				entityExtensionHandler.getExtendedProperties(
					objectDefinition.getCompanyId(),
					dtoConverterContext.getUserId(), systemObjectEntry);
		}

		return ExtendedEntity.extend(
			systemObjectEntry, nestedFieldsRelatedProperties, null);
	}

	private Map<String, Object> _toMap(ObjectEntry objectEntry) {
		return _toMap(objectEntry, getIdName());
	}

	private Map<String, Object> _toMap(
		ObjectEntry objectEntry, String objectEntryIdName) {

		if (objectEntry == null) {
			return null;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		properties.put(objectEntryIdName, objectEntry.getId());

		properties.put("creator", objectEntry.getCreator());
		properties.put("dateCreated", objectEntry.getDateCreated());
		properties.put("dateModified", objectEntry.getDateModified());
		properties.put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode());
		properties.put("id", objectEntry.getId());

		Status status = objectEntry.getStatus();

		properties.put("status", status.getLabel());
		properties.put("statusCode", status.getCode());

		return properties;
	}

	private ObjectEntry _toObjectEntry(Map<String, Object> map) {
		ObjectEntry objectEntry = new ObjectEntry();

		if (map == null) {
			return objectEntry;
		}

		objectEntry.setId(() -> (Long)map.get(getIdName()));
		objectEntry.setStatus(
			() -> {
				if (map.get("statusCode") == null) {
					return null;
				}

				int statusCode = (int)map.get("statusCode");

				return new Status() {
					{
						setCode(() -> statusCode);
						setLabel(
							() -> WorkflowConstants.getStatusLabel(statusCode));
					}
				};
			});

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (Objects.equals(entry.getKey(), "statusCode")) {
				continue;
			}

			properties.put(entry.getKey(), entry.getValue());
		}

		return objectEntry;
	}

	private static final Map<Operation, ObjectValuePair<Class<?>, String>>
		_objectValuePairs =
			HashMapBuilder.<Operation, ObjectValuePair<Class<?>, String>>put(
				Operation.CREATE,
				new ObjectValuePair<>(
					ObjectEntryResourceImpl.class, "postObjectEntry")
			).put(
				Operation.DELETE,
				new ObjectValuePair<>(
					ObjectEntryResourceImpl.class, "deleteObjectEntry")
			).put(
				Operation.GET,
				new ObjectValuePair<>(
					ObjectEntryResourceImpl.class, "getObjectEntry")
			).put(
				Operation.GET_RELATIONSHIP,
				new ObjectValuePair<>(
					ObjectEntryRelatedObjectsResourceImpl.class,
					"getCurrentObjectEntriesObjectRelationshipNamePage")
			).put(
				Operation.LIST,
				new ObjectValuePair<>(
					ObjectEntryResourceImpl.class, "getObjectEntriesPage")
			).put(
				Operation.UPDATE,
				new ObjectValuePair<>(
					ObjectEntryResourceImpl.class, "putObjectEntry")
			).build();
	private static final Pattern _relationshipIdNamePattern = Pattern.compile(
		"r_.+_.+Id");
	private static final Map<String, Class<?>> _typedClasses =
		HashMapBuilder.<String, Class<?>>put(
			"BigDecimal", BigDecimal.class
		).put(
			"Blob", Blob.class
		).put(
			"Boolean", Boolean.class
		).put(
			"Date", Date.class
		).put(
			"Double", Double.class
		).put(
			"Integer", Integer.class
		).put(
			"Long", Long.class
		).put(
			"String", String.class
		).build();

	private final long _companyId;
	private final EntityModel _entityModel;
	private final ExtensionProviderRegistry _extensionProviderRegistry;
	private final List<GraphQLDTOProperty> _graphQLDTOProperties;
	private final String _idName;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProvider _objectScopeProvider;
	private final List<GraphQLDTOProperty> _relationshipGraphQLDTOProperties;
	private final String _resourceName;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private final String _typeName;

}