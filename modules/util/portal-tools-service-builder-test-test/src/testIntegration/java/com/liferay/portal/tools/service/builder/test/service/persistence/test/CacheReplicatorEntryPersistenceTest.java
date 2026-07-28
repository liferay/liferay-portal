/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheReplicatorEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheReplicatorEntry;
import com.liferay.portal.tools.service.builder.test.service.CacheReplicatorEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheReplicatorEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheReplicatorEntryUtil;

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
public class CacheReplicatorEntryPersistenceTest {

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
		_persistence = CacheReplicatorEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CacheReplicatorEntry> iterator =
			_cacheReplicatorEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheReplicatorEntry cacheReplicatorEntry = _persistence.create(pk);

		Assert.assertNotNull(cacheReplicatorEntry);

		Assert.assertEquals(cacheReplicatorEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		_persistence.remove(newCacheReplicatorEntry);

		CacheReplicatorEntry existingCacheReplicatorEntry =
			_persistence.fetchByPrimaryKey(
				newCacheReplicatorEntry.getPrimaryKey());

		Assert.assertNull(existingCacheReplicatorEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCacheReplicatorEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		newCacheReplicatorEntry.setCompanyId(RandomTestUtil.nextLong());

		newCacheReplicatorEntry.setName(RandomTestUtil.randomString());

		newCacheReplicatorEntry = _persistence.update(newCacheReplicatorEntry);

		_cacheReplicatorEntries.add(newCacheReplicatorEntry);

		CacheReplicatorEntry existingCacheReplicatorEntry =
			_persistence.findByPrimaryKey(
				newCacheReplicatorEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheReplicatorEntry.getCacheReplicatorEntryId(),
			newCacheReplicatorEntry.getCacheReplicatorEntryId());
		Assert.assertEquals(
			existingCacheReplicatorEntry.getCompanyId(),
			newCacheReplicatorEntry.getCompanyId());
		Assert.assertEquals(
			existingCacheReplicatorEntry.getName(),
			newCacheReplicatorEntry.getName());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		CacheReplicatorEntry existingCacheReplicatorEntry =
			_persistence.findByPrimaryKey(
				newCacheReplicatorEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheReplicatorEntry, newCacheReplicatorEntry);
	}

	@Test(expected = NoSuchCacheReplicatorEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CacheReplicatorEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CacheReplicatorEntry", "cacheReplicatorEntryId", true, "companyId",
			true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		CacheReplicatorEntry existingCacheReplicatorEntry =
			_persistence.fetchByPrimaryKey(
				newCacheReplicatorEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheReplicatorEntry, newCacheReplicatorEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheReplicatorEntry missingCacheReplicatorEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCacheReplicatorEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CacheReplicatorEntry newCacheReplicatorEntry1 =
			addCacheReplicatorEntry();
		CacheReplicatorEntry newCacheReplicatorEntry2 =
			addCacheReplicatorEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheReplicatorEntry1.getPrimaryKey());
		primaryKeys.add(newCacheReplicatorEntry2.getPrimaryKey());

		Map<Serializable, CacheReplicatorEntry> cacheReplicatorEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cacheReplicatorEntries.size());
		Assert.assertEquals(
			newCacheReplicatorEntry1,
			cacheReplicatorEntries.get(
				newCacheReplicatorEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCacheReplicatorEntry2,
			cacheReplicatorEntries.get(
				newCacheReplicatorEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CacheReplicatorEntry> cacheReplicatorEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheReplicatorEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheReplicatorEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CacheReplicatorEntry> cacheReplicatorEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheReplicatorEntries.size());
		Assert.assertEquals(
			newCacheReplicatorEntry,
			cacheReplicatorEntries.get(
				newCacheReplicatorEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CacheReplicatorEntry> cacheReplicatorEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheReplicatorEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheReplicatorEntry.getPrimaryKey());

		Map<Serializable, CacheReplicatorEntry> cacheReplicatorEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheReplicatorEntries.size());
		Assert.assertEquals(
			newCacheReplicatorEntry,
			cacheReplicatorEntries.get(
				newCacheReplicatorEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CacheReplicatorEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CacheReplicatorEntry>() {

				@Override
				public void performAction(
					CacheReplicatorEntry cacheReplicatorEntry) {

					Assert.assertNotNull(cacheReplicatorEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheReplicatorEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheReplicatorEntryId",
				newCacheReplicatorEntry.getCacheReplicatorEntryId()));

		List<CacheReplicatorEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CacheReplicatorEntry existingCacheReplicatorEntry = result.get(0);

		Assert.assertEquals(
			existingCacheReplicatorEntry, newCacheReplicatorEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheReplicatorEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheReplicatorEntryId", RandomTestUtil.nextLong()));

		List<CacheReplicatorEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheReplicatorEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheReplicatorEntryId"));

		Object newCacheReplicatorEntryId =
			newCacheReplicatorEntry.getCacheReplicatorEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheReplicatorEntryId",
				new Object[] {newCacheReplicatorEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCacheReplicatorEntryId = result.get(0);

		Assert.assertEquals(
			existingCacheReplicatorEntryId, newCacheReplicatorEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheReplicatorEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheReplicatorEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheReplicatorEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCacheReplicatorEntry.getPrimaryKey()));
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

		CacheReplicatorEntry newCacheReplicatorEntry =
			addCacheReplicatorEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheReplicatorEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheReplicatorEntryId",
				newCacheReplicatorEntry.getCacheReplicatorEntryId()));

		List<CacheReplicatorEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CacheReplicatorEntry cacheReplicatorEntry) {

		Assert.assertEquals(
			cacheReplicatorEntry.getName(),
			ReflectionTestUtil.invoke(
				cacheReplicatorEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected CacheReplicatorEntry addCacheReplicatorEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheReplicatorEntry cacheReplicatorEntry = _persistence.create(pk);

		cacheReplicatorEntry.setCompanyId(RandomTestUtil.nextLong());

		cacheReplicatorEntry.setName(RandomTestUtil.randomString());

		_cacheReplicatorEntries.add(_persistence.update(cacheReplicatorEntry));

		return cacheReplicatorEntry;
	}

	private List<CacheReplicatorEntry> _cacheReplicatorEntries =
		new ArrayList<CacheReplicatorEntry>();
	private CacheReplicatorEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:489683320