/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntrySettingException;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntrySettingPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntrySettingUtil;
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
public class CPConfigurationEntrySettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPConfigurationEntrySettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPConfigurationEntrySetting> iterator =
			_cpConfigurationEntrySettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_persistence.create(pk);

		Assert.assertNotNull(cpConfigurationEntrySetting);

		Assert.assertEquals(cpConfigurationEntrySetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		_persistence.remove(newCPConfigurationEntrySetting);

		CPConfigurationEntrySetting existingCPConfigurationEntrySetting =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationEntrySetting.getPrimaryKey());

		Assert.assertNull(existingCPConfigurationEntrySetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPConfigurationEntrySetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			_persistence.create(pk);

		newCPConfigurationEntrySetting.setMvccVersion(
			RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setUuid(RandomTestUtil.randomString());

		newCPConfigurationEntrySetting.setGroupId(RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setCompanyId(RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setUserId(RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setUserName(
			RandomTestUtil.randomString());

		newCPConfigurationEntrySetting.setCreateDate(RandomTestUtil.nextDate());

		newCPConfigurationEntrySetting.setModifiedDate(
			RandomTestUtil.nextDate());

		newCPConfigurationEntrySetting.setCPConfigurationEntryId(
			RandomTestUtil.nextLong());

		newCPConfigurationEntrySetting.setType(RandomTestUtil.nextInt());

		newCPConfigurationEntrySetting.setValue(RandomTestUtil.randomString());

		_cpConfigurationEntrySettings.add(
			_persistence.update(newCPConfigurationEntrySetting));

		CPConfigurationEntrySetting existingCPConfigurationEntrySetting =
			_persistence.findByPrimaryKey(
				newCPConfigurationEntrySetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getMvccVersion(),
			newCPConfigurationEntrySetting.getMvccVersion());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getCtCollectionId(),
			newCPConfigurationEntrySetting.getCtCollectionId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getUuid(),
			newCPConfigurationEntrySetting.getUuid());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.
				getCPConfigurationEntrySettingId(),
			newCPConfigurationEntrySetting.getCPConfigurationEntrySettingId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getGroupId(),
			newCPConfigurationEntrySetting.getGroupId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getCompanyId(),
			newCPConfigurationEntrySetting.getCompanyId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getUserId(),
			newCPConfigurationEntrySetting.getUserId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getUserName(),
			newCPConfigurationEntrySetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationEntrySetting.getCreateDate()),
			Time.getShortTimestamp(
				newCPConfigurationEntrySetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationEntrySetting.getModifiedDate()),
			Time.getShortTimestamp(
				newCPConfigurationEntrySetting.getModifiedDate()));
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getCPConfigurationEntryId(),
			newCPConfigurationEntrySetting.getCPConfigurationEntryId());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getType(),
			newCPConfigurationEntrySetting.getType());
		Assert.assertEquals(
			existingCPConfigurationEntrySetting.getValue(),
			newCPConfigurationEntrySetting.getValue());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
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
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_T(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		CPConfigurationEntrySetting existingCPConfigurationEntrySetting =
			_persistence.findByPrimaryKey(
				newCPConfigurationEntrySetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntrySetting,
			newCPConfigurationEntrySetting);
	}

	@Test(expected = NoSuchCPConfigurationEntrySettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPConfigurationEntrySetting>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPConfigurationEntrySetting", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"CPConfigurationEntrySettingId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPConfigurationEntryId", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		CPConfigurationEntrySetting existingCPConfigurationEntrySetting =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationEntrySetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntrySetting,
			newCPConfigurationEntrySetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntrySetting missingCPConfigurationEntrySetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPConfigurationEntrySetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPConfigurationEntrySetting newCPConfigurationEntrySetting1 =
			addCPConfigurationEntrySetting();
		CPConfigurationEntrySetting newCPConfigurationEntrySetting2 =
			addCPConfigurationEntrySetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntrySetting1.getPrimaryKey());
		primaryKeys.add(newCPConfigurationEntrySetting2.getPrimaryKey());

		Map<Serializable, CPConfigurationEntrySetting>
			cpConfigurationEntrySettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, cpConfigurationEntrySettings.size());
		Assert.assertEquals(
			newCPConfigurationEntrySetting1,
			cpConfigurationEntrySettings.get(
				newCPConfigurationEntrySetting1.getPrimaryKey()));
		Assert.assertEquals(
			newCPConfigurationEntrySetting2,
			cpConfigurationEntrySettings.get(
				newCPConfigurationEntrySetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPConfigurationEntrySetting>
			cpConfigurationEntrySettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpConfigurationEntrySettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntrySetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPConfigurationEntrySetting>
			cpConfigurationEntrySettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpConfigurationEntrySettings.size());
		Assert.assertEquals(
			newCPConfigurationEntrySetting,
			cpConfigurationEntrySettings.get(
				newCPConfigurationEntrySetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPConfigurationEntrySetting>
			cpConfigurationEntrySettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpConfigurationEntrySettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntrySetting.getPrimaryKey());

		Map<Serializable, CPConfigurationEntrySetting>
			cpConfigurationEntrySettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpConfigurationEntrySettings.size());
		Assert.assertEquals(
			newCPConfigurationEntrySetting,
			cpConfigurationEntrySettings.get(
				newCPConfigurationEntrySetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPConfigurationEntrySetting newCPConfigurationEntrySetting =
			addCPConfigurationEntrySetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPConfigurationEntrySetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		Assert.assertEquals(
			cpConfigurationEntrySetting.getUuid(),
			ReflectionTestUtil.invoke(
				cpConfigurationEntrySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntrySetting.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntrySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(
				cpConfigurationEntrySetting.getCPConfigurationEntryId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntrySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPConfigurationEntryId"));
		Assert.assertEquals(
			Integer.valueOf(cpConfigurationEntrySetting.getType()),
			ReflectionTestUtil.<Integer>invoke(
				cpConfigurationEntrySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected CPConfigurationEntrySetting addCPConfigurationEntrySetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_persistence.create(pk);

		cpConfigurationEntrySetting.setMvccVersion(RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setCtCollectionId(
			RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setUuid(RandomTestUtil.randomString());

		cpConfigurationEntrySetting.setGroupId(RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setCompanyId(RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setUserId(RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setUserName(RandomTestUtil.randomString());

		cpConfigurationEntrySetting.setCreateDate(RandomTestUtil.nextDate());

		cpConfigurationEntrySetting.setModifiedDate(RandomTestUtil.nextDate());

		cpConfigurationEntrySetting.setCPConfigurationEntryId(
			RandomTestUtil.nextLong());

		cpConfigurationEntrySetting.setType(RandomTestUtil.nextInt());

		cpConfigurationEntrySetting.setValue(RandomTestUtil.randomString());

		_cpConfigurationEntrySettings.add(
			_persistence.update(cpConfigurationEntrySetting));

		return cpConfigurationEntrySetting;
	}

	private List<CPConfigurationEntrySetting> _cpConfigurationEntrySettings =
		new ArrayList<CPConfigurationEntrySetting>();
	private CPConfigurationEntrySettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}