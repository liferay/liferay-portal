/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.object.service.persistence.ObjectRelationshipUtil;
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
public class ObjectRelationshipPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectRelationshipUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectRelationship> iterator = _objectRelationships.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectRelationship objectRelationship = _persistence.create(pk);

		Assert.assertNotNull(objectRelationship);

		Assert.assertEquals(objectRelationship.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		_persistence.remove(newObjectRelationship);

		ObjectRelationship existingObjectRelationship =
			_persistence.fetchByPrimaryKey(
				newObjectRelationship.getPrimaryKey());

		Assert.assertNull(existingObjectRelationship);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectRelationship();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectRelationship newObjectRelationship = _persistence.create(pk);

		newObjectRelationship.setMvccVersion(RandomTestUtil.nextLong());

		newObjectRelationship.setUuid(RandomTestUtil.randomString());

		newObjectRelationship.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newObjectRelationship.setCompanyId(RandomTestUtil.nextLong());

		newObjectRelationship.setUserId(RandomTestUtil.nextLong());

		newObjectRelationship.setUserName(RandomTestUtil.randomString());

		newObjectRelationship.setCreateDate(RandomTestUtil.nextDate());

		newObjectRelationship.setModifiedDate(RandomTestUtil.nextDate());

		newObjectRelationship.setObjectDefinitionId1(RandomTestUtil.nextLong());

		newObjectRelationship.setObjectDefinitionId2(RandomTestUtil.nextLong());

		newObjectRelationship.setObjectFieldId2(RandomTestUtil.nextLong());

		newObjectRelationship.setParameterObjectFieldId(
			RandomTestUtil.nextLong());

		newObjectRelationship.setDeletionType(RandomTestUtil.randomString());

		newObjectRelationship.setDBTableName(RandomTestUtil.randomString());

		newObjectRelationship.setEdge(RandomTestUtil.randomBoolean());

		newObjectRelationship.setLabel(RandomTestUtil.randomString());

		newObjectRelationship.setName(RandomTestUtil.randomString());

		newObjectRelationship.setReverse(RandomTestUtil.randomBoolean());

		newObjectRelationship.setSystem(RandomTestUtil.randomBoolean());

		newObjectRelationship.setType(RandomTestUtil.randomString());

		_objectRelationships.add(_persistence.update(newObjectRelationship));

		ObjectRelationship existingObjectRelationship =
			_persistence.findByPrimaryKey(
				newObjectRelationship.getPrimaryKey());

		Assert.assertEquals(
			existingObjectRelationship.getMvccVersion(),
			newObjectRelationship.getMvccVersion());
		Assert.assertEquals(
			existingObjectRelationship.getUuid(),
			newObjectRelationship.getUuid());
		Assert.assertEquals(
			existingObjectRelationship.getExternalReferenceCode(),
			newObjectRelationship.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectRelationship.getObjectRelationshipId(),
			newObjectRelationship.getObjectRelationshipId());
		Assert.assertEquals(
			existingObjectRelationship.getCompanyId(),
			newObjectRelationship.getCompanyId());
		Assert.assertEquals(
			existingObjectRelationship.getUserId(),
			newObjectRelationship.getUserId());
		Assert.assertEquals(
			existingObjectRelationship.getUserName(),
			newObjectRelationship.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectRelationship.getCreateDate()),
			Time.getShortTimestamp(newObjectRelationship.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectRelationship.getModifiedDate()),
			Time.getShortTimestamp(newObjectRelationship.getModifiedDate()));
		Assert.assertEquals(
			existingObjectRelationship.getObjectDefinitionId1(),
			newObjectRelationship.getObjectDefinitionId1());
		Assert.assertEquals(
			existingObjectRelationship.getObjectDefinitionId2(),
			newObjectRelationship.getObjectDefinitionId2());
		Assert.assertEquals(
			existingObjectRelationship.getObjectFieldId2(),
			newObjectRelationship.getObjectFieldId2());
		Assert.assertEquals(
			existingObjectRelationship.getParameterObjectFieldId(),
			newObjectRelationship.getParameterObjectFieldId());
		Assert.assertEquals(
			existingObjectRelationship.getDeletionType(),
			newObjectRelationship.getDeletionType());
		Assert.assertEquals(
			existingObjectRelationship.getDBTableName(),
			newObjectRelationship.getDBTableName());
		Assert.assertEquals(
			existingObjectRelationship.isEdge(),
			newObjectRelationship.isEdge());
		Assert.assertEquals(
			existingObjectRelationship.getLabel(),
			newObjectRelationship.getLabel());
		Assert.assertEquals(
			existingObjectRelationship.getName(),
			newObjectRelationship.getName());
		Assert.assertEquals(
			existingObjectRelationship.isReverse(),
			newObjectRelationship.isReverse());
		Assert.assertEquals(
			existingObjectRelationship.isSystem(),
			newObjectRelationship.isSystem());
		Assert.assertEquals(
			existingObjectRelationship.getType(),
			newObjectRelationship.getType());
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
	public void testCountByObjectDefinitionId1() throws Exception {
		_persistence.countByObjectDefinitionId1(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId1(0L);
	}

	@Test
	public void testCountByObjectDefinitionId2() throws Exception {
		_persistence.countByObjectDefinitionId2(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId2(0L);
	}

	@Test
	public void testCountByObjectFieldId2() throws Exception {
		_persistence.countByObjectFieldId2(RandomTestUtil.nextLong());

		_persistence.countByObjectFieldId2(0L);
	}

	@Test
	public void testCountByParameterObjectFieldId() throws Exception {
		_persistence.countByParameterObjectFieldId(RandomTestUtil.nextLong());

		_persistence.countByParameterObjectFieldId(0L);
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByODI1_E() throws Exception {
		_persistence.countByODI1_E(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI1_E(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI1_N() throws Exception {
		_persistence.countByODI1_N(RandomTestUtil.nextLong(), "");

		_persistence.countByODI1_N(0L, "null");

		_persistence.countByODI1_N(0L, (String)null);
	}

	@Test
	public void testCountByODI1_R() throws Exception {
		_persistence.countByODI1_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI1_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI2_E() throws Exception {
		_persistence.countByODI2_E(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI2_E(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI2_R() throws Exception {
		_persistence.countByODI2_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI2_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByDTN_R() throws Exception {
		_persistence.countByDTN_R("", RandomTestUtil.randomBoolean());

		_persistence.countByDTN_R("null", RandomTestUtil.randomBoolean());

		_persistence.countByDTN_R((String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C_ODI1() throws Exception {
		_persistence.countByERC_C_ODI1(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_C_ODI1("null", 0L, 0L);

		_persistence.countByERC_C_ODI1((String)null, 0L, 0L);
	}

	@Test
	public void testCountByODI1_ODI2_T() throws Exception {
		_persistence.countByODI1_ODI2_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByODI1_ODI2_T(0L, 0L, "null");

		_persistence.countByODI1_ODI2_T(0L, 0L, (String)null);
	}

	@Test
	public void testCountByODI1_DT_R() throws Exception {
		_persistence.countByODI1_DT_R(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByODI1_DT_R(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByODI1_DT_R(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI1_R_T() throws Exception {
		_persistence.countByODI1_R_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByODI1_R_T(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByODI1_R_T(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByODI2_R_T() throws Exception {
		_persistence.countByODI2_R_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByODI2_R_T(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByODI2_R_T(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByODI1_ODI2_N_T() throws Exception {
		_persistence.countByODI1_ODI2_N_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByODI1_ODI2_N_T(0L, 0L, "null", "null");

		_persistence.countByODI1_ODI2_N_T(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testCountByODI1_ODI2_N_R_T() throws Exception {
		_persistence.countByODI1_ODI2_N_R_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.randomBoolean(), "");

		_persistence.countByODI1_ODI2_N_R_T(
			0L, 0L, "null", RandomTestUtil.randomBoolean(), "null");

		_persistence.countByODI1_ODI2_N_R_T(
			0L, 0L, (String)null, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		ObjectRelationship existingObjectRelationship =
			_persistence.findByPrimaryKey(
				newObjectRelationship.getPrimaryKey());

		Assert.assertEquals(existingObjectRelationship, newObjectRelationship);
	}

	@Test(expected = NoSuchObjectRelationshipException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectRelationship> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectRelationship", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectRelationshipId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "objectDefinitionId1", true,
			"objectDefinitionId2", true, "objectFieldId2", true,
			"parameterObjectFieldId", true, "deletionType", true, "dbTableName",
			true, "edge", true, "label", true, "name", true, "reverse", true,
			"system", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		ObjectRelationship existingObjectRelationship =
			_persistence.fetchByPrimaryKey(
				newObjectRelationship.getPrimaryKey());

		Assert.assertEquals(existingObjectRelationship, newObjectRelationship);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectRelationship missingObjectRelationship =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectRelationship);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectRelationship newObjectRelationship1 = addObjectRelationship();
		ObjectRelationship newObjectRelationship2 = addObjectRelationship();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectRelationship1.getPrimaryKey());
		primaryKeys.add(newObjectRelationship2.getPrimaryKey());

		Map<Serializable, ObjectRelationship> objectRelationships =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectRelationships.size());
		Assert.assertEquals(
			newObjectRelationship1,
			objectRelationships.get(newObjectRelationship1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectRelationship2,
			objectRelationships.get(newObjectRelationship2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectRelationship> objectRelationships =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectRelationships.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectRelationship newObjectRelationship = addObjectRelationship();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectRelationship.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectRelationship> objectRelationships =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectRelationships.size());
		Assert.assertEquals(
			newObjectRelationship,
			objectRelationships.get(newObjectRelationship.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectRelationship> objectRelationships =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectRelationships.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectRelationship.getPrimaryKey());

		Map<Serializable, ObjectRelationship> objectRelationships =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectRelationships.size());
		Assert.assertEquals(
			newObjectRelationship,
			objectRelationships.get(newObjectRelationship.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectRelationshipLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ObjectRelationship>() {

				@Override
				public void performAction(
					ObjectRelationship objectRelationship) {

					Assert.assertNotNull(objectRelationship);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectRelationship.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectRelationshipId",
				newObjectRelationship.getObjectRelationshipId()));

		List<ObjectRelationship> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectRelationship existingObjectRelationship = result.get(0);

		Assert.assertEquals(existingObjectRelationship, newObjectRelationship);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectRelationship.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectRelationshipId", RandomTestUtil.nextLong()));

		List<ObjectRelationship> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectRelationship.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectRelationshipId"));

		Object newObjectRelationshipId =
			newObjectRelationship.getObjectRelationshipId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectRelationshipId",
				new Object[] {newObjectRelationshipId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectRelationshipId = result.get(0);

		Assert.assertEquals(
			existingObjectRelationshipId, newObjectRelationshipId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectRelationship.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectRelationshipId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectRelationshipId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectRelationship newObjectRelationship = addObjectRelationship();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectRelationship.getPrimaryKey()));
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

		ObjectRelationship newObjectRelationship = addObjectRelationship();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectRelationship.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectRelationshipId",
				newObjectRelationship.getObjectRelationshipId()));

		List<ObjectRelationship> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectRelationship objectRelationship) {
		Assert.assertEquals(
			Long.valueOf(objectRelationship.getObjectFieldId2()),
			ReflectionTestUtil.<Long>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectFieldId2"));

		Assert.assertEquals(
			objectRelationship.getDBTableName(),
			ReflectionTestUtil.invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "dbTableName"));
		Assert.assertEquals(
			Boolean.valueOf(objectRelationship.getReverse()),
			ReflectionTestUtil.<Boolean>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "reverse"));

		Assert.assertEquals(
			objectRelationship.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectRelationship.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectRelationship.getObjectDefinitionId1()),
			ReflectionTestUtil.<Long>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId1"));

		Assert.assertEquals(
			Long.valueOf(objectRelationship.getObjectDefinitionId1()),
			ReflectionTestUtil.<Long>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId1"));
		Assert.assertEquals(
			Long.valueOf(objectRelationship.getObjectDefinitionId2()),
			ReflectionTestUtil.<Long>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId2"));
		Assert.assertEquals(
			objectRelationship.getName(),
			ReflectionTestUtil.invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Boolean.valueOf(objectRelationship.getReverse()),
			ReflectionTestUtil.<Boolean>invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "reverse"));
		Assert.assertEquals(
			objectRelationship.getType(),
			ReflectionTestUtil.invoke(
				objectRelationship, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected ObjectRelationship addObjectRelationship() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectRelationship objectRelationship = _persistence.create(pk);

		objectRelationship.setMvccVersion(RandomTestUtil.nextLong());

		objectRelationship.setUuid(RandomTestUtil.randomString());

		objectRelationship.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectRelationship.setCompanyId(RandomTestUtil.nextLong());

		objectRelationship.setUserId(RandomTestUtil.nextLong());

		objectRelationship.setUserName(RandomTestUtil.randomString());

		objectRelationship.setCreateDate(RandomTestUtil.nextDate());

		objectRelationship.setModifiedDate(RandomTestUtil.nextDate());

		objectRelationship.setObjectDefinitionId1(RandomTestUtil.nextLong());

		objectRelationship.setObjectDefinitionId2(RandomTestUtil.nextLong());

		objectRelationship.setObjectFieldId2(RandomTestUtil.nextLong());

		objectRelationship.setParameterObjectFieldId(RandomTestUtil.nextLong());

		objectRelationship.setDeletionType(RandomTestUtil.randomString());

		objectRelationship.setDBTableName(RandomTestUtil.randomString());

		objectRelationship.setEdge(RandomTestUtil.randomBoolean());

		objectRelationship.setLabel(RandomTestUtil.randomString());

		objectRelationship.setName(RandomTestUtil.randomString());

		objectRelationship.setReverse(RandomTestUtil.randomBoolean());

		objectRelationship.setSystem(RandomTestUtil.randomBoolean());

		objectRelationship.setType(RandomTestUtil.randomString());

		_objectRelationships.add(_persistence.update(objectRelationship));

		return objectRelationship;
	}

	private List<ObjectRelationship> _objectRelationships =
		new ArrayList<ObjectRelationship>();
	private ObjectRelationshipPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}