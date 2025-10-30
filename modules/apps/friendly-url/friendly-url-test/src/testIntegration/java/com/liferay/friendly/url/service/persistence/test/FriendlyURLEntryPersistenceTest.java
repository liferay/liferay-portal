/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryUtil;
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
public class FriendlyURLEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.friendly.url.service"));

	@Before
	public void setUp() {
		_persistence = FriendlyURLEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FriendlyURLEntry> iterator = _friendlyURLEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntry friendlyURLEntry = _persistence.create(pk);

		Assert.assertNotNull(friendlyURLEntry);

		Assert.assertEquals(friendlyURLEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		_persistence.remove(newFriendlyURLEntry);

		FriendlyURLEntry existingFriendlyURLEntry =
			_persistence.fetchByPrimaryKey(newFriendlyURLEntry.getPrimaryKey());

		Assert.assertNull(existingFriendlyURLEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFriendlyURLEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntry newFriendlyURLEntry = _persistence.create(pk);

		newFriendlyURLEntry.setMvccVersion(RandomTestUtil.nextLong());

		newFriendlyURLEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newFriendlyURLEntry.setUuid(RandomTestUtil.randomString());

		newFriendlyURLEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		newFriendlyURLEntry.setGroupId(RandomTestUtil.nextLong());

		newFriendlyURLEntry.setCompanyId(RandomTestUtil.nextLong());

		newFriendlyURLEntry.setCreateDate(RandomTestUtil.nextDate());

		newFriendlyURLEntry.setModifiedDate(RandomTestUtil.nextDate());

		newFriendlyURLEntry.setClassNameId(RandomTestUtil.nextLong());

		newFriendlyURLEntry.setClassPK(RandomTestUtil.nextLong());

		_friendlyURLEntries.add(_persistence.update(newFriendlyURLEntry));

		FriendlyURLEntry existingFriendlyURLEntry =
			_persistence.findByPrimaryKey(newFriendlyURLEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFriendlyURLEntry.getMvccVersion(),
			newFriendlyURLEntry.getMvccVersion());
		Assert.assertEquals(
			existingFriendlyURLEntry.getCtCollectionId(),
			newFriendlyURLEntry.getCtCollectionId());
		Assert.assertEquals(
			existingFriendlyURLEntry.getUuid(), newFriendlyURLEntry.getUuid());
		Assert.assertEquals(
			existingFriendlyURLEntry.getDefaultLanguageId(),
			newFriendlyURLEntry.getDefaultLanguageId());
		Assert.assertEquals(
			existingFriendlyURLEntry.getFriendlyURLEntryId(),
			newFriendlyURLEntry.getFriendlyURLEntryId());
		Assert.assertEquals(
			existingFriendlyURLEntry.getGroupId(),
			newFriendlyURLEntry.getGroupId());
		Assert.assertEquals(
			existingFriendlyURLEntry.getCompanyId(),
			newFriendlyURLEntry.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFriendlyURLEntry.getCreateDate()),
			Time.getShortTimestamp(newFriendlyURLEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingFriendlyURLEntry.getModifiedDate()),
			Time.getShortTimestamp(newFriendlyURLEntry.getModifiedDate()));
		Assert.assertEquals(
			existingFriendlyURLEntry.getClassNameId(),
			newFriendlyURLEntry.getClassNameId());
		Assert.assertEquals(
			existingFriendlyURLEntry.getClassPK(),
			newFriendlyURLEntry.getClassPK());
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
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		FriendlyURLEntry existingFriendlyURLEntry =
			_persistence.findByPrimaryKey(newFriendlyURLEntry.getPrimaryKey());

		Assert.assertEquals(existingFriendlyURLEntry, newFriendlyURLEntry);
	}

	@Test(expected = NoSuchFriendlyURLEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FriendlyURLEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FriendlyURLEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "defaultLanguageId", true, "friendlyURLEntryId", true,
			"groupId", true, "companyId", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		FriendlyURLEntry existingFriendlyURLEntry =
			_persistence.fetchByPrimaryKey(newFriendlyURLEntry.getPrimaryKey());

		Assert.assertEquals(existingFriendlyURLEntry, newFriendlyURLEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntry missingFriendlyURLEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFriendlyURLEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FriendlyURLEntry newFriendlyURLEntry1 = addFriendlyURLEntry();
		FriendlyURLEntry newFriendlyURLEntry2 = addFriendlyURLEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntry1.getPrimaryKey());
		primaryKeys.add(newFriendlyURLEntry2.getPrimaryKey());

		Map<Serializable, FriendlyURLEntry> friendlyURLEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, friendlyURLEntries.size());
		Assert.assertEquals(
			newFriendlyURLEntry1,
			friendlyURLEntries.get(newFriendlyURLEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFriendlyURLEntry2,
			friendlyURLEntries.get(newFriendlyURLEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FriendlyURLEntry> friendlyURLEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(friendlyURLEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FriendlyURLEntry> friendlyURLEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, friendlyURLEntries.size());
		Assert.assertEquals(
			newFriendlyURLEntry,
			friendlyURLEntries.get(newFriendlyURLEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FriendlyURLEntry> friendlyURLEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(friendlyURLEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntry.getPrimaryKey());

		Map<Serializable, FriendlyURLEntry> friendlyURLEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, friendlyURLEntries.size());
		Assert.assertEquals(
			newFriendlyURLEntry,
			friendlyURLEntries.get(newFriendlyURLEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FriendlyURLEntry newFriendlyURLEntry = addFriendlyURLEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newFriendlyURLEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(FriendlyURLEntry friendlyURLEntry) {
		Assert.assertEquals(
			friendlyURLEntry.getUuid(),
			ReflectionTestUtil.invoke(
				friendlyURLEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(friendlyURLEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				friendlyURLEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected FriendlyURLEntry addFriendlyURLEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntry friendlyURLEntry = _persistence.create(pk);

		friendlyURLEntry.setMvccVersion(RandomTestUtil.nextLong());

		friendlyURLEntry.setCtCollectionId(RandomTestUtil.nextLong());

		friendlyURLEntry.setUuid(RandomTestUtil.randomString());

		friendlyURLEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		friendlyURLEntry.setGroupId(RandomTestUtil.nextLong());

		friendlyURLEntry.setCompanyId(RandomTestUtil.nextLong());

		friendlyURLEntry.setCreateDate(RandomTestUtil.nextDate());

		friendlyURLEntry.setModifiedDate(RandomTestUtil.nextDate());

		friendlyURLEntry.setClassNameId(RandomTestUtil.nextLong());

		friendlyURLEntry.setClassPK(RandomTestUtil.nextLong());

		_friendlyURLEntries.add(_persistence.update(friendlyURLEntry));

		return friendlyURLEntry;
	}

	private List<FriendlyURLEntry> _friendlyURLEntries =
		new ArrayList<FriendlyURLEntry>();
	private FriendlyURLEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}