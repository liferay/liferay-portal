/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchRenameFinderColumnEntryException;
import com.liferay.portal.tools.service.builder.test.model.RenameFinderColumnEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenameFinderColumnEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenameFinderColumnEntryUtil;

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
public class RenameFinderColumnEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = RenameFinderColumnEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RenameFinderColumnEntry> iterator =
			_renameFinderColumnEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RenameFinderColumnEntry renameFinderColumnEntry = _persistence.create(
			pk);

		Assert.assertNotNull(renameFinderColumnEntry);

		Assert.assertEquals(renameFinderColumnEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		_persistence.remove(newRenameFinderColumnEntry);

		RenameFinderColumnEntry existingRenameFinderColumnEntry =
			_persistence.fetchByPrimaryKey(
				newRenameFinderColumnEntry.getPrimaryKey());

		Assert.assertNull(existingRenameFinderColumnEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRenameFinderColumnEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RenameFinderColumnEntry newRenameFinderColumnEntry =
			_persistence.create(pk);

		newRenameFinderColumnEntry.setGroupId(RandomTestUtil.nextLong());

		newRenameFinderColumnEntry.setColumnToRename(
			RandomTestUtil.randomString());

		_renameFinderColumnEntries.add(
			_persistence.update(newRenameFinderColumnEntry));

		RenameFinderColumnEntry existingRenameFinderColumnEntry =
			_persistence.findByPrimaryKey(
				newRenameFinderColumnEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRenameFinderColumnEntry.getRenameFinderColumnEntryId(),
			newRenameFinderColumnEntry.getRenameFinderColumnEntryId());
		Assert.assertEquals(
			existingRenameFinderColumnEntry.getGroupId(),
			newRenameFinderColumnEntry.getGroupId());
		Assert.assertEquals(
			existingRenameFinderColumnEntry.getColumnToRename(),
			newRenameFinderColumnEntry.getColumnToRename());
	}

	@Test
	public void testCountByColumnToRename() throws Exception {
		_persistence.countByColumnToRename("");

		_persistence.countByColumnToRename("null");

		_persistence.countByColumnToRename((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		RenameFinderColumnEntry existingRenameFinderColumnEntry =
			_persistence.findByPrimaryKey(
				newRenameFinderColumnEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRenameFinderColumnEntry, newRenameFinderColumnEntry);
	}

	@Test(expected = NoSuchRenameFinderColumnEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RenameFinderColumnEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"RenameFinderColumnEntry", "renameFinderColumnEntryId", true,
			"groupId", true, "columnToRename", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		RenameFinderColumnEntry existingRenameFinderColumnEntry =
			_persistence.fetchByPrimaryKey(
				newRenameFinderColumnEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRenameFinderColumnEntry, newRenameFinderColumnEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RenameFinderColumnEntry missingRenameFinderColumnEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRenameFinderColumnEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RenameFinderColumnEntry newRenameFinderColumnEntry1 =
			addRenameFinderColumnEntry();
		RenameFinderColumnEntry newRenameFinderColumnEntry2 =
			addRenameFinderColumnEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRenameFinderColumnEntry1.getPrimaryKey());
		primaryKeys.add(newRenameFinderColumnEntry2.getPrimaryKey());

		Map<Serializable, RenameFinderColumnEntry> renameFinderColumnEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, renameFinderColumnEntries.size());
		Assert.assertEquals(
			newRenameFinderColumnEntry1,
			renameFinderColumnEntries.get(
				newRenameFinderColumnEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRenameFinderColumnEntry2,
			renameFinderColumnEntries.get(
				newRenameFinderColumnEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RenameFinderColumnEntry> renameFinderColumnEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(renameFinderColumnEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRenameFinderColumnEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RenameFinderColumnEntry> renameFinderColumnEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, renameFinderColumnEntries.size());
		Assert.assertEquals(
			newRenameFinderColumnEntry,
			renameFinderColumnEntries.get(
				newRenameFinderColumnEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RenameFinderColumnEntry> renameFinderColumnEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(renameFinderColumnEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRenameFinderColumnEntry.getPrimaryKey());

		Map<Serializable, RenameFinderColumnEntry> renameFinderColumnEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, renameFinderColumnEntries.size());
		Assert.assertEquals(
			newRenameFinderColumnEntry,
			renameFinderColumnEntries.get(
				newRenameFinderColumnEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RenameFinderColumnEntry newRenameFinderColumnEntry =
			addRenameFinderColumnEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newRenameFinderColumnEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		RenameFinderColumnEntry renameFinderColumnEntry) {

		Assert.assertEquals(
			renameFinderColumnEntry.getColumnToRename(),
			ReflectionTestUtil.invoke(
				renameFinderColumnEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "columnToRename"));
	}

	protected RenameFinderColumnEntry addRenameFinderColumnEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		RenameFinderColumnEntry renameFinderColumnEntry = _persistence.create(
			pk);

		renameFinderColumnEntry.setGroupId(RandomTestUtil.nextLong());

		renameFinderColumnEntry.setColumnToRename(
			RandomTestUtil.randomString());

		_renameFinderColumnEntries.add(
			_persistence.update(renameFinderColumnEntry));

		return renameFinderColumnEntry;
	}

	private List<RenameFinderColumnEntry> _renameFinderColumnEntries =
		new ArrayList<RenameFinderColumnEntry>();
	private RenameFinderColumnEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}