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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;
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
		long pk = RandomTestUtil.nextLong();

		FinderWhereClauseEntry newFinderWhereClauseEntry = _persistence.create(
			pk);

		newFinderWhereClauseEntry.setName(RandomTestUtil.randomString());

		newFinderWhereClauseEntry.setNickname(RandomTestUtil.randomString());

		_finderWhereClauseEntries.add(
			_persistence.update(newFinderWhereClauseEntry));

		FinderWhereClauseEntry existingFinderWhereClauseEntry =
			_persistence.findByPrimaryKey(
				newFinderWhereClauseEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderWhereClauseEntry.getFinderWhereClauseEntryId(),
			newFinderWhereClauseEntry.getFinderWhereClauseEntryId());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getName(),
			newFinderWhereClauseEntry.getName());
		Assert.assertEquals(
			existingFinderWhereClauseEntry.getNickname(),
			newFinderWhereClauseEntry.getNickname());
	}

	@Test
	public void testCountByName_Nickname() throws Exception {
		_persistence.countByName_Nickname("");

		_persistence.countByName_Nickname("null");

		_persistence.countByName_Nickname((String)null);
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
			"FinderWhereClauseEntry", "finderWhereClauseEntryId", true, "name",
			true, "nickname", true);
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

	protected FinderWhereClauseEntry addFinderWhereClauseEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		FinderWhereClauseEntry finderWhereClauseEntry = _persistence.create(pk);

		finderWhereClauseEntry.setName(RandomTestUtil.randomString());

		finderWhereClauseEntry.setNickname(RandomTestUtil.randomString());

		_finderWhereClauseEntries.add(
			_persistence.update(finderWhereClauseEntry));

		return finderWhereClauseEntry;
	}

	private List<FinderWhereClauseEntry> _finderWhereClauseEntries =
		new ArrayList<FinderWhereClauseEntry>();
	private FinderWhereClauseEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}