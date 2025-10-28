/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.entity.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class ObjectEntryEntityModelTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		for (ObjectRelationship objectRelationship : _objectRelationships) {
			_objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);
		}

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}

		_serviceTrackerMap.close();
	}

	@Test
	public void testGetEntityFieldsMap() throws Exception {
		String value = ObjectDefinitionTestUtil.getRandomName();

		List<ObjectField> customObjectFields = Arrays.asList(
			_createObjectField(ObjectFieldConstants.DB_TYPE_BIG_DECIMAL),
			_createObjectField(ObjectFieldConstants.DB_TYPE_BOOLEAN),
			_createObjectField(ObjectFieldConstants.DB_TYPE_CLOB),
			_createObjectField(ObjectFieldConstants.DB_TYPE_DATE),
			_createObjectField(ObjectFieldConstants.DB_TYPE_DOUBLE),
			_createObjectField(ObjectFieldConstants.DB_TYPE_INTEGER),
			_createObjectField(ObjectFieldConstants.DB_TYPE_LONG),
			_createObjectField(ObjectFieldConstants.DB_TYPE_STRING));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			value, customObjectFields);

		ObjectDefinition relatedObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(), customObjectFields);

		ObjectRelationship manyToManyObjectRelationship =
			_addObjectRelationship(
				objectDefinition, relatedObjectDefinition,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		ObjectRelationship manyToOneObjectRelationship = _addObjectRelationship(
			relatedObjectDefinition, objectDefinition,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship oneToManyObjectRelationship = _addObjectRelationship(
			objectDefinition, relatedObjectDefinition,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_assertEquals(
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
				new StringEntityField(
					"externalReferenceCode", locale -> "externalReferenceCode")
			).put(
				"id", new IdEntityField("id", locale -> "id", String::valueOf)
			).put(
				"keywords",
				new CollectionEntityField(
					new StringEntityField(
						"keywords", locale -> "assetTagNames.lowercase"))
			).put(
				"status",
				new IntegerEntityField("status", locale -> Field.STATUS)
			).put(
				"taxonomyCategoryIds",
				new CollectionEntityField(
					new IntegerEntityField(
						"taxonomyCategoryIds", locale -> "assetCategoryIds"))
			).put(
				"userId",
				new IntegerEntityField("userId", locale -> Field.USER_ID)
			).putAll(
				_getExpectedObjectFieldsEntityFieldsMap(customObjectFields)
			).putAll(
				_getExpectedRelationshipFieldsEntityFieldsMap(
					manyToManyObjectRelationship, relatedObjectDefinition)
			).putAll(
				_getExpectedRelationshipFieldsEntityFieldsMap(
					manyToOneObjectRelationship, relatedObjectDefinition)
			).putAll(
				_getExpectedRelationshipFieldsEntityFieldsMap(
					oneToManyObjectRelationship, relatedObjectDefinition)
			).build(),
			_getObjectDefinitionEntityFieldsMap(objectDefinition));
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				relatedObjectDefinition.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false, type, null);

		_objectRelationships.add(objectRelationship);

		return objectRelationship;
	}

	private void _assertEquals(
		Map<String, EntityField> expectedEntityFieldsMap,
		Map<String, EntityField> actualEntityFieldsMap) {

		Assert.assertEquals(
			actualEntityFieldsMap.toString(), expectedEntityFieldsMap.size(),
			actualEntityFieldsMap.size());

		for (Map.Entry<String, EntityField> entry :
				expectedEntityFieldsMap.entrySet()) {

			EntityField expectedEntityField = entry.getValue();
			EntityField actualEntityField = actualEntityFieldsMap.get(
				entry.getKey());

			Assert.assertEquals(
				actualEntityFieldsMap.toString(), expectedEntityField.getName(),
				actualEntityField.getName());
			Assert.assertEquals(
				actualEntityField.toString(), expectedEntityField.getType(),
				actualEntityField.getType());
		}
	}

	private ObjectField _createObjectField(String dbType) {
		return new ObjectFieldBuilder(
		).dbType(
			dbType
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"a" + RandomTestUtil.randomString()
		).build();
	}

	private Map<String, EntityField> _getExpectedObjectFieldsEntityFieldsMap(
		List<ObjectField> customObjectFields) {

		Map<String, EntityField> entityFieldsMap = new HashMap<>();

		for (ObjectField customObjectField : customObjectFields) {
			EntityField entityField = _toExpectedEntityField(customObjectField);

			entityFieldsMap.put(entityField.getName(), entityField);
		}

		return entityFieldsMap;
	}

	private Map<String, EntityField>
		_getExpectedRelationshipFieldsEntityFieldsMap(
			ObjectRelationship objectRelationship,
			ObjectDefinition relatedObjectDefinition) {

		Map<String, EntityField> entityFieldsMap = new HashMap<>();

		if (StringUtil.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			entityFieldsMap.put(
				objectRelationship.getName(),
				new ComplexEntityField(
					objectRelationship.getName(), Collections.emptyList()));
		}
		else if (StringUtil.equals(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			if (objectRelationship.getObjectDefinitionId1() ==
					relatedObjectDefinition.getObjectDefinitionId()) {

				String pkObjectFieldName =
					relatedObjectDefinition.getPKObjectFieldName();
				String prefix = StringBundler.concat(
					"r_", objectRelationship.getName(), "_");

				String expectedObjectFieldName = prefix + pkObjectFieldName;

				entityFieldsMap.put(
					expectedObjectFieldName,
					new IdEntityField(
						expectedObjectFieldName,
						locale -> expectedObjectFieldName, String::valueOf));

				String expectedObjectRelationshipERCObjectFieldName =
					prefix +
						StringUtil.replaceLast(pkObjectFieldName, "Id", "ERC");

				entityFieldsMap.put(
					expectedObjectRelationshipERCObjectFieldName,
					new StringEntityField(
						expectedObjectRelationshipERCObjectFieldName,
						locale -> expectedObjectFieldName));

				String expectedRelatedObjectDefinitionIdObjectFieldName =
					pkObjectFieldName.replaceFirst("c_", "");

				entityFieldsMap.put(
					expectedRelatedObjectDefinitionIdObjectFieldName,
					new IdEntityField(
						expectedRelatedObjectDefinitionIdObjectFieldName,
						locale -> expectedObjectFieldName, String::valueOf));

				entityFieldsMap.put(
					objectRelationship.getName(),
					new ComplexEntityField(
						objectRelationship.getName(), Collections.emptyList()));
			}
			else {
				entityFieldsMap.put(
					objectRelationship.getName(),
					new ComplexEntityField(
						objectRelationship.getName(), Collections.emptyList()));
			}
		}
		else {
			throw new IllegalStateException();
		}

		return entityFieldsMap;
	}

	private Map<String, EntityField> _getObjectDefinitionEntityFieldsMap(
			ObjectDefinition objectDefinition)
		throws Exception {

		Map<String, EntityField> entityFieldsMap = null;

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryEntityModelTest.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(), ObjectEntryResource.class,
			"entity.class.name");

		ObjectEntryResource objectEntryResource = _serviceTrackerMap.getService(
			StringBundler.concat(
				ObjectEntry.class.getName(), StringPool.POUND,
				StringUtil.toLowerCase(objectDefinition.getShortName())));

		if (objectEntryResource instanceof EntityModelResource) {
			Class<?> clazz = objectEntryResource.getClass();

			Method method = clazz.getMethod(
				"setObjectDefinition", ObjectDefinition.class);

			method.invoke(objectEntryResource, objectDefinition);

			EntityModelResource entityModelResource =
				(EntityModelResource)objectEntryResource;

			EntityModel entityModel = entityModelResource.getEntityModel(null);

			entityFieldsMap = entityModel.getEntityFieldsMap();
		}

		return entityFieldsMap;
	}

	private ObjectDefinition _publishObjectDefinition(
			String objectDefinitionName, List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(objectDefinitionName),
				objectDefinitionName, null, null,
				LocalizedMapUtil.getLocalizedMap(objectDefinitionName), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields, Collections.emptyList());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private EntityField _toExpectedEntityField(ObjectField objectField) {
		return new EntityField(
			objectField.getName(),
			_objectFieldDBTypeEntityFieldTypeMap.get(objectField.getDBType()),
			locale -> objectField.getName(), locale -> objectField.getName(),
			String::valueOf);
	}

	private static final Map<String, EntityField.Type>
		_objectFieldDBTypeEntityFieldTypeMap = HashMapBuilder.put(
			ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, EntityField.Type.DOUBLE
		).put(
			ObjectFieldConstants.DB_TYPE_BOOLEAN, EntityField.Type.BOOLEAN
		).put(
			ObjectFieldConstants.DB_TYPE_CLOB, EntityField.Type.STRING
		).put(
			ObjectFieldConstants.DB_TYPE_DATE, EntityField.Type.DATE
		).put(
			ObjectFieldConstants.DB_TYPE_DOUBLE, EntityField.Type.DOUBLE
		).put(
			ObjectFieldConstants.DB_TYPE_INTEGER, EntityField.Type.INTEGER
		).put(
			ObjectFieldConstants.DB_TYPE_LONG, EntityField.Type.INTEGER
		).put(
			ObjectFieldConstants.DB_TYPE_STRING, EntityField.Type.STRING
		).build();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private final List<ObjectRelationship> _objectRelationships =
		new ArrayList<>();
	private ServiceTrackerMap<String, ObjectEntryResource> _serviceTrackerMap;

}