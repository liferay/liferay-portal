/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.odata.entity;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carlos Correa
 */
public class APISchemaEntityModel implements EntityModel {

	public APISchemaEntityModel(
		EntityModel entityModel, APIApplication.Schema schema) {

		_entityModel = entityModel;
		_schema = schema;
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		Map<String, EntityField> entityModelEntityFieldsMap =
			_entityModel.getEntityFieldsMap();

		if (MapUtil.isEmpty(entityModelEntityFieldsMap)) {
			return entityModelEntityFieldsMap;
		}

		Map<String, EntityField> entityFieldsMap = new HashMap<>();

		for (APIApplication.Property property : _schema.getProperties()) {
			EntityField entityField = _findDeepestEntityField(
				entityModelEntityFieldsMap, property);

			if ((entityField == null) ||
				(entityField instanceof ComplexEntityField)) {

				continue;
			}

			entityFieldsMap.put(
				property.getName(),
				_toAPIPropertyEntityField(entityField, property));
		}

		return entityFieldsMap;
	}

	public Map<String, EntityRelationship> getEntityRelationshipsMap() {
		return _entityModel.getEntityRelationshipsMap();
	}

	@Override
	public String getName() {
		return _entityModel.getName();
	}

	private EntityField _findDeepestEntityField(
		Map<String, EntityField> entityFieldsMap,
		APIApplication.Property property) {

		if (ListUtil.isEmpty(property.getObjectRelationshipNames())) {
			return entityFieldsMap.get(property.getSourceFieldName());
		}

		EntityField entityField = null;

		for (String objectRelationshipName :
				property.getObjectRelationshipNames()) {

			entityField = entityFieldsMap.get(objectRelationshipName);

			if (!(entityField instanceof ComplexEntityField)) {
				return null;
			}

			ComplexEntityField complexEntityField =
				(ComplexEntityField)entityField;

			entityFieldsMap = complexEntityField.getEntityFieldsMap();
		}

		return entityFieldsMap.get(property.getSourceFieldName());
	}

	private EntityField _toAPIPropertyEntityField(
		EntityField entityField, APIApplication.Property property) {

		if (Objects.equals(
				entityField.getType(), EntityField.Type.COLLECTION)) {

			CollectionEntityField collectionEntityField =
				(CollectionEntityField)entityField;

			return new CollectionEntityField(
				_toAPIPropertyEntityField(
					collectionEntityField.getEntityField(), property));
		}

		return new APIPropertyEntityField(
			_toInternalFieldName(entityField, property), property.getName(),
			entityField.getType(), locale -> property.getName(),
			locale -> property.getName(), locale -> property.getName());
	}

	private String _toInternalFieldName(
		EntityField entityField, APIApplication.Property property) {

		if (ListUtil.isEmpty(property.getObjectRelationshipNames())) {
			return entityField.getName();
		}

		String prefix = ListUtil.toString(
			property.getObjectRelationshipNames(), (String)null,
			StringPool.SLASH);

		return prefix + StringPool.SLASH + entityField.getName();
	}

	private final EntityModel _entityModel;
	private final APIApplication.Schema _schema;

}