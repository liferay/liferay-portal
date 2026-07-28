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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;
import com.liferay.portal.tools.service.builder.test.service.DSLQueryEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryUtil;

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
public class DSLQueryEntryPersistenceTest {

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
		_persistence = DSLQueryEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DSLQueryEntry> iterator = _dslQueryEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryEntry dslQueryEntry = _persistence.create(pk);

		Assert.assertNotNull(dslQueryEntry);

		Assert.assertEquals(dslQueryEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		_persistence.remove(newDSLQueryEntry);

		DSLQueryEntry existingDSLQueryEntry = _persistence.fetchByPrimaryKey(
			newDSLQueryEntry.getPrimaryKey());

		Assert.assertNull(existingDSLQueryEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDSLQueryEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		newDSLQueryEntry.setName(RandomTestUtil.randomString());

		newDSLQueryEntry = _persistence.update(newDSLQueryEntry);

		_dslQueryEntries.add(newDSLQueryEntry);

		DSLQueryEntry existingDSLQueryEntry = _persistence.findByPrimaryKey(
			newDSLQueryEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDSLQueryEntry.getDslQueryEntryId(),
			newDSLQueryEntry.getDslQueryEntryId());
		Assert.assertEquals(
			existingDSLQueryEntry.getName(), newDSLQueryEntry.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		DSLQueryEntry existingDSLQueryEntry = _persistence.findByPrimaryKey(
			newDSLQueryEntry.getPrimaryKey());

		Assert.assertEquals(existingDSLQueryEntry, newDSLQueryEntry);
	}

	@Test(expected = NoSuchDSLQueryEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DSLQueryEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DSLQueryEntry", "dslQueryEntryId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		DSLQueryEntry existingDSLQueryEntry = _persistence.fetchByPrimaryKey(
			newDSLQueryEntry.getPrimaryKey());

		Assert.assertEquals(existingDSLQueryEntry, newDSLQueryEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryEntry missingDSLQueryEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDSLQueryEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DSLQueryEntry newDSLQueryEntry1 = addDSLQueryEntry();
		DSLQueryEntry newDSLQueryEntry2 = addDSLQueryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryEntry1.getPrimaryKey());
		primaryKeys.add(newDSLQueryEntry2.getPrimaryKey());

		Map<Serializable, DSLQueryEntry> dslQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dslQueryEntries.size());
		Assert.assertEquals(
			newDSLQueryEntry1,
			dslQueryEntries.get(newDSLQueryEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDSLQueryEntry2,
			dslQueryEntries.get(newDSLQueryEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DSLQueryEntry> dslQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dslQueryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DSLQueryEntry> dslQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dslQueryEntries.size());
		Assert.assertEquals(
			newDSLQueryEntry,
			dslQueryEntries.get(newDSLQueryEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DSLQueryEntry> dslQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dslQueryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryEntry.getPrimaryKey());

		Map<Serializable, DSLQueryEntry> dslQueryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dslQueryEntries.size());
		Assert.assertEquals(
			newDSLQueryEntry,
			dslQueryEntries.get(newDSLQueryEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DSLQueryEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DSLQueryEntry>() {

				@Override
				public void performAction(DSLQueryEntry dslQueryEntry) {
					Assert.assertNotNull(dslQueryEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dslQueryEntryId", newDSLQueryEntry.getDslQueryEntryId()));

		List<DSLQueryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DSLQueryEntry existingDSLQueryEntry = result.get(0);

		Assert.assertEquals(existingDSLQueryEntry, newDSLQueryEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dslQueryEntryId", RandomTestUtil.nextLong()));

		List<DSLQueryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DSLQueryEntry newDSLQueryEntry = addDSLQueryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dslQueryEntryId"));

		Object newDslQueryEntryId = newDSLQueryEntry.getDslQueryEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dslQueryEntryId", new Object[] {newDslQueryEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDslQueryEntryId = result.get(0);

		Assert.assertEquals(existingDslQueryEntryId, newDslQueryEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dslQueryEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dslQueryEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected DSLQueryEntry addDSLQueryEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryEntry dslQueryEntry = _persistence.create(pk);

		dslQueryEntry.setName(RandomTestUtil.randomString());

		_dslQueryEntries.add(_persistence.update(dslQueryEntry));

		return dslQueryEntry;
	}

	private List<DSLQueryEntry> _dslQueryEntries =
		new ArrayList<DSLQueryEntry>();
	private DSLQueryEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1991246646