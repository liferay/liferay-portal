/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.exception.NoSuchDiscountUsageEntryException;
import com.liferay.commerce.discount.model.CommerceDiscountUsageEntry;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountUsageEntryPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountUsageEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CommerceDiscountUsageEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.discount.service"));

	@Before
	public void setUp() {
		_persistence = CommerceDiscountUsageEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceDiscountUsageEntry> iterator =
			_commerceDiscountUsageEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountUsageEntry commerceDiscountUsageEntry =
			_persistence.create(pk);

		Assert.assertNotNull(commerceDiscountUsageEntry);

		Assert.assertEquals(commerceDiscountUsageEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			addCommerceDiscountUsageEntry();

		_persistence.remove(newCommerceDiscountUsageEntry);

		CommerceDiscountUsageEntry existingCommerceDiscountUsageEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountUsageEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceDiscountUsageEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceDiscountUsageEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			_persistence.create(pk);

		newCommerceDiscountUsageEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceDiscountUsageEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommerceDiscountUsageEntry.setUserId(RandomTestUtil.nextLong());

		newCommerceDiscountUsageEntry.setUserName(
			RandomTestUtil.randomString());

		newCommerceDiscountUsageEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommerceDiscountUsageEntry.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceDiscountUsageEntry.setCommerceAccountId(
			RandomTestUtil.nextLong());

		newCommerceDiscountUsageEntry.setCommerceOrderId(
			RandomTestUtil.nextLong());

		newCommerceDiscountUsageEntry.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		_commerceDiscountUsageEntries.add(
			_persistence.update(newCommerceDiscountUsageEntry));

		CommerceDiscountUsageEntry existingCommerceDiscountUsageEntry =
			_persistence.findByPrimaryKey(
				newCommerceDiscountUsageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getMvccVersion(),
			newCommerceDiscountUsageEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.
				getCommerceDiscountUsageEntryId(),
			newCommerceDiscountUsageEntry.getCommerceDiscountUsageEntryId());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getCompanyId(),
			newCommerceDiscountUsageEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getUserId(),
			newCommerceDiscountUsageEntry.getUserId());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getUserName(),
			newCommerceDiscountUsageEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountUsageEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceDiscountUsageEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountUsageEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceDiscountUsageEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getCommerceAccountId(),
			newCommerceDiscountUsageEntry.getCommerceAccountId());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getCommerceOrderId(),
			newCommerceDiscountUsageEntry.getCommerceOrderId());
		Assert.assertEquals(
			existingCommerceDiscountUsageEntry.getCommerceDiscountId(),
			newCommerceDiscountUsageEntry.getCommerceDiscountId());
	}

	@Test
	public void testCountByCommerceDiscountId() throws Exception {
		_persistence.countByCommerceDiscountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceDiscountId(0L);
	}

	@Test
	public void testCountByCAI_CDI() throws Exception {
		_persistence.countByCAI_CDI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCAI_CDI(0L, 0L);
	}

	@Test
	public void testCountByCOI_CDI() throws Exception {
		_persistence.countByCOI_CDI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCOI_CDI(0L, 0L);
	}

	@Test
	public void testCountByCAI_COI_CDI() throws Exception {
		_persistence.countByCAI_COI_CDI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByCAI_COI_CDI(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			addCommerceDiscountUsageEntry();

		CommerceDiscountUsageEntry existingCommerceDiscountUsageEntry =
			_persistence.findByPrimaryKey(
				newCommerceDiscountUsageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountUsageEntry, newCommerceDiscountUsageEntry);
	}

	@Test(expected = NoSuchDiscountUsageEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceDiscountUsageEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceDiscountUsageEntry", "mvccVersion", true,
			"commerceDiscountUsageEntryId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commerceAccountId", true, "commerceOrderId", true,
			"commerceDiscountId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			addCommerceDiscountUsageEntry();

		CommerceDiscountUsageEntry existingCommerceDiscountUsageEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountUsageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountUsageEntry, newCommerceDiscountUsageEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountUsageEntry missingCommerceDiscountUsageEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceDiscountUsageEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry1 =
			addCommerceDiscountUsageEntry();
		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry2 =
			addCommerceDiscountUsageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountUsageEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceDiscountUsageEntry2.getPrimaryKey());

		Map<Serializable, CommerceDiscountUsageEntry>
			commerceDiscountUsageEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceDiscountUsageEntries.size());
		Assert.assertEquals(
			newCommerceDiscountUsageEntry1,
			commerceDiscountUsageEntries.get(
				newCommerceDiscountUsageEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceDiscountUsageEntry2,
			commerceDiscountUsageEntries.get(
				newCommerceDiscountUsageEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceDiscountUsageEntry>
			commerceDiscountUsageEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceDiscountUsageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			addCommerceDiscountUsageEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountUsageEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceDiscountUsageEntry>
			commerceDiscountUsageEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceDiscountUsageEntries.size());
		Assert.assertEquals(
			newCommerceDiscountUsageEntry,
			commerceDiscountUsageEntries.get(
				newCommerceDiscountUsageEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceDiscountUsageEntry>
			commerceDiscountUsageEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceDiscountUsageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceDiscountUsageEntry newCommerceDiscountUsageEntry =
			addCommerceDiscountUsageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountUsageEntry.getPrimaryKey());

		Map<Serializable, CommerceDiscountUsageEntry>
			commerceDiscountUsageEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceDiscountUsageEntries.size());
		Assert.assertEquals(
			newCommerceDiscountUsageEntry,
			commerceDiscountUsageEntries.get(
				newCommerceDiscountUsageEntry.getPrimaryKey()));
	}

	protected CommerceDiscountUsageEntry addCommerceDiscountUsageEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceDiscountUsageEntry commerceDiscountUsageEntry =
			_persistence.create(pk);

		commerceDiscountUsageEntry.setMvccVersion(RandomTestUtil.nextLong());

		commerceDiscountUsageEntry.setCompanyId(RandomTestUtil.nextLong());

		commerceDiscountUsageEntry.setUserId(RandomTestUtil.nextLong());

		commerceDiscountUsageEntry.setUserName(RandomTestUtil.randomString());

		commerceDiscountUsageEntry.setCreateDate(RandomTestUtil.nextDate());

		commerceDiscountUsageEntry.setModifiedDate(RandomTestUtil.nextDate());

		commerceDiscountUsageEntry.setCommerceAccountId(
			RandomTestUtil.nextLong());

		commerceDiscountUsageEntry.setCommerceOrderId(
			RandomTestUtil.nextLong());

		commerceDiscountUsageEntry.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		_commerceDiscountUsageEntries.add(
			_persistence.update(commerceDiscountUsageEntry));

		return commerceDiscountUsageEntry;
	}

	private List<CommerceDiscountUsageEntry> _commerceDiscountUsageEntries =
		new ArrayList<CommerceDiscountUsageEntry>();
	private CommerceDiscountUsageEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}