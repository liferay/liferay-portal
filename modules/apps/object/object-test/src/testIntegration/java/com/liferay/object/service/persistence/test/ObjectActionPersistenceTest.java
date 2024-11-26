/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectActionException;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.service.ObjectActionLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectActionPersistence;
import com.liferay.object.service.persistence.ObjectActionUtil;
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
public class ObjectActionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectActionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectAction> iterator = _objectActions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectAction objectAction = _persistence.create(pk);

		Assert.assertNotNull(objectAction);

		Assert.assertEquals(objectAction.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		_persistence.remove(newObjectAction);

		ObjectAction existingObjectAction = _persistence.fetchByPrimaryKey(
			newObjectAction.getPrimaryKey());

		Assert.assertNull(existingObjectAction);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectAction();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectAction newObjectAction = _persistence.create(pk);

		newObjectAction.setMvccVersion(RandomTestUtil.nextLong());

		newObjectAction.setUuid(RandomTestUtil.randomString());

		newObjectAction.setExternalReferenceCode(RandomTestUtil.randomString());

		newObjectAction.setCompanyId(RandomTestUtil.nextLong());

		newObjectAction.setUserId(RandomTestUtil.nextLong());

		newObjectAction.setUserName(RandomTestUtil.randomString());

		newObjectAction.setCreateDate(RandomTestUtil.nextDate());

		newObjectAction.setModifiedDate(RandomTestUtil.nextDate());

		newObjectAction.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectAction.setActive(RandomTestUtil.randomBoolean());

		newObjectAction.setConditionExpression(RandomTestUtil.randomString());

		newObjectAction.setDescription(RandomTestUtil.randomString());

		newObjectAction.setErrorMessage(RandomTestUtil.randomString());

		newObjectAction.setLabel(RandomTestUtil.randomString());

		newObjectAction.setName(RandomTestUtil.randomString());

		newObjectAction.setObjectActionExecutorKey(
			RandomTestUtil.randomString());

		newObjectAction.setObjectActionTriggerKey(
			RandomTestUtil.randomString());

		newObjectAction.setParameters(RandomTestUtil.randomString());

		newObjectAction.setSystem(RandomTestUtil.randomBoolean());

		newObjectAction.setStatus(RandomTestUtil.nextInt());

		_objectActions.add(_persistence.update(newObjectAction));

		ObjectAction existingObjectAction = _persistence.findByPrimaryKey(
			newObjectAction.getPrimaryKey());

		Assert.assertEquals(
			existingObjectAction.getMvccVersion(),
			newObjectAction.getMvccVersion());
		Assert.assertEquals(
			existingObjectAction.getUuid(), newObjectAction.getUuid());
		Assert.assertEquals(
			existingObjectAction.getExternalReferenceCode(),
			newObjectAction.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectAction.getObjectActionId(),
			newObjectAction.getObjectActionId());
		Assert.assertEquals(
			existingObjectAction.getCompanyId(),
			newObjectAction.getCompanyId());
		Assert.assertEquals(
			existingObjectAction.getUserId(), newObjectAction.getUserId());
		Assert.assertEquals(
			existingObjectAction.getUserName(), newObjectAction.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectAction.getCreateDate()),
			Time.getShortTimestamp(newObjectAction.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectAction.getModifiedDate()),
			Time.getShortTimestamp(newObjectAction.getModifiedDate()));
		Assert.assertEquals(
			existingObjectAction.getObjectDefinitionId(),
			newObjectAction.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectAction.isActive(), newObjectAction.isActive());
		Assert.assertEquals(
			existingObjectAction.getConditionExpression(),
			newObjectAction.getConditionExpression());
		Assert.assertEquals(
			existingObjectAction.getDescription(),
			newObjectAction.getDescription());
		Assert.assertEquals(
			existingObjectAction.getErrorMessage(),
			newObjectAction.getErrorMessage());
		Assert.assertEquals(
			existingObjectAction.getLabel(), newObjectAction.getLabel());
		Assert.assertEquals(
			existingObjectAction.getName(), newObjectAction.getName());
		Assert.assertEquals(
			existingObjectAction.getObjectActionExecutorKey(),
			newObjectAction.getObjectActionExecutorKey());
		Assert.assertEquals(
			existingObjectAction.getObjectActionTriggerKey(),
			newObjectAction.getObjectActionTriggerKey());
		Assert.assertEquals(
			existingObjectAction.getParameters(),
			newObjectAction.getParameters());
		Assert.assertEquals(
			existingObjectAction.isSystem(), newObjectAction.isSystem());
		Assert.assertEquals(
			existingObjectAction.getStatus(), newObjectAction.getStatus());
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
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByODI_N() throws Exception {
		_persistence.countByODI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_N(0L, "null");

		_persistence.countByODI_N(0L, (String)null);
	}

	@Test
	public void testCountByA_OAEK() throws Exception {
		_persistence.countByA_OAEK(RandomTestUtil.randomBoolean(), "");

		_persistence.countByA_OAEK(RandomTestUtil.randomBoolean(), "null");

		_persistence.countByA_OAEK(
			RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByERC_C_ODI() throws Exception {
		_persistence.countByERC_C_ODI(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_C_ODI("null", 0L, 0L);

		_persistence.countByERC_C_ODI((String)null, 0L, 0L);
	}

	@Test
	public void testCountByC_A_OATK() throws Exception {
		_persistence.countByC_A_OATK(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByC_A_OATK(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByC_A_OATK(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByO_A_OATK() throws Exception {
		_persistence.countByO_A_OATK(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByO_A_OATK(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByO_A_OATK(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByODI_A_N_OATK() throws Exception {
		_persistence.countByODI_A_N_OATK(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "", "");

		_persistence.countByODI_A_N_OATK(
			0L, RandomTestUtil.randomBoolean(), "null", "null");

		_persistence.countByODI_A_N_OATK(
			0L, RandomTestUtil.randomBoolean(), (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		ObjectAction existingObjectAction = _persistence.findByPrimaryKey(
			newObjectAction.getPrimaryKey());

		Assert.assertEquals(existingObjectAction, newObjectAction);
	}

	@Test(expected = NoSuchObjectActionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectAction> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectAction", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectActionId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "objectDefinitionId", true, "active", true,
			"description", true, "errorMessage", true, "label", true, "name",
			true, "objectActionExecutorKey", true, "objectActionTriggerKey",
			true, "system", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		ObjectAction existingObjectAction = _persistence.fetchByPrimaryKey(
			newObjectAction.getPrimaryKey());

		Assert.assertEquals(existingObjectAction, newObjectAction);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectAction missingObjectAction = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectAction);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectAction newObjectAction1 = addObjectAction();
		ObjectAction newObjectAction2 = addObjectAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectAction1.getPrimaryKey());
		primaryKeys.add(newObjectAction2.getPrimaryKey());

		Map<Serializable, ObjectAction> objectActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectActions.size());
		Assert.assertEquals(
			newObjectAction1,
			objectActions.get(newObjectAction1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectAction2,
			objectActions.get(newObjectAction2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectAction> objectActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectAction newObjectAction = addObjectAction();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectAction.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectAction> objectActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectActions.size());
		Assert.assertEquals(
			newObjectAction,
			objectActions.get(newObjectAction.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectAction> objectActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectAction.getPrimaryKey());

		Map<Serializable, ObjectAction> objectActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectActions.size());
		Assert.assertEquals(
			newObjectAction,
			objectActions.get(newObjectAction.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectActionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectAction>() {

				@Override
				public void performAction(ObjectAction objectAction) {
					Assert.assertNotNull(objectAction);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectAction.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectActionId", newObjectAction.getObjectActionId()));

		List<ObjectAction> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectAction existingObjectAction = result.get(0);

		Assert.assertEquals(existingObjectAction, newObjectAction);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectAction.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectActionId", RandomTestUtil.nextLong()));

		List<ObjectAction> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectAction.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectActionId"));

		Object newObjectActionId = newObjectAction.getObjectActionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectActionId", new Object[] {newObjectActionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectActionId = result.get(0);

		Assert.assertEquals(existingObjectActionId, newObjectActionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectAction.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectActionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectActionId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectAction newObjectAction = addObjectAction();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectAction.getPrimaryKey()));
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

		ObjectAction newObjectAction = addObjectAction();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectAction.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectActionId", newObjectAction.getObjectActionId()));

		List<ObjectAction> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectAction objectAction) {
		Assert.assertEquals(
			Long.valueOf(objectAction.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
		Assert.assertEquals(
			objectAction.getName(),
			ReflectionTestUtil.invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			objectAction.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectAction.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectAction.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));

		Assert.assertEquals(
			Long.valueOf(objectAction.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
		Assert.assertEquals(
			Boolean.valueOf(objectAction.getActive()),
			ReflectionTestUtil.<Boolean>invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "active_"));
		Assert.assertEquals(
			objectAction.getName(),
			ReflectionTestUtil.invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			objectAction.getObjectActionTriggerKey(),
			ReflectionTestUtil.invoke(
				objectAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectActionTriggerKey"));
	}

	protected ObjectAction addObjectAction() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectAction objectAction = _persistence.create(pk);

		objectAction.setMvccVersion(RandomTestUtil.nextLong());

		objectAction.setUuid(RandomTestUtil.randomString());

		objectAction.setExternalReferenceCode(RandomTestUtil.randomString());

		objectAction.setCompanyId(RandomTestUtil.nextLong());

		objectAction.setUserId(RandomTestUtil.nextLong());

		objectAction.setUserName(RandomTestUtil.randomString());

		objectAction.setCreateDate(RandomTestUtil.nextDate());

		objectAction.setModifiedDate(RandomTestUtil.nextDate());

		objectAction.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectAction.setActive(RandomTestUtil.randomBoolean());

		objectAction.setConditionExpression(RandomTestUtil.randomString());

		objectAction.setDescription(RandomTestUtil.randomString());

		objectAction.setErrorMessage(RandomTestUtil.randomString());

		objectAction.setLabel(RandomTestUtil.randomString());

		objectAction.setName(RandomTestUtil.randomString());

		objectAction.setObjectActionExecutorKey(RandomTestUtil.randomString());

		objectAction.setObjectActionTriggerKey(RandomTestUtil.randomString());

		objectAction.setParameters(RandomTestUtil.randomString());

		objectAction.setSystem(RandomTestUtil.randomBoolean());

		objectAction.setStatus(RandomTestUtil.nextInt());

		_objectActions.add(_persistence.update(objectAction));

		return objectAction;
	}

	private List<ObjectAction> _objectActions = new ArrayList<ObjectAction>();
	private ObjectActionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}