/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDefinedDefaultOrderEntryException;
import com.liferay.portal.tools.service.builder.test.model.DefinedDefaultOrderEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.DefinedDefaultOrderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DefinedDefaultOrderEntryUtil;

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
public class DefinedDefaultOrderEntryPersistenceTest {

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
		_persistence = DefinedDefaultOrderEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DefinedDefaultOrderEntry> iterator =
			_definedDefaultOrderEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DefinedDefaultOrderEntry definedDefaultOrderEntry = _persistence.create(
			pk);

		Assert.assertNotNull(definedDefaultOrderEntry);

		Assert.assertEquals(definedDefaultOrderEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		_persistence.remove(newDefinedDefaultOrderEntry);

		DefinedDefaultOrderEntry existingDefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(
				newDefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertNull(existingDefinedDefaultOrderEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDefinedDefaultOrderEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			_persistence.create(pk);

		newDefinedDefaultOrderEntry.setModifiedDate(RandomTestUtil.nextDate());

		newDefinedDefaultOrderEntry.setName(RandomTestUtil.randomString());

		_definedDefaultOrderEntries.add(
			_persistence.update(newDefinedDefaultOrderEntry));

		DefinedDefaultOrderEntry existingDefinedDefaultOrderEntry =
			_persistence.findByPrimaryKey(
				newDefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDefinedDefaultOrderEntry.getDefinedDefaultOrderEntryId(),
			newDefinedDefaultOrderEntry.getDefinedDefaultOrderEntryId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDefinedDefaultOrderEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newDefinedDefaultOrderEntry.getModifiedDate()));
		Assert.assertEquals(
			existingDefinedDefaultOrderEntry.getName(),
			newDefinedDefaultOrderEntry.getName());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testCountByName_Collection() throws Exception {
		_persistence.countByName_Collection("");

		_persistence.countByName_Collection("null");

		_persistence.countByName_Collection((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		DefinedDefaultOrderEntry existingDefinedDefaultOrderEntry =
			_persistence.findByPrimaryKey(
				newDefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDefinedDefaultOrderEntry, newDefinedDefaultOrderEntry);
	}

	@Test(expected = NoSuchDefinedDefaultOrderEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DefinedDefaultOrderEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"DefinedDefaultOrderEntry", "definedDefaultOrderEntryId", true,
			"modifiedDate", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		DefinedDefaultOrderEntry existingDefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(
				newDefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDefinedDefaultOrderEntry, newDefinedDefaultOrderEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DefinedDefaultOrderEntry missingDefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDefinedDefaultOrderEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry1 =
			addDefinedDefaultOrderEntry();
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry2 =
			addDefinedDefaultOrderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinedDefaultOrderEntry1.getPrimaryKey());
		primaryKeys.add(newDefinedDefaultOrderEntry2.getPrimaryKey());

		Map<Serializable, DefinedDefaultOrderEntry> definedDefaultOrderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, definedDefaultOrderEntries.size());
		Assert.assertEquals(
			newDefinedDefaultOrderEntry1,
			definedDefaultOrderEntries.get(
				newDefinedDefaultOrderEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDefinedDefaultOrderEntry2,
			definedDefaultOrderEntries.get(
				newDefinedDefaultOrderEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DefinedDefaultOrderEntry> definedDefaultOrderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(definedDefaultOrderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinedDefaultOrderEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DefinedDefaultOrderEntry> definedDefaultOrderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, definedDefaultOrderEntries.size());
		Assert.assertEquals(
			newDefinedDefaultOrderEntry,
			definedDefaultOrderEntries.get(
				newDefinedDefaultOrderEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DefinedDefaultOrderEntry> definedDefaultOrderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(definedDefaultOrderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinedDefaultOrderEntry.getPrimaryKey());

		Map<Serializable, DefinedDefaultOrderEntry> definedDefaultOrderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, definedDefaultOrderEntries.size());
		Assert.assertEquals(
			newDefinedDefaultOrderEntry,
			definedDefaultOrderEntries.get(
				newDefinedDefaultOrderEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DefinedDefaultOrderEntry newDefinedDefaultOrderEntry =
			addDefinedDefaultOrderEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDefinedDefaultOrderEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DefinedDefaultOrderEntry definedDefaultOrderEntry) {

		Assert.assertEquals(
			definedDefaultOrderEntry.getName(),
			ReflectionTestUtil.invoke(
				definedDefaultOrderEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected DefinedDefaultOrderEntry addDefinedDefaultOrderEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DefinedDefaultOrderEntry definedDefaultOrderEntry = _persistence.create(
			pk);

		definedDefaultOrderEntry.setModifiedDate(RandomTestUtil.nextDate());

		definedDefaultOrderEntry.setName(RandomTestUtil.randomString());

		_definedDefaultOrderEntries.add(
			_persistence.update(definedDefaultOrderEntry));

		return definedDefaultOrderEntry;
	}

	private List<DefinedDefaultOrderEntry> _definedDefaultOrderEntries =
		new ArrayList<DefinedDefaultOrderEntry>();
	private DefinedDefaultOrderEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}