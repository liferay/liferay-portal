/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.DuplicateJournalFolderExternalReferenceCodeException;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.persistence.JournalFolderPersistence;
import com.liferay.journal.service.persistence.JournalFolderUtil;
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
public class JournalFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalFolder> iterator = _journalFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFolder journalFolder = _persistence.create(pk);

		Assert.assertNotNull(journalFolder);

		Assert.assertEquals(journalFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalFolder newJournalFolder = addJournalFolder();

		_persistence.remove(newJournalFolder);

		JournalFolder existingJournalFolder = _persistence.fetchByPrimaryKey(
			newJournalFolder.getPrimaryKey());

		Assert.assertNull(existingJournalFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFolder newJournalFolder = _persistence.create(pk);

		newJournalFolder.setMvccVersion(RandomTestUtil.nextLong());

		newJournalFolder.setCtCollectionId(RandomTestUtil.nextLong());

		newJournalFolder.setUuid(RandomTestUtil.randomString());

		newJournalFolder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newJournalFolder.setGroupId(RandomTestUtil.nextLong());

		newJournalFolder.setCompanyId(RandomTestUtil.nextLong());

		newJournalFolder.setUserId(RandomTestUtil.nextLong());

		newJournalFolder.setUserName(RandomTestUtil.randomString());

		newJournalFolder.setCreateDate(RandomTestUtil.nextDate());

		newJournalFolder.setModifiedDate(RandomTestUtil.nextDate());

		newJournalFolder.setParentFolderId(RandomTestUtil.nextLong());

		newJournalFolder.setTreePath(RandomTestUtil.randomString());

		newJournalFolder.setName(RandomTestUtil.randomString());

		newJournalFolder.setDescription(RandomTestUtil.randomString());

		newJournalFolder.setRestrictionType(RandomTestUtil.nextInt());

		newJournalFolder.setLastPublishDate(RandomTestUtil.nextDate());

		newJournalFolder.setStatus(RandomTestUtil.nextInt());

		newJournalFolder.setStatusByUserId(RandomTestUtil.nextLong());

		newJournalFolder.setStatusByUserName(RandomTestUtil.randomString());

		newJournalFolder.setStatusDate(RandomTestUtil.nextDate());

		_journalFolders.add(_persistence.update(newJournalFolder));

		JournalFolder existingJournalFolder = _persistence.findByPrimaryKey(
			newJournalFolder.getPrimaryKey());

		Assert.assertEquals(
			existingJournalFolder.getMvccVersion(),
			newJournalFolder.getMvccVersion());
		Assert.assertEquals(
			existingJournalFolder.getCtCollectionId(),
			newJournalFolder.getCtCollectionId());
		Assert.assertEquals(
			existingJournalFolder.getUuid(), newJournalFolder.getUuid());
		Assert.assertEquals(
			existingJournalFolder.getExternalReferenceCode(),
			newJournalFolder.getExternalReferenceCode());
		Assert.assertEquals(
			existingJournalFolder.getFolderId(),
			newJournalFolder.getFolderId());
		Assert.assertEquals(
			existingJournalFolder.getGroupId(), newJournalFolder.getGroupId());
		Assert.assertEquals(
			existingJournalFolder.getCompanyId(),
			newJournalFolder.getCompanyId());
		Assert.assertEquals(
			existingJournalFolder.getUserId(), newJournalFolder.getUserId());
		Assert.assertEquals(
			existingJournalFolder.getUserName(),
			newJournalFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFolder.getCreateDate()),
			Time.getShortTimestamp(newJournalFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFolder.getModifiedDate()),
			Time.getShortTimestamp(newJournalFolder.getModifiedDate()));
		Assert.assertEquals(
			existingJournalFolder.getParentFolderId(),
			newJournalFolder.getParentFolderId());
		Assert.assertEquals(
			existingJournalFolder.getTreePath(),
			newJournalFolder.getTreePath());
		Assert.assertEquals(
			existingJournalFolder.getName(), newJournalFolder.getName());
		Assert.assertEquals(
			existingJournalFolder.getDescription(),
			newJournalFolder.getDescription());
		Assert.assertEquals(
			existingJournalFolder.getRestrictionType(),
			newJournalFolder.getRestrictionType());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFolder.getLastPublishDate()),
			Time.getShortTimestamp(newJournalFolder.getLastPublishDate()));
		Assert.assertEquals(
			existingJournalFolder.getStatus(), newJournalFolder.getStatus());
		Assert.assertEquals(
			existingJournalFolder.getStatusByUserId(),
			newJournalFolder.getStatusByUserId());
		Assert.assertEquals(
			existingJournalFolder.getStatusByUserName(),
			newJournalFolder.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFolder.getStatusDate()),
			Time.getShortTimestamp(newJournalFolder.getStatusDate()));
	}

	@Test(expected = DuplicateJournalFolderExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		JournalFolder journalFolder = addJournalFolder();

		JournalFolder newJournalFolder = addJournalFolder();

		newJournalFolder.setGroupId(journalFolder.getGroupId());

		newJournalFolder = _persistence.update(newJournalFolder);

		Session session = _persistence.getCurrentSession();

		session.evict(newJournalFolder);

		newJournalFolder.setExternalReferenceCode(
			journalFolder.getExternalReferenceCode());

		_persistence.update(newJournalFolder);
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
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByC_NotS() throws Exception {
		_persistence.countByC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotS(0L, 0);
	}

	@Test
	public void testCountByG_P_N() throws Exception {
		_persistence.countByG_P_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_P_N(0L, 0L, "null");

		_persistence.countByG_P_N(0L, 0L, (String)null);
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
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalFolder newJournalFolder = addJournalFolder();

		JournalFolder existingJournalFolder = _persistence.findByPrimaryKey(
			newJournalFolder.getPrimaryKey());

		Assert.assertEquals(existingJournalFolder, newJournalFolder);
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

	protected OrderByComparator<JournalFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JournalFolder", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "folderId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "parentFolderId",
			true, "treePath", true, "name", true, "description", true,
			"restrictionType", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalFolder newJournalFolder = addJournalFolder();

		JournalFolder existingJournalFolder = _persistence.fetchByPrimaryKey(
			newJournalFolder.getPrimaryKey());

		Assert.assertEquals(existingJournalFolder, newJournalFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFolder missingJournalFolder = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJournalFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalFolder newJournalFolder1 = addJournalFolder();
		JournalFolder newJournalFolder2 = addJournalFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFolder1.getPrimaryKey());
		primaryKeys.add(newJournalFolder2.getPrimaryKey());

		Map<Serializable, JournalFolder> journalFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, journalFolders.size());
		Assert.assertEquals(
			newJournalFolder1,
			journalFolders.get(newJournalFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalFolder2,
			journalFolders.get(newJournalFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalFolder> journalFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalFolder newJournalFolder = addJournalFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalFolder> journalFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalFolders.size());
		Assert.assertEquals(
			newJournalFolder,
			journalFolders.get(newJournalFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalFolder> journalFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalFolder newJournalFolder = addJournalFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFolder.getPrimaryKey());

		Map<Serializable, JournalFolder> journalFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalFolders.size());
		Assert.assertEquals(
			newJournalFolder,
			journalFolders.get(newJournalFolder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalFolder newJournalFolder = addJournalFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newJournalFolder.getPrimaryKey()));
	}

	private void _assertOriginalValues(JournalFolder journalFolder) {
		Assert.assertEquals(
			journalFolder.getUuid(),
			ReflectionTestUtil.invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(journalFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(journalFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			journalFolder.getName(),
			ReflectionTestUtil.invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(journalFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(journalFolder.getParentFolderId()),
			ReflectionTestUtil.<Long>invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentFolderId"));
		Assert.assertEquals(
			journalFolder.getName(),
			ReflectionTestUtil.invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			journalFolder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(journalFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected JournalFolder addJournalFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFolder journalFolder = _persistence.create(pk);

		journalFolder.setMvccVersion(RandomTestUtil.nextLong());

		journalFolder.setCtCollectionId(RandomTestUtil.nextLong());

		journalFolder.setUuid(RandomTestUtil.randomString());

		journalFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		journalFolder.setGroupId(RandomTestUtil.nextLong());

		journalFolder.setCompanyId(RandomTestUtil.nextLong());

		journalFolder.setUserId(RandomTestUtil.nextLong());

		journalFolder.setUserName(RandomTestUtil.randomString());

		journalFolder.setCreateDate(RandomTestUtil.nextDate());

		journalFolder.setModifiedDate(RandomTestUtil.nextDate());

		journalFolder.setParentFolderId(RandomTestUtil.nextLong());

		journalFolder.setTreePath(RandomTestUtil.randomString());

		journalFolder.setName(RandomTestUtil.randomString());

		journalFolder.setDescription(RandomTestUtil.randomString());

		journalFolder.setRestrictionType(RandomTestUtil.nextInt());

		journalFolder.setLastPublishDate(RandomTestUtil.nextDate());

		journalFolder.setStatus(RandomTestUtil.nextInt());

		journalFolder.setStatusByUserId(RandomTestUtil.nextLong());

		journalFolder.setStatusByUserName(RandomTestUtil.randomString());

		journalFolder.setStatusDate(RandomTestUtil.nextDate());

		_journalFolders.add(_persistence.update(journalFolder));

		return journalFolder;
	}

	private List<JournalFolder> _journalFolders =
		new ArrayList<JournalFolder>();
	private JournalFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}