/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.DuplicateDLFileShortcutExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class DLFileShortcutPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFileShortcutUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFileShortcut> iterator = _dlFileShortcuts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileShortcut dlFileShortcut = _persistence.create(pk);

		Assert.assertNotNull(dlFileShortcut);

		Assert.assertEquals(dlFileShortcut.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		_persistence.remove(newDLFileShortcut);

		DLFileShortcut existingDLFileShortcut = _persistence.fetchByPrimaryKey(
			newDLFileShortcut.getPrimaryKey());

		Assert.assertNull(existingDLFileShortcut);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFileShortcut();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileShortcut newDLFileShortcut = _persistence.create(pk);

		newDLFileShortcut.setMvccVersion(RandomTestUtil.nextLong());

		newDLFileShortcut.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFileShortcut.setUuid(RandomTestUtil.randomString());

		newDLFileShortcut.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newDLFileShortcut.setGroupId(RandomTestUtil.nextLong());

		newDLFileShortcut.setCompanyId(RandomTestUtil.nextLong());

		newDLFileShortcut.setUserId(RandomTestUtil.nextLong());

		newDLFileShortcut.setUserName(RandomTestUtil.randomString());

		newDLFileShortcut.setCreateDate(RandomTestUtil.nextDate());

		newDLFileShortcut.setModifiedDate(RandomTestUtil.nextDate());

		newDLFileShortcut.setRepositoryId(RandomTestUtil.nextLong());

		newDLFileShortcut.setFolderId(RandomTestUtil.nextLong());

		newDLFileShortcut.setToFileEntryId(RandomTestUtil.nextLong());

		newDLFileShortcut.setTreePath(RandomTestUtil.randomString());

		newDLFileShortcut.setActive(RandomTestUtil.randomBoolean());

		newDLFileShortcut.setLastPublishDate(RandomTestUtil.nextDate());

		newDLFileShortcut.setStatus(RandomTestUtil.nextInt());

		newDLFileShortcut.setStatusByUserId(RandomTestUtil.nextLong());

		newDLFileShortcut.setStatusByUserName(RandomTestUtil.randomString());

		newDLFileShortcut.setStatusDate(RandomTestUtil.nextDate());

		_dlFileShortcuts.add(_persistence.update(newDLFileShortcut));

		DLFileShortcut existingDLFileShortcut = _persistence.findByPrimaryKey(
			newDLFileShortcut.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileShortcut.getMvccVersion(),
			newDLFileShortcut.getMvccVersion());
		Assert.assertEquals(
			existingDLFileShortcut.getCtCollectionId(),
			newDLFileShortcut.getCtCollectionId());
		Assert.assertEquals(
			existingDLFileShortcut.getUuid(), newDLFileShortcut.getUuid());
		Assert.assertEquals(
			existingDLFileShortcut.getExternalReferenceCode(),
			newDLFileShortcut.getExternalReferenceCode());
		Assert.assertEquals(
			existingDLFileShortcut.getFileShortcutId(),
			newDLFileShortcut.getFileShortcutId());
		Assert.assertEquals(
			existingDLFileShortcut.getGroupId(),
			newDLFileShortcut.getGroupId());
		Assert.assertEquals(
			existingDLFileShortcut.getCompanyId(),
			newDLFileShortcut.getCompanyId());
		Assert.assertEquals(
			existingDLFileShortcut.getUserId(), newDLFileShortcut.getUserId());
		Assert.assertEquals(
			existingDLFileShortcut.getUserName(),
			newDLFileShortcut.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileShortcut.getCreateDate()),
			Time.getShortTimestamp(newDLFileShortcut.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileShortcut.getModifiedDate()),
			Time.getShortTimestamp(newDLFileShortcut.getModifiedDate()));
		Assert.assertEquals(
			existingDLFileShortcut.getRepositoryId(),
			newDLFileShortcut.getRepositoryId());
		Assert.assertEquals(
			existingDLFileShortcut.getFolderId(),
			newDLFileShortcut.getFolderId());
		Assert.assertEquals(
			existingDLFileShortcut.getToFileEntryId(),
			newDLFileShortcut.getToFileEntryId());
		Assert.assertEquals(
			existingDLFileShortcut.getTreePath(),
			newDLFileShortcut.getTreePath());
		Assert.assertEquals(
			existingDLFileShortcut.isActive(), newDLFileShortcut.isActive());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileShortcut.getLastPublishDate()),
			Time.getShortTimestamp(newDLFileShortcut.getLastPublishDate()));
		Assert.assertEquals(
			existingDLFileShortcut.getStatus(), newDLFileShortcut.getStatus());
		Assert.assertEquals(
			existingDLFileShortcut.getStatusByUserId(),
			newDLFileShortcut.getStatusByUserId());
		Assert.assertEquals(
			existingDLFileShortcut.getStatusByUserName(),
			newDLFileShortcut.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileShortcut.getStatusDate()),
			Time.getShortTimestamp(newDLFileShortcut.getStatusDate()));
	}

	@Test(
		expected = DuplicateDLFileShortcutExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DLFileShortcut dlFileShortcut = addDLFileShortcut();

		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		newDLFileShortcut.setGroupId(dlFileShortcut.getGroupId());

		newDLFileShortcut = _persistence.update(newDLFileShortcut);

		Session session = _persistence.getCurrentSession();

		session.evict(newDLFileShortcut);

		newDLFileShortcut.setExternalReferenceCode(
			dlFileShortcut.getExternalReferenceCode());

		_persistence.update(newDLFileShortcut);
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
	public void testCountByToFileEntryId() throws Exception {
		_persistence.countByToFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByToFileEntryId(0L);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_F(0L, 0L);
	}

	@Test
	public void testCountByC_NotS() throws Exception {
		_persistence.countByC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotS(0L, 0);
	}

	@Test
	public void testCountByG_F_A() throws Exception {
		_persistence.countByG_F_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_F_A(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_F_A_S() throws Exception {
		_persistence.countByG_F_A_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_F_A_S(0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		DLFileShortcut existingDLFileShortcut = _persistence.findByPrimaryKey(
			newDLFileShortcut.getPrimaryKey());

		Assert.assertEquals(existingDLFileShortcut, newDLFileShortcut);
	}

	@Test(expected = NoSuchFileShortcutException.class)
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

	protected OrderByComparator<DLFileShortcut> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFileShortcut", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "fileShortcutId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "repositoryId",
			true, "folderId", true, "toFileEntryId", true, "treePath", true,
			"active", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		DLFileShortcut existingDLFileShortcut = _persistence.fetchByPrimaryKey(
			newDLFileShortcut.getPrimaryKey());

		Assert.assertEquals(existingDLFileShortcut, newDLFileShortcut);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileShortcut missingDLFileShortcut = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDLFileShortcut);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFileShortcut newDLFileShortcut1 = addDLFileShortcut();
		DLFileShortcut newDLFileShortcut2 = addDLFileShortcut();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileShortcut1.getPrimaryKey());
		primaryKeys.add(newDLFileShortcut2.getPrimaryKey());

		Map<Serializable, DLFileShortcut> dlFileShortcuts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlFileShortcuts.size());
		Assert.assertEquals(
			newDLFileShortcut1,
			dlFileShortcuts.get(newDLFileShortcut1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFileShortcut2,
			dlFileShortcuts.get(newDLFileShortcut2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFileShortcut> dlFileShortcuts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileShortcuts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileShortcut.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFileShortcut> dlFileShortcuts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileShortcuts.size());
		Assert.assertEquals(
			newDLFileShortcut,
			dlFileShortcuts.get(newDLFileShortcut.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFileShortcut> dlFileShortcuts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileShortcuts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileShortcut.getPrimaryKey());

		Map<Serializable, DLFileShortcut> dlFileShortcuts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileShortcuts.size());
		Assert.assertEquals(
			newDLFileShortcut,
			dlFileShortcuts.get(newDLFileShortcut.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFileShortcut newDLFileShortcut = addDLFileShortcut();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLFileShortcut.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLFileShortcut dlFileShortcut) {
		Assert.assertEquals(
			dlFileShortcut.getUuid(),
			ReflectionTestUtil.invoke(
				dlFileShortcut, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(dlFileShortcut.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileShortcut, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			dlFileShortcut.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				dlFileShortcut, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(dlFileShortcut.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileShortcut, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DLFileShortcut addDLFileShortcut() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileShortcut dlFileShortcut = _persistence.create(pk);

		dlFileShortcut.setMvccVersion(RandomTestUtil.nextLong());

		dlFileShortcut.setCtCollectionId(RandomTestUtil.nextLong());

		dlFileShortcut.setUuid(RandomTestUtil.randomString());

		dlFileShortcut.setExternalReferenceCode(RandomTestUtil.randomString());

		dlFileShortcut.setGroupId(RandomTestUtil.nextLong());

		dlFileShortcut.setCompanyId(RandomTestUtil.nextLong());

		dlFileShortcut.setUserId(RandomTestUtil.nextLong());

		dlFileShortcut.setUserName(RandomTestUtil.randomString());

		dlFileShortcut.setCreateDate(RandomTestUtil.nextDate());

		dlFileShortcut.setModifiedDate(RandomTestUtil.nextDate());

		dlFileShortcut.setRepositoryId(RandomTestUtil.nextLong());

		dlFileShortcut.setFolderId(RandomTestUtil.nextLong());

		dlFileShortcut.setToFileEntryId(RandomTestUtil.nextLong());

		dlFileShortcut.setTreePath(RandomTestUtil.randomString());

		dlFileShortcut.setActive(RandomTestUtil.randomBoolean());

		dlFileShortcut.setLastPublishDate(RandomTestUtil.nextDate());

		dlFileShortcut.setStatus(RandomTestUtil.nextInt());

		dlFileShortcut.setStatusByUserId(RandomTestUtil.nextLong());

		dlFileShortcut.setStatusByUserName(RandomTestUtil.randomString());

		dlFileShortcut.setStatusDate(RandomTestUtil.nextDate());

		_dlFileShortcuts.add(_persistence.update(dlFileShortcut));

		return dlFileShortcut;
	}

	private List<DLFileShortcut> _dlFileShortcuts =
		new ArrayList<DLFileShortcut>();
	private DLFileShortcutPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}