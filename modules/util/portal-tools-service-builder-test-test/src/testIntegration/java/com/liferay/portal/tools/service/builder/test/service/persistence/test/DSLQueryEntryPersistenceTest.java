/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;
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
		long pk = RandomTestUtil.nextLong();

		DSLQueryEntry newDSLQueryEntry = _persistence.create(pk);

		newDSLQueryEntry.setName(RandomTestUtil.randomString());

		_dslQueryEntries.add(_persistence.update(newDSLQueryEntry));

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