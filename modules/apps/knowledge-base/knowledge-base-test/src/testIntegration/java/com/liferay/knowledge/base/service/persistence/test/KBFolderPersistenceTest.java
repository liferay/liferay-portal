/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.exception.DuplicateKBFolderExternalReferenceCodeException;
import com.liferay.knowledge.base.exception.NoSuchFolderException;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.persistence.KBFolderPersistence;
import com.liferay.knowledge.base.service.persistence.KBFolderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class KBFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.knowledge.base.service"));

	@Before
	public void setUp() {
		_persistence = KBFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KBFolder> iterator = _kbFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBFolder kbFolder = _persistence.create(pk);

		Assert.assertNotNull(kbFolder);

		Assert.assertEquals(kbFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KBFolder newKBFolder = addKBFolder();

		_persistence.remove(newKBFolder);

		KBFolder existingKBFolder = _persistence.fetchByPrimaryKey(
			newKBFolder.getPrimaryKey());

		Assert.assertNull(existingKBFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKBFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBFolder newKBFolder = _persistence.create(pk);

		newKBFolder.setMvccVersion(RandomTestUtil.nextLong());

		newKBFolder.setCtCollectionId(RandomTestUtil.nextLong());

		newKBFolder.setUuid(RandomTestUtil.randomString());

		newKBFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		newKBFolder.setGroupId(RandomTestUtil.nextLong());

		newKBFolder.setCompanyId(RandomTestUtil.nextLong());

		newKBFolder.setUserId(RandomTestUtil.nextLong());

		newKBFolder.setUserName(RandomTestUtil.randomString());

		newKBFolder.setCreateDate(RandomTestUtil.nextDate());

		newKBFolder.setModifiedDate(RandomTestUtil.nextDate());

		newKBFolder.setParentKBFolderId(RandomTestUtil.nextLong());

		newKBFolder.setName(RandomTestUtil.randomString());

		newKBFolder.setUrlTitle(RandomTestUtil.randomString());

		newKBFolder.setDescription(RandomTestUtil.randomString());

		newKBFolder.setLastPublishDate(RandomTestUtil.nextDate());

		newKBFolder.setStatus(RandomTestUtil.nextInt());

		newKBFolder.setStatusByUserId(RandomTestUtil.nextLong());

		newKBFolder.setStatusByUserName(RandomTestUtil.randomString());

		newKBFolder.setStatusDate(RandomTestUtil.nextDate());

		_kbFolders.add(_persistence.update(newKBFolder));

		KBFolder existingKBFolder = _persistence.findByPrimaryKey(
			newKBFolder.getPrimaryKey());

		Assert.assertEquals(
			existingKBFolder.getMvccVersion(), newKBFolder.getMvccVersion());
		Assert.assertEquals(
			existingKBFolder.getCtCollectionId(),
			newKBFolder.getCtCollectionId());
		Assert.assertEquals(existingKBFolder.getUuid(), newKBFolder.getUuid());
		Assert.assertEquals(
			existingKBFolder.getExternalReferenceCode(),
			newKBFolder.getExternalReferenceCode());
		Assert.assertEquals(
			existingKBFolder.getKbFolderId(), newKBFolder.getKbFolderId());
		Assert.assertEquals(
			existingKBFolder.getGroupId(), newKBFolder.getGroupId());
		Assert.assertEquals(
			existingKBFolder.getCompanyId(), newKBFolder.getCompanyId());
		Assert.assertEquals(
			existingKBFolder.getUserId(), newKBFolder.getUserId());
		Assert.assertEquals(
			existingKBFolder.getUserName(), newKBFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBFolder.getCreateDate()),
			Time.getShortTimestamp(newKBFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBFolder.getModifiedDate()),
			Time.getShortTimestamp(newKBFolder.getModifiedDate()));
		Assert.assertEquals(
			existingKBFolder.getParentKBFolderId(),
			newKBFolder.getParentKBFolderId());
		Assert.assertEquals(existingKBFolder.getName(), newKBFolder.getName());
		Assert.assertEquals(
			existingKBFolder.getUrlTitle(), newKBFolder.getUrlTitle());
		Assert.assertEquals(
			existingKBFolder.getDescription(), newKBFolder.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBFolder.getLastPublishDate()),
			Time.getShortTimestamp(newKBFolder.getLastPublishDate()));
		Assert.assertEquals(
			existingKBFolder.getStatus(), newKBFolder.getStatus());
		Assert.assertEquals(
			existingKBFolder.getStatusByUserId(),
			newKBFolder.getStatusByUserId());
		Assert.assertEquals(
			existingKBFolder.getStatusByUserName(),
			newKBFolder.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBFolder.getStatusDate()),
			Time.getShortTimestamp(newKBFolder.getStatusDate()));
	}

	@Test(expected = DuplicateKBFolderExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		KBFolder kbFolder = addKBFolder();

		KBFolder newKBFolder = addKBFolder();

		newKBFolder.setGroupId(kbFolder.getGroupId());

		newKBFolder = _persistence.update(newKBFolder);

		Session session = _persistence.getCurrentSession();

		session.evict(newKBFolder);

		newKBFolder.setExternalReferenceCode(
			kbFolder.getExternalReferenceCode());

		_persistence.update(newKBFolder);
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
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByG_P_N() throws Exception {
		_persistence.countByG_P_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_P_N(0L, 0L, "null");

		_persistence.countByG_P_N(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_P_UT() throws Exception {
		_persistence.countByG_P_UT(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_P_UT(0L, 0L, "null");

		_persistence.countByG_P_UT(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_P_S() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_S(0L, 0L, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KBFolder newKBFolder = addKBFolder();

		KBFolder existingKBFolder = _persistence.findByPrimaryKey(
			newKBFolder.getPrimaryKey());

		Assert.assertEquals(existingKBFolder, newKBFolder);
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

	protected OrderByComparator<KBFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KBFolder", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "kbFolderId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "parentKBFolderId", true,
			"name", true, "urlTitle", true, "description", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KBFolder newKBFolder = addKBFolder();

		KBFolder existingKBFolder = _persistence.fetchByPrimaryKey(
			newKBFolder.getPrimaryKey());

		Assert.assertEquals(existingKBFolder, newKBFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBFolder missingKBFolder = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKBFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KBFolder newKBFolder1 = addKBFolder();
		KBFolder newKBFolder2 = addKBFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBFolder1.getPrimaryKey());
		primaryKeys.add(newKBFolder2.getPrimaryKey());

		Map<Serializable, KBFolder> kbFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, kbFolders.size());
		Assert.assertEquals(
			newKBFolder1, kbFolders.get(newKBFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newKBFolder2, kbFolders.get(newKBFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KBFolder> kbFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(kbFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KBFolder newKBFolder = addKBFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KBFolder> kbFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, kbFolders.size());
		Assert.assertEquals(
			newKBFolder, kbFolders.get(newKBFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KBFolder> kbFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(kbFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KBFolder newKBFolder = addKBFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBFolder.getPrimaryKey());

		Map<Serializable, KBFolder> kbFolders = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, kbFolders.size());
		Assert.assertEquals(
			newKBFolder, kbFolders.get(newKBFolder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KBFolder newKBFolder = addKBFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKBFolder.getPrimaryKey()));
	}

	private void _assertOriginalValues(KBFolder kbFolder) {
		Assert.assertEquals(
			kbFolder.getUuid(),
			ReflectionTestUtil.invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(kbFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(kbFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(kbFolder.getParentKBFolderId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentKBFolderId"));
		Assert.assertEquals(
			kbFolder.getName(),
			ReflectionTestUtil.invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(kbFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(kbFolder.getParentKBFolderId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentKBFolderId"));
		Assert.assertEquals(
			kbFolder.getUrlTitle(),
			ReflectionTestUtil.invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "urlTitle"));

		Assert.assertEquals(
			kbFolder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(kbFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected KBFolder addKBFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBFolder kbFolder = _persistence.create(pk);

		kbFolder.setMvccVersion(RandomTestUtil.nextLong());

		kbFolder.setCtCollectionId(RandomTestUtil.nextLong());

		kbFolder.setUuid(RandomTestUtil.randomString());

		kbFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		kbFolder.setGroupId(RandomTestUtil.nextLong());

		kbFolder.setCompanyId(RandomTestUtil.nextLong());

		kbFolder.setUserId(RandomTestUtil.nextLong());

		kbFolder.setUserName(RandomTestUtil.randomString());

		kbFolder.setCreateDate(RandomTestUtil.nextDate());

		kbFolder.setModifiedDate(RandomTestUtil.nextDate());

		kbFolder.setParentKBFolderId(RandomTestUtil.nextLong());

		kbFolder.setName(RandomTestUtil.randomString());

		kbFolder.setUrlTitle(RandomTestUtil.randomString());

		kbFolder.setDescription(RandomTestUtil.randomString());

		kbFolder.setLastPublishDate(RandomTestUtil.nextDate());

		kbFolder.setStatus(RandomTestUtil.nextInt());

		kbFolder.setStatusByUserId(RandomTestUtil.nextLong());

		kbFolder.setStatusByUserName(RandomTestUtil.randomString());

		kbFolder.setStatusDate(RandomTestUtil.nextDate());

		_kbFolders.add(_persistence.update(kbFolder));

		return kbFolder;
	}

	private List<KBFolder> _kbFolders = new ArrayList<KBFolder>();
	private KBFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}