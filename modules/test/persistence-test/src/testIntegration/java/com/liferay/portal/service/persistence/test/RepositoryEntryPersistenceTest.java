/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.service.persistence.RepositoryEntryPersistence;
import com.liferay.portal.kernel.service.persistence.RepositoryEntryUtil;
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
public class RepositoryEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RepositoryEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RepositoryEntry> iterator = _repositoryEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RepositoryEntry repositoryEntry = _persistence.create(pk);

		Assert.assertNotNull(repositoryEntry);

		Assert.assertEquals(repositoryEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		_persistence.remove(newRepositoryEntry);

		RepositoryEntry existingRepositoryEntry =
			_persistence.fetchByPrimaryKey(newRepositoryEntry.getPrimaryKey());

		Assert.assertNull(existingRepositoryEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRepositoryEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RepositoryEntry newRepositoryEntry = _persistence.create(pk);

		newRepositoryEntry.setMvccVersion(RandomTestUtil.nextLong());

		newRepositoryEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newRepositoryEntry.setUuid(RandomTestUtil.randomString());

		newRepositoryEntry.setGroupId(RandomTestUtil.nextLong());

		newRepositoryEntry.setCompanyId(RandomTestUtil.nextLong());

		newRepositoryEntry.setUserId(RandomTestUtil.nextLong());

		newRepositoryEntry.setUserName(RandomTestUtil.randomString());

		newRepositoryEntry.setCreateDate(RandomTestUtil.nextDate());

		newRepositoryEntry.setModifiedDate(RandomTestUtil.nextDate());

		newRepositoryEntry.setRepositoryId(RandomTestUtil.nextLong());

		newRepositoryEntry.setMappedId(RandomTestUtil.randomString());

		newRepositoryEntry.setManualCheckInRequired(
			RandomTestUtil.randomBoolean());

		newRepositoryEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_repositoryEntries.add(_persistence.update(newRepositoryEntry));

		RepositoryEntry existingRepositoryEntry = _persistence.findByPrimaryKey(
			newRepositoryEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRepositoryEntry.getMvccVersion(),
			newRepositoryEntry.getMvccVersion());
		Assert.assertEquals(
			existingRepositoryEntry.getCtCollectionId(),
			newRepositoryEntry.getCtCollectionId());
		Assert.assertEquals(
			existingRepositoryEntry.getUuid(), newRepositoryEntry.getUuid());
		Assert.assertEquals(
			existingRepositoryEntry.getRepositoryEntryId(),
			newRepositoryEntry.getRepositoryEntryId());
		Assert.assertEquals(
			existingRepositoryEntry.getGroupId(),
			newRepositoryEntry.getGroupId());
		Assert.assertEquals(
			existingRepositoryEntry.getCompanyId(),
			newRepositoryEntry.getCompanyId());
		Assert.assertEquals(
			existingRepositoryEntry.getUserId(),
			newRepositoryEntry.getUserId());
		Assert.assertEquals(
			existingRepositoryEntry.getUserName(),
			newRepositoryEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRepositoryEntry.getCreateDate()),
			Time.getShortTimestamp(newRepositoryEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRepositoryEntry.getModifiedDate()),
			Time.getShortTimestamp(newRepositoryEntry.getModifiedDate()));
		Assert.assertEquals(
			existingRepositoryEntry.getRepositoryId(),
			newRepositoryEntry.getRepositoryId());
		Assert.assertEquals(
			existingRepositoryEntry.getMappedId(),
			newRepositoryEntry.getMappedId());
		Assert.assertEquals(
			existingRepositoryEntry.isManualCheckInRequired(),
			newRepositoryEntry.isManualCheckInRequired());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingRepositoryEntry.getLastPublishDate()),
			Time.getShortTimestamp(newRepositoryEntry.getLastPublishDate()));
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
	public void testCountByRepositoryId() throws Exception {
		_persistence.countByRepositoryId(RandomTestUtil.nextLong());

		_persistence.countByRepositoryId(0L);
	}

	@Test
	public void testCountByR_M() throws Exception {
		_persistence.countByR_M(RandomTestUtil.nextLong(), "");

		_persistence.countByR_M(0L, "null");

		_persistence.countByR_M(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		RepositoryEntry existingRepositoryEntry = _persistence.findByPrimaryKey(
			newRepositoryEntry.getPrimaryKey());

		Assert.assertEquals(existingRepositoryEntry, newRepositoryEntry);
	}

	@Test(expected = NoSuchRepositoryEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RepositoryEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RepositoryEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "repositoryEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "repositoryId", true, "mappedId", true,
			"manualCheckInRequired", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		RepositoryEntry existingRepositoryEntry =
			_persistence.fetchByPrimaryKey(newRepositoryEntry.getPrimaryKey());

		Assert.assertEquals(existingRepositoryEntry, newRepositoryEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RepositoryEntry missingRepositoryEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingRepositoryEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RepositoryEntry newRepositoryEntry1 = addRepositoryEntry();
		RepositoryEntry newRepositoryEntry2 = addRepositoryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRepositoryEntry1.getPrimaryKey());
		primaryKeys.add(newRepositoryEntry2.getPrimaryKey());

		Map<Serializable, RepositoryEntry> repositoryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, repositoryEntries.size());
		Assert.assertEquals(
			newRepositoryEntry1,
			repositoryEntries.get(newRepositoryEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRepositoryEntry2,
			repositoryEntries.get(newRepositoryEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RepositoryEntry> repositoryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(repositoryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRepositoryEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RepositoryEntry> repositoryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, repositoryEntries.size());
		Assert.assertEquals(
			newRepositoryEntry,
			repositoryEntries.get(newRepositoryEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RepositoryEntry> repositoryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(repositoryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRepositoryEntry.getPrimaryKey());

		Map<Serializable, RepositoryEntry> repositoryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, repositoryEntries.size());
		Assert.assertEquals(
			newRepositoryEntry,
			repositoryEntries.get(newRepositoryEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RepositoryEntry newRepositoryEntry = addRepositoryEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newRepositoryEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(RepositoryEntry repositoryEntry) {
		Assert.assertEquals(
			repositoryEntry.getUuid(),
			ReflectionTestUtil.invoke(
				repositoryEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(repositoryEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				repositoryEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(repositoryEntry.getRepositoryId()),
			ReflectionTestUtil.<Long>invoke(
				repositoryEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "repositoryId"));
		Assert.assertEquals(
			repositoryEntry.getMappedId(),
			ReflectionTestUtil.invoke(
				repositoryEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "mappedId"));
	}

	protected RepositoryEntry addRepositoryEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RepositoryEntry repositoryEntry = _persistence.create(pk);

		repositoryEntry.setMvccVersion(RandomTestUtil.nextLong());

		repositoryEntry.setCtCollectionId(RandomTestUtil.nextLong());

		repositoryEntry.setUuid(RandomTestUtil.randomString());

		repositoryEntry.setGroupId(RandomTestUtil.nextLong());

		repositoryEntry.setCompanyId(RandomTestUtil.nextLong());

		repositoryEntry.setUserId(RandomTestUtil.nextLong());

		repositoryEntry.setUserName(RandomTestUtil.randomString());

		repositoryEntry.setCreateDate(RandomTestUtil.nextDate());

		repositoryEntry.setModifiedDate(RandomTestUtil.nextDate());

		repositoryEntry.setRepositoryId(RandomTestUtil.nextLong());

		repositoryEntry.setMappedId(RandomTestUtil.randomString());

		repositoryEntry.setManualCheckInRequired(
			RandomTestUtil.randomBoolean());

		repositoryEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_repositoryEntries.add(_persistence.update(repositoryEntry));

		return repositoryEntry;
	}

	private List<RepositoryEntry> _repositoryEntries =
		new ArrayList<RepositoryEntry>();
	private RepositoryEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}