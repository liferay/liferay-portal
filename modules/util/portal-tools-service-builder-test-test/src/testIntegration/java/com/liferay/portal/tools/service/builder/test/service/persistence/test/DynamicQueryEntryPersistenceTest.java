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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDynamicQueryEntryException;
import com.liferay.portal.tools.service.builder.test.model.DynamicQueryEntry;
import com.liferay.portal.tools.service.builder.test.service.DynamicQueryEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.DynamicQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DynamicQueryEntryUtil;

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
public class DynamicQueryEntryPersistenceTest {

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
		_persistence = DynamicQueryEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DynamicQueryEntry> iterator = _dynamicQueryEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DynamicQueryEntry dynamicQueryEntry = _persistence.create(pk);

		Assert.assertNotNull(dynamicQueryEntry);

		Assert.assertEquals(dynamicQueryEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		_persistence.remove(newDynamicQueryEntry);

		DynamicQueryEntry existingDynamicQueryEntry =
			_persistence.fetchByPrimaryKey(
				newDynamicQueryEntry.getPrimaryKey());

		Assert.assertNull(existingDynamicQueryEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDynamicQueryEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		newDynamicQueryEntry.setCreateDate(RandomTestUtil.nextDate());

		newDynamicQueryEntry.setModifiedDate(RandomTestUtil.nextDate());

		newDynamicQueryEntry.setAmount(RandomTestUtil.nextLong());

		newDynamicQueryEntry.setDescription(RandomTestUtil.randomString());

		newDynamicQueryEntry.setName(RandomTestUtil.randomString());

		newDynamicQueryEntry.setStatus(RandomTestUtil.nextInt());

		newDynamicQueryEntry = _persistence.update(newDynamicQueryEntry);

		_dynamicQueryEntries.add(newDynamicQueryEntry);

		DynamicQueryEntry existingDynamicQueryEntry =
			_persistence.findByPrimaryKey(newDynamicQueryEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDynamicQueryEntry.getDynamicQueryEntryId(),
			newDynamicQueryEntry.getDynamicQueryEntryId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDynamicQueryEntry.getCreateDate()),
			Time.getShortTimestamp(newDynamicQueryEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDynamicQueryEntry.getModifiedDate()),
			Time.getShortTimestamp(newDynamicQueryEntry.getModifiedDate()));
		Assert.assertEquals(
			existingDynamicQueryEntry.getAmount(),
			newDynamicQueryEntry.getAmount());
		Assert.assertEquals(
			existingDynamicQueryEntry.getDescription(),
			newDynamicQueryEntry.getDescription());
		Assert.assertEquals(
			existingDynamicQueryEntry.getName(),
			newDynamicQueryEntry.getName());
		Assert.assertEquals(
			existingDynamicQueryEntry.getStatus(),
			newDynamicQueryEntry.getStatus());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		DynamicQueryEntry existingDynamicQueryEntry =
			_persistence.findByPrimaryKey(newDynamicQueryEntry.getPrimaryKey());

		Assert.assertEquals(existingDynamicQueryEntry, newDynamicQueryEntry);
	}

	@Test(expected = NoSuchDynamicQueryEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DynamicQueryEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DynamicQueryEntry", "dynamicQueryEntryId", true, "createDate",
			true, "modifiedDate", true, "amount", true, "description", true,
			"name", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		DynamicQueryEntry existingDynamicQueryEntry =
			_persistence.fetchByPrimaryKey(
				newDynamicQueryEntry.getPrimaryKey());

		Assert.assertEquals(existingDynamicQueryEntry, newDynamicQueryEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DynamicQueryEntry missingDynamicQueryEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDynamicQueryEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DynamicQueryEntry newDynamicQueryEntry1 = addDynamicQueryEntry();
		DynamicQueryEntry newDynamicQueryEntry2 = addDynamicQueryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDynamicQueryEntry1.getPrimaryKey());
		primaryKeys.add(newDynamicQueryEntry2.getPrimaryKey());

		Map<Serializable, DynamicQueryEntry> dynamicQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dynamicQueryEntries.size());
		Assert.assertEquals(
			newDynamicQueryEntry1,
			dynamicQueryEntries.get(newDynamicQueryEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDynamicQueryEntry2,
			dynamicQueryEntries.get(newDynamicQueryEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DynamicQueryEntry> dynamicQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dynamicQueryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDynamicQueryEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DynamicQueryEntry> dynamicQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dynamicQueryEntries.size());
		Assert.assertEquals(
			newDynamicQueryEntry,
			dynamicQueryEntries.get(newDynamicQueryEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DynamicQueryEntry> dynamicQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dynamicQueryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDynamicQueryEntry.getPrimaryKey());

		Map<Serializable, DynamicQueryEntry> dynamicQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dynamicQueryEntries.size());
		Assert.assertEquals(
			newDynamicQueryEntry,
			dynamicQueryEntries.get(newDynamicQueryEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DynamicQueryEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<DynamicQueryEntry>() {

				@Override
				public void performAction(DynamicQueryEntry dynamicQueryEntry) {
					Assert.assertNotNull(dynamicQueryEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dynamicQueryEntryId",
				newDynamicQueryEntry.getDynamicQueryEntryId()));

		List<DynamicQueryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DynamicQueryEntry existingDynamicQueryEntry = result.get(0);

		Assert.assertEquals(existingDynamicQueryEntry, newDynamicQueryEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dynamicQueryEntryId", RandomTestUtil.nextLong()));

		List<DynamicQueryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DynamicQueryEntry newDynamicQueryEntry = addDynamicQueryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dynamicQueryEntryId"));

		Object newDynamicQueryEntryId =
			newDynamicQueryEntry.getDynamicQueryEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dynamicQueryEntryId", new Object[] {newDynamicQueryEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDynamicQueryEntryId = result.get(0);

		Assert.assertEquals(
			existingDynamicQueryEntryId, newDynamicQueryEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dynamicQueryEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dynamicQueryEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected DynamicQueryEntry addDynamicQueryEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DynamicQueryEntry dynamicQueryEntry = _persistence.create(pk);

		dynamicQueryEntry.setCreateDate(RandomTestUtil.nextDate());

		dynamicQueryEntry.setModifiedDate(RandomTestUtil.nextDate());

		dynamicQueryEntry.setAmount(RandomTestUtil.nextLong());

		dynamicQueryEntry.setDescription(RandomTestUtil.randomString());

		dynamicQueryEntry.setName(RandomTestUtil.randomString());

		dynamicQueryEntry.setStatus(RandomTestUtil.nextInt());

		_dynamicQueryEntries.add(_persistence.update(dynamicQueryEntry));

		return dynamicQueryEntry;
	}

	private List<DynamicQueryEntry> _dynamicQueryEntries =
		new ArrayList<DynamicQueryEntry>();
	private DynamicQueryEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:2084055262