/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionUtil;
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
public class DLFileVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFileVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFileVersion> iterator = _dlFileVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileVersion dlFileVersion = _persistence.create(pk);

		Assert.assertNotNull(dlFileVersion);

		Assert.assertEquals(dlFileVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFileVersion newDLFileVersion = addDLFileVersion();

		_persistence.remove(newDLFileVersion);

		DLFileVersion existingDLFileVersion = _persistence.fetchByPrimaryKey(
			newDLFileVersion.getPrimaryKey());

		Assert.assertNull(existingDLFileVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFileVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileVersion newDLFileVersion = _persistence.create(pk);

		newDLFileVersion.setMvccVersion(RandomTestUtil.nextLong());

		newDLFileVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFileVersion.setUuid(RandomTestUtil.randomString());

		newDLFileVersion.setGroupId(RandomTestUtil.nextLong());

		newDLFileVersion.setCompanyId(RandomTestUtil.nextLong());

		newDLFileVersion.setUserId(RandomTestUtil.nextLong());

		newDLFileVersion.setUserName(RandomTestUtil.randomString());

		newDLFileVersion.setCreateDate(RandomTestUtil.nextDate());

		newDLFileVersion.setModifiedDate(RandomTestUtil.nextDate());

		newDLFileVersion.setRepositoryId(RandomTestUtil.nextLong());

		newDLFileVersion.setFolderId(RandomTestUtil.nextLong());

		newDLFileVersion.setFileEntryId(RandomTestUtil.nextLong());

		newDLFileVersion.setTreePath(RandomTestUtil.randomString());

		newDLFileVersion.setFileName(RandomTestUtil.randomString());

		newDLFileVersion.setExtension(RandomTestUtil.randomString());

		newDLFileVersion.setMimeType(RandomTestUtil.randomString());

		newDLFileVersion.setTitle(RandomTestUtil.randomString());

		newDLFileVersion.setDescription(RandomTestUtil.randomString());

		newDLFileVersion.setChangeLog(RandomTestUtil.randomString());

		newDLFileVersion.setExtraSettings(RandomTestUtil.randomString());

		newDLFileVersion.setFileEntryTypeId(RandomTestUtil.nextLong());

		newDLFileVersion.setVersion(RandomTestUtil.randomString());

		newDLFileVersion.setSize(RandomTestUtil.nextLong());

		newDLFileVersion.setChecksum(RandomTestUtil.randomString());

		newDLFileVersion.setStoreUUID(RandomTestUtil.randomString());

		newDLFileVersion.setDisplayDate(RandomTestUtil.nextDate());

		newDLFileVersion.setExpirationDate(RandomTestUtil.nextDate());

		newDLFileVersion.setReviewDate(RandomTestUtil.nextDate());

		newDLFileVersion.setLastPublishDate(RandomTestUtil.nextDate());

		newDLFileVersion.setStatus(RandomTestUtil.nextInt());

		newDLFileVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newDLFileVersion.setStatusByUserName(RandomTestUtil.randomString());

		newDLFileVersion.setStatusDate(RandomTestUtil.nextDate());

		_dlFileVersions.add(_persistence.update(newDLFileVersion));

		DLFileVersion existingDLFileVersion = _persistence.findByPrimaryKey(
			newDLFileVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileVersion.getMvccVersion(),
			newDLFileVersion.getMvccVersion());
		Assert.assertEquals(
			existingDLFileVersion.getCtCollectionId(),
			newDLFileVersion.getCtCollectionId());
		Assert.assertEquals(
			existingDLFileVersion.getUuid(), newDLFileVersion.getUuid());
		Assert.assertEquals(
			existingDLFileVersion.getFileVersionId(),
			newDLFileVersion.getFileVersionId());
		Assert.assertEquals(
			existingDLFileVersion.getGroupId(), newDLFileVersion.getGroupId());
		Assert.assertEquals(
			existingDLFileVersion.getCompanyId(),
			newDLFileVersion.getCompanyId());
		Assert.assertEquals(
			existingDLFileVersion.getUserId(), newDLFileVersion.getUserId());
		Assert.assertEquals(
			existingDLFileVersion.getUserName(),
			newDLFileVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getCreateDate()),
			Time.getShortTimestamp(newDLFileVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getModifiedDate()),
			Time.getShortTimestamp(newDLFileVersion.getModifiedDate()));
		Assert.assertEquals(
			existingDLFileVersion.getRepositoryId(),
			newDLFileVersion.getRepositoryId());
		Assert.assertEquals(
			existingDLFileVersion.getFolderId(),
			newDLFileVersion.getFolderId());
		Assert.assertEquals(
			existingDLFileVersion.getFileEntryId(),
			newDLFileVersion.getFileEntryId());
		Assert.assertEquals(
			existingDLFileVersion.getTreePath(),
			newDLFileVersion.getTreePath());
		Assert.assertEquals(
			existingDLFileVersion.getFileName(),
			newDLFileVersion.getFileName());
		Assert.assertEquals(
			existingDLFileVersion.getExtension(),
			newDLFileVersion.getExtension());
		Assert.assertEquals(
			existingDLFileVersion.getMimeType(),
			newDLFileVersion.getMimeType());
		Assert.assertEquals(
			existingDLFileVersion.getTitle(), newDLFileVersion.getTitle());
		Assert.assertEquals(
			existingDLFileVersion.getDescription(),
			newDLFileVersion.getDescription());
		Assert.assertEquals(
			existingDLFileVersion.getChangeLog(),
			newDLFileVersion.getChangeLog());
		Assert.assertEquals(
			existingDLFileVersion.getExtraSettings(),
			newDLFileVersion.getExtraSettings());
		Assert.assertEquals(
			existingDLFileVersion.getFileEntryTypeId(),
			newDLFileVersion.getFileEntryTypeId());
		Assert.assertEquals(
			existingDLFileVersion.getVersion(), newDLFileVersion.getVersion());
		Assert.assertEquals(
			existingDLFileVersion.getSize(), newDLFileVersion.getSize());
		Assert.assertEquals(
			existingDLFileVersion.getChecksum(),
			newDLFileVersion.getChecksum());
		Assert.assertEquals(
			existingDLFileVersion.getStoreUUID(),
			newDLFileVersion.getStoreUUID());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getDisplayDate()),
			Time.getShortTimestamp(newDLFileVersion.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getExpirationDate()),
			Time.getShortTimestamp(newDLFileVersion.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getReviewDate()),
			Time.getShortTimestamp(newDLFileVersion.getReviewDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getLastPublishDate()),
			Time.getShortTimestamp(newDLFileVersion.getLastPublishDate()));
		Assert.assertEquals(
			existingDLFileVersion.getStatus(), newDLFileVersion.getStatus());
		Assert.assertEquals(
			existingDLFileVersion.getStatusByUserId(),
			newDLFileVersion.getStatusByUserId());
		Assert.assertEquals(
			existingDLFileVersion.getStatusByUserName(),
			newDLFileVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileVersion.getStatusDate()),
			Time.getShortTimestamp(newDLFileVersion.getStatusDate()));
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
	public void testCountByFileEntryId() throws Exception {
		_persistence.countByFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryId(0L);
	}

	@Test
	public void testCountByMimeType() throws Exception {
		_persistence.countByMimeType("");

		_persistence.countByMimeType("null");

		_persistence.countByMimeType((String)null);
	}

	@Test
	public void testCountByC_SU() throws Exception {
		_persistence.countByC_SU(RandomTestUtil.nextLong(), "");

		_persistence.countByC_SU(0L, "null");

		_persistence.countByC_SU(0L, (String)null);
	}

	@Test
	public void testCountByC_NotS() throws Exception {
		_persistence.countByC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotS(0L, 0);
	}

	@Test
	public void testCountByF_V() throws Exception {
		_persistence.countByF_V(RandomTestUtil.nextLong(), "");

		_persistence.countByF_V(0L, "null");

		_persistence.countByF_V(0L, (String)null);
	}

	@Test
	public void testCountByF_S() throws Exception {
		_persistence.countByF_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByF_S(0L, 0);
	}

	@Test
	public void testCountByF_SArrayable() throws Exception {
		_persistence.countByF_S(
			RandomTestUtil.nextLong(), new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByG_F_S() throws Exception {
		_persistence.countByG_F_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_F_S(0L, 0L, 0);
	}

	@Test
	public void testCountByC_E_S() throws Exception {
		_persistence.countByC_E_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextInt());

		_persistence.countByC_E_S(0L, RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_E_SArrayable() throws Exception {
		_persistence.countByC_E_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_F_T_V() throws Exception {
		_persistence.countByG_F_T_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_F_T_V(0L, 0L, "null", "null");

		_persistence.countByG_F_T_V(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFileVersion newDLFileVersion = addDLFileVersion();

		DLFileVersion existingDLFileVersion = _persistence.findByPrimaryKey(
			newDLFileVersion.getPrimaryKey());

		Assert.assertEquals(existingDLFileVersion, newDLFileVersion);
	}

	@Test(expected = NoSuchFileVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DLFileVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFileVersion", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "fileVersionId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "repositoryId", true, "folderId", true,
			"fileEntryId", true, "treePath", true, "fileName", true,
			"extension", true, "mimeType", true, "title", true, "description",
			true, "changeLog", true, "fileEntryTypeId", true, "version", true,
			"size", true, "checksum", true, "storeUUID", true, "displayDate",
			true, "expirationDate", true, "reviewDate", true, "lastPublishDate",
			true, "status", true, "statusByUserId", true, "statusByUserName",
			true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFileVersion newDLFileVersion = addDLFileVersion();

		DLFileVersion existingDLFileVersion = _persistence.fetchByPrimaryKey(
			newDLFileVersion.getPrimaryKey());

		Assert.assertEquals(existingDLFileVersion, newDLFileVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileVersion missingDLFileVersion = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLFileVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFileVersion newDLFileVersion1 = addDLFileVersion();
		DLFileVersion newDLFileVersion2 = addDLFileVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileVersion1.getPrimaryKey());
		primaryKeys.add(newDLFileVersion2.getPrimaryKey());

		Map<Serializable, DLFileVersion> dlFileVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlFileVersions.size());
		Assert.assertEquals(
			newDLFileVersion1,
			dlFileVersions.get(newDLFileVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFileVersion2,
			dlFileVersions.get(newDLFileVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFileVersion> dlFileVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFileVersion newDLFileVersion = addDLFileVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFileVersion> dlFileVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileVersions.size());
		Assert.assertEquals(
			newDLFileVersion,
			dlFileVersions.get(newDLFileVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFileVersion> dlFileVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFileVersion newDLFileVersion = addDLFileVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileVersion.getPrimaryKey());

		Map<Serializable, DLFileVersion> dlFileVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileVersions.size());
		Assert.assertEquals(
			newDLFileVersion,
			dlFileVersions.get(newDLFileVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFileVersion newDLFileVersion = addDLFileVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLFileVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLFileVersion dlFileVersion) {
		Assert.assertEquals(
			dlFileVersion.getUuid(),
			ReflectionTestUtil.invoke(
				dlFileVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(dlFileVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(dlFileVersion.getFileEntryId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileEntryId"));
		Assert.assertEquals(
			dlFileVersion.getVersion(),
			ReflectionTestUtil.invoke(
				dlFileVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DLFileVersion addDLFileVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileVersion dlFileVersion = _persistence.create(pk);

		dlFileVersion.setMvccVersion(RandomTestUtil.nextLong());

		dlFileVersion.setCtCollectionId(RandomTestUtil.nextLong());

		dlFileVersion.setUuid(RandomTestUtil.randomString());

		dlFileVersion.setGroupId(RandomTestUtil.nextLong());

		dlFileVersion.setCompanyId(RandomTestUtil.nextLong());

		dlFileVersion.setUserId(RandomTestUtil.nextLong());

		dlFileVersion.setUserName(RandomTestUtil.randomString());

		dlFileVersion.setCreateDate(RandomTestUtil.nextDate());

		dlFileVersion.setModifiedDate(RandomTestUtil.nextDate());

		dlFileVersion.setRepositoryId(RandomTestUtil.nextLong());

		dlFileVersion.setFolderId(RandomTestUtil.nextLong());

		dlFileVersion.setFileEntryId(RandomTestUtil.nextLong());

		dlFileVersion.setTreePath(RandomTestUtil.randomString());

		dlFileVersion.setFileName(RandomTestUtil.randomString());

		dlFileVersion.setExtension(RandomTestUtil.randomString());

		dlFileVersion.setMimeType(RandomTestUtil.randomString());

		dlFileVersion.setTitle(RandomTestUtil.randomString());

		dlFileVersion.setDescription(RandomTestUtil.randomString());

		dlFileVersion.setChangeLog(RandomTestUtil.randomString());

		dlFileVersion.setExtraSettings(RandomTestUtil.randomString());

		dlFileVersion.setFileEntryTypeId(RandomTestUtil.nextLong());

		dlFileVersion.setVersion(RandomTestUtil.randomString());

		dlFileVersion.setSize(RandomTestUtil.nextLong());

		dlFileVersion.setChecksum(RandomTestUtil.randomString());

		dlFileVersion.setStoreUUID(RandomTestUtil.randomString());

		dlFileVersion.setDisplayDate(RandomTestUtil.nextDate());

		dlFileVersion.setExpirationDate(RandomTestUtil.nextDate());

		dlFileVersion.setReviewDate(RandomTestUtil.nextDate());

		dlFileVersion.setLastPublishDate(RandomTestUtil.nextDate());

		dlFileVersion.setStatus(RandomTestUtil.nextInt());

		dlFileVersion.setStatusByUserId(RandomTestUtil.nextLong());

		dlFileVersion.setStatusByUserName(RandomTestUtil.randomString());

		dlFileVersion.setStatusDate(RandomTestUtil.nextDate());

		_dlFileVersions.add(_persistence.update(dlFileVersion));

		return dlFileVersion;
	}

	private List<DLFileVersion> _dlFileVersions =
		new ArrayList<DLFileVersion>();
	private DLFileVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}