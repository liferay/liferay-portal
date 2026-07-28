/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheDisabledEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheDisabledEntry;
import com.liferay.portal.tools.service.builder.test.service.CacheDisabledEntryLocalServiceUtil;
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
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		newCacheDisabledEntry.setName(RandomTestUtil.randomString());

		newCacheDisabledEntry = _persistence.update(newCacheDisabledEntry);

		_cacheDisabledEntries.add(newCacheDisabledEntry);

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
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CacheDisabledEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CacheDisabledEntry>() {

				@Override
				public void performAction(
					CacheDisabledEntry cacheDisabledEntry) {

					Assert.assertNotNull(cacheDisabledEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheDisabledEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheDisabledEntryId",
				newCacheDisabledEntry.getCacheDisabledEntryId()));

		List<CacheDisabledEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CacheDisabledEntry existingCacheDisabledEntry = result.get(0);

		Assert.assertEquals(existingCacheDisabledEntry, newCacheDisabledEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheDisabledEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheDisabledEntryId", RandomTestUtil.nextLong()));

		List<CacheDisabledEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheDisabledEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheDisabledEntryId"));

		Object newCacheDisabledEntryId =
			newCacheDisabledEntry.getCacheDisabledEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheDisabledEntryId",
				new Object[] {newCacheDisabledEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCacheDisabledEntryId = result.get(0);

		Assert.assertEquals(
			existingCacheDisabledEntryId, newCacheDisabledEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheDisabledEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheDisabledEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheDisabledEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCacheDisabledEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		CacheDisabledEntry newCacheDisabledEntry = addCacheDisabledEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheDisabledEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheDisabledEntryId",
				newCacheDisabledEntry.getCacheDisabledEntryId()));

		List<CacheDisabledEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
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
// LIFERAY-SERVICE-BUILDER-HASH:2037975984