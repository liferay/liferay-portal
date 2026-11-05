/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.helper;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.odata.entity.APIPropertyEntityField;
import com.liferay.headless.builder.internal.odata.entity.APISchemaEntityModel;
import com.liferay.headless.builder.internal.odata.filter.expression.APISchemaTranslatorExpressionVisitor;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.filter.parser.ObjectDefinitionFilterParser;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.factory.ExpressionFactory;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SortUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Luis Miguel Barcos
 * @author Carlos Correa
 * @author Alejandro Tardín
 */
public class EndpointHelper {

	public EndpointHelper(
		EntityModelProvider entityModelProvider,
		ExpressionFactory expressionFactory,
		ObjectDefinitionFilterParser objectDefinitionFilterParser,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryHelper objectEntryHelper,
		SortParserProvider sortParserProvider) {

		_entityModelProvider = entityModelProvider;
		_expressionFactory = expressionFactory;
		_objectDefinitionFilterParser = objectDefinitionFilterParser;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryHelper = objectEntryHelper;
		_sortParserProvider = sortParserProvider;
	}

	public Map<String, Object> getResponseEntityMap(
			long companyId, String pathParameter, String pathParameterValue,
			APIApplication.Schema schema, String scopeKey)
		throws Exception {

		ObjectEntry objectEntry = null;

		Set<String> relationshipsNames = new HashSet<>();

		for (APIApplication.Property property : schema.getProperties()) {
			relationshipsNames.addAll(property.getObjectRelationshipNames());
		}

		if (Objects.equals(
				pathParameter, HeadlessBuilderConstants.PATH_PARAMETER_ERC)) {

			objectEntry = _objectEntryHelper.getObjectEntry(
				companyId, ListUtil.fromCollection(relationshipsNames),
				schema.getMainObjectDefinitionExternalReferenceCode(),
				pathParameterValue, scopeKey);
		}
		else if (Objects.equals(
					pathParameter,
					HeadlessBuilderConstants.PATH_PARAMETER_ID)) {

			objectEntry = _objectEntryHelper.getObjectEntry(
				companyId, ListUtil.fromCollection(relationshipsNames),
				GetterUtil.getLong(pathParameterValue),
				schema.getMainObjectDefinitionExternalReferenceCode());
		}
		else {
			String filterString = StringBundler.concat(
				pathParameter, " eq '", pathParameterValue, "'");

			List<ObjectEntry> objectEntries =
				_objectEntryHelper.getObjectEntries(
					companyId, filterString,
					ListUtil.fromCollection(relationshipsNames),
					schema.getMainObjectDefinitionExternalReferenceCode(),
					scopeKey);

			if (objectEntries.isEmpty()) {
				throw new NoSuchObjectEntryException(
					"No object entry exists with the filter " + filterString);
			}

			objectEntry = objectEntries.get(0);
		}

		return _getResponseEntityMap(objectEntry, schema);
	}

	public Page<Map<String, Object>> getResponseEntityMapsPage(
			AcceptLanguage acceptLanguage, long companyId,
			APIApplication.Endpoint endpoint, String filterString,
			Pagination pagination, String scopeKey, String sortString)
		throws Exception {

		List<Map<String, Object>> responseEntityMaps = new ArrayList<>();

		Set<String> relationshipsNames = new HashSet<>();

		APIApplication.Schema schema = endpoint.getResponseSchema();

		for (APIApplication.Property property : schema.getProperties()) {
			relationshipsNames.addAll(property.getObjectRelationshipNames());
		}

		Page<ObjectEntry> objectEntriesPage =
			_objectEntryHelper.getObjectEntriesPage(
				companyId, _getExpression(companyId, endpoint, filterString),
				ListUtil.fromCollection(relationshipsNames), pagination,
				schema.getMainObjectDefinitionExternalReferenceCode(), scopeKey,
				_getSorts(acceptLanguage, companyId, endpoint, sortString));

		for (ObjectEntry objectEntry : objectEntriesPage.getItems()) {
			responseEntityMaps.add(_getResponseEntityMap(objectEntry, schema));
		}

		return Page.of(
			responseEntityMaps, pagination, objectEntriesPage.getTotalCount());
	}

	public Map<String, Object> postObjectEntry(
			long companyId, Map<String, Object> properties,
			APIApplication.Schema requestSchema,
			APIApplication.Schema responseSchema, String scopeKey)
		throws Exception {

		ObjectEntry objectEntry = new ObjectEntry();

		Map<String, Object> objectEntryProperties = new HashMap<>();

		for (APIApplication.Property property : requestSchema.getProperties()) {
			_populateObjectEntryProperties(
				objectEntryProperties, properties, property);
		}

		objectEntry.setProperties(() -> objectEntryProperties);

		return _getResponseEntityMap(
			_objectEntryHelper.addObjectEntry(
				companyId,
				requestSchema.getMainObjectDefinitionExternalReferenceCode(),
				objectEntry, scopeKey),
			responseSchema);
	}

	private EntityModel _getEntityModel(
			long companyId, APIApplication.Schema schema)
		throws Exception {

		return _entityModelProvider.getEntityModel(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					schema.getMainObjectDefinitionExternalReferenceCode(),
					companyId));
	}

	private Expression _getExpression(
			long companyId, APIApplication.Endpoint endpoint,
			String filterString)
		throws Exception {

		APIApplication.Filter filter = endpoint.getFilter();

		if ((filter == null) && Validator.isNull(filterString)) {
			return null;
		}

		Expression endpointFilterExpression = null;

		APIApplication.Schema schema = endpoint.getResponseSchema();

		String objectDefinitionExternalReferenceCode =
			schema.getMainObjectDefinitionExternalReferenceCode();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode, companyId);

		EntityModel entityModel = _entityModelProvider.getEntityModel(
			objectDefinition);

		if (filter != null) {
			endpointFilterExpression = _objectDefinitionFilterParser.parse(
				entityModel, filter.getODataFilterString(), objectDefinition);
		}

		Expression requestFilterExpression = null;

		if (Validator.isNotNull(filterString)) {
			EntityModel apiSchemaEntityModel = new APISchemaEntityModel(
				entityModel, endpoint.getResponseSchema());

			Expression expression = _objectDefinitionFilterParser.parse(
				apiSchemaEntityModel, filterString, objectDefinition);

			try {
				requestFilterExpression = expression.accept(
					new APISchemaTranslatorExpressionVisitor(
						apiSchemaEntityModel, _expressionFactory));
			}
			catch (ExpressionVisitException expressionVisitException) {
				throw new InvalidFilterException(
					expressionVisitException.getMessage(),
					expressionVisitException);
			}
		}

		if (endpointFilterExpression == null) {
			return requestFilterExpression;
		}
		else if (requestFilterExpression == null) {
			return endpointFilterExpression;
		}

		return _expressionFactory.createBinaryExpression(
			endpointFilterExpression, BinaryExpression.Operation.AND,
			requestFilterExpression);
	}

	private Map<String, Object> _getObjectEntryProperties(
		ObjectEntry objectEntry) {

		return HashMapBuilder.<String, Object>putAll(
			objectEntry.getProperties()
		).put(
			"createDate", objectEntry.getDateCreated()
		).put(
			"creator", objectEntry.getCreator()
		).put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"id", objectEntry.getId()
		).put(
			"modifiedDate", objectEntry.getDateModified()
		).build();
	}

	private Object _getPropertyValue(
		ObjectEntry objectEntry, APIApplication.Property property) {

		if (property.getType() == APIApplication.Property.Type.RECORD) {
			Map<String, Object> properties = new HashMap<>();

			for (APIApplication.Property childProperty :
					property.getProperties()) {

				properties.put(
					childProperty.getName(),
					_getPropertyValue(objectEntry, childProperty));
			}

			return properties;
		}

		Map<String, Object> objectEntryProperties = _getObjectEntryProperties(
			objectEntry);

		List<String> objectRelationshipNames =
			property.getObjectRelationshipNames();

		if (objectRelationshipNames.isEmpty()) {
			return objectEntryProperties.get(property.getSourceFieldName());
		}

		return _getRelatedObjectValue(
			objectEntry, property, objectRelationshipNames);
	}

	private Object _getRelatedObjectValue(
		ObjectEntry objectEntry, APIApplication.Property property,
		List<String> relationshipsNames) {

		if (objectEntry == null) {
			return Collections.emptyList();
		}

		if (relationshipsNames.isEmpty()) {
			return objectEntry.getPropertyValue(property.getSourceFieldName());
		}

		List<Object> values = new ArrayList<>();

		ObjectEntry[] relatedObjectEntries = null;

		Map<String, Object> properties = objectEntry.getProperties();

		Object relationshipNameValue = properties.get(
			relationshipsNames.remove(0));

		if (relationshipNameValue instanceof ObjectEntry[]) {
			relatedObjectEntries = (ObjectEntry[])relationshipNameValue;
		}
		else {
			relatedObjectEntries = new ObjectEntry[] {
				(ObjectEntry)relationshipNameValue
			};
		}

		for (ObjectEntry relatedObjectEntry : relatedObjectEntries) {
			Object value = _getRelatedObjectValue(
				relatedObjectEntry, property,
				new ArrayList<>(relationshipsNames));

			if (value instanceof Collection<?>) {
				values.addAll((Collection<?>)value);
			}
			else {
				values.add(value);
			}
		}

		return values;
	}

	private Map<String, Object> _getResponseEntityMap(
		ObjectEntry objectEntry, APIApplication.Schema schema) {

		if (schema == null) {
			return null;
		}

		Map<String, Object> responseEntityMap = new HashMap<>();

		for (APIApplication.Property property : schema.getProperties()) {
			responseEntityMap.put(
				property.getName(), _getPropertyValue(objectEntry, property));
		}

		return responseEntityMap;
	}

	private Sort[] _getSorts(
			AcceptLanguage acceptLanguage, long companyId,
			APIApplication.Endpoint endpoint, String sortString)
		throws Exception {

		APIApplication.Schema responseSchema = endpoint.getResponseSchema();

		if (Validator.isNotNull(sortString)) {
			APISchemaEntityModel apiSchemaEntityModel =
				new APISchemaEntityModel(
					_getEntityModel(companyId, responseSchema), responseSchema);

			Map<String, EntityField> entityFieldsMap =
				apiSchemaEntityModel.getEntityFieldsMap();

			return TransformUtil.transform(
				SortUtil.getSorts(
					acceptLanguage, apiSchemaEntityModel,
					_sortParserProvider.provide(apiSchemaEntityModel),
					sortString),
				sort -> {
					APIPropertyEntityField apiPropertyEntityField =
						(APIPropertyEntityField)entityFieldsMap.get(
							sort.getFieldName());

					sort.setFieldName(apiPropertyEntityField.getInternalName());
					sort.setFieldPath(apiPropertyEntityField.getInternalName());

					return sort;
				},
				Sort.class);
		}

		APIApplication.Sort sort = endpoint.getSort();

		if (sort == null) {
			return null;
		}

		return SortUtil.getSorts(
			acceptLanguage, _getEntityModel(companyId, responseSchema),
			_sortParserProvider.provide(
				_getEntityModel(companyId, responseSchema)),
			sort.getODataSortString());
	}

	private void _populateObjectEntryProperties(
		Map<String, Object> objectEntryProperties,
		Map<String, Object> properties, APIApplication.Property property) {

		if (property.getType() == APIApplication.Property.Type.RECORD) {
			for (APIApplication.Property childProperty :
					property.getProperties()) {

				_populateObjectEntryProperties(
					objectEntryProperties,
					(Map<String, Object>)properties.get(property.getName()),
					childProperty);
			}
		}
		else {
			Object value = properties.get(property.getName());

			if (value != null) {
				objectEntryProperties.put(property.getSourceFieldName(), value);
			}
		}
	}

	private final EntityModelProvider _entityModelProvider;
	private final ExpressionFactory _expressionFactory;
	private final ObjectDefinitionFilterParser _objectDefinitionFilterParser;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryHelper _objectEntryHelper;
	private final SortParserProvider _sortParserProvider;

}