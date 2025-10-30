/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.DuplicateDLFolderExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderUtil;
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
public class DLFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFolder> iterator = _dlFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFolder dlFolder = _persistence.create(pk);

		Assert.assertNotNull(dlFolder);

		Assert.assertEquals(dlFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFolder newDLFolder = addDLFolder();

		_persistence.remove(newDLFolder);

		DLFolder existingDLFolder = _persistence.fetchByPrimaryKey(
			newDLFolder.getPrimaryKey());

		Assert.assertNull(existingDLFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFolder newDLFolder = _persistence.create(pk);

		newDLFolder.setMvccVersion(RandomTestUtil.nextLong());

		newDLFolder.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFolder.setUuid(RandomTestUtil.randomString());

		newDLFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		newDLFolder.setGroupId(RandomTestUtil.nextLong());

		newDLFolder.setCompanyId(RandomTestUtil.nextLong());

		newDLFolder.setUserId(RandomTestUtil.nextLong());

		newDLFolder.setUserName(RandomTestUtil.randomString());

		newDLFolder.setCreateDate(RandomTestUtil.nextDate());

		newDLFolder.setModifiedDate(RandomTestUtil.nextDate());

		newDLFolder.setRepositoryId(RandomTestUtil.nextLong());

		newDLFolder.setMountPoint(RandomTestUtil.randomBoolean());

		newDLFolder.setParentFolderId(RandomTestUtil.nextLong());

		newDLFolder.setTreePath(RandomTestUtil.randomString());

		newDLFolder.setName(RandomTestUtil.randomString());

		newDLFolder.setDescription(RandomTestUtil.randomString());

		newDLFolder.setLastPostDate(RandomTestUtil.nextDate());

		newDLFolder.setDefaultFileEntryTypeId(RandomTestUtil.nextLong());

		newDLFolder.setHidden(RandomTestUtil.randomBoolean());

		newDLFolder.setRestrictionType(RandomTestUtil.nextInt());

		newDLFolder.setLastPublishDate(RandomTestUtil.nextDate());

		newDLFolder.setStatus(RandomTestUtil.nextInt());

		newDLFolder.setStatusByUserId(RandomTestUtil.nextLong());

		newDLFolder.setStatusByUserName(RandomTestUtil.randomString());

		newDLFolder.setStatusDate(RandomTestUtil.nextDate());

		_dlFolders.add(_persistence.update(newDLFolder));

		DLFolder existingDLFolder = _persistence.findByPrimaryKey(
			newDLFolder.getPrimaryKey());

		Assert.assertEquals(
			existingDLFolder.getMvccVersion(), newDLFolder.getMvccVersion());
		Assert.assertEquals(
			existingDLFolder.getCtCollectionId(),
			newDLFolder.getCtCollectionId());
		Assert.assertEquals(existingDLFolder.getUuid(), newDLFolder.getUuid());
		Assert.assertEquals(
			existingDLFolder.getExternalReferenceCode(),
			newDLFolder.getExternalReferenceCode());
		Assert.assertEquals(
			existingDLFolder.getFolderId(), newDLFolder.getFolderId());
		Assert.assertEquals(
			existingDLFolder.getGroupId(), newDLFolder.getGroupId());
		Assert.assertEquals(
			existingDLFolder.getCompanyId(), newDLFolder.getCompanyId());
		Assert.assertEquals(
			existingDLFolder.getUserId(), newDLFolder.getUserId());
		Assert.assertEquals(
			existingDLFolder.getUserName(), newDLFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFolder.getCreateDate()),
			Time.getShortTimestamp(newDLFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFolder.getModifiedDate()),
			Time.getShortTimestamp(newDLFolder.getModifiedDate()));
		Assert.assertEquals(
			existingDLFolder.getRepositoryId(), newDLFolder.getRepositoryId());
		Assert.assertEquals(
			existingDLFolder.isMountPoint(), newDLFolder.isMountPoint());
		Assert.assertEquals(
			existingDLFolder.getParentFolderId(),
			newDLFolder.getParentFolderId());
		Assert.assertEquals(
			existingDLFolder.getTreePath(), newDLFolder.getTreePath());
		Assert.assertEquals(existingDLFolder.getName(), newDLFolder.getName());
		Assert.assertEquals(
			existingDLFolder.getDescription(), newDLFolder.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFolder.getLastPostDate()),
			Time.getShortTimestamp(newDLFolder.getLastPostDate()));
		Assert.assertEquals(
			existingDLFolder.getDefaultFileEntryTypeId(),
			newDLFolder.getDefaultFileEntryTypeId());
		Assert.assertEquals(
			existingDLFolder.isHidden(), newDLFolder.isHidden());
		Assert.assertEquals(
			existingDLFolder.getRestrictionType(),
			newDLFolder.getRestrictionType());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFolder.getLastPublishDate()),
			Time.getShortTimestamp(newDLFolder.getLastPublishDate()));
		Assert.assertEquals(
			existingDLFolder.getStatus(), newDLFolder.getStatus());
		Assert.assertEquals(
			existingDLFolder.getStatusByUserId(),
			newDLFolder.getStatusByUserId());
		Assert.assertEquals(
			existingDLFolder.getStatusByUserName(),
			newDLFolder.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFolder.getStatusDate()),
			Time.getShortTimestamp(newDLFolder.getStatusDate()));
	}

	@Test(expected = DuplicateDLFolderExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DLFolder dlFolder = addDLFolder();

		DLFolder newDLFolder = addDLFolder();

		newDLFolder.setGroupId(dlFolder.getGroupId());

		newDLFolder = _persistence.update(newDLFolder);

		Session session = _persistence.getCurrentSession();

		session.evict(newDLFolder);

		newDLFolder.setExternalReferenceCode(
			dlFolder.getExternalReferenceCode());

		_persistence.update(newDLFolder);
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
	public void testCountByRepositoryId() throws Exception {
		_persistence.countByRepositoryId(RandomTestUtil.nextLong());

		_persistence.countByRepositoryId(0L);
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
	public void testCountByR_M() throws Exception {
		_persistence.countByR_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByR_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_P() throws Exception {
		_persistence.countByR_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByR_P(0L, 0L);
	}

	@Test
	public void testCountByP_N() throws Exception {
		_persistence.countByP_N(RandomTestUtil.nextLong(), "");

		_persistence.countByP_N(0L, "null");

		_persistence.countByP_N(0L, (String)null);
	}

	@Test
	public void testCountByGtF_C_P() throws Exception {
		_persistence.countByGtF_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByGtF_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_M_P() throws Exception {
		_persistence.countByG_M_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByG_M_P(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByG_P_N() throws Exception {
		_persistence.countByG_P_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_P_N(0L, 0L, "null");

		_persistence.countByG_P_N(0L, 0L, (String)null);
	}

	@Test
	public void testCountByGtF_C_P_NotS() throws Exception {
		_persistence.countByGtF_C_P_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByGtF_C_P_NotS(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByG_M_P_H() throws Exception {
		_persistence.countByG_M_P_H(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_M_P_H(
			0L, RandomTestUtil.randomBoolean(), 0L,
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_M_LikeT_H() throws Exception {
		_persistence.countByG_M_LikeT_H(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_M_LikeT_H(
			0L, RandomTestUtil.randomBoolean(), "null",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_M_LikeT_H(
			0L, RandomTestUtil.randomBoolean(), (String)null,
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_H_S() throws Exception {
		_persistence.countByG_P_H_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_P_H_S(0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_M_P_H_S() throws Exception {
		_persistence.countByG_M_P_H_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_M_P_H_S(
			0L, RandomTestUtil.randomBoolean(), 0L,
			RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_M_LikeT_H_NotS() throws Exception {
		_persistence.countByG_M_LikeT_H_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_M_LikeT_H_NotS(
			0L, RandomTestUtil.randomBoolean(), "null",
			RandomTestUtil.randomBoolean(), 0);

		_persistence.countByG_M_LikeT_H_NotS(
			0L, RandomTestUtil.randomBoolean(), (String)null,
			RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFolder newDLFolder = addDLFolder();

		DLFolder existingDLFolder = _persistence.findByPrimaryKey(
			newDLFolder.getPrimaryKey());

		Assert.assertEquals(existingDLFolder, newDLFolder);
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

	protected OrderByComparator<DLFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFolder", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "folderId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "repositoryId", true,
			"mountPoint", true, "parentFolderId", true, "treePath", true,
			"name", true, "description", true, "lastPostDate", true,
			"defaultFileEntryTypeId", true, "hidden", true, "restrictionType",
			true, "lastPublishDate", true, "status", true, "statusByUserId",
			true, "statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFolder newDLFolder = addDLFolder();

		DLFolder existingDLFolder = _persistence.fetchByPrimaryKey(
			newDLFolder.getPrimaryKey());

		Assert.assertEquals(existingDLFolder, newDLFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFolder missingDLFolder = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFolder newDLFolder1 = addDLFolder();
		DLFolder newDLFolder2 = addDLFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFolder1.getPrimaryKey());
		primaryKeys.add(newDLFolder2.getPrimaryKey());

		Map<Serializable, DLFolder> dlFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, dlFolders.size());
		Assert.assertEquals(
			newDLFolder1, dlFolders.get(newDLFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFolder2, dlFolders.get(newDLFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFolder> dlFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(dlFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFolder newDLFolder = addDLFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFolder> dlFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, dlFolders.size());
		Assert.assertEquals(
			newDLFolder, dlFolders.get(newDLFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFolder> dlFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(dlFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFolder newDLFolder = addDLFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFolder.getPrimaryKey());

		Map<Serializable, DLFolder> dlFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, dlFolders.size());
		Assert.assertEquals(
			newDLFolder, dlFolders.get(newDLFolder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFolder newDLFolder = addDLFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLFolder.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLFolder dlFolder) {
		Assert.assertEquals(
			dlFolder.getUuid(),
			ReflectionTestUtil.invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(dlFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(dlFolder.getRepositoryId()),
			ReflectionTestUtil.<Long>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "repositoryId"));
		Assert.assertEquals(
			Boolean.valueOf(dlFolder.getMountPoint()),
			ReflectionTestUtil.<Boolean>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "mountPoint"));

		Assert.assertEquals(
			Long.valueOf(dlFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(dlFolder.getParentFolderId()),
			ReflectionTestUtil.<Long>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentFolderId"));
		Assert.assertEquals(
			dlFolder.getName(),
			ReflectionTestUtil.invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			dlFolder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(dlFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DLFolder addDLFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFolder dlFolder = _persistence.create(pk);

		dlFolder.setMvccVersion(RandomTestUtil.nextLong());

		dlFolder.setCtCollectionId(RandomTestUtil.nextLong());

		dlFolder.setUuid(RandomTestUtil.randomString());

		dlFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		dlFolder.setGroupId(RandomTestUtil.nextLong());

		dlFolder.setCompanyId(RandomTestUtil.nextLong());

		dlFolder.setUserId(RandomTestUtil.nextLong());

		dlFolder.setUserName(RandomTestUtil.randomString());

		dlFolder.setCreateDate(RandomTestUtil.nextDate());

		dlFolder.setModifiedDate(RandomTestUtil.nextDate());

		dlFolder.setRepositoryId(RandomTestUtil.nextLong());

		dlFolder.setMountPoint(RandomTestUtil.randomBoolean());

		dlFolder.setParentFolderId(RandomTestUtil.nextLong());

		dlFolder.setTreePath(RandomTestUtil.randomString());

		dlFolder.setName(RandomTestUtil.randomString());

		dlFolder.setDescription(RandomTestUtil.randomString());

		dlFolder.setLastPostDate(RandomTestUtil.nextDate());

		dlFolder.setDefaultFileEntryTypeId(RandomTestUtil.nextLong());

		dlFolder.setHidden(RandomTestUtil.randomBoolean());

		dlFolder.setRestrictionType(RandomTestUtil.nextInt());

		dlFolder.setLastPublishDate(RandomTestUtil.nextDate());

		dlFolder.setStatus(RandomTestUtil.nextInt());

		dlFolder.setStatusByUserId(RandomTestUtil.nextLong());

		dlFolder.setStatusByUserName(RandomTestUtil.randomString());

		dlFolder.setStatusDate(RandomTestUtil.nextDate());

		_dlFolders.add(_persistence.update(dlFolder));

		return dlFolder;
	}

	private List<DLFolder> _dlFolders = new ArrayList<DLFolder>();
	private DLFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}