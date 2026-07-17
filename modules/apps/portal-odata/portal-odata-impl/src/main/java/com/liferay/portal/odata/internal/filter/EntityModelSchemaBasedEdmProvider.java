/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.internal.filter;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.server.core.SchemaBasedEdmProvider;

/**
 * Provides the Common Schema Definition Language (CSDL) for an Entity Data
 * Model (EDM) used by a service.
 *
 * <p>
 * To formalize the description of the resources they expose, OData services use
 * EDM as their underlying abstract data model. CSDL defines the entity model's
 * XML-based representation.
 * </p>
 *
 * @author David Arques
 */
public class EntityModelSchemaBasedEdmProvider extends SchemaBasedEdmProvider {

	public EntityModelSchemaBasedEdmProvider(EntityModel entityModel) {
		addSchema(
			_createCsdlSchema(
				_NAMESPACE, entityModel.getName(),
				_createCsdlComplexTypes(
					entityModel.getEntityFieldsMap(), _NAMESPACE),
				_createCsdlNavigationProperties(
					entityModel.getEntityRelationshipsMap()),
				_createCsdlProperties(
					entityModel.getEntityFieldsMap(), _NAMESPACE)));
	}

	private CsdlProperty _createCollectionCsdlProperty(
		EntityField entityField, FullQualifiedName fullQualifiedName) {

		CsdlProperty csdlProperty = new CsdlProperty();

		csdlProperty.setCollection(true);
		csdlProperty.setName(entityField.getName());
		csdlProperty.setType(fullQualifiedName);

		return csdlProperty;
	}

	private CsdlComplexType _createCsdlComplexType(
		int depth, EntityField entityField, String namespace,
		Map<String, ObjectValuePair<Integer, CsdlComplexType>>
			objectValuePairs) {

		if (!Objects.equals(entityField.getType(), EntityField.Type.COMPLEX)) {
			return null;
		}

		ComplexEntityField complexEntityField = (ComplexEntityField)entityField;

		CsdlComplexType csdlComplexType = _csdlComplexTypes.get(
			complexEntityField.getTypeKey());

		if (csdlComplexType != null) {
			return csdlComplexType;
		}

		csdlComplexType = new CsdlComplexType();

		_csdlComplexTypes.put(complexEntityField.getTypeKey(), csdlComplexType);

		csdlComplexType.setName(complexEntityField.getTypeKey());

		Map<String, EntityField> entityFieldsMap =
			complexEntityField.getEntityFieldsMap();

		List<CsdlProperty> csdlProperties = new ArrayList<>(
			entityFieldsMap.size());

		for (EntityField curEntityField : entityFieldsMap.values()) {
			CsdlProperty csdlProperty = _createCsdlProperty(
				namespace, curEntityField);

			if (csdlProperty != null) {
				csdlProperties.add(csdlProperty);

				if (Objects.equals(
						curEntityField.getType(), EntityField.Type.COMPLEX)) {

					ComplexEntityField curComplexEntityField =
						(ComplexEntityField)curEntityField;

					_handleComplexType(
						_createCsdlComplexType(
							depth++, curComplexEntityField, _NAMESPACE,
							objectValuePairs),
						depth, objectValuePairs);

					depth--;
				}
			}
		}

		csdlComplexType.setProperties(csdlProperties);

		return csdlComplexType;
	}

	private List<CsdlComplexType> _createCsdlComplexTypes(
		Map<String, EntityField> entityFieldsMap, String namespace) {

		Map<String, ObjectValuePair<Integer, CsdlComplexType>>
			objectValuePairs = new HashMap<>();

		for (EntityField entityField : entityFieldsMap.values()) {
			CsdlComplexType csdlComplexType = _createCsdlComplexType(
				1, entityField, namespace, objectValuePairs);

			if (csdlComplexType != null) {
				objectValuePairs.put(
					csdlComplexType.getName(),
					new ObjectValuePair<>(0, csdlComplexType));
			}
		}

		return TransformUtil.transform(
			objectValuePairs.values(), ObjectValuePair::getValue);
	}

	private CsdlEntityContainer _createCsdlEntityContainer(
		String namespace, String name) {

		CsdlEntityContainer csdlEntityContainer = new CsdlEntityContainer();

		csdlEntityContainer.setEntitySets(
			_createCsdlEntitySets(namespace, name));
		csdlEntityContainer.setName(name);

		return csdlEntityContainer;
	}

	private List<CsdlEntitySet> _createCsdlEntitySets(
		String namespace, String entityNameType) {

		CsdlEntitySet csdlEntitySet = new CsdlEntitySet();

		csdlEntitySet.setName(entityNameType);
		csdlEntitySet.setType(new FullQualifiedName(namespace, entityNameType));

		return Collections.singletonList(csdlEntitySet);
	}

	private CsdlEntityType _createCsdlEntityType(
		String name, List<CsdlNavigationProperty> csdlNavigationProperties,
		List<CsdlProperty> csdlProperties) {

		CsdlEntityType csdlEntityType = new CsdlEntityType();

		csdlEntityType.setName(name);
		csdlEntityType.setNavigationProperties(csdlNavigationProperties);
		csdlEntityType.setProperties(csdlProperties);

		return csdlEntityType;
	}

	private List<CsdlNavigationProperty> _createCsdlNavigationProperties(
		Map<String, EntityModel.EntityRelationship> entityRelationshipsMap) {

		List<CsdlNavigationProperty> csdlNavigationProperties = new ArrayList<>(
			entityRelationshipsMap.size());

		for (EntityModel.EntityRelationship entityRelationship :
				entityRelationshipsMap.values()) {

			CsdlNavigationProperty csdlNavigationProperty =
				new CsdlNavigationProperty();

			if (entityRelationship.getType() ==
					EntityModel.EntityRelationship.Type.COLLECTION) {

				csdlNavigationProperty.setCollection(true);
			}

			csdlNavigationProperty.setName(entityRelationship.getName());

			EntityModel entityModel = entityRelationship.getEntityModel();

			csdlNavigationProperty.setPartner(entityModel.getName());
			csdlNavigationProperty.setType(_getFullQualifiedName(entityModel));

			csdlNavigationProperties.add(csdlNavigationProperty);
		}

		return csdlNavigationProperties;
	}

	private List<CsdlProperty> _createCsdlProperties(
		Map<String, EntityField> entityFieldsMap, String namespace) {

		List<CsdlProperty> csdlProperties = new ArrayList<>(
			entityFieldsMap.size());

		for (EntityField entityField : entityFieldsMap.values()) {
			CsdlProperty csdlProperty = _createCsdlProperty(
				namespace, entityField);

			if (csdlProperty != null) {
				csdlProperties.add(csdlProperty);
			}
		}

		return csdlProperties;
	}

	private CsdlProperty _createCsdlProperty(
		String namespace, EntityField entityField) {

		if (Objects.equals(entityField.getType(), EntityField.Type.COMPLEX)) {
			ComplexEntityField complexEntityField =
				(ComplexEntityField)entityField;

			CsdlProperty csdlProperty = new CsdlProperty();

			csdlProperty.setName(complexEntityField.getName());
			csdlProperty.setType(
				new FullQualifiedName(
					namespace, complexEntityField.getTypeKey()));

			return csdlProperty;
		}

		FullQualifiedName fullQualifiedName = _getFullQualifiedName(
			entityField);

		if (fullQualifiedName == null) {
			return null;
		}

		if (Objects.equals(
				entityField.getType(), EntityField.Type.COLLECTION)) {

			return _createCollectionCsdlProperty(
				entityField, fullQualifiedName);
		}

		return _createPrimitiveCsdlProperty(entityField, fullQualifiedName);
	}

	private CsdlSchema _createCsdlSchema(
		String namespace, String name, List<CsdlComplexType> csdlComplexTypes,
		List<CsdlNavigationProperty> csdlNavigationProperties,
		List<CsdlProperty> csdlProperties) {

		CsdlSchema csdlSchema = new CsdlSchema();

		csdlSchema.setComplexTypes(csdlComplexTypes);
		csdlSchema.setEntityContainer(
			_createCsdlEntityContainer(namespace, name));
		csdlSchema.setEntityTypes(
			Collections.singletonList(
				_createCsdlEntityType(
					name, csdlNavigationProperties, csdlProperties)));
		csdlSchema.setNamespace(namespace);

		return csdlSchema;
	}

	private CsdlProperty _createPrimitiveCsdlProperty(
		EntityField entityField, FullQualifiedName fullQualifiedName) {

		CsdlProperty csdlProperty = new CsdlProperty();

		csdlProperty.setName(entityField.getName());
		csdlProperty.setType(fullQualifiedName);

		return csdlProperty;
	}

	private FullQualifiedName _getFullQualifiedName(EntityField entityField) {
		if (Objects.equals(entityField.getType(), EntityField.Type.BOOLEAN)) {
			return EdmPrimitiveTypeKind.Boolean.getFullQualifiedName();
		}
		else if (Objects.equals(
					entityField.getType(), EntityField.Type.COLLECTION)) {

			CollectionEntityField collectionEntityField =
				(CollectionEntityField)entityField;

			return _getFullQualifiedName(
				collectionEntityField.getEntityField());
		}
		else if (Objects.equals(entityField.getType(), EntityField.Type.DATE)) {
			return EdmPrimitiveTypeKind.Date.getFullQualifiedName();
		}
		else if (Objects.equals(
					entityField.getType(), EntityField.Type.DATE_TIME)) {

			return EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName();
		}
		else if (Objects.equals(
					entityField.getType(), EntityField.Type.DOUBLE)) {

			return EdmPrimitiveTypeKind.Double.getFullQualifiedName();
		}
		else if (Objects.equals(entityField.getType(), EntityField.Type.ID) ||
				 Objects.equals(
					 entityField.getType(), EntityField.Type.STRING)) {

			return EdmPrimitiveTypeKind.String.getFullQualifiedName();
		}
		else if (Objects.equals(
					entityField.getType(), EntityField.Type.INTEGER)) {

			return EdmPrimitiveTypeKind.Int64.getFullQualifiedName();
		}

		return null;
	}

	private FullQualifiedName _getFullQualifiedName(EntityModel entityModel) {
		return new FullQualifiedName(_NAMESPACE + "." + entityModel.getName());
	}

	private void _handleComplexType(
		CsdlComplexType csdlComplexType, int depth,
		Map<String, ObjectValuePair<Integer, CsdlComplexType>>
			objectValuePairs) {

		objectValuePairs.compute(
			csdlComplexType.getName(),
			(key, value) -> {
				if ((value == null) || (value.getKey() >= depth)) {
					return new ObjectValuePair<>(depth, csdlComplexType);
				}

				return value;
			});
	}

	private static final String _NAMESPACE = "HypermediaRestApis";

	private final HashMap<String, CsdlComplexType> _csdlComplexTypes =
		new HashMap<>();

}