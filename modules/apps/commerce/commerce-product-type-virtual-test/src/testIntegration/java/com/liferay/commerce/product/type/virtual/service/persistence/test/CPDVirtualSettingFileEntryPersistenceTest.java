/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDVirtualSettingFileEntryException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryPersistence;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryUtil;
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
public class CPDVirtualSettingFileEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.product.type.virtual.service"));

	@Before
	public void setUp() {
		_persistence = CPDVirtualSettingFileEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDVirtualSettingFileEntry> iterator =
			_cpdVirtualSettingFileEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_persistence.create(pk);

		Assert.assertNotNull(cpdVirtualSettingFileEntry);

		Assert.assertEquals(cpdVirtualSettingFileEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		_persistence.remove(newCPDVirtualSettingFileEntry);

		CPDVirtualSettingFileEntry existingCPDVirtualSettingFileEntry =
			_persistence.fetchByPrimaryKey(
				newCPDVirtualSettingFileEntry.getPrimaryKey());

		Assert.assertNull(existingCPDVirtualSettingFileEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDVirtualSettingFileEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			_persistence.create(pk);

		newCPDVirtualSettingFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setUuid(RandomTestUtil.randomString());

		newCPDVirtualSettingFileEntry.setGroupId(RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setCompanyId(RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setUserId(RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setUserName(
			RandomTestUtil.randomString());

		newCPDVirtualSettingFileEntry.setCreateDate(RandomTestUtil.nextDate());

		newCPDVirtualSettingFileEntry.setModifiedDate(
			RandomTestUtil.nextDate());

		newCPDVirtualSettingFileEntry.setCPDefinitionVirtualSettingId(
			RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setFileEntryId(RandomTestUtil.nextLong());

		newCPDVirtualSettingFileEntry.setUrl(RandomTestUtil.randomString());

		newCPDVirtualSettingFileEntry.setVersion(RandomTestUtil.randomString());

		_cpdVirtualSettingFileEntries.add(
			_persistence.update(newCPDVirtualSettingFileEntry));

		CPDVirtualSettingFileEntry existingCPDVirtualSettingFileEntry =
			_persistence.findByPrimaryKey(
				newCPDVirtualSettingFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getMvccVersion(),
			newCPDVirtualSettingFileEntry.getMvccVersion());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getUuid(),
			newCPDVirtualSettingFileEntry.getUuid());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.
				getCPDefinitionVirtualSettingFileEntryId(),
			newCPDVirtualSettingFileEntry.
				getCPDefinitionVirtualSettingFileEntryId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getGroupId(),
			newCPDVirtualSettingFileEntry.getGroupId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getCompanyId(),
			newCPDVirtualSettingFileEntry.getCompanyId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getUserId(),
			newCPDVirtualSettingFileEntry.getUserId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getUserName(),
			newCPDVirtualSettingFileEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDVirtualSettingFileEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCPDVirtualSettingFileEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDVirtualSettingFileEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCPDVirtualSettingFileEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.
				getCPDefinitionVirtualSettingId(),
			newCPDVirtualSettingFileEntry.getCPDefinitionVirtualSettingId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getFileEntryId(),
			newCPDVirtualSettingFileEntry.getFileEntryId());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getUrl(),
			newCPDVirtualSettingFileEntry.getUrl());
		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry.getVersion(),
			newCPDVirtualSettingFileEntry.getVersion());
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
	public void testCountByCPDefinitionVirtualSettingId() throws Exception {
		_persistence.countByCPDefinitionVirtualSettingId(
			RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionVirtualSettingId(0L);
	}

	@Test
	public void testCountByFileEntryId() throws Exception {
		_persistence.countByFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		CPDVirtualSettingFileEntry existingCPDVirtualSettingFileEntry =
			_persistence.findByPrimaryKey(
				newCPDVirtualSettingFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry, newCPDVirtualSettingFileEntry);
	}

	@Test(expected = NoSuchCPDVirtualSettingFileEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDVirtualSettingFileEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDVirtualSettingFileEntry", "mvccVersion", true, "uuid", true,
			"CPDefinitionVirtualSettingFileEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "CPDefinitionVirtualSettingId", true,
			"fileEntryId", true, "url", true, "version", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		CPDVirtualSettingFileEntry existingCPDVirtualSettingFileEntry =
			_persistence.fetchByPrimaryKey(
				newCPDVirtualSettingFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPDVirtualSettingFileEntry, newCPDVirtualSettingFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDVirtualSettingFileEntry missingCPDVirtualSettingFileEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDVirtualSettingFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry1 =
			addCPDVirtualSettingFileEntry();
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry2 =
			addCPDVirtualSettingFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDVirtualSettingFileEntry1.getPrimaryKey());
		primaryKeys.add(newCPDVirtualSettingFileEntry2.getPrimaryKey());

		Map<Serializable, CPDVirtualSettingFileEntry>
			cpdVirtualSettingFileEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, cpdVirtualSettingFileEntries.size());
		Assert.assertEquals(
			newCPDVirtualSettingFileEntry1,
			cpdVirtualSettingFileEntries.get(
				newCPDVirtualSettingFileEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDVirtualSettingFileEntry2,
			cpdVirtualSettingFileEntries.get(
				newCPDVirtualSettingFileEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDVirtualSettingFileEntry>
			cpdVirtualSettingFileEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpdVirtualSettingFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDVirtualSettingFileEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDVirtualSettingFileEntry>
			cpdVirtualSettingFileEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpdVirtualSettingFileEntries.size());
		Assert.assertEquals(
			newCPDVirtualSettingFileEntry,
			cpdVirtualSettingFileEntries.get(
				newCPDVirtualSettingFileEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDVirtualSettingFileEntry>
			cpdVirtualSettingFileEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpdVirtualSettingFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDVirtualSettingFileEntry.getPrimaryKey());

		Map<Serializable, CPDVirtualSettingFileEntry>
			cpdVirtualSettingFileEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpdVirtualSettingFileEntries.size());
		Assert.assertEquals(
			newCPDVirtualSettingFileEntry,
			cpdVirtualSettingFileEntries.get(
				newCPDVirtualSettingFileEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
			addCPDVirtualSettingFileEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDVirtualSettingFileEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		Assert.assertEquals(
			cpdVirtualSettingFileEntry.getUuid(),
			ReflectionTestUtil.invoke(
				cpdVirtualSettingFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpdVirtualSettingFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpdVirtualSettingFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected CPDVirtualSettingFileEntry addCPDVirtualSettingFileEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_persistence.create(pk);

		cpdVirtualSettingFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setUuid(RandomTestUtil.randomString());

		cpdVirtualSettingFileEntry.setGroupId(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setCompanyId(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setUserId(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setUserName(RandomTestUtil.randomString());

		cpdVirtualSettingFileEntry.setCreateDate(RandomTestUtil.nextDate());

		cpdVirtualSettingFileEntry.setModifiedDate(RandomTestUtil.nextDate());

		cpdVirtualSettingFileEntry.setCPDefinitionVirtualSettingId(
			RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setFileEntryId(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setUrl(RandomTestUtil.randomString());

		cpdVirtualSettingFileEntry.setVersion(RandomTestUtil.randomString());

		_cpdVirtualSettingFileEntries.add(
			_persistence.update(cpdVirtualSettingFileEntry));

		return cpdVirtualSettingFileEntry;
	}

	private List<CPDVirtualSettingFileEntry> _cpdVirtualSettingFileEntries =
		new ArrayList<CPDVirtualSettingFileEntry>();
	private CPDVirtualSettingFileEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}