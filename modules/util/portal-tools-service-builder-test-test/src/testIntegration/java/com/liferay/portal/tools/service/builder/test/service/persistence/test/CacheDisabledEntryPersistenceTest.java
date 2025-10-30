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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheDisabledEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheDisabledEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheDisabledEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheDisabledEntryUtil;

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
public class CacheDisabledEntryPersistenceTest {

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
		_persistence = CacheDisabledEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CacheDisabledEntry> iterator =
			_cacheDisabledEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheDisabledEntry cacheDisabledEntry = _persistence.create(pk);

		Assert.assertNotNull(cacheDisabledEntry);

		Assert.assertEquals(cacheDisabledEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		_persistence.remove(newCacheDisabledEntry);

		CacheDisabledEntry existingCacheDisabledEntry =
			_persistence.fetchByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey());

		Assert.assertNull(existingCacheDisabledEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCacheDisabledEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheDisabledEntry newCacheDisabledEntry = _persistence.create(pk);

		newCacheDisabledEntry.setName(RandomTestUtil.randomString());

		_cacheDisabledEntries.add(_persistence.update(newCacheDisabledEntry));

		CacheDisabledEntry existingCacheDisabledEntry =
			_persistence.findByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheDisabledEntry.getCacheDisabledEntryId(),
			newCacheDisabledEntry.getCacheDisabledEntryId());
		Assert.assertEquals(
			existingCacheDisabledEntry.getName(),
			newCacheDisabledEntry.getName());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		CacheDisabledEntry existingCacheDisabledEntry =
			_persistence.findByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheDisabledEntry, newCacheDisabledEntry);
	}

	@Test(expected = NoSuchCacheDisabledEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CacheDisabledEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CacheDisabledEntry", "cacheDisabledEntryId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		CacheDisabledEntry existingCacheDisabledEntry =
			_persistence.fetchByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheDisabledEntry, newCacheDisabledEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheDisabledEntry missingCacheDisabledEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCacheDisabledEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CacheDisabledEntry newCacheDisabledEntry1 = addCacheDisabledEntry();
		CacheDisabledEntry newCacheDisabledEntry2 = addCacheDisabledEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheDisabledEntry1.getPrimaryKey());
		primaryKeys.add(newCacheDisabledEntry2.getPrimaryKey());

		Map<Serializable, CacheDisabledEntry> cacheDisabledEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cacheDisabledEntries.size());
		Assert.assertEquals(
			newCacheDisabledEntry1,
			cacheDisabledEntries.get(newCacheDisabledEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCacheDisabledEntry2,
			cacheDisabledEntries.get(newCacheDisabledEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CacheDisabledEntry> cacheDisabledEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheDisabledEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheDisabledEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CacheDisabledEntry> cacheDisabledEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheDisabledEntries.size());
		Assert.assertEquals(
			newCacheDisabledEntry,
			cacheDisabledEntries.get(newCacheDisabledEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CacheDisabledEntry> cacheDisabledEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheDisabledEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheDisabledEntry.getPrimaryKey());

		Map<Serializable, CacheDisabledEntry> cacheDisabledEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheDisabledEntries.size());
		Assert.assertEquals(
			newCacheDisabledEntry,
			cacheDisabledEntries.get(newCacheDisabledEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(CacheDisabledEntry cacheDisabledEntry) {
		Assert.assertEquals(
			cacheDisabledEntry.getName(),
			ReflectionTestUtil.invoke(
				cacheDisabledEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected CacheDisabledEntry addCacheDisabledEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheDisabledEntry cacheDisabledEntry = _persistence.create(pk);

		cacheDisabledEntry.setName(RandomTestUtil.randomString());

		_cacheDisabledEntries.add(_persistence.update(cacheDisabledEntry));

		return cacheDisabledEntry;
	}

	private List<CacheDisabledEntry> _cacheDisabledEntries =
		new ArrayList<CacheDisabledEntry>();
	private CacheDisabledEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}