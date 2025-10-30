/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectValidationRuleSettingException;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.service.persistence.ObjectValidationRuleSettingPersistence;
import com.liferay.object.service.persistence.ObjectValidationRuleSettingUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class ObjectValidationRuleSettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectValidationRuleSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectValidationRuleSetting> iterator =
			_objectValidationRuleSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRuleSetting objectValidationRuleSetting =
			_persistence.create(pk);

		Assert.assertNotNull(objectValidationRuleSetting);

		Assert.assertEquals(objectValidationRuleSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		_persistence.remove(newObjectValidationRuleSetting);

		ObjectValidationRuleSetting existingObjectValidationRuleSetting =
			_persistence.fetchByPrimaryKey(
				newObjectValidationRuleSetting.getPrimaryKey());

		Assert.assertNull(existingObjectValidationRuleSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectValidationRuleSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			_persistence.create(pk);

		newObjectValidationRuleSetting.setMvccVersion(
			RandomTestUtil.nextLong());

		newObjectValidationRuleSetting.setUuid(RandomTestUtil.randomString());

		newObjectValidationRuleSetting.setCompanyId(RandomTestUtil.nextLong());

		newObjectValidationRuleSetting.setUserId(RandomTestUtil.nextLong());

		newObjectValidationRuleSetting.setUserName(
			RandomTestUtil.randomString());

		newObjectValidationRuleSetting.setCreateDate(RandomTestUtil.nextDate());

		newObjectValidationRuleSetting.setModifiedDate(
			RandomTestUtil.nextDate());

		newObjectValidationRuleSetting.setObjectValidationRuleId(
			RandomTestUtil.nextLong());

		newObjectValidationRuleSetting.setName(RandomTestUtil.randomString());

		newObjectValidationRuleSetting.setValue(RandomTestUtil.randomString());

		_objectValidationRuleSettings.add(
			_persistence.update(newObjectValidationRuleSetting));

		ObjectValidationRuleSetting existingObjectValidationRuleSetting =
			_persistence.findByPrimaryKey(
				newObjectValidationRuleSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRuleSetting.getMvccVersion(),
			newObjectValidationRuleSetting.getMvccVersion());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getUuid(),
			newObjectValidationRuleSetting.getUuid());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.
				getObjectValidationRuleSettingId(),
			newObjectValidationRuleSetting.getObjectValidationRuleSettingId());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getCompanyId(),
			newObjectValidationRuleSetting.getCompanyId());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getUserId(),
			newObjectValidationRuleSetting.getUserId());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getUserName(),
			newObjectValidationRuleSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectValidationRuleSetting.getCreateDate()),
			Time.getShortTimestamp(
				newObjectValidationRuleSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectValidationRuleSetting.getModifiedDate()),
			Time.getShortTimestamp(
				newObjectValidationRuleSetting.getModifiedDate()));
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getObjectValidationRuleId(),
			newObjectValidationRuleSetting.getObjectValidationRuleId());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getName(),
			newObjectValidationRuleSetting.getName());
		Assert.assertEquals(
			existingObjectValidationRuleSetting.getValue(),
			newObjectValidationRuleSetting.getValue());
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
	public void testCountByObjectValidationRuleId() throws Exception {
		_persistence.countByObjectValidationRuleId(RandomTestUtil.nextLong());

		_persistence.countByObjectValidationRuleId(0L);
	}

	@Test
	public void testCountByOVRI_N() throws Exception {
		_persistence.countByOVRI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByOVRI_N(0L, "null");

		_persistence.countByOVRI_N(0L, (String)null);
	}

	@Test
	public void testCountByN_V() throws Exception {
		_persistence.countByN_V("", "");

		_persistence.countByN_V("null", "null");

		_persistence.countByN_V((String)null, (String)null);
	}

	@Test
	public void testCountByOVRI_N_V() throws Exception {
		_persistence.countByOVRI_N_V(RandomTestUtil.nextLong(), "", "");

		_persistence.countByOVRI_N_V(0L, "null", "null");

		_persistence.countByOVRI_N_V(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		ObjectValidationRuleSetting existingObjectValidationRuleSetting =
			_persistence.findByPrimaryKey(
				newObjectValidationRuleSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRuleSetting,
			newObjectValidationRuleSetting);
	}

	@Test(expected = NoSuchObjectValidationRuleSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectValidationRuleSetting>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"ObjectValidationRuleSetting", "mvccVersion", true, "uuid", true,
			"objectValidationRuleSettingId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"objectValidationRuleId", true, "name", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		ObjectValidationRuleSetting existingObjectValidationRuleSetting =
			_persistence.fetchByPrimaryKey(
				newObjectValidationRuleSetting.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRuleSetting,
			newObjectValidationRuleSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRuleSetting missingObjectValidationRuleSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectValidationRuleSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectValidationRuleSetting newObjectValidationRuleSetting1 =
			addObjectValidationRuleSetting();
		ObjectValidationRuleSetting newObjectValidationRuleSetting2 =
			addObjectValidationRuleSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRuleSetting1.getPrimaryKey());
		primaryKeys.add(newObjectValidationRuleSetting2.getPrimaryKey());

		Map<Serializable, ObjectValidationRuleSetting>
			objectValidationRuleSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, objectValidationRuleSettings.size());
		Assert.assertEquals(
			newObjectValidationRuleSetting1,
			objectValidationRuleSettings.get(
				newObjectValidationRuleSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectValidationRuleSetting2,
			objectValidationRuleSettings.get(
				newObjectValidationRuleSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectValidationRuleSetting>
			objectValidationRuleSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(objectValidationRuleSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRuleSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectValidationRuleSetting>
			objectValidationRuleSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, objectValidationRuleSettings.size());
		Assert.assertEquals(
			newObjectValidationRuleSetting,
			objectValidationRuleSettings.get(
				newObjectValidationRuleSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectValidationRuleSetting>
			objectValidationRuleSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(objectValidationRuleSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRuleSetting.getPrimaryKey());

		Map<Serializable, ObjectValidationRuleSetting>
			objectValidationRuleSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, objectValidationRuleSettings.size());
		Assert.assertEquals(
			newObjectValidationRuleSetting,
			objectValidationRuleSettings.get(
				newObjectValidationRuleSetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectValidationRuleSetting newObjectValidationRuleSetting =
			addObjectValidationRuleSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectValidationRuleSetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		ObjectValidationRuleSetting objectValidationRuleSetting) {

		Assert.assertEquals(
			objectValidationRuleSetting.getName(),
			ReflectionTestUtil.invoke(
				objectValidationRuleSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			objectValidationRuleSetting.getValue(),
			ReflectionTestUtil.invoke(
				objectValidationRuleSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "value"));

		Assert.assertEquals(
			Long.valueOf(
				objectValidationRuleSetting.getObjectValidationRuleId()),
			ReflectionTestUtil.<Long>invoke(
				objectValidationRuleSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectValidationRuleId"));
		Assert.assertEquals(
			objectValidationRuleSetting.getName(),
			ReflectionTestUtil.invoke(
				objectValidationRuleSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			objectValidationRuleSetting.getValue(),
			ReflectionTestUtil.invoke(
				objectValidationRuleSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "value"));
	}

	protected ObjectValidationRuleSetting addObjectValidationRuleSetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ObjectValidationRuleSetting objectValidationRuleSetting =
			_persistence.create(pk);

		objectValidationRuleSetting.setMvccVersion(RandomTestUtil.nextLong());

		objectValidationRuleSetting.setUuid(RandomTestUtil.randomString());

		objectValidationRuleSetting.setCompanyId(RandomTestUtil.nextLong());

		objectValidationRuleSetting.setUserId(RandomTestUtil.nextLong());

		objectValidationRuleSetting.setUserName(RandomTestUtil.randomString());

		objectValidationRuleSetting.setCreateDate(RandomTestUtil.nextDate());

		objectValidationRuleSetting.setModifiedDate(RandomTestUtil.nextDate());

		objectValidationRuleSetting.setObjectValidationRuleId(
			RandomTestUtil.nextLong());

		objectValidationRuleSetting.setName(RandomTestUtil.randomString());

		objectValidationRuleSetting.setValue(RandomTestUtil.randomString());

		_objectValidationRuleSettings.add(
			_persistence.update(objectValidationRuleSetting));

		return objectValidationRuleSetting;
	}

	private List<ObjectValidationRuleSetting> _objectValidationRuleSettings =
		new ArrayList<ObjectValidationRuleSetting>();
	private ObjectValidationRuleSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}