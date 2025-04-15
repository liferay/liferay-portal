/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.entity.v1_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.internal.odata.entity.ReferenceStringEntityField;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import jakarta.ws.rs.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Javier de Arcos
 */
public class ObjectEntryEntityModel implements EntityModel {

	public ObjectEntryEntityModel(
		ObjectDefinition objectDefinition, List<ObjectField> objectFields,
		boolean useLegacyStatus) {

		_useLegacyStatus = useLegacyStatus;

		_entityFieldsMap = _getStringEntityFieldsMap(
			objectDefinition, objectFields);

		_entityFieldsMaps.put(
			objectDefinition.getObjectDefinitionId(), _entityFieldsMap);

		List<ObjectRelationship> objectRelationships =
			ObjectRelationshipLocalServiceUtil.getAllObjectRelationships(
				objectDefinition.getObjectDefinitionId());

		for (ObjectRelationship objectRelationship : objectRelationships) {
			_entityFieldsMap.put(
				objectRelationship.getName(),
				_getComplexEntityField(objectDefinition, objectRelationship));
		}
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private ComplexEntityField _getComplexEntityField(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				objectDefinition, objectRelationship);

		return new ComplexEntityField(
			objectRelationship.getName(),
			_getObjectDefinitionEntityFieldsMap(relatedObjectDefinition),
			relatedObjectDefinition.getName());
	}

	private EntityField _getEntityField(ObjectField objectField) {
		if (_unsupportedBusinessTypes.contains(objectField.getBusinessType())) {
			return null;
		}

		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			return new DateTimeEntityField(
				objectField.getName(), locale -> objectField.getName(),
				locale -> objectField.getName());
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			return new CollectionEntityField(
				new StringEntityField(
					objectField.getName(), locale -> objectField.getName()));
		}

		if (Objects.equals(
				objectField.getDBType(),
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL) ||
			Objects.equals(
				objectField.getDBType(), ObjectFieldConstants.DB_TYPE_DOUBLE)) {

			return new DoubleEntityField(
				objectField.getName(), locale -> objectField.getName());
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_BOOLEAN)) {

			return new BooleanEntityField(
				objectField.getName(), locale -> objectField.getName());
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_CLOB) ||
				 Objects.equals(
					 objectField.getDBType(),
					 ObjectFieldConstants.DB_TYPE_STRING)) {

			return new StringEntityField(
				objectField.getName(), locale -> objectField.getName());
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_DATE)) {

			return new DateEntityField(
				objectField.getName(), locale -> objectField.getName(),
				locale -> objectField.getName());
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_INTEGER) ||
				 Objects.equals(
					 objectField.getDBType(),
					 ObjectFieldConstants.DB_TYPE_LONG)) {

			return new IntegerEntityField(
				objectField.getName(), locale -> objectField.getName());
		}

		throw new BadRequestException(
			"Unable to get entity field for object field " + objectField);
	}

	private Function<Locale, String> _getExternalReferenceCodeFunction() {
		return locale -> "externalReferenceCode";
	}

	private Map<String, EntityField> _getObjectDefinitionEntityFieldsMap(
		ObjectDefinition objectDefinition) {

		if (_entityFieldsMaps.containsKey(
				objectDefinition.getObjectDefinitionId())) {

			return _entityFieldsMaps.get(
				objectDefinition.getObjectDefinitionId());
		}

		Map<String, EntityField> entityFieldsMap = _getStringEntityFieldsMap(
			objectDefinition,
			ObjectFieldLocalServiceUtil.getObjectFields(
				objectDefinition.getObjectDefinitionId()));

		_entityFieldsMaps.put(
			objectDefinition.getObjectDefinitionId(), entityFieldsMap);

		for (ObjectRelationship objectRelationship :
				ObjectRelationshipLocalServiceUtil.getAllObjectRelationships(
					objectDefinition.getObjectDefinitionId())) {

			ComplexEntityField complexEntityField = _getComplexEntityField(
				objectDefinition, objectRelationship);

			entityFieldsMap.put(
				complexEntityField.getName(), complexEntityField);
		}

		return entityFieldsMap;
	}

	private Map<String, EntityField> _getStringEntityFieldsMap(
		ObjectDefinition objectDefinition, List<ObjectField> objectFields) {

		Map<String, EntityField> entityFieldsMap =
			HashMapBuilder.<String, EntityField>put(
				"creator", new StringEntityField("creator", locale -> "creator")
			).put(
				"creatorId",
				new IntegerEntityField("creatorId", locale -> Field.USER_ID)
			).put(
				"dateCreated",
				new DateTimeEntityField(
					"dateCreated", locale -> Field.CREATE_DATE,
					locale -> Field.CREATE_DATE)
			).put(
				"dateModified",
				new DateTimeEntityField(
					"dateModified", locale -> "modifiedDate",
					locale -> "modifiedDate")
			).put(
				"externalReferenceCode",
				() -> new StringEntityField(
					"externalReferenceCode",
					_getExternalReferenceCodeFunction())
			).put(
				"id", new IdEntityField("id", locale -> "id", String::valueOf)
			).put(
				"keywords",
				new CollectionEntityField(
					new StringEntityField(
						"keywords", locale -> "assetTagNames.lowercase"))
			).put(
				"status",
				() -> {
					IntegerEntityField statusEntityField =
						new IntegerEntityField(
							"status", locale -> Field.STATUS);

					if (_useLegacyStatus) {
						return new CollectionEntityField(statusEntityField);
					}

					return statusEntityField;
				}
			).put(
				"taxonomyCategoryIds",
				new CollectionEntityField(
					new IntegerEntityField(
						"taxonomyCategoryIds", locale -> "assetCategoryIds"))
			).put(
				"userId",
				new IntegerEntityField("userId", locale -> Field.USER_ID)
			).build();

		for (ObjectField objectField : objectFields) {
			if (objectField.isSystem() &&
				!objectDefinition.isModifiableAndSystem()) {

				continue;
			}

			if (!Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				EntityField entityField = _getEntityField(objectField);

				if (entityField != null) {
					entityFieldsMap.putIfAbsent(
						objectField.getName(), entityField);
				}

				continue;
			}

			String objectFieldName = objectField.getName();

			entityFieldsMap.put(
				objectFieldName,
				new IdEntityField(
					objectFieldName, locale -> objectFieldName,
					String::valueOf));

			String objectRelationshipERCObjectFieldName =
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.
						NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
					objectField);

			entityFieldsMap.put(
				objectRelationshipERCObjectFieldName,
				new ReferenceStringEntityField(
					objectRelationshipERCObjectFieldName,
					_getExternalReferenceCodeFunction(),
					objectFieldName.split(StringPool.UNDERLINE)[1] +
						"/externalReferenceCode"));

			String relationshipIdName = objectFieldName.substring(
				objectFieldName.lastIndexOf(StringPool.UNDERLINE) + 1);

			entityFieldsMap.put(
				relationshipIdName,
				new IdEntityField(
					relationshipIdName, locale -> objectFieldName,
					String::valueOf));
		}

		return entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;
	private final Map<Long, Map<String, EntityField>> _entityFieldsMaps =
		new HashMap<>();
	private final Set<String> _unsupportedBusinessTypes = SetUtil.fromArray(
		ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION,
		ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
		ObjectFieldConstants.BUSINESS_TYPE_FORMULA,
		ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT);
	private final boolean _useLegacyStatus;

}