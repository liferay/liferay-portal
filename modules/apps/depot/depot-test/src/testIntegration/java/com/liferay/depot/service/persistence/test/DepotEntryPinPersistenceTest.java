/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.exception.NoSuchEntryPinException;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.service.persistence.DepotEntryPinPersistence;
import com.liferay.depot.service.persistence.DepotEntryPinUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class DepotEntryPinPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.depot.service"));

	@Before
	public void setUp() {
		_persistence = DepotEntryPinUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DepotEntryPin> iterator = _depotEntryPins.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryPin depotEntryPin = _persistence.create(pk);

		Assert.assertNotNull(depotEntryPin);

		Assert.assertEquals(depotEntryPin.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		_persistence.remove(newDepotEntryPin);

		DepotEntryPin existingDepotEntryPin = _persistence.fetchByPrimaryKey(
			newDepotEntryPin.getPrimaryKey());

		Assert.assertNull(existingDepotEntryPin);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDepotEntryPin();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryPin newDepotEntryPin = _persistence.create(pk);

		newDepotEntryPin.setMvccVersion(RandomTestUtil.nextLong());

		newDepotEntryPin.setCtCollectionId(RandomTestUtil.nextLong());

		newDepotEntryPin.setUuid(RandomTestUtil.randomString());

		newDepotEntryPin.setGroupId(RandomTestUtil.nextLong());

		newDepotEntryPin.setCompanyId(RandomTestUtil.nextLong());

		newDepotEntryPin.setUserId(RandomTestUtil.nextLong());

		newDepotEntryPin.setDepotEntryId(RandomTestUtil.nextLong());

		_depotEntryPins.add(_persistence.update(newDepotEntryPin));

		DepotEntryPin existingDepotEntryPin = _persistence.findByPrimaryKey(
			newDepotEntryPin.getPrimaryKey());

		Assert.assertEquals(
			existingDepotEntryPin.getMvccVersion(),
			newDepotEntryPin.getMvccVersion());
		Assert.assertEquals(
			existingDepotEntryPin.getCtCollectionId(),
			newDepotEntryPin.getCtCollectionId());
		Assert.assertEquals(
			existingDepotEntryPin.getUuid(), newDepotEntryPin.getUuid());
		Assert.assertEquals(
			existingDepotEntryPin.getDepotEntryPinId(),
			newDepotEntryPin.getDepotEntryPinId());
		Assert.assertEquals(
			existingDepotEntryPin.getGroupId(), newDepotEntryPin.getGroupId());
		Assert.assertEquals(
			existingDepotEntryPin.getCompanyId(),
			newDepotEntryPin.getCompanyId());
		Assert.assertEquals(
			existingDepotEntryPin.getUserId(), newDepotEntryPin.getUserId());
		Assert.assertEquals(
			existingDepotEntryPin.getDepotEntryId(),
			newDepotEntryPin.getDepotEntryId());
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
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByDepotEntryId() throws Exception {
		_persistence.countByDepotEntryId(RandomTestUtil.nextLong());

		_persistence.countByDepotEntryId(0L);
	}

	@Test
	public void testCountByU_D() throws Exception {
		_persistence.countByU_D(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_D(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		DepotEntryPin existingDepotEntryPin = _persistence.findByPrimaryKey(
			newDepotEntryPin.getPrimaryKey());

		Assert.assertEquals(existingDepotEntryPin, newDepotEntryPin);
	}

	@Test(expected = NoSuchEntryPinException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DepotEntryPin> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DepotEntryPin", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "depotEntryPinId", true, "groupId", true, "companyId",
			true, "userId", true, "depotEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		DepotEntryPin existingDepotEntryPin = _persistence.fetchByPrimaryKey(
			newDepotEntryPin.getPrimaryKey());

		Assert.assertEquals(existingDepotEntryPin, newDepotEntryPin);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryPin missingDepotEntryPin = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDepotEntryPin);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DepotEntryPin newDepotEntryPin1 = addDepotEntryPin();
		DepotEntryPin newDepotEntryPin2 = addDepotEntryPin();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryPin1.getPrimaryKey());
		primaryKeys.add(newDepotEntryPin2.getPrimaryKey());

		Map<Serializable, DepotEntryPin> depotEntryPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, depotEntryPins.size());
		Assert.assertEquals(
			newDepotEntryPin1,
			depotEntryPins.get(newDepotEntryPin1.getPrimaryKey()));
		Assert.assertEquals(
			newDepotEntryPin2,
			depotEntryPins.get(newDepotEntryPin2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DepotEntryPin> depotEntryPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotEntryPins.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryPin.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DepotEntryPin> depotEntryPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotEntryPins.size());
		Assert.assertEquals(
			newDepotEntryPin,
			depotEntryPins.get(newDepotEntryPin.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DepotEntryPin> depotEntryPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotEntryPins.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryPin.getPrimaryKey());

		Map<Serializable, DepotEntryPin> depotEntryPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotEntryPins.size());
		Assert.assertEquals(
			newDepotEntryPin,
			depotEntryPins.get(newDepotEntryPin.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DepotEntryPin newDepotEntryPin = addDepotEntryPin();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDepotEntryPin.getPrimaryKey()));
	}

	private void _assertOriginalValues(DepotEntryPin depotEntryPin) {
		Assert.assertEquals(
			depotEntryPin.getUuid(),
			ReflectionTestUtil.invoke(
				depotEntryPin, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(depotEntryPin.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryPin, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(depotEntryPin.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryPin, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(depotEntryPin.getDepotEntryId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryPin, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "depotEntryId"));
	}

	protected DepotEntryPin addDepotEntryPin() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryPin depotEntryPin = _persistence.create(pk);

		depotEntryPin.setMvccVersion(RandomTestUtil.nextLong());

		depotEntryPin.setCtCollectionId(RandomTestUtil.nextLong());

		depotEntryPin.setUuid(RandomTestUtil.randomString());

		depotEntryPin.setGroupId(RandomTestUtil.nextLong());

		depotEntryPin.setCompanyId(RandomTestUtil.nextLong());

		depotEntryPin.setUserId(RandomTestUtil.nextLong());

		depotEntryPin.setDepotEntryId(RandomTestUtil.nextLong());

		_depotEntryPins.add(_persistence.update(depotEntryPin));

		return depotEntryPin;
	}

	private List<DepotEntryPin> _depotEntryPins =
		new ArrayList<DepotEntryPin>();
	private DepotEntryPinPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}