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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.model.VersionedEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.VersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.VersionedEntryUtil;

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
public class VersionedEntryPersistenceTest {

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
		_persistence = VersionedEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<VersionedEntry> iterator = _versionedEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VersionedEntry versionedEntry = _persistence.create(pk);

		Assert.assertNotNull(versionedEntry);

		Assert.assertEquals(versionedEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		VersionedEntry newVersionedEntry = addVersionedEntry();

		_persistence.remove(newVersionedEntry);

		VersionedEntry existingVersionedEntry = _persistence.fetchByPrimaryKey(
			newVersionedEntry.getPrimaryKey());

		Assert.assertNull(existingVersionedEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addVersionedEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VersionedEntry newVersionedEntry = _persistence.create(pk);

		newVersionedEntry.setMvccVersion(RandomTestUtil.nextLong());

		newVersionedEntry.setHeadId(RandomTestUtil.nextLong());

		newVersionedEntry.setGroupId(RandomTestUtil.nextLong());

		_versionedEntries.add(_persistence.update(newVersionedEntry));

		VersionedEntry existingVersionedEntry = _persistence.findByPrimaryKey(
			newVersionedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingVersionedEntry.getMvccVersion(),
			newVersionedEntry.getMvccVersion());
		Assert.assertEquals(
			existingVersionedEntry.getHeadId(), newVersionedEntry.getHeadId());
		Assert.assertEquals(
			existingVersionedEntry.getVersionedEntryId(),
			newVersionedEntry.getVersionedEntryId());
		Assert.assertEquals(
			existingVersionedEntry.getGroupId(),
			newVersionedEntry.getGroupId());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Head() throws Exception {
		_persistence.countByGroupId_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByGroupId_Head(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		VersionedEntry newVersionedEntry = addVersionedEntry();

		VersionedEntry existingVersionedEntry = _persistence.findByPrimaryKey(
			newVersionedEntry.getPrimaryKey());

		Assert.assertEquals(existingVersionedEntry, newVersionedEntry);
	}

	@Test(expected = NoSuchVersionedEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<VersionedEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"VersionedEntry", "mvccVersion", true, "headId", true,
			"versionedEntryId", true, "groupId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		VersionedEntry newVersionedEntry = addVersionedEntry();

		VersionedEntry existingVersionedEntry = _persistence.fetchByPrimaryKey(
			newVersionedEntry.getPrimaryKey());

		Assert.assertEquals(existingVersionedEntry, newVersionedEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VersionedEntry missingVersionedEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingVersionedEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		VersionedEntry newVersionedEntry1 = addVersionedEntry();
		VersionedEntry newVersionedEntry2 = addVersionedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVersionedEntry1.getPrimaryKey());
		primaryKeys.add(newVersionedEntry2.getPrimaryKey());

		Map<Serializable, VersionedEntry> versionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, versionedEntries.size());
		Assert.assertEquals(
			newVersionedEntry1,
			versionedEntries.get(newVersionedEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newVersionedEntry2,
			versionedEntries.get(newVersionedEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, VersionedEntry> versionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(versionedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		VersionedEntry newVersionedEntry = addVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVersionedEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, VersionedEntry> versionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, versionedEntries.size());
		Assert.assertEquals(
			newVersionedEntry,
			versionedEntries.get(newVersionedEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, VersionedEntry> versionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(versionedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		VersionedEntry newVersionedEntry = addVersionedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVersionedEntry.getPrimaryKey());

		Map<Serializable, VersionedEntry> versionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, versionedEntries.size());
		Assert.assertEquals(
			newVersionedEntry,
			versionedEntries.get(newVersionedEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		VersionedEntry newVersionedEntry = addVersionedEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newVersionedEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(VersionedEntry versionedEntry) {
		Assert.assertEquals(
			Long.valueOf(versionedEntry.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				versionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected VersionedEntry addVersionedEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VersionedEntry versionedEntry = _persistence.create(pk);

		versionedEntry.setMvccVersion(RandomTestUtil.nextLong());

		versionedEntry.setHeadId(-pk);

		versionedEntry.setGroupId(RandomTestUtil.nextLong());

		_versionedEntries.add(_persistence.update(versionedEntry));

		return versionedEntry;
	}

	private List<VersionedEntry> _versionedEntries =
		new ArrayList<VersionedEntry>();
	private VersionedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}