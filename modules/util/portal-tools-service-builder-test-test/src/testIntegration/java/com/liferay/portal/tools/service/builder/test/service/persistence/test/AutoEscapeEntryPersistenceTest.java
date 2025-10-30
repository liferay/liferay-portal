/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchAutoEscapeEntryException;
import com.liferay.portal.tools.service.builder.test.model.AutoEscapeEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.AutoEscapeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.AutoEscapeEntryUtil;

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
public class AutoEscapeEntryPersistenceTest {

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
		_persistence = AutoEscapeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AutoEscapeEntry> iterator = _autoEscapeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AutoEscapeEntry autoEscapeEntry = _persistence.create(pk);

		Assert.assertNotNull(autoEscapeEntry);

		Assert.assertEquals(autoEscapeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AutoEscapeEntry newAutoEscapeEntry = addAutoEscapeEntry();

		_persistence.remove(newAutoEscapeEntry);

		AutoEscapeEntry existingAutoEscapeEntry =
			_persistence.fetchByPrimaryKey(newAutoEscapeEntry.getPrimaryKey());

		Assert.assertNull(existingAutoEscapeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAutoEscapeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AutoEscapeEntry newAutoEscapeEntry = _persistence.create(pk);

		newAutoEscapeEntry.setAutoEscapeDisabledColumn(
			RandomTestUtil.randomString());

		newAutoEscapeEntry.setAutoEscapeEnabledColumn(
			RandomTestUtil.randomString());

		_autoEscapeEntries.add(_persistence.update(newAutoEscapeEntry));

		AutoEscapeEntry existingAutoEscapeEntry = _persistence.findByPrimaryKey(
			newAutoEscapeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAutoEscapeEntry.getAutoEscapeEntryId(),
			newAutoEscapeEntry.getAutoEscapeEntryId());
		Assert.assertEquals(
			existingAutoEscapeEntry.getAutoEscapeDisabledColumn(),
			newAutoEscapeEntry.getAutoEscapeDisabledColumn());
		Assert.assertEquals(
			existingAutoEscapeEntry.getAutoEscapeEnabledColumn(),
			newAutoEscapeEntry.getAutoEscapeEnabledColumn());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AutoEscapeEntry newAutoEscapeEntry = addAutoEscapeEntry();

		AutoEscapeEntry existingAutoEscapeEntry = _persistence.findByPrimaryKey(
			newAutoEscapeEntry.getPrimaryKey());

		Assert.assertEquals(existingAutoEscapeEntry, newAutoEscapeEntry);
	}

	@Test(expected = NoSuchAutoEscapeEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AutoEscapeEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AutoEscapeEntry", "autoEscapeEntryId", true,
			"autoEscapeDisabledColumn", true, "autoEscapeEnabledColumn", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AutoEscapeEntry newAutoEscapeEntry = addAutoEscapeEntry();

		AutoEscapeEntry existingAutoEscapeEntry =
			_persistence.fetchByPrimaryKey(newAutoEscapeEntry.getPrimaryKey());

		Assert.assertEquals(existingAutoEscapeEntry, newAutoEscapeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AutoEscapeEntry missingAutoEscapeEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingAutoEscapeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AutoEscapeEntry newAutoEscapeEntry1 = addAutoEscapeEntry();
		AutoEscapeEntry newAutoEscapeEntry2 = addAutoEscapeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAutoEscapeEntry1.getPrimaryKey());
		primaryKeys.add(newAutoEscapeEntry2.getPrimaryKey());

		Map<Serializable, AutoEscapeEntry> autoEscapeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, autoEscapeEntries.size());
		Assert.assertEquals(
			newAutoEscapeEntry1,
			autoEscapeEntries.get(newAutoEscapeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newAutoEscapeEntry2,
			autoEscapeEntries.get(newAutoEscapeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AutoEscapeEntry> autoEscapeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(autoEscapeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AutoEscapeEntry newAutoEscapeEntry = addAutoEscapeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAutoEscapeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AutoEscapeEntry> autoEscapeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, autoEscapeEntries.size());
		Assert.assertEquals(
			newAutoEscapeEntry,
			autoEscapeEntries.get(newAutoEscapeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AutoEscapeEntry> autoEscapeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(autoEscapeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AutoEscapeEntry newAutoEscapeEntry = addAutoEscapeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAutoEscapeEntry.getPrimaryKey());

		Map<Serializable, AutoEscapeEntry> autoEscapeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, autoEscapeEntries.size());
		Assert.assertEquals(
			newAutoEscapeEntry,
			autoEscapeEntries.get(newAutoEscapeEntry.getPrimaryKey()));
	}

	protected AutoEscapeEntry addAutoEscapeEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AutoEscapeEntry autoEscapeEntry = _persistence.create(pk);

		autoEscapeEntry.setAutoEscapeDisabledColumn(
			RandomTestUtil.randomString());

		autoEscapeEntry.setAutoEscapeEnabledColumn(
			RandomTestUtil.randomString());

		_autoEscapeEntries.add(_persistence.update(autoEscapeEntry));

		return autoEscapeEntry;
	}

	private List<AutoEscapeEntry> _autoEscapeEntries =
		new ArrayList<AutoEscapeEntry>();
	private AutoEscapeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}