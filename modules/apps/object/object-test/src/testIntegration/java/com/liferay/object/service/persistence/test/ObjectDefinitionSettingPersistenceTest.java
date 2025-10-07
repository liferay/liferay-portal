/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectDefinitionSettingException;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionSettingLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectDefinitionSettingPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionSettingUtil;
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
public class ObjectDefinitionSettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectDefinitionSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectDefinitionSetting> iterator =
			_objectDefinitionSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinitionSetting objectDefinitionSetting = _persistence.create(
			pk);

		Assert.assertNotNull(objectDefinitionSetting);

		Assert.assertEquals(objectDefinitionSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		_persistence.remove(newObjectDefinitionSetting);

		ObjectDefinitionSetting existingObjectDefinitionSetting =
			_persistence.fetchByPrimaryKey(
				newObjectDefinitionSetting.getPrimaryKey());

		Assert.assertNull(existingObjectDefinitionSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectDefinitionSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinitionSetting newObjectDefinitionSetting =
			_persistence.create(pk);

		newObjectDefinitionSetting.setMvccVersion(RandomTestUtil.nextLong());

		newObjectDefinitionSetting.setUuid(RandomTestUtil.randomString());

		newObjectDefinitionSetting.setCompanyId(RandomTestUtil.nextLong());

		newObjectDefinitionSetting.setUserId(RandomTestUtil.nextLong());

		newObjectDefinitionSetting.setUserName(RandomTestUtil.randomString());

		newObjectDefinitionSetting.setCreateDate(RandomTestUtil.nextDate());

		newObjectDefinitionSetting.setModifiedDate(RandomTestUtil.nextDate());

		newObjectDefinitionSetting.setObjectDefinitionId(
			RandomTestUtil.nextLong());

		newObjectDefinitionSetting.setName(RandomTestUtil.randomString());

		newObjectDefinitionSetting.setValue(RandomTestUtil.randomString());

		_objectDefinitionSettings.add(
			_persistence.update(newObjectDefinitionSetting));

		ObjectDefinitionSetting existingObjectDefinitionSetting =
			_persistence.findByPrimaryKey(
				newObjectDefinitionSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectDefinitionSetting.getMvccVersion(),
			newObjectDefinitionSetting.getMvccVersion());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getUuid(),
			newObjectDefinitionSetting.getUuid());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getObjectDefinitionSettingId(),
			newObjectDefinitionSetting.getObjectDefinitionSettingId());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getCompanyId(),
			newObjectDefinitionSetting.getCompanyId());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getUserId(),
			newObjectDefinitionSetting.getUserId());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getUserName(),
			newObjectDefinitionSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectDefinitionSetting.getCreateDate()),
			Time.getShortTimestamp(newObjectDefinitionSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectDefinitionSetting.getModifiedDate()),
			Time.getShortTimestamp(
				newObjectDefinitionSetting.getModifiedDate()));
		Assert.assertEquals(
			existingObjectDefinitionSetting.getObjectDefinitionId(),
			newObjectDefinitionSetting.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getName(),
			newObjectDefinitionSetting.getName());
		Assert.assertEquals(
			existingObjectDefinitionSetting.getValue(),
			newObjectDefinitionSetting.getValue());
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
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByODI_N() throws Exception {
		_persistence.countByODI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_N(0L, "null");

		_persistence.countByODI_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		ObjectDefinitionSetting existingObjectDefinitionSetting =
			_persistence.findByPrimaryKey(
				newObjectDefinitionSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectDefinitionSetting, newObjectDefinitionSetting);
	}

	@Test(expected = NoSuchObjectDefinitionSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectDefinitionSetting>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"ObjectDefinitionSetting", "mvccVersion", true, "uuid", true,
			"objectDefinitionSettingId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"objectDefinitionId", true, "name", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		ObjectDefinitionSetting existingObjectDefinitionSetting =
			_persistence.fetchByPrimaryKey(
				newObjectDefinitionSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectDefinitionSetting, newObjectDefinitionSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinitionSetting missingObjectDefinitionSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectDefinitionSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectDefinitionSetting newObjectDefinitionSetting1 =
			addObjectDefinitionSetting();
		ObjectDefinitionSetting newObjectDefinitionSetting2 =
			addObjectDefinitionSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinitionSetting1.getPrimaryKey());
		primaryKeys.add(newObjectDefinitionSetting2.getPrimaryKey());

		Map<Serializable, ObjectDefinitionSetting> objectDefinitionSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectDefinitionSettings.size());
		Assert.assertEquals(
			newObjectDefinitionSetting1,
			objectDefinitionSettings.get(
				newObjectDefinitionSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectDefinitionSetting2,
			objectDefinitionSettings.get(
				newObjectDefinitionSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectDefinitionSetting> objectDefinitionSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectDefinitionSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinitionSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectDefinitionSetting> objectDefinitionSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectDefinitionSettings.size());
		Assert.assertEquals(
			newObjectDefinitionSetting,
			objectDefinitionSettings.get(
				newObjectDefinitionSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectDefinitionSetting> objectDefinitionSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectDefinitionSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinitionSetting.getPrimaryKey());

		Map<Serializable, ObjectDefinitionSetting> objectDefinitionSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectDefinitionSettings.size());
		Assert.assertEquals(
			newObjectDefinitionSetting,
			objectDefinitionSettings.get(
				newObjectDefinitionSetting.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectDefinitionSettingLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ObjectDefinitionSetting>() {

				@Override
				public void performAction(
					ObjectDefinitionSetting objectDefinitionSetting) {

					Assert.assertNotNull(objectDefinitionSetting);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinitionSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionSettingId",
				newObjectDefinitionSetting.getObjectDefinitionSettingId()));

		List<ObjectDefinitionSetting> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectDefinitionSetting existingObjectDefinitionSetting = result.get(0);

		Assert.assertEquals(
			existingObjectDefinitionSetting, newObjectDefinitionSetting);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinitionSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionSettingId", RandomTestUtil.nextLong()));

		List<ObjectDefinitionSetting> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinitionSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectDefinitionSettingId"));

		Object newObjectDefinitionSettingId =
			newObjectDefinitionSetting.getObjectDefinitionSettingId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectDefinitionSettingId",
				new Object[] {newObjectDefinitionSettingId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectDefinitionSettingId = result.get(0);

		Assert.assertEquals(
			existingObjectDefinitionSettingId, newObjectDefinitionSettingId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinitionSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectDefinitionSettingId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectDefinitionSettingId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectDefinitionSetting.getPrimaryKey()));
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

		ObjectDefinitionSetting newObjectDefinitionSetting =
			addObjectDefinitionSetting();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinitionSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionSettingId",
				newObjectDefinitionSetting.getObjectDefinitionSettingId()));

		List<ObjectDefinitionSetting> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		ObjectDefinitionSetting objectDefinitionSetting) {

		Assert.assertEquals(
			Long.valueOf(objectDefinitionSetting.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectDefinitionSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
		Assert.assertEquals(
			objectDefinitionSetting.getName(),
			ReflectionTestUtil.invoke(
				objectDefinitionSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected ObjectDefinitionSetting addObjectDefinitionSetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ObjectDefinitionSetting objectDefinitionSetting = _persistence.create(
			pk);

		objectDefinitionSetting.setMvccVersion(RandomTestUtil.nextLong());

		objectDefinitionSetting.setUuid(RandomTestUtil.randomString());

		objectDefinitionSetting.setCompanyId(RandomTestUtil.nextLong());

		objectDefinitionSetting.setUserId(RandomTestUtil.nextLong());

		objectDefinitionSetting.setUserName(RandomTestUtil.randomString());

		objectDefinitionSetting.setCreateDate(RandomTestUtil.nextDate());

		objectDefinitionSetting.setModifiedDate(RandomTestUtil.nextDate());

		objectDefinitionSetting.setObjectDefinitionId(
			RandomTestUtil.nextLong());

		objectDefinitionSetting.setName(RandomTestUtil.randomString());

		objectDefinitionSetting.setValue(RandomTestUtil.randomString());

		_objectDefinitionSettings.add(
			_persistence.update(objectDefinitionSetting));

		return objectDefinitionSetting;
	}

	private List<ObjectDefinitionSetting> _objectDefinitionSettings =
		new ArrayList<ObjectDefinitionSetting>();
	private ObjectDefinitionSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}