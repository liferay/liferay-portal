/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class DLFileEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFileEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFileEntry> iterator = _dlFileEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntry dlFileEntry = _persistence.create(pk);

		Assert.assertNotNull(dlFileEntry);

		Assert.assertEquals(dlFileEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		_persistence.remove(newDLFileEntry);

		DLFileEntry existingDLFileEntry = _persistence.fetchByPrimaryKey(
			newDLFileEntry.getPrimaryKey());

		Assert.assertNull(existingDLFileEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFileEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntry newDLFileEntry = _persistence.create(pk);

		newDLFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		newDLFileEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFileEntry.setUuid(RandomTestUtil.randomString());

		newDLFileEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		newDLFileEntry.setGroupId(RandomTestUtil.nextLong());

		newDLFileEntry.setCompanyId(RandomTestUtil.nextLong());

		newDLFileEntry.setUserId(RandomTestUtil.nextLong());

		newDLFileEntry.setUserName(RandomTestUtil.randomString());

		newDLFileEntry.setCreateDate(RandomTestUtil.nextDate());

		newDLFileEntry.setModifiedDate(RandomTestUtil.nextDate());

		newDLFileEntry.setClassNameId(RandomTestUtil.nextLong());

		newDLFileEntry.setClassPK(RandomTestUtil.nextLong());

		newDLFileEntry.setRepositoryId(RandomTestUtil.nextLong());

		newDLFileEntry.setFolderId(RandomTestUtil.nextLong());

		newDLFileEntry.setTreePath(RandomTestUtil.randomString());

		newDLFileEntry.setName(RandomTestUtil.randomString());

		newDLFileEntry.setFileName(RandomTestUtil.randomString());

		newDLFileEntry.setExtension(RandomTestUtil.randomString());

		newDLFileEntry.setMimeType(RandomTestUtil.randomString());

		newDLFileEntry.setTitle(RandomTestUtil.randomString());

		newDLFileEntry.setDescription(RandomTestUtil.randomString());

		newDLFileEntry.setExtraSettings(RandomTestUtil.randomString());

		newDLFileEntry.setFileEntryTypeId(RandomTestUtil.nextLong());

		newDLFileEntry.setVersion(RandomTestUtil.randomString());

		newDLFileEntry.setSize(RandomTestUtil.nextLong());

		newDLFileEntry.setSmallImageId(RandomTestUtil.nextLong());

		newDLFileEntry.setLargeImageId(RandomTestUtil.nextLong());

		newDLFileEntry.setCustom1ImageId(RandomTestUtil.nextLong());

		newDLFileEntry.setCustom2ImageId(RandomTestUtil.nextLong());

		newDLFileEntry.setManualCheckInRequired(RandomTestUtil.randomBoolean());

		newDLFileEntry.setDisplayDate(RandomTestUtil.nextDate());

		newDLFileEntry.setExpirationDate(RandomTestUtil.nextDate());

		newDLFileEntry.setReviewDate(RandomTestUtil.nextDate());

		newDLFileEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_dlFileEntries.add(_persistence.update(newDLFileEntry));

		DLFileEntry existingDLFileEntry = _persistence.findByPrimaryKey(
			newDLFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileEntry.getMvccVersion(),
			newDLFileEntry.getMvccVersion());
		Assert.assertEquals(
			existingDLFileEntry.getCtCollectionId(),
			newDLFileEntry.getCtCollectionId());
		Assert.assertEquals(
			existingDLFileEntry.getUuid(), newDLFileEntry.getUuid());
		Assert.assertEquals(
			existingDLFileEntry.getExternalReferenceCode(),
			newDLFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingDLFileEntry.getFileEntryId(),
			newDLFileEntry.getFileEntryId());
		Assert.assertEquals(
			existingDLFileEntry.getGroupId(), newDLFileEntry.getGroupId());
		Assert.assertEquals(
			existingDLFileEntry.getCompanyId(), newDLFileEntry.getCompanyId());
		Assert.assertEquals(
			existingDLFileEntry.getUserId(), newDLFileEntry.getUserId());
		Assert.assertEquals(
			existingDLFileEntry.getUserName(), newDLFileEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getCreateDate()),
			Time.getShortTimestamp(newDLFileEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getModifiedDate()),
			Time.getShortTimestamp(newDLFileEntry.getModifiedDate()));
		Assert.assertEquals(
			existingDLFileEntry.getClassNameId(),
			newDLFileEntry.getClassNameId());
		Assert.assertEquals(
			existingDLFileEntry.getClassPK(), newDLFileEntry.getClassPK());
		Assert.assertEquals(
			existingDLFileEntry.getRepositoryId(),
			newDLFileEntry.getRepositoryId());
		Assert.assertEquals(
			existingDLFileEntry.getFolderId(), newDLFileEntry.getFolderId());
		Assert.assertEquals(
			existingDLFileEntry.getTreePath(), newDLFileEntry.getTreePath());
		Assert.assertEquals(
			existingDLFileEntry.getName(), newDLFileEntry.getName());
		Assert.assertEquals(
			existingDLFileEntry.getFileName(), newDLFileEntry.getFileName());
		Assert.assertEquals(
			existingDLFileEntry.getExtension(), newDLFileEntry.getExtension());
		Assert.assertEquals(
			existingDLFileEntry.getMimeType(), newDLFileEntry.getMimeType());
		Assert.assertEquals(
			existingDLFileEntry.getTitle(), newDLFileEntry.getTitle());
		Assert.assertEquals(
			existingDLFileEntry.getDescription(),
			newDLFileEntry.getDescription());
		Assert.assertEquals(
			existingDLFileEntry.getExtraSettings(),
			newDLFileEntry.getExtraSettings());
		Assert.assertEquals(
			existingDLFileEntry.getFileEntryTypeId(),
			newDLFileEntry.getFileEntryTypeId());
		Assert.assertEquals(
			existingDLFileEntry.getVersion(), newDLFileEntry.getVersion());
		Assert.assertEquals(
			existingDLFileEntry.getSize(), newDLFileEntry.getSize());
		Assert.assertEquals(
			existingDLFileEntry.getSmallImageId(),
			newDLFileEntry.getSmallImageId());
		Assert.assertEquals(
			existingDLFileEntry.getLargeImageId(),
			newDLFileEntry.getLargeImageId());
		Assert.assertEquals(
			existingDLFileEntry.getCustom1ImageId(),
			newDLFileEntry.getCustom1ImageId());
		Assert.assertEquals(
			existingDLFileEntry.getCustom2ImageId(),
			newDLFileEntry.getCustom2ImageId());
		Assert.assertEquals(
			existingDLFileEntry.isManualCheckInRequired(),
			newDLFileEntry.isManualCheckInRequired());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getDisplayDate()),
			Time.getShortTimestamp(newDLFileEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getExpirationDate()),
			Time.getShortTimestamp(newDLFileEntry.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getReviewDate()),
			Time.getShortTimestamp(newDLFileEntry.getReviewDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntry.getLastPublishDate()),
			Time.getShortTimestamp(newDLFileEntry.getLastPublishDate()));
	}

	@Test(expected = DuplicateDLFileEntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DLFileEntry dlFileEntry = addDLFileEntry();

		DLFileEntry newDLFileEntry = addDLFileEntry();

		newDLFileEntry.setGroupId(dlFileEntry.getGroupId());

		newDLFileEntry = _persistence.update(newDLFileEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newDLFileEntry);

		newDLFileEntry.setExternalReferenceCode(
			dlFileEntry.getExternalReferenceCode());

		_persistence.update(newDLFileEntry);
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
	public void testCountByMimeType() throws Exception {
		_persistence.countByMimeType("");

		_persistence.countByMimeType("null");

		_persistence.countByMimeType((String)null);
	}

	@Test
	public void testCountByFileEntryTypeId() throws Exception {
		_persistence.countByFileEntryTypeId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryTypeId(0L);
	}

	@Test
	public void testCountBySmallImageId() throws Exception {
		_persistence.countBySmallImageId(RandomTestUtil.nextLong());

		_persistence.countBySmallImageId(0L);
	}

	@Test
	public void testCountByLargeImageId() throws Exception {
		_persistence.countByLargeImageId(RandomTestUtil.nextLong());

		_persistence.countByLargeImageId(0L);
	}

	@Test
	public void testCountByCustom1ImageId() throws Exception {
		_persistence.countByCustom1ImageId(RandomTestUtil.nextLong());

		_persistence.countByCustom1ImageId(0L);
	}

	@Test
	public void testCountByCustom2ImageId() throws Exception {
		_persistence.countByCustom2ImageId(RandomTestUtil.nextLong());

		_persistence.countByCustom2ImageId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
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
	public void testCountByR_F() throws Exception {
		_persistence.countByR_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByR_F(0L, 0L);
	}

	@Test
	public void testCountByF_N() throws Exception {
		_persistence.countByF_N(RandomTestUtil.nextLong(), "");

		_persistence.countByF_N(0L, "null");

		_persistence.countByF_N(0L, (String)null);
	}

	@Test
	public void testCountByG_U_F() throws Exception {
		_persistence.countByG_U_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_U_F(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_U_FArrayable() throws Exception {
		_persistence.countByG_U_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_F_N() throws Exception {
		_persistence.countByG_F_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_F_N(0L, 0L, "null");

		_persistence.countByG_F_N(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_F_FN() throws Exception {
		_persistence.countByG_F_FN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_F_FN(0L, 0L, "null");

		_persistence.countByG_F_FN(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_F_T() throws Exception {
		_persistence.countByG_F_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_F_T(0L, 0L, "null");

		_persistence.countByG_F_T(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_F_F() throws Exception {
		_persistence.countByG_F_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_F_F(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_F_FArrayable() throws Exception {
		_persistence.countByG_F_F(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByS_L_C1_C2() throws Exception {
		_persistence.countByS_L_C1_C2(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByS_L_C1_C2(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		DLFileEntry existingDLFileEntry = _persistence.findByPrimaryKey(
			newDLFileEntry.getPrimaryKey());

		Assert.assertEquals(existingDLFileEntry, newDLFileEntry);
	}

	@Test(expected = NoSuchFileEntryException.class)
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

	protected OrderByComparator<DLFileEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFileEntry", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "fileEntryId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "classNameId", true,
			"classPK", true, "repositoryId", true, "folderId", true, "treePath",
			true, "name", true, "fileName", true, "extension", true, "mimeType",
			true, "title", true, "description", true, "fileEntryTypeId", true,
			"version", true, "size", true, "smallImageId", true, "largeImageId",
			true, "custom1ImageId", true, "custom2ImageId", true,
			"manualCheckInRequired", true, "displayDate", true,
			"expirationDate", true, "reviewDate", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		DLFileEntry existingDLFileEntry = _persistence.fetchByPrimaryKey(
			newDLFileEntry.getPrimaryKey());

		Assert.assertEquals(existingDLFileEntry, newDLFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntry missingDLFileEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFileEntry newDLFileEntry1 = addDLFileEntry();
		DLFileEntry newDLFileEntry2 = addDLFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntry1.getPrimaryKey());
		primaryKeys.add(newDLFileEntry2.getPrimaryKey());

		Map<Serializable, DLFileEntry> dlFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlFileEntries.size());
		Assert.assertEquals(
			newDLFileEntry1,
			dlFileEntries.get(newDLFileEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFileEntry2,
			dlFileEntries.get(newDLFileEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFileEntry> dlFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFileEntry newDLFileEntry = addDLFileEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFileEntry> dlFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntries.size());
		Assert.assertEquals(
			newDLFileEntry, dlFileEntries.get(newDLFileEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFileEntry> dlFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntry.getPrimaryKey());

		Map<Serializable, DLFileEntry> dlFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntries.size());
		Assert.assertEquals(
			newDLFileEntry, dlFileEntries.get(newDLFileEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DLFileEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DLFileEntry>() {

				@Override
				public void performAction(DLFileEntry dlFileEntry) {
					Assert.assertNotNull(dlFileEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"fileEntryId", newDLFileEntry.getFileEntryId()));

		List<DLFileEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DLFileEntry existingDLFileEntry = result.get(0);

		Assert.assertEquals(existingDLFileEntry, newDLFileEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"fileEntryId", RandomTestUtil.nextLong()));

		List<DLFileEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("fileEntryId"));

		Object newFileEntryId = newDLFileEntry.getFileEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"fileEntryId", new Object[] {newFileEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingFileEntryId = result.get(0);

		Assert.assertEquals(existingFileEntryId, newFileEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("fileEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"fileEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFileEntry newDLFileEntry = addDLFileEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLFileEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		DLFileEntry newDLFileEntry = addDLFileEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"fileEntryId", newDLFileEntry.getFileEntryId()));

		List<DLFileEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DLFileEntry dlFileEntry) {
		Assert.assertEquals(
			dlFileEntry.getUuid(),
			ReflectionTestUtil.invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getFolderId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "folderId"));
		Assert.assertEquals(
			dlFileEntry.getName(),
			ReflectionTestUtil.invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getFolderId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "folderId"));
		Assert.assertEquals(
			dlFileEntry.getFileName(),
			ReflectionTestUtil.invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileName"));

		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getFolderId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "folderId"));
		Assert.assertEquals(
			dlFileEntry.getTitle(),
			ReflectionTestUtil.invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "title"));

		Assert.assertEquals(
			dlFileEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DLFileEntry addDLFileEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntry dlFileEntry = _persistence.create(pk);

		dlFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		dlFileEntry.setCtCollectionId(RandomTestUtil.nextLong());

		dlFileEntry.setUuid(RandomTestUtil.randomString());

		dlFileEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		dlFileEntry.setGroupId(RandomTestUtil.nextLong());

		dlFileEntry.setCompanyId(RandomTestUtil.nextLong());

		dlFileEntry.setUserId(RandomTestUtil.nextLong());

		dlFileEntry.setUserName(RandomTestUtil.randomString());

		dlFileEntry.setCreateDate(RandomTestUtil.nextDate());

		dlFileEntry.setModifiedDate(RandomTestUtil.nextDate());

		dlFileEntry.setClassNameId(RandomTestUtil.nextLong());

		dlFileEntry.setClassPK(RandomTestUtil.nextLong());

		dlFileEntry.setRepositoryId(RandomTestUtil.nextLong());

		dlFileEntry.setFolderId(RandomTestUtil.nextLong());

		dlFileEntry.setTreePath(RandomTestUtil.randomString());

		dlFileEntry.setName(RandomTestUtil.randomString());

		dlFileEntry.setFileName(RandomTestUtil.randomString());

		dlFileEntry.setExtension(RandomTestUtil.randomString());

		dlFileEntry.setMimeType(RandomTestUtil.randomString());

		dlFileEntry.setTitle(RandomTestUtil.randomString());

		dlFileEntry.setDescription(RandomTestUtil.randomString());

		dlFileEntry.setExtraSettings(RandomTestUtil.randomString());

		dlFileEntry.setFileEntryTypeId(RandomTestUtil.nextLong());

		dlFileEntry.setVersion(RandomTestUtil.randomString());

		dlFileEntry.setSize(RandomTestUtil.nextLong());

		dlFileEntry.setSmallImageId(RandomTestUtil.nextLong());

		dlFileEntry.setLargeImageId(RandomTestUtil.nextLong());

		dlFileEntry.setCustom1ImageId(RandomTestUtil.nextLong());

		dlFileEntry.setCustom2ImageId(RandomTestUtil.nextLong());

		dlFileEntry.setManualCheckInRequired(RandomTestUtil.randomBoolean());

		dlFileEntry.setDisplayDate(RandomTestUtil.nextDate());

		dlFileEntry.setExpirationDate(RandomTestUtil.nextDate());

		dlFileEntry.setReviewDate(RandomTestUtil.nextDate());

		dlFileEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_dlFileEntries.add(_persistence.update(dlFileEntry));

		return dlFileEntry;
	}

	private List<DLFileEntry> _dlFileEntries = new ArrayList<DLFileEntry>();
	private DLFileEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}