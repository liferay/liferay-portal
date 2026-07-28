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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUniqueFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.UniqueFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.UniqueFinderEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueFinderEntryUtil;

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
public class UniqueFinderEntryPersistenceTest {

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
		_persistence = UniqueFinderEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UniqueFinderEntry> iterator = _uniqueFinderEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueFinderEntry uniqueFinderEntry = _persistence.create(pk);

		Assert.assertNotNull(uniqueFinderEntry);

		Assert.assertEquals(uniqueFinderEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		_persistence.remove(newUniqueFinderEntry);

		UniqueFinderEntry existingUniqueFinderEntry =
			_persistence.fetchByPrimaryKey(
				newUniqueFinderEntry.getPrimaryKey());

		Assert.assertNull(existingUniqueFinderEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUniqueFinderEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		newUniqueFinderEntry.setModifiedDate(RandomTestUtil.nextDate());

		newUniqueFinderEntry.setName(RandomTestUtil.randomString());

		newUniqueFinderEntry = _persistence.update(newUniqueFinderEntry);

		_uniqueFinderEntries.add(newUniqueFinderEntry);

		UniqueFinderEntry existingUniqueFinderEntry =
			_persistence.findByPrimaryKey(newUniqueFinderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUniqueFinderEntry.getUniqueFinderEntryId(),
			newUniqueFinderEntry.getUniqueFinderEntryId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUniqueFinderEntry.getModifiedDate()),
			Time.getShortTimestamp(newUniqueFinderEntry.getModifiedDate()));
		Assert.assertEquals(
			existingUniqueFinderEntry.getName(),
			newUniqueFinderEntry.getName());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		UniqueFinderEntry existingUniqueFinderEntry =
			_persistence.findByPrimaryKey(newUniqueFinderEntry.getPrimaryKey());

		Assert.assertEquals(existingUniqueFinderEntry, newUniqueFinderEntry);
	}

	@Test(expected = NoSuchUniqueFinderEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UniqueFinderEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UniqueFinderEntry", "uniqueFinderEntryId", true, "modifiedDate",
			true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		UniqueFinderEntry existingUniqueFinderEntry =
			_persistence.fetchByPrimaryKey(
				newUniqueFinderEntry.getPrimaryKey());

		Assert.assertEquals(existingUniqueFinderEntry, newUniqueFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueFinderEntry missingUniqueFinderEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUniqueFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UniqueFinderEntry newUniqueFinderEntry1 = addUniqueFinderEntry();
		UniqueFinderEntry newUniqueFinderEntry2 = addUniqueFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueFinderEntry1.getPrimaryKey());
		primaryKeys.add(newUniqueFinderEntry2.getPrimaryKey());

		Map<Serializable, UniqueFinderEntry> uniqueFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, uniqueFinderEntries.size());
		Assert.assertEquals(
			newUniqueFinderEntry1,
			uniqueFinderEntries.get(newUniqueFinderEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newUniqueFinderEntry2,
			uniqueFinderEntries.get(newUniqueFinderEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UniqueFinderEntry> uniqueFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(uniqueFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueFinderEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UniqueFinderEntry> uniqueFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, uniqueFinderEntries.size());
		Assert.assertEquals(
			newUniqueFinderEntry,
			uniqueFinderEntries.get(newUniqueFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UniqueFinderEntry> uniqueFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(uniqueFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueFinderEntry.getPrimaryKey());

		Map<Serializable, UniqueFinderEntry> uniqueFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, uniqueFinderEntries.size());
		Assert.assertEquals(
			newUniqueFinderEntry,
			uniqueFinderEntries.get(newUniqueFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			UniqueFinderEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<UniqueFinderEntry>() {

				@Override
				public void performAction(UniqueFinderEntry uniqueFinderEntry) {
					Assert.assertNotNull(uniqueFinderEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueFinderEntryId",
				newUniqueFinderEntry.getUniqueFinderEntryId()));

		List<UniqueFinderEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		UniqueFinderEntry existingUniqueFinderEntry = result.get(0);

		Assert.assertEquals(existingUniqueFinderEntry, newUniqueFinderEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueFinderEntryId", RandomTestUtil.nextLong()));

		List<UniqueFinderEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("uniqueFinderEntryId"));

		Object newUniqueFinderEntryId =
			newUniqueFinderEntry.getUniqueFinderEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"uniqueFinderEntryId", new Object[] {newUniqueFinderEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingUniqueFinderEntryId = result.get(0);

		Assert.assertEquals(
			existingUniqueFinderEntryId, newUniqueFinderEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("uniqueFinderEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"uniqueFinderEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newUniqueFinderEntry.getPrimaryKey()));
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

		UniqueFinderEntry newUniqueFinderEntry = addUniqueFinderEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueFinderEntryId",
				newUniqueFinderEntry.getUniqueFinderEntryId()));

		List<UniqueFinderEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(UniqueFinderEntry uniqueFinderEntry) {
		Assert.assertEquals(
			uniqueFinderEntry.getName(),
			ReflectionTestUtil.invoke(
				uniqueFinderEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected UniqueFinderEntry addUniqueFinderEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueFinderEntry uniqueFinderEntry = _persistence.create(pk);

		uniqueFinderEntry.setModifiedDate(RandomTestUtil.nextDate());

		uniqueFinderEntry.setName(RandomTestUtil.randomString());

		_uniqueFinderEntries.add(_persistence.update(uniqueFinderEntry));

		return uniqueFinderEntry;
	}

	private List<UniqueFinderEntry> _uniqueFinderEntries =
		new ArrayList<UniqueFinderEntry>();
	private UniqueFinderEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1353080357