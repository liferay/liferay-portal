/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.language.override.exception.NoSuchPLOEntryException;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.persistence.PLOEntryPersistence;
import com.liferay.portal.language.override.service.persistence.PLOEntryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

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
public class PLOEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.language.override.service"));

	@Before
	public void setUp() {
		_persistence = PLOEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PLOEntry> iterator = _ploEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PLOEntry ploEntry = _persistence.create(pk);

		Assert.assertNotNull(ploEntry);

		Assert.assertEquals(ploEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PLOEntry newPLOEntry = addPLOEntry();

		_persistence.remove(newPLOEntry);

		PLOEntry existingPLOEntry = _persistence.fetchByPrimaryKey(
			newPLOEntry.getPrimaryKey());

		Assert.assertNull(existingPLOEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPLOEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PLOEntry newPLOEntry = _persistence.create(pk);

		newPLOEntry.setMvccVersion(RandomTestUtil.nextLong());

		newPLOEntry.setCompanyId(RandomTestUtil.nextLong());

		newPLOEntry.setUserId(RandomTestUtil.nextLong());

		newPLOEntry.setCreateDate(RandomTestUtil.nextDate());

		newPLOEntry.setModifiedDate(RandomTestUtil.nextDate());

		newPLOEntry.setKey(RandomTestUtil.randomString());

		newPLOEntry.setLanguageId(RandomTestUtil.randomString());

		newPLOEntry.setValue(RandomTestUtil.randomString());

		_ploEntries.add(_persistence.update(newPLOEntry));

		PLOEntry existingPLOEntry = _persistence.findByPrimaryKey(
			newPLOEntry.getPrimaryKey());

		Assert.assertEquals(
			existingPLOEntry.getMvccVersion(), newPLOEntry.getMvccVersion());
		Assert.assertEquals(
			existingPLOEntry.getPloEntryId(), newPLOEntry.getPloEntryId());
		Assert.assertEquals(
			existingPLOEntry.getCompanyId(), newPLOEntry.getCompanyId());
		Assert.assertEquals(
			existingPLOEntry.getUserId(), newPLOEntry.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPLOEntry.getCreateDate()),
			Time.getShortTimestamp(newPLOEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPLOEntry.getModifiedDate()),
			Time.getShortTimestamp(newPLOEntry.getModifiedDate()));
		Assert.assertEquals(existingPLOEntry.getKey(), newPLOEntry.getKey());
		Assert.assertEquals(
			existingPLOEntry.getLanguageId(), newPLOEntry.getLanguageId());
		Assert.assertEquals(
			existingPLOEntry.getValue(), newPLOEntry.getValue());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(RandomTestUtil.nextLong(), "");

		_persistence.countByC_L(0L, "null");

		_persistence.countByC_L(0L, (String)null);
	}

	@Test
	public void testCountByC_K_L() throws Exception {
		_persistence.countByC_K_L(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_K_L(0L, "null", "null");

		_persistence.countByC_K_L(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PLOEntry newPLOEntry = addPLOEntry();

		PLOEntry existingPLOEntry = _persistence.findByPrimaryKey(
			newPLOEntry.getPrimaryKey());

		Assert.assertEquals(existingPLOEntry, newPLOEntry);
	}

	@Test(expected = NoSuchPLOEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PLOEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PLOEntry", "mvccVersion", true, "ploEntryId", true, "companyId",
			true, "userId", true, "createDate", true, "modifiedDate", true,
			"key", true, "languageId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PLOEntry newPLOEntry = addPLOEntry();

		PLOEntry existingPLOEntry = _persistence.fetchByPrimaryKey(
			newPLOEntry.getPrimaryKey());

		Assert.assertEquals(existingPLOEntry, newPLOEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PLOEntry missingPLOEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPLOEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PLOEntry newPLOEntry1 = addPLOEntry();
		PLOEntry newPLOEntry2 = addPLOEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPLOEntry1.getPrimaryKey());
		primaryKeys.add(newPLOEntry2.getPrimaryKey());

		Map<Serializable, PLOEntry> ploEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ploEntries.size());
		Assert.assertEquals(
			newPLOEntry1, ploEntries.get(newPLOEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newPLOEntry2, ploEntries.get(newPLOEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PLOEntry> ploEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ploEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PLOEntry newPLOEntry = addPLOEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPLOEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PLOEntry> ploEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ploEntries.size());
		Assert.assertEquals(
			newPLOEntry, ploEntries.get(newPLOEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PLOEntry> ploEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ploEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PLOEntry newPLOEntry = addPLOEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPLOEntry.getPrimaryKey());

		Map<Serializable, PLOEntry> ploEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ploEntries.size());
		Assert.assertEquals(
			newPLOEntry, ploEntries.get(newPLOEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PLOEntry newPLOEntry = addPLOEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newPLOEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(PLOEntry ploEntry) {
		Assert.assertEquals(
			Long.valueOf(ploEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ploEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			ploEntry.getKey(),
			ReflectionTestUtil.invoke(
				ploEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
		Assert.assertEquals(
			ploEntry.getLanguageId(),
			ReflectionTestUtil.invoke(
				ploEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected PLOEntry addPLOEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PLOEntry ploEntry = _persistence.create(pk);

		ploEntry.setMvccVersion(RandomTestUtil.nextLong());

		ploEntry.setCompanyId(RandomTestUtil.nextLong());

		ploEntry.setUserId(RandomTestUtil.nextLong());

		ploEntry.setCreateDate(RandomTestUtil.nextDate());

		ploEntry.setModifiedDate(RandomTestUtil.nextDate());

		ploEntry.setKey(RandomTestUtil.randomString());

		ploEntry.setLanguageId(RandomTestUtil.randomString());

		ploEntry.setValue(RandomTestUtil.randomString());

		_ploEntries.add(_persistence.update(ploEntry));

		return ploEntry;
	}

	private List<PLOEntry> _ploEntries = new ArrayList<PLOEntry>();
	private PLOEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}