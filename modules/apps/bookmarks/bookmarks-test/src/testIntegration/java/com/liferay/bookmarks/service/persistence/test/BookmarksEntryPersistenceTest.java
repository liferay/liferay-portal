/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.service.persistence.BookmarksEntryPersistence;
import com.liferay.bookmarks.service.persistence.BookmarksEntryUtil;
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
public class BookmarksEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.bookmarks.service"));

	@Before
	public void setUp() {
		_persistence = BookmarksEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BookmarksEntry> iterator = _bookmarksEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksEntry bookmarksEntry = _persistence.create(pk);

		Assert.assertNotNull(bookmarksEntry);

		Assert.assertEquals(bookmarksEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		_persistence.remove(newBookmarksEntry);

		BookmarksEntry existingBookmarksEntry = _persistence.fetchByPrimaryKey(
			newBookmarksEntry.getPrimaryKey());

		Assert.assertNull(existingBookmarksEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBookmarksEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksEntry newBookmarksEntry = _persistence.create(pk);

		newBookmarksEntry.setMvccVersion(RandomTestUtil.nextLong());

		newBookmarksEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newBookmarksEntry.setUuid(RandomTestUtil.randomString());

		newBookmarksEntry.setGroupId(RandomTestUtil.nextLong());

		newBookmarksEntry.setCompanyId(RandomTestUtil.nextLong());

		newBookmarksEntry.setUserId(RandomTestUtil.nextLong());

		newBookmarksEntry.setUserName(RandomTestUtil.randomString());

		newBookmarksEntry.setCreateDate(RandomTestUtil.nextDate());

		newBookmarksEntry.setModifiedDate(RandomTestUtil.nextDate());

		newBookmarksEntry.setFolderId(RandomTestUtil.nextLong());

		newBookmarksEntry.setTreePath(RandomTestUtil.randomString());

		newBookmarksEntry.setName(RandomTestUtil.randomString());

		newBookmarksEntry.setUrl(RandomTestUtil.randomString());

		newBookmarksEntry.setDescription(RandomTestUtil.randomString());

		newBookmarksEntry.setPriority(RandomTestUtil.nextInt());

		newBookmarksEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newBookmarksEntry.setStatus(RandomTestUtil.nextInt());

		newBookmarksEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newBookmarksEntry.setStatusByUserName(RandomTestUtil.randomString());

		newBookmarksEntry.setStatusDate(RandomTestUtil.nextDate());

		_bookmarksEntries.add(_persistence.update(newBookmarksEntry));

		BookmarksEntry existingBookmarksEntry = _persistence.findByPrimaryKey(
			newBookmarksEntry.getPrimaryKey());

		Assert.assertEquals(
			existingBookmarksEntry.getMvccVersion(),
			newBookmarksEntry.getMvccVersion());
		Assert.assertEquals(
			existingBookmarksEntry.getCtCollectionId(),
			newBookmarksEntry.getCtCollectionId());
		Assert.assertEquals(
			existingBookmarksEntry.getUuid(), newBookmarksEntry.getUuid());
		Assert.assertEquals(
			existingBookmarksEntry.getEntryId(),
			newBookmarksEntry.getEntryId());
		Assert.assertEquals(
			existingBookmarksEntry.getGroupId(),
			newBookmarksEntry.getGroupId());
		Assert.assertEquals(
			existingBookmarksEntry.getCompanyId(),
			newBookmarksEntry.getCompanyId());
		Assert.assertEquals(
			existingBookmarksEntry.getUserId(), newBookmarksEntry.getUserId());
		Assert.assertEquals(
			existingBookmarksEntry.getUserName(),
			newBookmarksEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksEntry.getCreateDate()),
			Time.getShortTimestamp(newBookmarksEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksEntry.getModifiedDate()),
			Time.getShortTimestamp(newBookmarksEntry.getModifiedDate()));
		Assert.assertEquals(
			existingBookmarksEntry.getFolderId(),
			newBookmarksEntry.getFolderId());
		Assert.assertEquals(
			existingBookmarksEntry.getTreePath(),
			newBookmarksEntry.getTreePath());
		Assert.assertEquals(
			existingBookmarksEntry.getName(), newBookmarksEntry.getName());
		Assert.assertEquals(
			existingBookmarksEntry.getUrl(), newBookmarksEntry.getUrl());
		Assert.assertEquals(
			existingBookmarksEntry.getDescription(),
			newBookmarksEntry.getDescription());
		Assert.assertEquals(
			existingBookmarksEntry.getPriority(),
			newBookmarksEntry.getPriority());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksEntry.getLastPublishDate()),
			Time.getShortTimestamp(newBookmarksEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingBookmarksEntry.getStatus(), newBookmarksEntry.getStatus());
		Assert.assertEquals(
			existingBookmarksEntry.getStatusByUserId(),
			newBookmarksEntry.getStatusByUserId());
		Assert.assertEquals(
			existingBookmarksEntry.getStatusByUserName(),
			newBookmarksEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksEntry.getStatusDate()),
			Time.getShortTimestamp(newBookmarksEntry.getStatusDate()));
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_F(0L, 0L);
	}

	@Test
	public void testCountByG_FArrayable() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByG_NotS() throws Exception {
		_persistence.countByG_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_NotS(0L, 0);
	}

	@Test
	public void testCountByC_NotS() throws Exception {
		_persistence.countByC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotS(0L, 0);
	}

	@Test
	public void testCountByG_U_S() throws Exception {
		_persistence.countByG_U_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_U_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_U_NotS() throws Exception {
		_persistence.countByG_U_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_U_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_F_S() throws Exception {
		_persistence.countByG_F_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_F_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_F_SArrayable() throws Exception {
		_persistence.countByG_F_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_F_NotS() throws Exception {
		_persistence.countByG_F_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_F_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_F_NotSArrayable() throws Exception {
		_persistence.countByG_F_NotS(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_U_F_S() throws Exception {
		_persistence.countByG_U_F_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_U_F_S(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByG_U_F_SArrayable() throws Exception {
		_persistence.countByG_U_F_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		BookmarksEntry existingBookmarksEntry = _persistence.findByPrimaryKey(
			newBookmarksEntry.getPrimaryKey());

		Assert.assertEquals(existingBookmarksEntry, newBookmarksEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BookmarksEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BookmarksEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "entryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "folderId", true, "treePath", true, "name",
			true, "url", true, "description", true, "priority", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		BookmarksEntry existingBookmarksEntry = _persistence.fetchByPrimaryKey(
			newBookmarksEntry.getPrimaryKey());

		Assert.assertEquals(existingBookmarksEntry, newBookmarksEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksEntry missingBookmarksEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingBookmarksEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BookmarksEntry newBookmarksEntry1 = addBookmarksEntry();
		BookmarksEntry newBookmarksEntry2 = addBookmarksEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksEntry1.getPrimaryKey());
		primaryKeys.add(newBookmarksEntry2.getPrimaryKey());

		Map<Serializable, BookmarksEntry> bookmarksEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, bookmarksEntries.size());
		Assert.assertEquals(
			newBookmarksEntry1,
			bookmarksEntries.get(newBookmarksEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newBookmarksEntry2,
			bookmarksEntries.get(newBookmarksEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BookmarksEntry> bookmarksEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bookmarksEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BookmarksEntry> bookmarksEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bookmarksEntries.size());
		Assert.assertEquals(
			newBookmarksEntry,
			bookmarksEntries.get(newBookmarksEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BookmarksEntry> bookmarksEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bookmarksEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksEntry.getPrimaryKey());

		Map<Serializable, BookmarksEntry> bookmarksEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bookmarksEntries.size());
		Assert.assertEquals(
			newBookmarksEntry,
			bookmarksEntries.get(newBookmarksEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		BookmarksEntry newBookmarksEntry = addBookmarksEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newBookmarksEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(BookmarksEntry bookmarksEntry) {
		Assert.assertEquals(
			bookmarksEntry.getUuid(),
			ReflectionTestUtil.invoke(
				bookmarksEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(bookmarksEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				bookmarksEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected BookmarksEntry addBookmarksEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksEntry bookmarksEntry = _persistence.create(pk);

		bookmarksEntry.setMvccVersion(RandomTestUtil.nextLong());

		bookmarksEntry.setCtCollectionId(RandomTestUtil.nextLong());

		bookmarksEntry.setUuid(RandomTestUtil.randomString());

		bookmarksEntry.setGroupId(RandomTestUtil.nextLong());

		bookmarksEntry.setCompanyId(RandomTestUtil.nextLong());

		bookmarksEntry.setUserId(RandomTestUtil.nextLong());

		bookmarksEntry.setUserName(RandomTestUtil.randomString());

		bookmarksEntry.setCreateDate(RandomTestUtil.nextDate());

		bookmarksEntry.setModifiedDate(RandomTestUtil.nextDate());

		bookmarksEntry.setFolderId(RandomTestUtil.nextLong());

		bookmarksEntry.setTreePath(RandomTestUtil.randomString());

		bookmarksEntry.setName(RandomTestUtil.randomString());

		bookmarksEntry.setUrl(RandomTestUtil.randomString());

		bookmarksEntry.setDescription(RandomTestUtil.randomString());

		bookmarksEntry.setPriority(RandomTestUtil.nextInt());

		bookmarksEntry.setLastPublishDate(RandomTestUtil.nextDate());

		bookmarksEntry.setStatus(RandomTestUtil.nextInt());

		bookmarksEntry.setStatusByUserId(RandomTestUtil.nextLong());

		bookmarksEntry.setStatusByUserName(RandomTestUtil.randomString());

		bookmarksEntry.setStatusDate(RandomTestUtil.nextDate());

		_bookmarksEntries.add(_persistence.update(bookmarksEntry));

		return bookmarksEntry;
	}

	private List<BookmarksEntry> _bookmarksEntries =
		new ArrayList<BookmarksEntry>();
	private BookmarksEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}