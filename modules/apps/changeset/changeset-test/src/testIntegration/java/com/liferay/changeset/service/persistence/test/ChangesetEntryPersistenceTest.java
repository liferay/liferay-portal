/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.changeset.exception.NoSuchEntryException;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.service.persistence.ChangesetEntryPersistence;
import com.liferay.changeset.service.persistence.ChangesetEntryUtil;
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
public class ChangesetEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.changeset.service"));

	@Before
	public void setUp() {
		_persistence = ChangesetEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ChangesetEntry> iterator = _changesetEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetEntry changesetEntry = _persistence.create(pk);

		Assert.assertNotNull(changesetEntry);

		Assert.assertEquals(changesetEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ChangesetEntry newChangesetEntry = addChangesetEntry();

		_persistence.remove(newChangesetEntry);

		ChangesetEntry existingChangesetEntry = _persistence.fetchByPrimaryKey(
			newChangesetEntry.getPrimaryKey());

		Assert.assertNull(existingChangesetEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addChangesetEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetEntry newChangesetEntry = _persistence.create(pk);

		newChangesetEntry.setGroupId(RandomTestUtil.nextLong());

		newChangesetEntry.setCompanyId(RandomTestUtil.nextLong());

		newChangesetEntry.setUserId(RandomTestUtil.nextLong());

		newChangesetEntry.setUserName(RandomTestUtil.randomString());

		newChangesetEntry.setCreateDate(RandomTestUtil.nextDate());

		newChangesetEntry.setModifiedDate(RandomTestUtil.nextDate());

		newChangesetEntry.setChangesetCollectionId(RandomTestUtil.nextLong());

		newChangesetEntry.setClassNameId(RandomTestUtil.nextLong());

		newChangesetEntry.setClassPK(RandomTestUtil.nextLong());

		_changesetEntries.add(_persistence.update(newChangesetEntry));

		ChangesetEntry existingChangesetEntry = _persistence.findByPrimaryKey(
			newChangesetEntry.getPrimaryKey());

		Assert.assertEquals(
			existingChangesetEntry.getChangesetEntryId(),
			newChangesetEntry.getChangesetEntryId());
		Assert.assertEquals(
			existingChangesetEntry.getGroupId(),
			newChangesetEntry.getGroupId());
		Assert.assertEquals(
			existingChangesetEntry.getCompanyId(),
			newChangesetEntry.getCompanyId());
		Assert.assertEquals(
			existingChangesetEntry.getUserId(), newChangesetEntry.getUserId());
		Assert.assertEquals(
			existingChangesetEntry.getUserName(),
			newChangesetEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingChangesetEntry.getCreateDate()),
			Time.getShortTimestamp(newChangesetEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingChangesetEntry.getModifiedDate()),
			Time.getShortTimestamp(newChangesetEntry.getModifiedDate()));
		Assert.assertEquals(
			existingChangesetEntry.getChangesetCollectionId(),
			newChangesetEntry.getChangesetCollectionId());
		Assert.assertEquals(
			existingChangesetEntry.getClassNameId(),
			newChangesetEntry.getClassNameId());
		Assert.assertEquals(
			existingChangesetEntry.getClassPK(),
			newChangesetEntry.getClassPK());
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
	public void testCountByChangesetCollectionId() throws Exception {
		_persistence.countByChangesetCollectionId(RandomTestUtil.nextLong());

		_persistence.countByChangesetCollectionId(0L);
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
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ChangesetEntry newChangesetEntry = addChangesetEntry();

		ChangesetEntry existingChangesetEntry = _persistence.findByPrimaryKey(
			newChangesetEntry.getPrimaryKey());

		Assert.assertEquals(existingChangesetEntry, newChangesetEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ChangesetEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ChangesetEntry", "changesetEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "changesetCollectionId", true,
			"classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ChangesetEntry newChangesetEntry = addChangesetEntry();

		ChangesetEntry existingChangesetEntry = _persistence.fetchByPrimaryKey(
			newChangesetEntry.getPrimaryKey());

		Assert.assertEquals(existingChangesetEntry, newChangesetEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetEntry missingChangesetEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingChangesetEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ChangesetEntry newChangesetEntry1 = addChangesetEntry();
		ChangesetEntry newChangesetEntry2 = addChangesetEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetEntry1.getPrimaryKey());
		primaryKeys.add(newChangesetEntry2.getPrimaryKey());

		Map<Serializable, ChangesetEntry> changesetEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, changesetEntries.size());
		Assert.assertEquals(
			newChangesetEntry1,
			changesetEntries.get(newChangesetEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newChangesetEntry2,
			changesetEntries.get(newChangesetEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ChangesetEntry> changesetEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(changesetEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ChangesetEntry newChangesetEntry = addChangesetEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ChangesetEntry> changesetEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, changesetEntries.size());
		Assert.assertEquals(
			newChangesetEntry,
			changesetEntries.get(newChangesetEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ChangesetEntry> changesetEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(changesetEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ChangesetEntry newChangesetEntry = addChangesetEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetEntry.getPrimaryKey());

		Map<Serializable, ChangesetEntry> changesetEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, changesetEntries.size());
		Assert.assertEquals(
			newChangesetEntry,
			changesetEntries.get(newChangesetEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ChangesetEntry newChangesetEntry = addChangesetEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newChangesetEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(ChangesetEntry changesetEntry) {
		Assert.assertEquals(
			Long.valueOf(changesetEntry.getChangesetCollectionId()),
			ReflectionTestUtil.<Long>invoke(
				changesetEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "changesetCollectionId"));
		Assert.assertEquals(
			Long.valueOf(changesetEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				changesetEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(changesetEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				changesetEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected ChangesetEntry addChangesetEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetEntry changesetEntry = _persistence.create(pk);

		changesetEntry.setGroupId(RandomTestUtil.nextLong());

		changesetEntry.setCompanyId(RandomTestUtil.nextLong());

		changesetEntry.setUserId(RandomTestUtil.nextLong());

		changesetEntry.setUserName(RandomTestUtil.randomString());

		changesetEntry.setCreateDate(RandomTestUtil.nextDate());

		changesetEntry.setModifiedDate(RandomTestUtil.nextDate());

		changesetEntry.setChangesetCollectionId(RandomTestUtil.nextLong());

		changesetEntry.setClassNameId(RandomTestUtil.nextLong());

		changesetEntry.setClassPK(RandomTestUtil.nextLong());

		_changesetEntries.add(_persistence.update(changesetEntry));

		return changesetEntry;
	}

	private List<ChangesetEntry> _changesetEntries =
		new ArrayList<ChangesetEntry>();
	private ChangesetEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}