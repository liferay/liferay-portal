/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheFieldEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheFieldEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheFieldEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheFieldEntryUtil;

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
public class CacheFieldEntryPersistenceTest {

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
		_persistence = CacheFieldEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CacheFieldEntry> iterator = _cacheFieldEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheFieldEntry cacheFieldEntry = _persistence.create(pk);

		Assert.assertNotNull(cacheFieldEntry);

		Assert.assertEquals(cacheFieldEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CacheFieldEntry newCacheFieldEntry = addCacheFieldEntry();

		_persistence.remove(newCacheFieldEntry);

		CacheFieldEntry existingCacheFieldEntry =
			_persistence.fetchByPrimaryKey(newCacheFieldEntry.getPrimaryKey());

		Assert.assertNull(existingCacheFieldEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCacheFieldEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheFieldEntry newCacheFieldEntry = _persistence.create(pk);

		newCacheFieldEntry.setGroupId(RandomTestUtil.nextLong());

		newCacheFieldEntry.setName(RandomTestUtil.randomString());

		_cacheFieldEntries.add(_persistence.update(newCacheFieldEntry));

		CacheFieldEntry existingCacheFieldEntry = _persistence.findByPrimaryKey(
			newCacheFieldEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheFieldEntry.getCacheFieldEntryId(),
			newCacheFieldEntry.getCacheFieldEntryId());
		Assert.assertEquals(
			existingCacheFieldEntry.getGroupId(),
			newCacheFieldEntry.getGroupId());
		Assert.assertEquals(
			existingCacheFieldEntry.getName(), newCacheFieldEntry.getName());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CacheFieldEntry newCacheFieldEntry = addCacheFieldEntry();

		CacheFieldEntry existingCacheFieldEntry = _persistence.findByPrimaryKey(
			newCacheFieldEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheFieldEntry, newCacheFieldEntry);
	}

	@Test(expected = NoSuchCacheFieldEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CacheFieldEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CacheFieldEntry", "cacheFieldEntryId", true, "groupId", true,
			"name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CacheFieldEntry newCacheFieldEntry = addCacheFieldEntry();

		CacheFieldEntry existingCacheFieldEntry =
			_persistence.fetchByPrimaryKey(newCacheFieldEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheFieldEntry, newCacheFieldEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheFieldEntry missingCacheFieldEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCacheFieldEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CacheFieldEntry newCacheFieldEntry1 = addCacheFieldEntry();
		CacheFieldEntry newCacheFieldEntry2 = addCacheFieldEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheFieldEntry1.getPrimaryKey());
		primaryKeys.add(newCacheFieldEntry2.getPrimaryKey());

		Map<Serializable, CacheFieldEntry> cacheFieldEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cacheFieldEntries.size());
		Assert.assertEquals(
			newCacheFieldEntry1,
			cacheFieldEntries.get(newCacheFieldEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCacheFieldEntry2,
			cacheFieldEntries.get(newCacheFieldEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CacheFieldEntry> cacheFieldEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheFieldEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CacheFieldEntry newCacheFieldEntry = addCacheFieldEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheFieldEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CacheFieldEntry> cacheFieldEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheFieldEntries.size());
		Assert.assertEquals(
			newCacheFieldEntry,
			cacheFieldEntries.get(newCacheFieldEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CacheFieldEntry> cacheFieldEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheFieldEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CacheFieldEntry newCacheFieldEntry = addCacheFieldEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheFieldEntry.getPrimaryKey());

		Map<Serializable, CacheFieldEntry> cacheFieldEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheFieldEntries.size());
		Assert.assertEquals(
			newCacheFieldEntry,
			cacheFieldEntries.get(newCacheFieldEntry.getPrimaryKey()));
	}

	protected CacheFieldEntry addCacheFieldEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheFieldEntry cacheFieldEntry = _persistence.create(pk);

		cacheFieldEntry.setGroupId(RandomTestUtil.nextLong());

		cacheFieldEntry.setName(RandomTestUtil.randomString());

		_cacheFieldEntries.add(_persistence.update(cacheFieldEntry));

		return cacheFieldEntry;
	}

	private List<CacheFieldEntry> _cacheFieldEntries =
		new ArrayList<CacheFieldEntry>();
	private CacheFieldEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}