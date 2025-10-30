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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchRedundantIndexEntryException;
import com.liferay.portal.tools.service.builder.test.model.RedundantIndexEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.RedundantIndexEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RedundantIndexEntryUtil;

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
public class RedundantIndexEntryPersistenceTest {

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
		_persistence = RedundantIndexEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RedundantIndexEntry> iterator =
			_redundantIndexEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedundantIndexEntry redundantIndexEntry = _persistence.create(pk);

		Assert.assertNotNull(redundantIndexEntry);

		Assert.assertEquals(redundantIndexEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		_persistence.remove(newRedundantIndexEntry);

		RedundantIndexEntry existingRedundantIndexEntry =
			_persistence.fetchByPrimaryKey(
				newRedundantIndexEntry.getPrimaryKey());

		Assert.assertNull(existingRedundantIndexEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRedundantIndexEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedundantIndexEntry newRedundantIndexEntry = _persistence.create(pk);

		newRedundantIndexEntry.setCompanyId(RandomTestUtil.nextLong());

		newRedundantIndexEntry.setName(RandomTestUtil.randomString());

		_redundantIndexEntries.add(_persistence.update(newRedundantIndexEntry));

		RedundantIndexEntry existingRedundantIndexEntry =
			_persistence.findByPrimaryKey(
				newRedundantIndexEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedundantIndexEntry.getRedundantIndexEntryId(),
			newRedundantIndexEntry.getRedundantIndexEntryId());
		Assert.assertEquals(
			existingRedundantIndexEntry.getCompanyId(),
			newRedundantIndexEntry.getCompanyId());
		Assert.assertEquals(
			existingRedundantIndexEntry.getName(),
			newRedundantIndexEntry.getName());
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		RedundantIndexEntry existingRedundantIndexEntry =
			_persistence.findByPrimaryKey(
				newRedundantIndexEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedundantIndexEntry, newRedundantIndexEntry);
	}

	@Test(expected = NoSuchRedundantIndexEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RedundantIndexEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RedundantIndexEntry", "redundantIndexEntryId", true, "companyId",
			true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		RedundantIndexEntry existingRedundantIndexEntry =
			_persistence.fetchByPrimaryKey(
				newRedundantIndexEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedundantIndexEntry, newRedundantIndexEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedundantIndexEntry missingRedundantIndexEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRedundantIndexEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RedundantIndexEntry newRedundantIndexEntry1 = addRedundantIndexEntry();
		RedundantIndexEntry newRedundantIndexEntry2 = addRedundantIndexEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedundantIndexEntry1.getPrimaryKey());
		primaryKeys.add(newRedundantIndexEntry2.getPrimaryKey());

		Map<Serializable, RedundantIndexEntry> redundantIndexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, redundantIndexEntries.size());
		Assert.assertEquals(
			newRedundantIndexEntry1,
			redundantIndexEntries.get(newRedundantIndexEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRedundantIndexEntry2,
			redundantIndexEntries.get(newRedundantIndexEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RedundantIndexEntry> redundantIndexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redundantIndexEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedundantIndexEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RedundantIndexEntry> redundantIndexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redundantIndexEntries.size());
		Assert.assertEquals(
			newRedundantIndexEntry,
			redundantIndexEntries.get(newRedundantIndexEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RedundantIndexEntry> redundantIndexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redundantIndexEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedundantIndexEntry.getPrimaryKey());

		Map<Serializable, RedundantIndexEntry> redundantIndexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redundantIndexEntries.size());
		Assert.assertEquals(
			newRedundantIndexEntry,
			redundantIndexEntries.get(newRedundantIndexEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RedundantIndexEntry newRedundantIndexEntry = addRedundantIndexEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newRedundantIndexEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		RedundantIndexEntry redundantIndexEntry) {

		Assert.assertEquals(
			Long.valueOf(redundantIndexEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				redundantIndexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			redundantIndexEntry.getName(),
			ReflectionTestUtil.invoke(
				redundantIndexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected RedundantIndexEntry addRedundantIndexEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedundantIndexEntry redundantIndexEntry = _persistence.create(pk);

		redundantIndexEntry.setCompanyId(RandomTestUtil.nextLong());

		redundantIndexEntry.setName(RandomTestUtil.randomString());

		_redundantIndexEntries.add(_persistence.update(redundantIndexEntry));

		return redundantIndexEntry;
	}

	private List<RedundantIndexEntry> _redundantIndexEntries =
		new ArrayList<RedundantIndexEntry>();
	private RedundantIndexEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}