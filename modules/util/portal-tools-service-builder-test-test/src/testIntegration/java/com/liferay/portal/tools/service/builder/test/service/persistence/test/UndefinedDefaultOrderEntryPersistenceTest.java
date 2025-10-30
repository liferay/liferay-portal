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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUndefinedDefaultOrderEntryException;
import com.liferay.portal.tools.service.builder.test.model.UndefinedDefaultOrderEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.UndefinedDefaultOrderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UndefinedDefaultOrderEntryUtil;

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
public class UndefinedDefaultOrderEntryPersistenceTest {

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
		_persistence = UndefinedDefaultOrderEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UndefinedDefaultOrderEntry> iterator =
			_undefinedDefaultOrderEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry =
			_persistence.create(pk);

		Assert.assertNotNull(undefinedDefaultOrderEntry);

		Assert.assertEquals(undefinedDefaultOrderEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		_persistence.remove(newUndefinedDefaultOrderEntry);

		UndefinedDefaultOrderEntry existingUndefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(
				newUndefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertNull(existingUndefinedDefaultOrderEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUndefinedDefaultOrderEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			_persistence.create(pk);

		newUndefinedDefaultOrderEntry.setModifiedDate(
			RandomTestUtil.nextDate());

		newUndefinedDefaultOrderEntry.setName(RandomTestUtil.randomString());

		_undefinedDefaultOrderEntries.add(
			_persistence.update(newUndefinedDefaultOrderEntry));

		UndefinedDefaultOrderEntry existingUndefinedDefaultOrderEntry =
			_persistence.findByPrimaryKey(
				newUndefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUndefinedDefaultOrderEntry.
				getUndefinedDefaultOrderEntryId(),
			newUndefinedDefaultOrderEntry.getUndefinedDefaultOrderEntryId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingUndefinedDefaultOrderEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newUndefinedDefaultOrderEntry.getModifiedDate()));
		Assert.assertEquals(
			existingUndefinedDefaultOrderEntry.getName(),
			newUndefinedDefaultOrderEntry.getName());
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
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		UndefinedDefaultOrderEntry existingUndefinedDefaultOrderEntry =
			_persistence.findByPrimaryKey(
				newUndefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUndefinedDefaultOrderEntry, newUndefinedDefaultOrderEntry);
	}

	@Test(expected = NoSuchUndefinedDefaultOrderEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UndefinedDefaultOrderEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"UndefinedDefaultOrderEntry", "undefinedDefaultOrderEntryId", true,
			"modifiedDate", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		UndefinedDefaultOrderEntry existingUndefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(
				newUndefinedDefaultOrderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUndefinedDefaultOrderEntry, newUndefinedDefaultOrderEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UndefinedDefaultOrderEntry missingUndefinedDefaultOrderEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUndefinedDefaultOrderEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry1 =
			addUndefinedDefaultOrderEntry();
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry2 =
			addUndefinedDefaultOrderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUndefinedDefaultOrderEntry1.getPrimaryKey());
		primaryKeys.add(newUndefinedDefaultOrderEntry2.getPrimaryKey());

		Map<Serializable, UndefinedDefaultOrderEntry>
			undefinedDefaultOrderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, undefinedDefaultOrderEntries.size());
		Assert.assertEquals(
			newUndefinedDefaultOrderEntry1,
			undefinedDefaultOrderEntries.get(
				newUndefinedDefaultOrderEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newUndefinedDefaultOrderEntry2,
			undefinedDefaultOrderEntries.get(
				newUndefinedDefaultOrderEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UndefinedDefaultOrderEntry>
			undefinedDefaultOrderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(undefinedDefaultOrderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUndefinedDefaultOrderEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UndefinedDefaultOrderEntry>
			undefinedDefaultOrderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, undefinedDefaultOrderEntries.size());
		Assert.assertEquals(
			newUndefinedDefaultOrderEntry,
			undefinedDefaultOrderEntries.get(
				newUndefinedDefaultOrderEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UndefinedDefaultOrderEntry>
			undefinedDefaultOrderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(undefinedDefaultOrderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUndefinedDefaultOrderEntry.getPrimaryKey());

		Map<Serializable, UndefinedDefaultOrderEntry>
			undefinedDefaultOrderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, undefinedDefaultOrderEntries.size());
		Assert.assertEquals(
			newUndefinedDefaultOrderEntry,
			undefinedDefaultOrderEntries.get(
				newUndefinedDefaultOrderEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UndefinedDefaultOrderEntry newUndefinedDefaultOrderEntry =
			addUndefinedDefaultOrderEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newUndefinedDefaultOrderEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry) {

		Assert.assertEquals(
			undefinedDefaultOrderEntry.getName(),
			ReflectionTestUtil.invoke(
				undefinedDefaultOrderEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected UndefinedDefaultOrderEntry addUndefinedDefaultOrderEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry =
			_persistence.create(pk);

		undefinedDefaultOrderEntry.setModifiedDate(RandomTestUtil.nextDate());

		undefinedDefaultOrderEntry.setName(RandomTestUtil.randomString());

		_undefinedDefaultOrderEntries.add(
			_persistence.update(undefinedDefaultOrderEntry));

		return undefinedDefaultOrderEntry;
	}

	private List<UndefinedDefaultOrderEntry> _undefinedDefaultOrderEntries =
		new ArrayList<UndefinedDefaultOrderEntry>();
	private UndefinedDefaultOrderEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}