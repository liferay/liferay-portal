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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheMissEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheMissEntry;
import com.liferay.portal.tools.service.builder.test.service.CacheMissEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheMissEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheMissEntryUtil;

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
public class CacheMissEntryPersistenceTest {

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
		_persistence = CacheMissEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CacheMissEntry> iterator = _cacheMissEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheMissEntry cacheMissEntry = _persistence.create(pk);

		Assert.assertNotNull(cacheMissEntry);

		Assert.assertEquals(cacheMissEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		_persistence.remove(newCacheMissEntry);

		CacheMissEntry existingCacheMissEntry = _persistence.fetchByPrimaryKey(
			newCacheMissEntry.getPrimaryKey());

		Assert.assertNull(existingCacheMissEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCacheMissEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		newCacheMissEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCacheMissEntry = _persistence.update(newCacheMissEntry);

		_cacheMissEntries.add(newCacheMissEntry);

		CacheMissEntry existingCacheMissEntry = _persistence.findByPrimaryKey(
			newCacheMissEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCacheMissEntry.getMvccVersion(),
			newCacheMissEntry.getMvccVersion());
		Assert.assertEquals(
			existingCacheMissEntry.getCtCollectionId(),
			newCacheMissEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCacheMissEntry.getCacheMissEntryId(),
			newCacheMissEntry.getCacheMissEntryId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		CacheMissEntry existingCacheMissEntry = _persistence.findByPrimaryKey(
			newCacheMissEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheMissEntry, newCacheMissEntry);
	}

	@Test(expected = NoSuchCacheMissEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CacheMissEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CacheMissEntry", "mvccVersion", true, "ctCollectionId", true,
			"cacheMissEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		CacheMissEntry existingCacheMissEntry = _persistence.fetchByPrimaryKey(
			newCacheMissEntry.getPrimaryKey());

		Assert.assertEquals(existingCacheMissEntry, newCacheMissEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheMissEntry missingCacheMissEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCacheMissEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CacheMissEntry newCacheMissEntry1 = addCacheMissEntry();
		CacheMissEntry newCacheMissEntry2 = addCacheMissEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheMissEntry1.getPrimaryKey());
		primaryKeys.add(newCacheMissEntry2.getPrimaryKey());

		Map<Serializable, CacheMissEntry> cacheMissEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cacheMissEntries.size());
		Assert.assertEquals(
			newCacheMissEntry1,
			cacheMissEntries.get(newCacheMissEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCacheMissEntry2,
			cacheMissEntries.get(newCacheMissEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CacheMissEntry> cacheMissEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheMissEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheMissEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CacheMissEntry> cacheMissEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheMissEntries.size());
		Assert.assertEquals(
			newCacheMissEntry,
			cacheMissEntries.get(newCacheMissEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CacheMissEntry> cacheMissEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cacheMissEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCacheMissEntry.getPrimaryKey());

		Map<Serializable, CacheMissEntry> cacheMissEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cacheMissEntries.size());
		Assert.assertEquals(
			newCacheMissEntry,
			cacheMissEntries.get(newCacheMissEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CacheMissEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CacheMissEntry>() {

				@Override
				public void performAction(CacheMissEntry cacheMissEntry) {
					Assert.assertNotNull(cacheMissEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheMissEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheMissEntryId", newCacheMissEntry.getCacheMissEntryId()));

		List<CacheMissEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CacheMissEntry existingCacheMissEntry = result.get(0);

		Assert.assertEquals(existingCacheMissEntry, newCacheMissEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheMissEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cacheMissEntryId", RandomTestUtil.nextLong()));

		List<CacheMissEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CacheMissEntry newCacheMissEntry = addCacheMissEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheMissEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheMissEntryId"));

		Object newCacheMissEntryId = newCacheMissEntry.getCacheMissEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheMissEntryId", new Object[] {newCacheMissEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCacheMissEntryId = result.get(0);

		Assert.assertEquals(existingCacheMissEntryId, newCacheMissEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CacheMissEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cacheMissEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cacheMissEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CacheMissEntry addCacheMissEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CacheMissEntry cacheMissEntry = _persistence.create(pk);

		cacheMissEntry.setCtCollectionId(RandomTestUtil.nextLong());

		_cacheMissEntries.add(_persistence.update(cacheMissEntry));

		return cacheMissEntry;
	}

	private List<CacheMissEntry> _cacheMissEntries =
		new ArrayList<CacheMissEntry>();
	private CacheMissEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:687880770