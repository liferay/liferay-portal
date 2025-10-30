/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchSubscriptionEntryException;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.service.persistence.CommerceSubscriptionEntryPersistence;
import com.liferay.commerce.service.persistence.CommerceSubscriptionEntryUtil;
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
public class CommerceSubscriptionEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceSubscriptionEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceSubscriptionEntry> iterator =
			_commerceSubscriptionEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_persistence.create(pk);

		Assert.assertNotNull(commerceSubscriptionEntry);

		Assert.assertEquals(commerceSubscriptionEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		_persistence.remove(newCommerceSubscriptionEntry);

		CommerceSubscriptionEntry existingCommerceSubscriptionEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceSubscriptionEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceSubscriptionEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceSubscriptionEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			_persistence.create(pk);

		newCommerceSubscriptionEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setUuid(RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setGroupId(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setUserId(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setUserName(RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setCPInstanceUuid(
			RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setCProductId(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setCommerceOrderItemId(
			RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setSubscriptionLength(
			RandomTestUtil.nextInt());

		newCommerceSubscriptionEntry.setSubscriptionType(
			RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setSubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setCurrentCycle(RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setSubscriptionStatus(
			RandomTestUtil.nextInt());

		newCommerceSubscriptionEntry.setLastIterationDate(
			RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setNextIterationDate(
			RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setStartDate(RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setDeliverySubscriptionLength(
			RandomTestUtil.nextInt());

		newCommerceSubscriptionEntry.setDeliverySubscriptionType(
			RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCommerceSubscriptionEntry.setDeliveryCurrentCycle(
			RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setDeliveryMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		newCommerceSubscriptionEntry.setDeliverySubscriptionStatus(
			RandomTestUtil.nextInt());

		newCommerceSubscriptionEntry.setDeliveryLastIterationDate(
			RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setDeliveryNextIterationDate(
			RandomTestUtil.nextDate());

		newCommerceSubscriptionEntry.setDeliveryStartDate(
			RandomTestUtil.nextDate());

		_commerceSubscriptionEntries.add(
			_persistence.update(newCommerceSubscriptionEntry));

		CommerceSubscriptionEntry existingCommerceSubscriptionEntry =
			_persistence.findByPrimaryKey(
				newCommerceSubscriptionEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getMvccVersion(),
			newCommerceSubscriptionEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getUuid(),
			newCommerceSubscriptionEntry.getUuid());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCommerceSubscriptionEntryId(),
			newCommerceSubscriptionEntry.getCommerceSubscriptionEntryId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getGroupId(),
			newCommerceSubscriptionEntry.getGroupId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCompanyId(),
			newCommerceSubscriptionEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getUserId(),
			newCommerceSubscriptionEntry.getUserId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getUserName(),
			newCommerceSubscriptionEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCPInstanceUuid(),
			newCommerceSubscriptionEntry.getCPInstanceUuid());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCProductId(),
			newCommerceSubscriptionEntry.getCProductId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCommerceOrderItemId(),
			newCommerceSubscriptionEntry.getCommerceOrderItemId());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getSubscriptionLength(),
			newCommerceSubscriptionEntry.getSubscriptionLength());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getSubscriptionType(),
			newCommerceSubscriptionEntry.getSubscriptionType());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getSubscriptionTypeSettings(),
			newCommerceSubscriptionEntry.getSubscriptionTypeSettings());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getCurrentCycle(),
			newCommerceSubscriptionEntry.getCurrentCycle());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getMaxSubscriptionCycles(),
			newCommerceSubscriptionEntry.getMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getSubscriptionStatus(),
			newCommerceSubscriptionEntry.getSubscriptionStatus());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getLastIterationDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getLastIterationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getNextIterationDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getNextIterationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getStartDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getStartDate()));
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getDeliverySubscriptionLength(),
			newCommerceSubscriptionEntry.getDeliverySubscriptionLength());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getDeliverySubscriptionType(),
			newCommerceSubscriptionEntry.getDeliverySubscriptionType());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.
				getDeliverySubscriptionTypeSettings(),
			newCommerceSubscriptionEntry.getDeliverySubscriptionTypeSettings());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getDeliveryCurrentCycle(),
			newCommerceSubscriptionEntry.getDeliveryCurrentCycle());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.
				getDeliveryMaxSubscriptionCycles(),
			newCommerceSubscriptionEntry.getDeliveryMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCommerceSubscriptionEntry.getDeliverySubscriptionStatus(),
			newCommerceSubscriptionEntry.getDeliverySubscriptionStatus());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.
					getDeliveryLastIterationDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getDeliveryLastIterationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.
					getDeliveryNextIterationDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getDeliveryNextIterationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceSubscriptionEntry.getDeliveryStartDate()),
			Time.getShortTimestamp(
				newCommerceSubscriptionEntry.getDeliveryStartDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCommerceOrderItemId() throws Exception {
		_persistence.countByCommerceOrderItemId(RandomTestUtil.nextLong());

		_persistence.countByCommerceOrderItemId(0L);
	}

	@Test
	public void testCountBySubscriptionStatus() throws Exception {
		_persistence.countBySubscriptionStatus(RandomTestUtil.nextInt());

		_persistence.countBySubscriptionStatus(0);
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByG_C_U() throws Exception {
		_persistence.countByG_C_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_U(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C_C("null", 0L, 0L);

		_persistence.countByC_C_C((String)null, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		CommerceSubscriptionEntry existingCommerceSubscriptionEntry =
			_persistence.findByPrimaryKey(
				newCommerceSubscriptionEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceSubscriptionEntry, newCommerceSubscriptionEntry);
	}

	@Test(expected = NoSuchSubscriptionEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceSubscriptionEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceSubscriptionEntry", "mvccVersion", true, "uuid", true,
			"commerceSubscriptionEntryId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPInstanceUuid", true, "CProductId", true,
			"commerceOrderItemId", true, "subscriptionLength", true,
			"subscriptionType", true, "currentCycle", true,
			"maxSubscriptionCycles", true, "subscriptionStatus", true,
			"lastIterationDate", true, "nextIterationDate", true, "startDate",
			true, "deliverySubscriptionLength", true,
			"deliverySubscriptionType", true,
			"deliverySubscriptionTypeSettings", true, "deliveryCurrentCycle",
			true, "deliveryMaxSubscriptionCycles", true,
			"deliverySubscriptionStatus", true, "deliveryLastIterationDate",
			true, "deliveryNextIterationDate", true, "deliveryStartDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		CommerceSubscriptionEntry existingCommerceSubscriptionEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceSubscriptionEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceSubscriptionEntry, newCommerceSubscriptionEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceSubscriptionEntry missingCommerceSubscriptionEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceSubscriptionEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceSubscriptionEntry newCommerceSubscriptionEntry1 =
			addCommerceSubscriptionEntry();
		CommerceSubscriptionEntry newCommerceSubscriptionEntry2 =
			addCommerceSubscriptionEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceSubscriptionEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceSubscriptionEntry2.getPrimaryKey());

		Map<Serializable, CommerceSubscriptionEntry>
			commerceSubscriptionEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceSubscriptionEntries.size());
		Assert.assertEquals(
			newCommerceSubscriptionEntry1,
			commerceSubscriptionEntries.get(
				newCommerceSubscriptionEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceSubscriptionEntry2,
			commerceSubscriptionEntries.get(
				newCommerceSubscriptionEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceSubscriptionEntry>
			commerceSubscriptionEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceSubscriptionEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceSubscriptionEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceSubscriptionEntry>
			commerceSubscriptionEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceSubscriptionEntries.size());
		Assert.assertEquals(
			newCommerceSubscriptionEntry,
			commerceSubscriptionEntries.get(
				newCommerceSubscriptionEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceSubscriptionEntry>
			commerceSubscriptionEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceSubscriptionEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceSubscriptionEntry.getPrimaryKey());

		Map<Serializable, CommerceSubscriptionEntry>
			commerceSubscriptionEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceSubscriptionEntries.size());
		Assert.assertEquals(
			newCommerceSubscriptionEntry,
			commerceSubscriptionEntries.get(
				newCommerceSubscriptionEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceSubscriptionEntry newCommerceSubscriptionEntry =
			addCommerceSubscriptionEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceSubscriptionEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		Assert.assertEquals(
			commerceSubscriptionEntry.getUuid(),
			ReflectionTestUtil.invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceSubscriptionEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(commerceSubscriptionEntry.getCommerceOrderItemId()),
			ReflectionTestUtil.<Long>invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceOrderItemId"));

		Assert.assertEquals(
			commerceSubscriptionEntry.getCPInstanceUuid(),
			ReflectionTestUtil.invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPInstanceUuid"));
		Assert.assertEquals(
			Long.valueOf(commerceSubscriptionEntry.getCProductId()),
			ReflectionTestUtil.<Long>invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CProductId"));
		Assert.assertEquals(
			Long.valueOf(commerceSubscriptionEntry.getCommerceOrderItemId()),
			ReflectionTestUtil.<Long>invoke(
				commerceSubscriptionEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceOrderItemId"));
	}

	protected CommerceSubscriptionEntry addCommerceSubscriptionEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_persistence.create(pk);

		commerceSubscriptionEntry.setMvccVersion(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setUuid(RandomTestUtil.randomString());

		commerceSubscriptionEntry.setGroupId(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setCompanyId(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setUserId(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setUserName(RandomTestUtil.randomString());

		commerceSubscriptionEntry.setCreateDate(RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setModifiedDate(RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setCPInstanceUuid(
			RandomTestUtil.randomString());

		commerceSubscriptionEntry.setCProductId(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setCommerceOrderItemId(
			RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setSubscriptionLength(
			RandomTestUtil.nextInt());

		commerceSubscriptionEntry.setSubscriptionType(
			RandomTestUtil.randomString());

		commerceSubscriptionEntry.setSubscriptionTypeSettings(
			RandomTestUtil.randomString());

		commerceSubscriptionEntry.setCurrentCycle(RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setSubscriptionStatus(
			RandomTestUtil.nextInt());

		commerceSubscriptionEntry.setLastIterationDate(
			RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setNextIterationDate(
			RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setStartDate(RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setDeliverySubscriptionLength(
			RandomTestUtil.nextInt());

		commerceSubscriptionEntry.setDeliverySubscriptionType(
			RandomTestUtil.randomString());

		commerceSubscriptionEntry.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		commerceSubscriptionEntry.setDeliveryCurrentCycle(
			RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setDeliveryMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		commerceSubscriptionEntry.setDeliverySubscriptionStatus(
			RandomTestUtil.nextInt());

		commerceSubscriptionEntry.setDeliveryLastIterationDate(
			RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setDeliveryNextIterationDate(
			RandomTestUtil.nextDate());

		commerceSubscriptionEntry.setDeliveryStartDate(
			RandomTestUtil.nextDate());

		_commerceSubscriptionEntries.add(
			_persistence.update(commerceSubscriptionEntry));

		return commerceSubscriptionEntry;
	}

	private List<CommerceSubscriptionEntry> _commerceSubscriptionEntries =
		new ArrayList<CommerceSubscriptionEntry>();
	private CommerceSubscriptionEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}