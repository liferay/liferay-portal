/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.DuplicateObjectFolderExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectFolderException;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.persistence.ObjectFolderPersistence;
import com.liferay.object.service.persistence.ObjectFolderUtil;
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
public class ObjectFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectFolder> iterator = _objectFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolder objectFolder = _persistence.create(pk);

		Assert.assertNotNull(objectFolder);

		Assert.assertEquals(objectFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectFolder newObjectFolder = addObjectFolder();

		_persistence.remove(newObjectFolder);

		ObjectFolder existingObjectFolder = _persistence.fetchByPrimaryKey(
			newObjectFolder.getPrimaryKey());

		Assert.assertNull(existingObjectFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolder newObjectFolder = _persistence.create(pk);

		newObjectFolder.setMvccVersion(RandomTestUtil.nextLong());

		newObjectFolder.setUuid(RandomTestUtil.randomString());

		newObjectFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		newObjectFolder.setCompanyId(RandomTestUtil.nextLong());

		newObjectFolder.setUserId(RandomTestUtil.nextLong());

		newObjectFolder.setUserName(RandomTestUtil.randomString());

		newObjectFolder.setCreateDate(RandomTestUtil.nextDate());

		newObjectFolder.setModifiedDate(RandomTestUtil.nextDate());

		newObjectFolder.setLabel(RandomTestUtil.randomString());

		newObjectFolder.setName(RandomTestUtil.randomString());

		_objectFolders.add(_persistence.update(newObjectFolder));

		ObjectFolder existingObjectFolder = _persistence.findByPrimaryKey(
			newObjectFolder.getPrimaryKey());

		Assert.assertEquals(
			existingObjectFolder.getMvccVersion(),
			newObjectFolder.getMvccVersion());
		Assert.assertEquals(
			existingObjectFolder.getUuid(), newObjectFolder.getUuid());
		Assert.assertEquals(
			existingObjectFolder.getExternalReferenceCode(),
			newObjectFolder.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectFolder.getObjectFolderId(),
			newObjectFolder.getObjectFolderId());
		Assert.assertEquals(
			existingObjectFolder.getCompanyId(),
			newObjectFolder.getCompanyId());
		Assert.assertEquals(
			existingObjectFolder.getUserId(), newObjectFolder.getUserId());
		Assert.assertEquals(
			existingObjectFolder.getUserName(), newObjectFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFolder.getCreateDate()),
			Time.getShortTimestamp(newObjectFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFolder.getModifiedDate()),
			Time.getShortTimestamp(newObjectFolder.getModifiedDate()));
		Assert.assertEquals(
			existingObjectFolder.getLabel(), newObjectFolder.getLabel());
		Assert.assertEquals(
			existingObjectFolder.getName(), newObjectFolder.getName());
	}

	@Test(expected = DuplicateObjectFolderExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ObjectFolder objectFolder = addObjectFolder();

		ObjectFolder newObjectFolder = addObjectFolder();

		newObjectFolder.setCompanyId(objectFolder.getCompanyId());

		newObjectFolder = _persistence.update(newObjectFolder);

		Session session = _persistence.getCurrentSession();

		session.evict(newObjectFolder);

		newObjectFolder.setExternalReferenceCode(
			objectFolder.getExternalReferenceCode());

		_persistence.update(newObjectFolder);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
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
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectFolder newObjectFolder = addObjectFolder();

		ObjectFolder existingObjectFolder = _persistence.findByPrimaryKey(
			newObjectFolder.getPrimaryKey());

		Assert.assertEquals(existingObjectFolder, newObjectFolder);
	}

	@Test(expected = NoSuchObjectFolderException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectFolder", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectFolderId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "label", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectFolder newObjectFolder = addObjectFolder();

		ObjectFolder existingObjectFolder = _persistence.fetchByPrimaryKey(
			newObjectFolder.getPrimaryKey());

		Assert.assertEquals(existingObjectFolder, newObjectFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolder missingObjectFolder = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectFolder newObjectFolder1 = addObjectFolder();
		ObjectFolder newObjectFolder2 = addObjectFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolder1.getPrimaryKey());
		primaryKeys.add(newObjectFolder2.getPrimaryKey());

		Map<Serializable, ObjectFolder> objectFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectFolders.size());
		Assert.assertEquals(
			newObjectFolder1,
			objectFolders.get(newObjectFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectFolder2,
			objectFolders.get(newObjectFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectFolder> objectFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectFolder newObjectFolder = addObjectFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectFolder> objectFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFolders.size());
		Assert.assertEquals(
			newObjectFolder,
			objectFolders.get(newObjectFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectFolder> objectFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectFolder newObjectFolder = addObjectFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolder.getPrimaryKey());

		Map<Serializable, ObjectFolder> objectFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFolders.size());
		Assert.assertEquals(
			newObjectFolder,
			objectFolders.get(newObjectFolder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectFolder newObjectFolder = addObjectFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectFolder.getPrimaryKey()));
	}

	private void _assertOriginalValues(ObjectFolder objectFolder) {
		Assert.assertEquals(
			Long.valueOf(objectFolder.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			objectFolder.getName(),
			ReflectionTestUtil.invoke(
				objectFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			objectFolder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectFolder.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected ObjectFolder addObjectFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolder objectFolder = _persistence.create(pk);

		objectFolder.setMvccVersion(RandomTestUtil.nextLong());

		objectFolder.setUuid(RandomTestUtil.randomString());

		objectFolder.setExternalReferenceCode(RandomTestUtil.randomString());

		objectFolder.setCompanyId(RandomTestUtil.nextLong());

		objectFolder.setUserId(RandomTestUtil.nextLong());

		objectFolder.setUserName(RandomTestUtil.randomString());

		objectFolder.setCreateDate(RandomTestUtil.nextDate());

		objectFolder.setModifiedDate(RandomTestUtil.nextDate());

		objectFolder.setLabel(RandomTestUtil.randomString());

		objectFolder.setName(RandomTestUtil.randomString());

		_objectFolders.add(_persistence.update(objectFolder));

		return objectFolder;
	}

	private List<ObjectFolder> _objectFolders = new ArrayList<ObjectFolder>();
	private ObjectFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}