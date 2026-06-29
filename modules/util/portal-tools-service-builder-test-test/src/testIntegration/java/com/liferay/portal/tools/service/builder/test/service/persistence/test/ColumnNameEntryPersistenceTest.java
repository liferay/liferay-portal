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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryUtil;

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
public class ColumnNameEntryPersistenceTest {

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
		_persistence = ColumnNameEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ColumnNameEntry> iterator = _columnNameEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ColumnNameEntry columnNameEntry = _persistence.create(pk);

		Assert.assertNotNull(columnNameEntry);

		Assert.assertEquals(columnNameEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		_persistence.remove(newColumnNameEntry);

		ColumnNameEntry existingColumnNameEntry =
			_persistence.fetchByPrimaryKey(newColumnNameEntry.getPrimaryKey());

		Assert.assertNull(existingColumnNameEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addColumnNameEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		newColumnNameEntry.setName(RandomTestUtil.randomString());

		newColumnNameEntry = _persistence.update(newColumnNameEntry);

		_columnNameEntries.add(newColumnNameEntry);

		ColumnNameEntry existingColumnNameEntry = _persistence.findByPrimaryKey(
			newColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(
			existingColumnNameEntry.getColumnNameEntryId(),
			newColumnNameEntry.getColumnNameEntryId());
		Assert.assertEquals(
			existingColumnNameEntry.getName(), newColumnNameEntry.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		ColumnNameEntry existingColumnNameEntry = _persistence.findByPrimaryKey(
			newColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(existingColumnNameEntry, newColumnNameEntry);
	}

	@Test(expected = NoSuchColumnNameEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ColumnNameEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ColumnNameEntry", "columnNameEntryId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		ColumnNameEntry existingColumnNameEntry =
			_persistence.fetchByPrimaryKey(newColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(existingColumnNameEntry, newColumnNameEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ColumnNameEntry missingColumnNameEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingColumnNameEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ColumnNameEntry newColumnNameEntry1 = addColumnNameEntry();
		ColumnNameEntry newColumnNameEntry2 = addColumnNameEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newColumnNameEntry1.getPrimaryKey());
		primaryKeys.add(newColumnNameEntry2.getPrimaryKey());

		Map<Serializable, ColumnNameEntry> columnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, columnNameEntries.size());
		Assert.assertEquals(
			newColumnNameEntry1,
			columnNameEntries.get(newColumnNameEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newColumnNameEntry2,
			columnNameEntries.get(newColumnNameEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ColumnNameEntry> columnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(columnNameEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newColumnNameEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ColumnNameEntry> columnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, columnNameEntries.size());
		Assert.assertEquals(
			newColumnNameEntry,
			columnNameEntries.get(newColumnNameEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ColumnNameEntry> columnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(columnNameEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newColumnNameEntry.getPrimaryKey());

		Map<Serializable, ColumnNameEntry> columnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, columnNameEntries.size());
		Assert.assertEquals(
			newColumnNameEntry,
			columnNameEntries.get(newColumnNameEntry.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"columnNameEntryId",
				newColumnNameEntry.getColumnNameEntryId()));

		List<ColumnNameEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ColumnNameEntry existingColumnNameEntry = result.get(0);

		Assert.assertEquals(existingColumnNameEntry, newColumnNameEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"columnNameEntryId", RandomTestUtil.nextLong()));

		List<ColumnNameEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ColumnNameEntry newColumnNameEntry = addColumnNameEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("columnNameEntryId"));

		Object newColumnNameEntryId = newColumnNameEntry.getColumnNameEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"columnNameEntryId", new Object[] {newColumnNameEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingColumnNameEntryId = result.get(0);

		Assert.assertEquals(existingColumnNameEntryId, newColumnNameEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("columnNameEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"columnNameEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ColumnNameEntry addColumnNameEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ColumnNameEntry columnNameEntry = _persistence.create(pk);

		columnNameEntry.setName(RandomTestUtil.randomString());

		_columnNameEntries.add(_persistence.update(columnNameEntry));

		return columnNameEntry;
	}

	private List<ColumnNameEntry> _columnNameEntries =
		new ArrayList<ColumnNameEntry>();
	private ColumnNameEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:12840868