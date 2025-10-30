/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.order.exception.NoSuchVirtualOrderItemFileEntryException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemFileEntryPersistence;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemFileEntryUtil;
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
public class CommerceVirtualOrderItemFileEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.product.type.virtual.order.service"));

	@Before
	public void setUp() {
		_persistence = CommerceVirtualOrderItemFileEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceVirtualOrderItemFileEntry> iterator =
			_commerceVirtualOrderItemFileEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			_persistence.create(pk);

		Assert.assertNotNull(commerceVirtualOrderItemFileEntry);

		Assert.assertEquals(
			commerceVirtualOrderItemFileEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		_persistence.remove(newCommerceVirtualOrderItemFileEntry);

		CommerceVirtualOrderItemFileEntry
			existingCommerceVirtualOrderItemFileEntry =
				_persistence.fetchByPrimaryKey(
					newCommerceVirtualOrderItemFileEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceVirtualOrderItemFileEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceVirtualOrderItemFileEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			_persistence.create(pk);

		newCommerceVirtualOrderItemFileEntry.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setUuid(
			RandomTestUtil.randomString());

		newCommerceVirtualOrderItemFileEntry.setGroupId(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setUserId(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setUserName(
			RandomTestUtil.randomString());

		newCommerceVirtualOrderItemFileEntry.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceVirtualOrderItemFileEntry.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceVirtualOrderItemFileEntry.setCommerceVirtualOrderItemId(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setFileEntryId(
			RandomTestUtil.nextLong());

		newCommerceVirtualOrderItemFileEntry.setUrl(
			RandomTestUtil.randomString());

		newCommerceVirtualOrderItemFileEntry.setUsages(
			RandomTestUtil.nextInt());

		newCommerceVirtualOrderItemFileEntry.setVersion(
			RandomTestUtil.randomString());

		_commerceVirtualOrderItemFileEntries.add(
			_persistence.update(newCommerceVirtualOrderItemFileEntry));

		CommerceVirtualOrderItemFileEntry
			existingCommerceVirtualOrderItemFileEntry =
				_persistence.findByPrimaryKey(
					newCommerceVirtualOrderItemFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getMvccVersion(),
			newCommerceVirtualOrderItemFileEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getUuid(),
			newCommerceVirtualOrderItemFileEntry.getUuid());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId(),
			newCommerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getGroupId(),
			newCommerceVirtualOrderItemFileEntry.getGroupId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getCompanyId(),
			newCommerceVirtualOrderItemFileEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getUserId(),
			newCommerceVirtualOrderItemFileEntry.getUserId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getUserName(),
			newCommerceVirtualOrderItemFileEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceVirtualOrderItemFileEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceVirtualOrderItemFileEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceVirtualOrderItemFileEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceVirtualOrderItemFileEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemId(),
			newCommerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getFileEntryId(),
			newCommerceVirtualOrderItemFileEntry.getFileEntryId());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getUrl(),
			newCommerceVirtualOrderItemFileEntry.getUrl());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getUsages(),
			newCommerceVirtualOrderItemFileEntry.getUsages());
		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry.getVersion(),
			newCommerceVirtualOrderItemFileEntry.getVersion());
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
	public void testCountByCommerceVirtualOrderItemId() throws Exception {
		_persistence.countByCommerceVirtualOrderItemId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceVirtualOrderItemId(0L);
	}

	@Test
	public void testCountByC_F() throws Exception {
		_persistence.countByC_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_F(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		CommerceVirtualOrderItemFileEntry
			existingCommerceVirtualOrderItemFileEntry =
				_persistence.findByPrimaryKey(
					newCommerceVirtualOrderItemFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry,
			newCommerceVirtualOrderItemFileEntry);
	}

	@Test(expected = NoSuchVirtualOrderItemFileEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceVirtualOrderItemFileEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CVirtualOrderItemFileEntry", "mvccVersion", true, "uuid", true,
			"commerceVirtualOrderItemFileEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "commerceVirtualOrderItemId", true,
			"fileEntryId", true, "url", true, "usages", true, "version", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		CommerceVirtualOrderItemFileEntry
			existingCommerceVirtualOrderItemFileEntry =
				_persistence.fetchByPrimaryKey(
					newCommerceVirtualOrderItemFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceVirtualOrderItemFileEntry,
			newCommerceVirtualOrderItemFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceVirtualOrderItemFileEntry
			missingCommerceVirtualOrderItemFileEntry =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceVirtualOrderItemFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceVirtualOrderItemFileEntry
			newCommerceVirtualOrderItemFileEntry1 =
				addCommerceVirtualOrderItemFileEntry();
		CommerceVirtualOrderItemFileEntry
			newCommerceVirtualOrderItemFileEntry2 =
				addCommerceVirtualOrderItemFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceVirtualOrderItemFileEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceVirtualOrderItemFileEntry2.getPrimaryKey());

		Map<Serializable, CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceVirtualOrderItemFileEntries.size());
		Assert.assertEquals(
			newCommerceVirtualOrderItemFileEntry1,
			commerceVirtualOrderItemFileEntries.get(
				newCommerceVirtualOrderItemFileEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceVirtualOrderItemFileEntry2,
			commerceVirtualOrderItemFileEntries.get(
				newCommerceVirtualOrderItemFileEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceVirtualOrderItemFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceVirtualOrderItemFileEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceVirtualOrderItemFileEntries.size());
		Assert.assertEquals(
			newCommerceVirtualOrderItemFileEntry,
			commerceVirtualOrderItemFileEntries.get(
				newCommerceVirtualOrderItemFileEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceVirtualOrderItemFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceVirtualOrderItemFileEntry.getPrimaryKey());

		Map<Serializable, CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceVirtualOrderItemFileEntries.size());
		Assert.assertEquals(
			newCommerceVirtualOrderItemFileEntry,
			commerceVirtualOrderItemFileEntries.get(
				newCommerceVirtualOrderItemFileEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceVirtualOrderItemFileEntry newCommerceVirtualOrderItemFileEntry =
			addCommerceVirtualOrderItemFileEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceVirtualOrderItemFileEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry) {

		Assert.assertEquals(
			commerceVirtualOrderItemFileEntry.getUuid(),
			ReflectionTestUtil.invoke(
				commerceVirtualOrderItemFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceVirtualOrderItemFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceVirtualOrderItemFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected CommerceVirtualOrderItemFileEntry
			addCommerceVirtualOrderItemFileEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			_persistence.create(pk);

		commerceVirtualOrderItemFileEntry.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setUuid(
			RandomTestUtil.randomString());

		commerceVirtualOrderItemFileEntry.setGroupId(RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setCompanyId(
			RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setUserId(RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setUserName(
			RandomTestUtil.randomString());

		commerceVirtualOrderItemFileEntry.setCreateDate(
			RandomTestUtil.nextDate());

		commerceVirtualOrderItemFileEntry.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceVirtualOrderItemFileEntry.setCommerceVirtualOrderItemId(
			RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setFileEntryId(
			RandomTestUtil.nextLong());

		commerceVirtualOrderItemFileEntry.setUrl(RandomTestUtil.randomString());

		commerceVirtualOrderItemFileEntry.setUsages(RandomTestUtil.nextInt());

		commerceVirtualOrderItemFileEntry.setVersion(
			RandomTestUtil.randomString());

		_commerceVirtualOrderItemFileEntries.add(
			_persistence.update(commerceVirtualOrderItemFileEntry));

		return commerceVirtualOrderItemFileEntry;
	}

	private List<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntries =
			new ArrayList<CommerceVirtualOrderItemFileEntry>();
	private CommerceVirtualOrderItemFileEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}