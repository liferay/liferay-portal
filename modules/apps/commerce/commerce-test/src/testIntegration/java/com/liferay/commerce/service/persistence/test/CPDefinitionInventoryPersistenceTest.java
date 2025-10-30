/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchCPDefinitionInventoryException;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.service.persistence.CPDefinitionInventoryPersistence;
import com.liferay.commerce.service.persistence.CPDefinitionInventoryUtil;
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

import java.math.BigDecimal;

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
public class CPDefinitionInventoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionInventoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionInventory> iterator =
			_cpDefinitionInventories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionInventory cpDefinitionInventory = _persistence.create(pk);

		Assert.assertNotNull(cpDefinitionInventory);

		Assert.assertEquals(cpDefinitionInventory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		_persistence.remove(newCPDefinitionInventory);

		CPDefinitionInventory existingCPDefinitionInventory =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionInventory.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionInventory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionInventory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionInventory newCPDefinitionInventory = _persistence.create(
			pk);

		newCPDefinitionInventory.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setCtCollectionId(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setUuid(RandomTestUtil.randomString());

		newCPDefinitionInventory.setGroupId(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setUserId(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setUserName(RandomTestUtil.randomString());

		newCPDefinitionInventory.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinitionInventory.setModifiedDate(RandomTestUtil.nextDate());

		newCPDefinitionInventory.setCPDefinitionId(RandomTestUtil.nextLong());

		newCPDefinitionInventory.setCPDefinitionInventoryEngine(
			RandomTestUtil.randomString());

		newCPDefinitionInventory.setLowStockActivity(
			RandomTestUtil.randomString());

		newCPDefinitionInventory.setDisplayAvailability(
			RandomTestUtil.randomBoolean());

		newCPDefinitionInventory.setDisplayStockQuantity(
			RandomTestUtil.randomBoolean());

		newCPDefinitionInventory.setMinStockQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPDefinitionInventory.setBackOrders(RandomTestUtil.randomBoolean());

		newCPDefinitionInventory.setMinOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPDefinitionInventory.setMaxOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPDefinitionInventory.setAllowedOrderQuantities(
			RandomTestUtil.randomString());

		newCPDefinitionInventory.setMultipleOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		_cpDefinitionInventories.add(
			_persistence.update(newCPDefinitionInventory));

		CPDefinitionInventory existingCPDefinitionInventory =
			_persistence.findByPrimaryKey(
				newCPDefinitionInventory.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionInventory.getMvccVersion(),
			newCPDefinitionInventory.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionInventory.getCtCollectionId(),
			newCPDefinitionInventory.getCtCollectionId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getUuid(),
			newCPDefinitionInventory.getUuid());
		Assert.assertEquals(
			existingCPDefinitionInventory.getCPDefinitionInventoryId(),
			newCPDefinitionInventory.getCPDefinitionInventoryId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getGroupId(),
			newCPDefinitionInventory.getGroupId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getCompanyId(),
			newCPDefinitionInventory.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getUserId(),
			newCPDefinitionInventory.getUserId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getUserName(),
			newCPDefinitionInventory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionInventory.getCreateDate()),
			Time.getShortTimestamp(newCPDefinitionInventory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionInventory.getModifiedDate()),
			Time.getShortTimestamp(newCPDefinitionInventory.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinitionInventory.getCPDefinitionId(),
			newCPDefinitionInventory.getCPDefinitionId());
		Assert.assertEquals(
			existingCPDefinitionInventory.getCPDefinitionInventoryEngine(),
			newCPDefinitionInventory.getCPDefinitionInventoryEngine());
		Assert.assertEquals(
			existingCPDefinitionInventory.getLowStockActivity(),
			newCPDefinitionInventory.getLowStockActivity());
		Assert.assertEquals(
			existingCPDefinitionInventory.isDisplayAvailability(),
			newCPDefinitionInventory.isDisplayAvailability());
		Assert.assertEquals(
			existingCPDefinitionInventory.isDisplayStockQuantity(),
			newCPDefinitionInventory.isDisplayStockQuantity());
		Assert.assertEquals(
			existingCPDefinitionInventory.getMinStockQuantity(),
			newCPDefinitionInventory.getMinStockQuantity());
		Assert.assertEquals(
			existingCPDefinitionInventory.isBackOrders(),
			newCPDefinitionInventory.isBackOrders());
		Assert.assertEquals(
			existingCPDefinitionInventory.getMinOrderQuantity(),
			newCPDefinitionInventory.getMinOrderQuantity());
		Assert.assertEquals(
			existingCPDefinitionInventory.getMaxOrderQuantity(),
			newCPDefinitionInventory.getMaxOrderQuantity());
		Assert.assertEquals(
			existingCPDefinitionInventory.getAllowedOrderQuantities(),
			newCPDefinitionInventory.getAllowedOrderQuantities());
		Assert.assertEquals(
			existingCPDefinitionInventory.getMultipleOrderQuantity(),
			newCPDefinitionInventory.getMultipleOrderQuantity());
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
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		CPDefinitionInventory existingCPDefinitionInventory =
			_persistence.findByPrimaryKey(
				newCPDefinitionInventory.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionInventory, newCPDefinitionInventory);
	}

	@Test(expected = NoSuchCPDefinitionInventoryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionInventory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionInventory", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "CPDefinitionInventoryId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CPDefinitionId", true,
			"CPDefinitionInventoryEngine", true, "lowStockActivity", true,
			"displayAvailability", true, "displayStockQuantity", true,
			"minStockQuantity", true, "backOrders", true, "minOrderQuantity",
			true, "maxOrderQuantity", true, "allowedOrderQuantities", true,
			"multipleOrderQuantity", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		CPDefinitionInventory existingCPDefinitionInventory =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionInventory.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionInventory, newCPDefinitionInventory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionInventory missingCPDefinitionInventory =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionInventory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionInventory newCPDefinitionInventory1 =
			addCPDefinitionInventory();
		CPDefinitionInventory newCPDefinitionInventory2 =
			addCPDefinitionInventory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionInventory1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionInventory2.getPrimaryKey());

		Map<Serializable, CPDefinitionInventory> cpDefinitionInventories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDefinitionInventories.size());
		Assert.assertEquals(
			newCPDefinitionInventory1,
			cpDefinitionInventories.get(
				newCPDefinitionInventory1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionInventory2,
			cpDefinitionInventories.get(
				newCPDefinitionInventory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionInventory> cpDefinitionInventories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionInventories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionInventory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionInventory> cpDefinitionInventories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionInventories.size());
		Assert.assertEquals(
			newCPDefinitionInventory,
			cpDefinitionInventories.get(
				newCPDefinitionInventory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionInventory> cpDefinitionInventories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionInventories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionInventory.getPrimaryKey());

		Map<Serializable, CPDefinitionInventory> cpDefinitionInventories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionInventories.size());
		Assert.assertEquals(
			newCPDefinitionInventory,
			cpDefinitionInventories.get(
				newCPDefinitionInventory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionInventory newCPDefinitionInventory =
			addCPDefinitionInventory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionInventory.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDefinitionInventory cpDefinitionInventory) {

		Assert.assertEquals(
			cpDefinitionInventory.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinitionInventory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionInventory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionInventory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinitionInventory.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionInventory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
	}

	protected CPDefinitionInventory addCPDefinitionInventory()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionInventory cpDefinitionInventory = _persistence.create(pk);

		cpDefinitionInventory.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionInventory.setCtCollectionId(RandomTestUtil.nextLong());

		cpDefinitionInventory.setUuid(RandomTestUtil.randomString());

		cpDefinitionInventory.setGroupId(RandomTestUtil.nextLong());

		cpDefinitionInventory.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionInventory.setUserId(RandomTestUtil.nextLong());

		cpDefinitionInventory.setUserName(RandomTestUtil.randomString());

		cpDefinitionInventory.setCreateDate(RandomTestUtil.nextDate());

		cpDefinitionInventory.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinitionInventory.setCPDefinitionId(RandomTestUtil.nextLong());

		cpDefinitionInventory.setCPDefinitionInventoryEngine(
			RandomTestUtil.randomString());

		cpDefinitionInventory.setLowStockActivity(
			RandomTestUtil.randomString());

		cpDefinitionInventory.setDisplayAvailability(
			RandomTestUtil.randomBoolean());

		cpDefinitionInventory.setDisplayStockQuantity(
			RandomTestUtil.randomBoolean());

		cpDefinitionInventory.setMinStockQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpDefinitionInventory.setBackOrders(RandomTestUtil.randomBoolean());

		cpDefinitionInventory.setMinOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpDefinitionInventory.setMaxOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpDefinitionInventory.setAllowedOrderQuantities(
			RandomTestUtil.randomString());

		cpDefinitionInventory.setMultipleOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		_cpDefinitionInventories.add(
			_persistence.update(cpDefinitionInventory));

		return cpDefinitionInventory;
	}

	private List<CPDefinitionInventory> _cpDefinitionInventories =
		new ArrayList<CPDefinitionInventory>();
	private CPDefinitionInventoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}