/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNullConvertibleEntryException;
import com.liferay.portal.tools.service.builder.test.model.NullConvertibleEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.NullConvertibleEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.NullConvertibleEntryUtil;

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
public class NullConvertibleEntryPersistenceTest {

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
		_persistence = NullConvertibleEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NullConvertibleEntry> iterator =
			_nullConvertibleEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NullConvertibleEntry nullConvertibleEntry = _persistence.create(pk);

		Assert.assertNotNull(nullConvertibleEntry);

		Assert.assertEquals(nullConvertibleEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		_persistence.remove(newNullConvertibleEntry);

		NullConvertibleEntry existingNullConvertibleEntry =
			_persistence.fetchByPrimaryKey(
				newNullConvertibleEntry.getPrimaryKey());

		Assert.assertNull(existingNullConvertibleEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNullConvertibleEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NullConvertibleEntry newNullConvertibleEntry = _persistence.create(pk);

		newNullConvertibleEntry.setName(RandomTestUtil.randomString());

		_nullConvertibleEntries.add(
			_persistence.update(newNullConvertibleEntry));

		NullConvertibleEntry existingNullConvertibleEntry =
			_persistence.findByPrimaryKey(
				newNullConvertibleEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNullConvertibleEntry.getNullConvertibleEntryId(),
			newNullConvertibleEntry.getNullConvertibleEntryId());
		Assert.assertEquals(
			existingNullConvertibleEntry.getName(),
			newNullConvertibleEntry.getName());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		NullConvertibleEntry existingNullConvertibleEntry =
			_persistence.findByPrimaryKey(
				newNullConvertibleEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNullConvertibleEntry, newNullConvertibleEntry);
	}

	@Test(expected = NoSuchNullConvertibleEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NullConvertibleEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NullConvertibleEntry", "nullConvertibleEntryId", true, "name",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		NullConvertibleEntry existingNullConvertibleEntry =
			_persistence.fetchByPrimaryKey(
				newNullConvertibleEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNullConvertibleEntry, newNullConvertibleEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NullConvertibleEntry missingNullConvertibleEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNullConvertibleEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NullConvertibleEntry newNullConvertibleEntry1 =
			addNullConvertibleEntry();
		NullConvertibleEntry newNullConvertibleEntry2 =
			addNullConvertibleEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNullConvertibleEntry1.getPrimaryKey());
		primaryKeys.add(newNullConvertibleEntry2.getPrimaryKey());

		Map<Serializable, NullConvertibleEntry> nullConvertibleEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, nullConvertibleEntries.size());
		Assert.assertEquals(
			newNullConvertibleEntry1,
			nullConvertibleEntries.get(
				newNullConvertibleEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newNullConvertibleEntry2,
			nullConvertibleEntries.get(
				newNullConvertibleEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NullConvertibleEntry> nullConvertibleEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(nullConvertibleEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNullConvertibleEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NullConvertibleEntry> nullConvertibleEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, nullConvertibleEntries.size());
		Assert.assertEquals(
			newNullConvertibleEntry,
			nullConvertibleEntries.get(
				newNullConvertibleEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NullConvertibleEntry> nullConvertibleEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(nullConvertibleEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNullConvertibleEntry.getPrimaryKey());

		Map<Serializable, NullConvertibleEntry> nullConvertibleEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, nullConvertibleEntries.size());
		Assert.assertEquals(
			newNullConvertibleEntry,
			nullConvertibleEntries.get(
				newNullConvertibleEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		NullConvertibleEntry newNullConvertibleEntry =
			addNullConvertibleEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newNullConvertibleEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		NullConvertibleEntry nullConvertibleEntry) {

		Assert.assertEquals(
			nullConvertibleEntry.getName(),
			ReflectionTestUtil.invoke(
				nullConvertibleEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected NullConvertibleEntry addNullConvertibleEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NullConvertibleEntry nullConvertibleEntry = _persistence.create(pk);

		nullConvertibleEntry.setName(RandomTestUtil.randomString());

		_nullConvertibleEntries.add(_persistence.update(nullConvertibleEntry));

		return nullConvertibleEntry;
	}

	private List<NullConvertibleEntry> _nullConvertibleEntries =
		new ArrayList<NullConvertibleEntry>();
	private NullConvertibleEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}