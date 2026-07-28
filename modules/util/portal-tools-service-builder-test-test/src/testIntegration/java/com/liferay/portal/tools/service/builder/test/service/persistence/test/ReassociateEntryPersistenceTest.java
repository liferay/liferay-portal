/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchReassociateEntryException;
import com.liferay.portal.tools.service.builder.test.model.ReassociateEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ReassociateEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ReassociateEntryUtil;

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
public class ReassociateEntryPersistenceTest {

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
		_persistence = ReassociateEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ReassociateEntry> iterator = _reassociateEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReassociateEntry reassociateEntry = _persistence.create(pk);

		Assert.assertNotNull(reassociateEntry);

		Assert.assertEquals(reassociateEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		_persistence.remove(newReassociateEntry);

		ReassociateEntry existingReassociateEntry =
			_persistence.fetchByPrimaryKey(newReassociateEntry.getPrimaryKey());

		Assert.assertNull(existingReassociateEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addReassociateEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		newReassociateEntry.setName(RandomTestUtil.randomString());

		newReassociateEntry = _persistence.update(newReassociateEntry);

		_reassociateEntries.add(newReassociateEntry);

		ReassociateEntry existingReassociateEntry =
			_persistence.findByPrimaryKey(newReassociateEntry.getPrimaryKey());

		Assert.assertEquals(
			existingReassociateEntry.getReassociateEntryId(),
			newReassociateEntry.getReassociateEntryId());
		Assert.assertEquals(
			existingReassociateEntry.getName(), newReassociateEntry.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		ReassociateEntry existingReassociateEntry =
			_persistence.findByPrimaryKey(newReassociateEntry.getPrimaryKey());

		Assert.assertEquals(existingReassociateEntry, newReassociateEntry);
	}

	@Test(expected = NoSuchReassociateEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ReassociateEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ReassociateEntry", "reassociateEntryId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		ReassociateEntry existingReassociateEntry =
			_persistence.fetchByPrimaryKey(newReassociateEntry.getPrimaryKey());

		Assert.assertEquals(existingReassociateEntry, newReassociateEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReassociateEntry missingReassociateEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingReassociateEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ReassociateEntry newReassociateEntry1 = addReassociateEntry();
		ReassociateEntry newReassociateEntry2 = addReassociateEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReassociateEntry1.getPrimaryKey());
		primaryKeys.add(newReassociateEntry2.getPrimaryKey());

		Map<Serializable, ReassociateEntry> reassociateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, reassociateEntries.size());
		Assert.assertEquals(
			newReassociateEntry1,
			reassociateEntries.get(newReassociateEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newReassociateEntry2,
			reassociateEntries.get(newReassociateEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ReassociateEntry> reassociateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(reassociateEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ReassociateEntry newReassociateEntry = addReassociateEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReassociateEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ReassociateEntry> reassociateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, reassociateEntries.size());
		Assert.assertEquals(
			newReassociateEntry,
			reassociateEntries.get(newReassociateEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ReassociateEntry> reassociateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(reassociateEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReassociateEntry.getPrimaryKey());

		Map<Serializable, ReassociateEntry> reassociateEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, reassociateEntries.size());
		Assert.assertEquals(
			newReassociateEntry,
			reassociateEntries.get(newReassociateEntry.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReassociateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"reassociateEntryId",
				newReassociateEntry.getReassociateEntryId()));

		List<ReassociateEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ReassociateEntry existingReassociateEntry = result.get(0);

		Assert.assertEquals(existingReassociateEntry, newReassociateEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReassociateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"reassociateEntryId", RandomTestUtil.nextLong()));

		List<ReassociateEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ReassociateEntry newReassociateEntry = addReassociateEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReassociateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("reassociateEntryId"));

		Object newReassociateEntryId =
			newReassociateEntry.getReassociateEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"reassociateEntryId", new Object[] {newReassociateEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingReassociateEntryId = result.get(0);

		Assert.assertEquals(existingReassociateEntryId, newReassociateEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReassociateEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("reassociateEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"reassociateEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ReassociateEntry addReassociateEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReassociateEntry reassociateEntry = _persistence.create(pk);

		reassociateEntry.setName(RandomTestUtil.randomString());

		_reassociateEntries.add(_persistence.update(reassociateEntry));

		return reassociateEntry;
	}

	private List<ReassociateEntry> _reassociateEntries =
		new ArrayList<ReassociateEntry>();
	private ReassociateEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1094575834