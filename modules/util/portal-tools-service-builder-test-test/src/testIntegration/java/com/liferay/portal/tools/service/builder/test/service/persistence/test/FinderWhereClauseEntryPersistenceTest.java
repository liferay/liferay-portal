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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;
import com.liferay.portal.tools.service.builder.test.service.FinderWhereClauseEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderWhereClauseEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderWhereClauseEntryUtil;

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
public class FinderWhereClauseEntryPersistenceTest {

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
		_persistence = FinderWhereClauseEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FinderWhereClauseEntry> iterator =
			_finderWhereClauseEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FinderWhereClauseEntry finderWhereClauseEntry = _persistence.create(pk);

		Assert.assertNotNull(finderWhereClauseEntry);

		Assert.assertEquals(finderWhereClauseEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		_persistence.remove(newFinderWhereClauseEntry);

		FinderWhereClauseEntry existingFinderWhereClauseEntry =
			_persistence.fetchByPrimaryKey(
				newFinderWhereClauseEntry.getPrimaryKey());

		Assert.assertNull(existingFinderWhereClauseEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFinderWhereClauseEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		newFinderWhereClauseEntry.setHeadId(RandomTestUtil.nextLong());

		newFinderWhereClauseEntry.setName(RandomTestUtil.randomString());

		newFinderWhereClauseEntry.setNickname(RandomTestUtil.randomString());

		newFinderWhereClauseEntry.setStatus(RandomTestUtil.nextInt());

		newFinderWhereClauseEntry = _persistence.update(
			newFinderWhereClauseEntry);

		_finderWhereClauseEntries.add(newFinderWhereClauseEntry);

		FinderWhereClauseEntry existingFinderWhereClauseEntry =
			_persistence.findByPrimaryKey(
				newFinderWhereClauseEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderWhereClauseEntry.getFinderWhereClauseEntryId(),
			newFinderWhereClauseEntry.getFinderWhereClauseEntryId());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getHeadId(),
			newFinderWhereClauseEntry.getHeadId());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getName(),
			newFinderWhereClauseEntry.getName());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getNickname(),
			newFinderWhereClauseEntry.getNickname());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getStatus(),
			newFinderWhereClauseEntry.getStatus());
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testCountByName_Nickname() throws Exception {
		_persistence.countByName_Nickname("");

		_persistence.countByName_Nickname("null");

		_persistence.countByName_Nickname((String)null);
	}

	@Test
	public void testCountByStatus() throws Exception {
		_persistence.countByStatus(RandomTestUtil.nextInt());

		_persistence.countByStatus(0);
	}

	@Test
	public void testCountByName_Status() throws Exception {
		_persistence.countByName_Status("", RandomTestUtil.nextInt());

		_persistence.countByName_Status("null", 0);

		_persistence.countByName_Status((String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		FinderWhereClauseEntry existingFinderWhereClauseEntry =
			_persistence.findByPrimaryKey(
				newFinderWhereClauseEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderWhereClauseEntry, newFinderWhereClauseEntry);
	}

	@Test(expected = NoSuchFinderWhereClauseEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FinderWhereClauseEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FinderWhereClauseEntry", "finderWhereClauseEntryId", true,
			"headId", true, "name", true, "nickname", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		FinderWhereClauseEntry existingFinderWhereClauseEntry =
			_persistence.fetchByPrimaryKey(
				newFinderWhereClauseEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderWhereClauseEntry, newFinderWhereClauseEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FinderWhereClauseEntry missingFinderWhereClauseEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFinderWhereClauseEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FinderWhereClauseEntry newFinderWhereClauseEntry1 =
			addFinderWhereClauseEntry();
		FinderWhereClauseEntry newFinderWhereClauseEntry2 =
			addFinderWhereClauseEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderWhereClauseEntry1.getPrimaryKey());
		primaryKeys.add(newFinderWhereClauseEntry2.getPrimaryKey());

		Map<Serializable, FinderWhereClauseEntry> finderWhereClauseEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, finderWhereClauseEntries.size());
		Assert.assertEquals(
			newFinderWhereClauseEntry1,
			finderWhereClauseEntries.get(
				newFinderWhereClauseEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFinderWhereClauseEntry2,
			finderWhereClauseEntries.get(
				newFinderWhereClauseEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FinderWhereClauseEntry> finderWhereClauseEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(finderWhereClauseEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderWhereClauseEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FinderWhereClauseEntry> finderWhereClauseEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, finderWhereClauseEntries.size());
		Assert.assertEquals(
			newFinderWhereClauseEntry,
			finderWhereClauseEntries.get(
				newFinderWhereClauseEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FinderWhereClauseEntry> finderWhereClauseEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(finderWhereClauseEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderWhereClauseEntry.getPrimaryKey());

		Map<Serializable, FinderWhereClauseEntry> finderWhereClauseEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, finderWhereClauseEntries.size());
		Assert.assertEquals(
			newFinderWhereClauseEntry,
			finderWhereClauseEntries.get(
				newFinderWhereClauseEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			FinderWhereClauseEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<FinderWhereClauseEntry>() {

				@Override
				public void performAction(
					FinderWhereClauseEntry finderWhereClauseEntry) {

					Assert.assertNotNull(finderWhereClauseEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderWhereClauseEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"finderWhereClauseEntryId",
				newFinderWhereClauseEntry.getFinderWhereClauseEntryId()));

		List<FinderWhereClauseEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		FinderWhereClauseEntry existingFinderWhereClauseEntry = result.get(0);

		Assert.assertEquals(
			existingFinderWhereClauseEntry, newFinderWhereClauseEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderWhereClauseEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"finderWhereClauseEntryId", RandomTestUtil.nextLong()));

		List<FinderWhereClauseEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		FinderWhereClauseEntry newFinderWhereClauseEntry =
			addFinderWhereClauseEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderWhereClauseEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("finderWhereClauseEntryId"));

		Object newFinderWhereClauseEntryId =
			newFinderWhereClauseEntry.getFinderWhereClauseEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"finderWhereClauseEntryId",
				new Object[] {newFinderWhereClauseEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingFinderWhereClauseEntryId = result.get(0);

		Assert.assertEquals(
			existingFinderWhereClauseEntryId, newFinderWhereClauseEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderWhereClauseEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("finderWhereClauseEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"finderWhereClauseEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected FinderWhereClauseEntry addFinderWhereClauseEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		FinderWhereClauseEntry finderWhereClauseEntry = _persistence.create(pk);

		finderWhereClauseEntry.setHeadId(RandomTestUtil.nextLong());

		finderWhereClauseEntry.setName(RandomTestUtil.randomString());

		finderWhereClauseEntry.setNickname(RandomTestUtil.randomString());

		finderWhereClauseEntry.setStatus(RandomTestUtil.nextInt());

		_finderWhereClauseEntries.add(
			_persistence.update(finderWhereClauseEntry));

		return finderWhereClauseEntry;
	}

	private List<FinderWhereClauseEntry> _finderWhereClauseEntries =
		new ArrayList<FinderWhereClauseEntry>();
	private FinderWhereClauseEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1380175781