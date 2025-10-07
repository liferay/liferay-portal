/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectFieldUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class ObjectFieldPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectFieldUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectField> iterator = _objectFields.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectField objectField = _persistence.create(pk);

		Assert.assertNotNull(objectField);

		Assert.assertEquals(objectField.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectField newObjectField = addObjectField();

		_persistence.remove(newObjectField);

		ObjectField existingObjectField = _persistence.fetchByPrimaryKey(
			newObjectField.getPrimaryKey());

		Assert.assertNull(existingObjectField);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectField();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectField newObjectField = _persistence.create(pk);

		newObjectField.setMvccVersion(RandomTestUtil.nextLong());

		newObjectField.setUuid(RandomTestUtil.randomString());

		newObjectField.setExternalReferenceCode(RandomTestUtil.randomString());

		newObjectField.setCompanyId(RandomTestUtil.nextLong());

		newObjectField.setUserId(RandomTestUtil.nextLong());

		newObjectField.setUserName(RandomTestUtil.randomString());

		newObjectField.setCreateDate(RandomTestUtil.nextDate());

		newObjectField.setModifiedDate(RandomTestUtil.nextDate());

		newObjectField.setListTypeDefinitionId(RandomTestUtil.nextLong());

		newObjectField.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectField.setBusinessType(RandomTestUtil.randomString());

		newObjectField.setDBColumnName(RandomTestUtil.randomString());

		newObjectField.setDBTableName(RandomTestUtil.randomString());

		newObjectField.setDBType(RandomTestUtil.randomString());

		newObjectField.setIndexed(RandomTestUtil.randomBoolean());

		newObjectField.setIndexedAsKeyword(RandomTestUtil.randomBoolean());

		newObjectField.setIndexedLanguageId(RandomTestUtil.randomString());

		newObjectField.setLabel(RandomTestUtil.randomString());

		newObjectField.setLocalized(RandomTestUtil.randomBoolean());

		newObjectField.setName(RandomTestUtil.randomString());

		newObjectField.setReadOnly(RandomTestUtil.randomString());

		newObjectField.setReadOnlyConditionExpression(
			RandomTestUtil.randomString());

		newObjectField.setRelationshipType(RandomTestUtil.randomString());

		newObjectField.setRequired(RandomTestUtil.randomBoolean());

		newObjectField.setState(RandomTestUtil.randomBoolean());

		newObjectField.setSystem(RandomTestUtil.randomBoolean());

		_objectFields.add(_persistence.update(newObjectField));

		ObjectField existingObjectField = _persistence.findByPrimaryKey(
			newObjectField.getPrimaryKey());

		Assert.assertEquals(
			existingObjectField.getMvccVersion(),
			newObjectField.getMvccVersion());
		Assert.assertEquals(
			existingObjectField.getUuid(), newObjectField.getUuid());
		Assert.assertEquals(
			existingObjectField.getExternalReferenceCode(),
			newObjectField.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectField.getObjectFieldId(),
			newObjectField.getObjectFieldId());
		Assert.assertEquals(
			existingObjectField.getCompanyId(), newObjectField.getCompanyId());
		Assert.assertEquals(
			existingObjectField.getUserId(), newObjectField.getUserId());
		Assert.assertEquals(
			existingObjectField.getUserName(), newObjectField.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectField.getCreateDate()),
			Time.getShortTimestamp(newObjectField.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectField.getModifiedDate()),
			Time.getShortTimestamp(newObjectField.getModifiedDate()));
		Assert.assertEquals(
			existingObjectField.getListTypeDefinitionId(),
			newObjectField.getListTypeDefinitionId());
		Assert.assertEquals(
			existingObjectField.getObjectDefinitionId(),
			newObjectField.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectField.getBusinessType(),
			newObjectField.getBusinessType());
		Assert.assertEquals(
			existingObjectField.getDBColumnName(),
			newObjectField.getDBColumnName());
		Assert.assertEquals(
			existingObjectField.getDBTableName(),
			newObjectField.getDBTableName());
		Assert.assertEquals(
			existingObjectField.getDBType(), newObjectField.getDBType());
		Assert.assertEquals(
			existingObjectField.isIndexed(), newObjectField.isIndexed());
		Assert.assertEquals(
			existingObjectField.isIndexedAsKeyword(),
			newObjectField.isIndexedAsKeyword());
		Assert.assertEquals(
			existingObjectField.getIndexedLanguageId(),
			newObjectField.getIndexedLanguageId());
		Assert.assertEquals(
			existingObjectField.getLabel(), newObjectField.getLabel());
		Assert.assertEquals(
			existingObjectField.isLocalized(), newObjectField.isLocalized());
		Assert.assertEquals(
			existingObjectField.getName(), newObjectField.getName());
		Assert.assertEquals(
			existingObjectField.getReadOnly(), newObjectField.getReadOnly());
		Assert.assertEquals(
			existingObjectField.getReadOnlyConditionExpression(),
			newObjectField.getReadOnlyConditionExpression());
		Assert.assertEquals(
			existingObjectField.getRelationshipType(),
			newObjectField.getRelationshipType());
		Assert.assertEquals(
			existingObjectField.isRequired(), newObjectField.isRequired());
		Assert.assertEquals(
			existingObjectField.isState(), newObjectField.isState());
		Assert.assertEquals(
			existingObjectField.isSystem(), newObjectField.isSystem());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByListTypeDefinitionId() throws Exception {
		_persistence.countByListTypeDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByListTypeDefinitionId(0L);
	}

	@Test
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByLTDI_S() throws Exception {
		_persistence.countByLTDI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByLTDI_S(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_BT() throws Exception {
		_persistence.countByODI_BT(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_BT(0L, "null");

		_persistence.countByODI_BT(0L, (String)null);
	}

	@Test
	public void testCountByODI_DTN() throws Exception {
		_persistence.countByODI_DTN(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_DTN(0L, "null");

		_persistence.countByODI_DTN(0L, (String)null);
	}

	@Test
	public void testCountByODI_I() throws Exception {
		_persistence.countByODI_I(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI_I(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_L() throws Exception {
		_persistence.countByODI_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_N() throws Exception {
		_persistence.countByODI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_N(0L, "null");

		_persistence.countByODI_N(0L, (String)null);
	}

	@Test
	public void testCountByODI_S() throws Exception {
		_persistence.countByODI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI_S(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C_ODI() throws Exception {
		_persistence.countByERC_C_ODI(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_C_ODI("null", 0L, 0L);

		_persistence.countByERC_C_ODI((String)null, 0L, 0L);
	}

	@Test
	public void testCountByODI_DBT_I() throws Exception {
		_persistence.countByODI_DBT_I(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByODI_DBT_I(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByODI_DBT_I(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_L_S() throws Exception {
		_persistence.countByODI_L_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByODI_L_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectField newObjectField = addObjectField();

		ObjectField existingObjectField = _persistence.findByPrimaryKey(
			newObjectField.getPrimaryKey());

		Assert.assertEquals(existingObjectField, newObjectField);
	}

	@Test(expected = NoSuchObjectFieldException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectField> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectField", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectFieldId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "listTypeDefinitionId", true,
			"objectDefinitionId", true, "businessType", true, "dbColumnName",
			true, "dbTableName", true, "dbType", true, "indexed", true,
			"indexedAsKeyword", true, "indexedLanguageId", true, "label", true,
			"localized", true, "name", true, "readOnly", true,
			"relationshipType", true, "required", true, "state", true, "system",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectField newObjectField = addObjectField();

		ObjectField existingObjectField = _persistence.fetchByPrimaryKey(
			newObjectField.getPrimaryKey());

		Assert.assertEquals(existingObjectField, newObjectField);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectField missingObjectField = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectField);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectField newObjectField1 = addObjectField();
		ObjectField newObjectField2 = addObjectField();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectField1.getPrimaryKey());
		primaryKeys.add(newObjectField2.getPrimaryKey());

		Map<Serializable, ObjectField> objectFields =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectFields.size());
		Assert.assertEquals(
			newObjectField1, objectFields.get(newObjectField1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectField2, objectFields.get(newObjectField2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectField> objectFields =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFields.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectField newObjectField = addObjectField();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectField.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectField> objectFields =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFields.size());
		Assert.assertEquals(
			newObjectField, objectFields.get(newObjectField.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectField> objectFields =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFields.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectField newObjectField = addObjectField();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectField.getPrimaryKey());

		Map<Serializable, ObjectField> objectFields =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFields.size());
		Assert.assertEquals(
			newObjectField, objectFields.get(newObjectField.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectFieldLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectField>() {

				@Override
				public void performAction(ObjectField objectField) {
					Assert.assertNotNull(objectField);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectField newObjectField = addObjectField();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectField.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectFieldId", newObjectField.getObjectFieldId()));

		List<ObjectField> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectField existingObjectField = result.get(0);

		Assert.assertEquals(existingObjectField, newObjectField);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectField.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectFieldId", RandomTestUtil.nextLong()));

		List<ObjectField> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectField newObjectField = addObjectField();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectField.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectFieldId"));

		Object newObjectFieldId = newObjectField.getObjectFieldId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectFieldId", new Object[] {newObjectFieldId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectFieldId = result.get(0);

		Assert.assertEquals(existingObjectFieldId, newObjectFieldId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectField.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectFieldId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectFieldId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectField newObjectField = addObjectField();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectField.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		ObjectField newObjectField = addObjectField();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectField.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectFieldId", newObjectField.getObjectFieldId()));

		List<ObjectField> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectField objectField) {
		Assert.assertEquals(
			Long.valueOf(objectField.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectField, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
		Assert.assertEquals(
			objectField.getName(),
			ReflectionTestUtil.invoke(
				objectField, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			objectField.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectField, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectField.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectField, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectField.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectField, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
	}

	protected ObjectField addObjectField() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectField objectField = _persistence.create(pk);

		objectField.setMvccVersion(RandomTestUtil.nextLong());

		objectField.setUuid(RandomTestUtil.randomString());

		objectField.setExternalReferenceCode(RandomTestUtil.randomString());

		objectField.setCompanyId(RandomTestUtil.nextLong());

		objectField.setUserId(RandomTestUtil.nextLong());

		objectField.setUserName(RandomTestUtil.randomString());

		objectField.setCreateDate(RandomTestUtil.nextDate());

		objectField.setModifiedDate(RandomTestUtil.nextDate());

		objectField.setListTypeDefinitionId(RandomTestUtil.nextLong());

		objectField.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectField.setBusinessType(RandomTestUtil.randomString());

		objectField.setDBColumnName(RandomTestUtil.randomString());

		objectField.setDBTableName(RandomTestUtil.randomString());

		objectField.setDBType(RandomTestUtil.randomString());

		objectField.setIndexed(RandomTestUtil.randomBoolean());

		objectField.setIndexedAsKeyword(RandomTestUtil.randomBoolean());

		objectField.setIndexedLanguageId(RandomTestUtil.randomString());

		objectField.setLabel(RandomTestUtil.randomString());

		objectField.setLocalized(RandomTestUtil.randomBoolean());

		objectField.setName(RandomTestUtil.randomString());

		objectField.setReadOnly(RandomTestUtil.randomString());

		objectField.setReadOnlyConditionExpression(
			RandomTestUtil.randomString());

		objectField.setRelationshipType(RandomTestUtil.randomString());

		objectField.setRequired(RandomTestUtil.randomBoolean());

		objectField.setState(RandomTestUtil.randomBoolean());

		objectField.setSystem(RandomTestUtil.randomBoolean());

		_objectFields.add(_persistence.update(objectField));

		return objectField;
	}

	private List<ObjectField> _objectFields = new ArrayList<ObjectField>();
	private ObjectFieldPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}