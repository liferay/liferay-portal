/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.grouped.exception.NoSuchCPDefinitionGroupedEntryException;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.persistence.CPDefinitionGroupedEntryPersistence;
import com.liferay.commerce.product.type.grouped.service.persistence.CPDefinitionGroupedEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CPDefinitionGroupedEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.product.type.grouped.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionGroupedEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionGroupedEntry> iterator =
			_cpDefinitionGroupedEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry = _persistence.create(
			pk);

		Assert.assertNotNull(cpDefinitionGroupedEntry);

		Assert.assertEquals(cpDefinitionGroupedEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		_persistence.remove(newCPDefinitionGroupedEntry);

		CPDefinitionGroupedEntry existingCPDefinitionGroupedEntry =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionGroupedEntry.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionGroupedEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionGroupedEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			_persistence.create(pk);

		newCPDefinitionGroupedEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setUuid(RandomTestUtil.randomString());

		newCPDefinitionGroupedEntry.setGroupId(RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setUserId(RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setUserName(RandomTestUtil.randomString());

		newCPDefinitionGroupedEntry.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinitionGroupedEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCPDefinitionGroupedEntry.setCPDefinitionId(
			RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setEntryCProductId(
			RandomTestUtil.nextLong());

		newCPDefinitionGroupedEntry.setPriority(RandomTestUtil.nextDouble());

		newCPDefinitionGroupedEntry.setQuantity(RandomTestUtil.nextInt());

		_cpDefinitionGroupedEntries.add(
			_persistence.update(newCPDefinitionGroupedEntry));

		CPDefinitionGroupedEntry existingCPDefinitionGroupedEntry =
			_persistence.findByPrimaryKey(
				newCPDefinitionGroupedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getMvccVersion(),
			newCPDefinitionGroupedEntry.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getUuid(),
			newCPDefinitionGroupedEntry.getUuid());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getCPDefinitionGroupedEntryId(),
			newCPDefinitionGroupedEntry.getCPDefinitionGroupedEntryId());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getGroupId(),
			newCPDefinitionGroupedEntry.getGroupId());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getCompanyId(),
			newCPDefinitionGroupedEntry.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getUserId(),
			newCPDefinitionGroupedEntry.getUserId());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getUserName(),
			newCPDefinitionGroupedEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionGroupedEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCPDefinitionGroupedEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionGroupedEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCPDefinitionGroupedEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getCPDefinitionId(),
			newCPDefinitionGroupedEntry.getCPDefinitionId());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getEntryCProductId(),
			newCPDefinitionGroupedEntry.getEntryCProductId());
		AssertUtils.assertEquals(
			existingCPDefinitionGroupedEntry.getPriority(),
			newCPDefinitionGroupedEntry.getPriority());
		Assert.assertEquals(
			existingCPDefinitionGroupedEntry.getQuantity(),
			newCPDefinitionGroupedEntry.getQuantity());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByEntryCProductId() throws Exception {
		_persistence.countByEntryCProductId(RandomTestUtil.nextLong());

		_persistence.countByEntryCProductId(0L);
	}

	@Test
	public void testCountByC_E() throws Exception {
		_persistence.countByC_E(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_E(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		CPDefinitionGroupedEntry existingCPDefinitionGroupedEntry =
			_persistence.findByPrimaryKey(
				newCPDefinitionGroupedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionGroupedEntry, newCPDefinitionGroupedEntry);
	}

	@Test(expected = NoSuchCPDefinitionGroupedEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionGroupedEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionGroupedEntry", "mvccVersion", true, "uuid", true,
			"CPDefinitionGroupedEntryId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPDefinitionId", true, "entryCProductId",
			true, "priority", true, "quantity", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		CPDefinitionGroupedEntry existingCPDefinitionGroupedEntry =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionGroupedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionGroupedEntry, newCPDefinitionGroupedEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionGroupedEntry missingCPDefinitionGroupedEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionGroupedEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry1 =
			addCPDefinitionGroupedEntry();
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry2 =
			addCPDefinitionGroupedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionGroupedEntry1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionGroupedEntry2.getPrimaryKey());

		Map<Serializable, CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDefinitionGroupedEntries.size());
		Assert.assertEquals(
			newCPDefinitionGroupedEntry1,
			cpDefinitionGroupedEntries.get(
				newCPDefinitionGroupedEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionGroupedEntry2,
			cpDefinitionGroupedEntries.get(
				newCPDefinitionGroupedEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionGroupedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionGroupedEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionGroupedEntries.size());
		Assert.assertEquals(
			newCPDefinitionGroupedEntry,
			cpDefinitionGroupedEntries.get(
				newCPDefinitionGroupedEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionGroupedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionGroupedEntry.getPrimaryKey());

		Map<Serializable, CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionGroupedEntries.size());
		Assert.assertEquals(
			newCPDefinitionGroupedEntry,
			cpDefinitionGroupedEntries.get(
				newCPDefinitionGroupedEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
			addCPDefinitionGroupedEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionGroupedEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		Assert.assertEquals(
			cpDefinitionGroupedEntry.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinitionGroupedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionGroupedEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionGroupedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinitionGroupedEntry.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionGroupedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionGroupedEntry.getEntryCProductId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionGroupedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "entryCProductId"));
	}

	protected CPDefinitionGroupedEntry addCPDefinitionGroupedEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry = _persistence.create(
			pk);

		cpDefinitionGroupedEntry.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setUuid(RandomTestUtil.randomString());

		cpDefinitionGroupedEntry.setGroupId(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setUserId(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setUserName(RandomTestUtil.randomString());

		cpDefinitionGroupedEntry.setCreateDate(RandomTestUtil.nextDate());

		cpDefinitionGroupedEntry.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinitionGroupedEntry.setCPDefinitionId(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setEntryCProductId(RandomTestUtil.nextLong());

		cpDefinitionGroupedEntry.setPriority(RandomTestUtil.nextDouble());

		cpDefinitionGroupedEntry.setQuantity(RandomTestUtil.nextInt());

		_cpDefinitionGroupedEntries.add(
			_persistence.update(cpDefinitionGroupedEntry));

		return cpDefinitionGroupedEntry;
	}

	private List<CPDefinitionGroupedEntry> _cpDefinitionGroupedEntries =
		new ArrayList<CPDefinitionGroupedEntry>();
	private CPDefinitionGroupedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}