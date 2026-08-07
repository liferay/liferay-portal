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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.service.DateEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryUtil;

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
public class DateEntryPersistenceTest {

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
		_persistence = DateEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DateEntry> iterator = _dateEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateEntry dateEntry = _persistence.create(pk);

		Assert.assertNotNull(dateEntry);

		Assert.assertEquals(dateEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		_persistence.remove(newDateEntry);

		DateEntry existingDateEntry = _persistence.fetchByPrimaryKey(
			newDateEntry.getPrimaryKey());

		Assert.assertNull(existingDateEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDateEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		newDateEntry.setCompanyId(RandomTestUtil.nextLong());

		newDateEntry.setSnapshotDate(RandomTestUtil.nextDate());

		newDateEntry = _persistence.update(newDateEntry);

		_dateEntries.add(newDateEntry);

		DateEntry existingDateEntry = _persistence.findByPrimaryKey(
			newDateEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDateEntry.getDateEntryId(), newDateEntry.getDateEntryId());
		Assert.assertEquals(
			existingDateEntry.getCompanyId(), newDateEntry.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDateEntry.getSnapshotDate()),
			Time.getShortTimestamp(newDateEntry.getSnapshotDate()));
	}

	@Test
	public void testCountBySnapshotDate() throws Exception {
		_persistence.countBySnapshotDate(RandomTestUtil.nextDate());

		_persistence.countBySnapshotDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

		_persistence.countByC_S(0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		DateEntry existingDateEntry = _persistence.findByPrimaryKey(
			newDateEntry.getPrimaryKey());

		Assert.assertEquals(existingDateEntry, newDateEntry);
	}

	@Test(expected = NoSuchDateEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DateEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DateEntry", "dateEntryId", true, "companyId", true, "snapshotDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		DateEntry existingDateEntry = _persistence.fetchByPrimaryKey(
			newDateEntry.getPrimaryKey());

		Assert.assertEquals(existingDateEntry, newDateEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateEntry missingDateEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDateEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DateEntry newDateEntry1 = addDateEntry();
		DateEntry newDateEntry2 = addDateEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateEntry1.getPrimaryKey());
		primaryKeys.add(newDateEntry2.getPrimaryKey());

		Map<Serializable, DateEntry> dateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dateEntries.size());
		Assert.assertEquals(
			newDateEntry1, dateEntries.get(newDateEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDateEntry2, dateEntries.get(newDateEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DateEntry> dateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dateEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DateEntry newDateEntry = addDateEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DateEntry> dateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dateEntries.size());
		Assert.assertEquals(
			newDateEntry, dateEntries.get(newDateEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DateEntry> dateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dateEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateEntry.getPrimaryKey());

		Map<Serializable, DateEntry> dateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dateEntries.size());
		Assert.assertEquals(
			newDateEntry, dateEntries.get(newDateEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DateEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DateEntry>() {

				@Override
				public void performAction(DateEntry dateEntry) {
					Assert.assertNotNull(dateEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dateEntryId", newDateEntry.getDateEntryId()));

		List<DateEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DateEntry existingDateEntry = result.get(0);

		Assert.assertEquals(existingDateEntry, newDateEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dateEntryId", RandomTestUtil.nextLong()));

		List<DateEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dateEntryId"));

		Object newDateEntryId = newDateEntry.getDateEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dateEntryId", new Object[] {newDateEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDateEntryId = result.get(0);

		Assert.assertEquals(existingDateEntryId, newDateEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dateEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dateEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DateEntry newDateEntry = addDateEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDateEntry.getPrimaryKey()));
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

		DateEntry newDateEntry = addDateEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dateEntryId", newDateEntry.getDateEntryId()));

		List<DateEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DateEntry dateEntry) {
		Assert.assertEquals(
			Long.valueOf(dateEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				dateEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			dateEntry.getSnapshotDate(),
			ReflectionTestUtil.invoke(
				dateEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "snapshotDate"));
	}

	protected DateEntry addDateEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateEntry dateEntry = _persistence.create(pk);

		dateEntry.setCompanyId(RandomTestUtil.nextLong());

		dateEntry.setSnapshotDate(RandomTestUtil.nextDate());

		_dateEntries.add(_persistence.update(dateEntry));

		return dateEntry;
	}

	private List<DateEntry> _dateEntries = new ArrayList<DateEntry>();
	private DateEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:507561005