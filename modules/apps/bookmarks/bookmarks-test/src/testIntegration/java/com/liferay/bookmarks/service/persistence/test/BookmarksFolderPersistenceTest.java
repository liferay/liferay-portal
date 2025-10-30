/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.persistence.BookmarksFolderPersistence;
import com.liferay.bookmarks.service.persistence.BookmarksFolderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class BookmarksFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.bookmarks.service"));

	@Before
	public void setUp() {
		_persistence = BookmarksFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BookmarksFolder> iterator = _bookmarksFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksFolder bookmarksFolder = _persistence.create(pk);

		Assert.assertNotNull(bookmarksFolder);

		Assert.assertEquals(bookmarksFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		_persistence.remove(newBookmarksFolder);

		BookmarksFolder existingBookmarksFolder =
			_persistence.fetchByPrimaryKey(newBookmarksFolder.getPrimaryKey());

		Assert.assertNull(existingBookmarksFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBookmarksFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksFolder newBookmarksFolder = _persistence.create(pk);

		newBookmarksFolder.setMvccVersion(RandomTestUtil.nextLong());

		newBookmarksFolder.setCtCollectionId(RandomTestUtil.nextLong());

		newBookmarksFolder.setUuid(RandomTestUtil.randomString());

		newBookmarksFolder.setGroupId(RandomTestUtil.nextLong());

		newBookmarksFolder.setCompanyId(RandomTestUtil.nextLong());

		newBookmarksFolder.setUserId(RandomTestUtil.nextLong());

		newBookmarksFolder.setUserName(RandomTestUtil.randomString());

		newBookmarksFolder.setCreateDate(RandomTestUtil.nextDate());

		newBookmarksFolder.setModifiedDate(RandomTestUtil.nextDate());

		newBookmarksFolder.setParentFolderId(RandomTestUtil.nextLong());

		newBookmarksFolder.setTreePath(RandomTestUtil.randomString());

		newBookmarksFolder.setName(RandomTestUtil.randomString());

		newBookmarksFolder.setDescription(RandomTestUtil.randomString());

		newBookmarksFolder.setLastPublishDate(RandomTestUtil.nextDate());

		newBookmarksFolder.setStatus(RandomTestUtil.nextInt());

		newBookmarksFolder.setStatusByUserId(RandomTestUtil.nextLong());

		newBookmarksFolder.setStatusByUserName(RandomTestUtil.randomString());

		newBookmarksFolder.setStatusDate(RandomTestUtil.nextDate());

		_bookmarksFolders.add(_persistence.update(newBookmarksFolder));

		BookmarksFolder existingBookmarksFolder = _persistence.findByPrimaryKey(
			newBookmarksFolder.getPrimaryKey());

		Assert.assertEquals(
			existingBookmarksFolder.getMvccVersion(),
			newBookmarksFolder.getMvccVersion());
		Assert.assertEquals(
			existingBookmarksFolder.getCtCollectionId(),
			newBookmarksFolder.getCtCollectionId());
		Assert.assertEquals(
			existingBookmarksFolder.getUuid(), newBookmarksFolder.getUuid());
		Assert.assertEquals(
			existingBookmarksFolder.getFolderId(),
			newBookmarksFolder.getFolderId());
		Assert.assertEquals(
			existingBookmarksFolder.getGroupId(),
			newBookmarksFolder.getGroupId());
		Assert.assertEquals(
			existingBookmarksFolder.getCompanyId(),
			newBookmarksFolder.getCompanyId());
		Assert.assertEquals(
			existingBookmarksFolder.getUserId(),
			newBookmarksFolder.getUserId());
		Assert.assertEquals(
			existingBookmarksFolder.getUserName(),
			newBookmarksFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksFolder.getCreateDate()),
			Time.getShortTimestamp(newBookmarksFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksFolder.getModifiedDate()),
			Time.getShortTimestamp(newBookmarksFolder.getModifiedDate()));
		Assert.assertEquals(
			existingBookmarksFolder.getParentFolderId(),
			newBookmarksFolder.getParentFolderId());
		Assert.assertEquals(
			existingBookmarksFolder.getTreePath(),
			newBookmarksFolder.getTreePath());
		Assert.assertEquals(
			existingBookmarksFolder.getName(), newBookmarksFolder.getName());
		Assert.assertEquals(
			existingBookmarksFolder.getDescription(),
			newBookmarksFolder.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingBookmarksFolder.getLastPublishDate()),
			Time.getShortTimestamp(newBookmarksFolder.getLastPublishDate()));
		Assert.assertEquals(
			existingBookmarksFolder.getStatus(),
			newBookmarksFolder.getStatus());
		Assert.assertEquals(
			existingBookmarksFolder.getStatusByUserId(),
			newBookmarksFolder.getStatusByUserId());
		Assert.assertEquals(
			existingBookmarksFolder.getStatusByUserName(),
			newBookmarksFolder.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBookmarksFolder.getStatusDate()),
			Time.getShortTimestamp(newBookmarksFolder.getStatusDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByC_NotS() throws Exception {
		_persistence.countByC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotS(0L, 0);
	}

	@Test
	public void testCountByG_P_S() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_NotS() throws Exception {
		_persistence.countByG_P_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByGtF_C_P_NotS() throws Exception {
		_persistence.countByGtF_C_P_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByGtF_C_P_NotS(0L, 0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		BookmarksFolder existingBookmarksFolder = _persistence.findByPrimaryKey(
			newBookmarksFolder.getPrimaryKey());

		Assert.assertEquals(existingBookmarksFolder, newBookmarksFolder);
	}

	@Test(expected = NoSuchFolderException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BookmarksFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BookmarksFolder", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "folderId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "parentFolderId", true, "treePath", true,
			"name", true, "description", true, "lastPublishDate", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		BookmarksFolder existingBookmarksFolder =
			_persistence.fetchByPrimaryKey(newBookmarksFolder.getPrimaryKey());

		Assert.assertEquals(existingBookmarksFolder, newBookmarksFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksFolder missingBookmarksFolder = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingBookmarksFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BookmarksFolder newBookmarksFolder1 = addBookmarksFolder();
		BookmarksFolder newBookmarksFolder2 = addBookmarksFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksFolder1.getPrimaryKey());
		primaryKeys.add(newBookmarksFolder2.getPrimaryKey());

		Map<Serializable, BookmarksFolder> bookmarksFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, bookmarksFolders.size());
		Assert.assertEquals(
			newBookmarksFolder1,
			bookmarksFolders.get(newBookmarksFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newBookmarksFolder2,
			bookmarksFolders.get(newBookmarksFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BookmarksFolder> bookmarksFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bookmarksFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BookmarksFolder> bookmarksFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bookmarksFolders.size());
		Assert.assertEquals(
			newBookmarksFolder,
			bookmarksFolders.get(newBookmarksFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BookmarksFolder> bookmarksFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bookmarksFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBookmarksFolder.getPrimaryKey());

		Map<Serializable, BookmarksFolder> bookmarksFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bookmarksFolders.size());
		Assert.assertEquals(
			newBookmarksFolder,
			bookmarksFolders.get(newBookmarksFolder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		BookmarksFolder newBookmarksFolder = addBookmarksFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newBookmarksFolder.getPrimaryKey()));
	}

	private void _assertOriginalValues(BookmarksFolder bookmarksFolder) {
		Assert.assertEquals(
			bookmarksFolder.getUuid(),
			ReflectionTestUtil.invoke(
				bookmarksFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(bookmarksFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				bookmarksFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected BookmarksFolder addBookmarksFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BookmarksFolder bookmarksFolder = _persistence.create(pk);

		bookmarksFolder.setMvccVersion(RandomTestUtil.nextLong());

		bookmarksFolder.setCtCollectionId(RandomTestUtil.nextLong());

		bookmarksFolder.setUuid(RandomTestUtil.randomString());

		bookmarksFolder.setGroupId(RandomTestUtil.nextLong());

		bookmarksFolder.setCompanyId(RandomTestUtil.nextLong());

		bookmarksFolder.setUserId(RandomTestUtil.nextLong());

		bookmarksFolder.setUserName(RandomTestUtil.randomString());

		bookmarksFolder.setCreateDate(RandomTestUtil.nextDate());

		bookmarksFolder.setModifiedDate(RandomTestUtil.nextDate());

		bookmarksFolder.setParentFolderId(RandomTestUtil.nextLong());

		bookmarksFolder.setTreePath(RandomTestUtil.randomString());

		bookmarksFolder.setName(RandomTestUtil.randomString());

		bookmarksFolder.setDescription(RandomTestUtil.randomString());

		bookmarksFolder.setLastPublishDate(RandomTestUtil.nextDate());

		bookmarksFolder.setStatus(RandomTestUtil.nextInt());

		bookmarksFolder.setStatusByUserId(RandomTestUtil.nextLong());

		bookmarksFolder.setStatusByUserName(RandomTestUtil.randomString());

		bookmarksFolder.setStatusDate(RandomTestUtil.nextDate());

		_bookmarksFolders.add(_persistence.update(bookmarksFolder));

		return bookmarksFolder;
	}

	private List<BookmarksFolder> _bookmarksFolders =
		new ArrayList<BookmarksFolder>();
	private BookmarksFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}