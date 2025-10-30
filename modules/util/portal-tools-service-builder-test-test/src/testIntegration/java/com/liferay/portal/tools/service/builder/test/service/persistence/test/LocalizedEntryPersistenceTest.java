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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLocalizedEntryException;
import com.liferay.portal.tools.service.builder.test.model.LocalizedEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.LocalizedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LocalizedEntryUtil;

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
public class LocalizedEntryPersistenceTest {

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
		_persistence = LocalizedEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LocalizedEntry> iterator = _localizedEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntry localizedEntry = _persistence.create(pk);

		Assert.assertNotNull(localizedEntry);

		Assert.assertEquals(localizedEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LocalizedEntry newLocalizedEntry = addLocalizedEntry();

		_persistence.remove(newLocalizedEntry);

		LocalizedEntry existingLocalizedEntry = _persistence.fetchByPrimaryKey(
			newLocalizedEntry.getPrimaryKey());

		Assert.assertNull(existingLocalizedEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLocalizedEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntry newLocalizedEntry = _persistence.create(pk);

		newLocalizedEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		_localizedEntries.add(_persistence.update(newLocalizedEntry));

		LocalizedEntry existingLocalizedEntry = _persistence.findByPrimaryKey(
			newLocalizedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLocalizedEntry.getDefaultLanguageId(),
			newLocalizedEntry.getDefaultLanguageId());
		Assert.assertEquals(
			existingLocalizedEntry.getLocalizedEntryId(),
			newLocalizedEntry.getLocalizedEntryId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LocalizedEntry newLocalizedEntry = addLocalizedEntry();

		LocalizedEntry existingLocalizedEntry = _persistence.findByPrimaryKey(
			newLocalizedEntry.getPrimaryKey());

		Assert.assertEquals(existingLocalizedEntry, newLocalizedEntry);
	}

	@Test(expected = NoSuchLocalizedEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LocalizedEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LocalizedEntry", "defaultLanguageId", true, "localizedEntryId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LocalizedEntry newLocalizedEntry = addLocalizedEntry();

		LocalizedEntry existingLocalizedEntry = _persistence.fetchByPrimaryKey(
			newLocalizedEntry.getPrimaryKey());

		Assert.assertEquals(existingLocalizedEntry, newLocalizedEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntry missingLocalizedEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingLocalizedEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LocalizedEntry newLocalizedEntry1 = addLocalizedEntry();
		LocalizedEntry newLocalizedEntry2 = addLocalizedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntry1.getPrimaryKey());
		primaryKeys.add(newLocalizedEntry2.getPrimaryKey());

		Map<Serializable, LocalizedEntry> localizedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, localizedEntries.size());
		Assert.assertEquals(
			newLocalizedEntry1,
			localizedEntries.get(newLocalizedEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newLocalizedEntry2,
			localizedEntries.get(newLocalizedEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LocalizedEntry> localizedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(localizedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LocalizedEntry newLocalizedEntry = addLocalizedEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LocalizedEntry> localizedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, localizedEntries.size());
		Assert.assertEquals(
			newLocalizedEntry,
			localizedEntries.get(newLocalizedEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LocalizedEntry> localizedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(localizedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LocalizedEntry newLocalizedEntry = addLocalizedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntry.getPrimaryKey());

		Map<Serializable, LocalizedEntry> localizedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, localizedEntries.size());
		Assert.assertEquals(
			newLocalizedEntry,
			localizedEntries.get(newLocalizedEntry.getPrimaryKey()));
	}

	protected LocalizedEntry addLocalizedEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntry localizedEntry = _persistence.create(pk);

		localizedEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		_localizedEntries.add(_persistence.update(localizedEntry));

		return localizedEntry;
	}

	private List<LocalizedEntry> _localizedEntries =
		new ArrayList<LocalizedEntry>();
	private LocalizedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}