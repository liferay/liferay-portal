/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectEntryFolderPersistence;
import com.liferay.object.service.persistence.ObjectEntryFolderUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class ObjectEntryFolderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectEntryFolderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectEntryFolder> iterator = _objectEntryFolders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryFolder objectEntryFolder = _persistence.create(pk);

		Assert.assertNotNull(objectEntryFolder);

		Assert.assertEquals(objectEntryFolder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		_persistence.remove(newObjectEntryFolder);

		ObjectEntryFolder existingObjectEntryFolder =
			_persistence.fetchByPrimaryKey(
				newObjectEntryFolder.getPrimaryKey());

		Assert.assertNull(existingObjectEntryFolder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectEntryFolder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryFolder newObjectEntryFolder = _persistence.create(pk);

		newObjectEntryFolder.setMvccVersion(RandomTestUtil.nextLong());

		newObjectEntryFolder.setUuid(RandomTestUtil.randomString());

		newObjectEntryFolder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newObjectEntryFolder.setGroupId(RandomTestUtil.nextLong());

		newObjectEntryFolder.setCompanyId(RandomTestUtil.nextLong());

		newObjectEntryFolder.setUserId(RandomTestUtil.nextLong());

		newObjectEntryFolder.setUserName(RandomTestUtil.randomString());

		newObjectEntryFolder.setCreateDate(RandomTestUtil.nextDate());

		newObjectEntryFolder.setModifiedDate(RandomTestUtil.nextDate());

		newObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.nextLong());

		newObjectEntryFolder.setDescription(RandomTestUtil.randomString());

		newObjectEntryFolder.setLabel(RandomTestUtil.randomString());

		newObjectEntryFolder.setName(RandomTestUtil.randomString());

		newObjectEntryFolder.setTreePath(RandomTestUtil.randomString());

		newObjectEntryFolder.setStatus(RandomTestUtil.nextInt());

		_objectEntryFolders.add(_persistence.update(newObjectEntryFolder));

		ObjectEntryFolder existingObjectEntryFolder =
			_persistence.findByPrimaryKey(newObjectEntryFolder.getPrimaryKey());

		Assert.assertEquals(
			existingObjectEntryFolder.getMvccVersion(),
			newObjectEntryFolder.getMvccVersion());
		Assert.assertEquals(
			existingObjectEntryFolder.getUuid(),
			newObjectEntryFolder.getUuid());
		Assert.assertEquals(
			existingObjectEntryFolder.getExternalReferenceCode(),
			newObjectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectEntryFolder.getObjectEntryFolderId(),
			newObjectEntryFolder.getObjectEntryFolderId());
		Assert.assertEquals(
			existingObjectEntryFolder.getGroupId(),
			newObjectEntryFolder.getGroupId());
		Assert.assertEquals(
			existingObjectEntryFolder.getCompanyId(),
			newObjectEntryFolder.getCompanyId());
		Assert.assertEquals(
			existingObjectEntryFolder.getUserId(),
			newObjectEntryFolder.getUserId());
		Assert.assertEquals(
			existingObjectEntryFolder.getUserName(),
			newObjectEntryFolder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryFolder.getCreateDate()),
			Time.getShortTimestamp(newObjectEntryFolder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryFolder.getModifiedDate()),
			Time.getShortTimestamp(newObjectEntryFolder.getModifiedDate()));
		Assert.assertEquals(
			existingObjectEntryFolder.getParentObjectEntryFolderId(),
			newObjectEntryFolder.getParentObjectEntryFolderId());
		Assert.assertEquals(
			existingObjectEntryFolder.getDescription(),
			newObjectEntryFolder.getDescription());
		Assert.assertEquals(
			existingObjectEntryFolder.getLabel(),
			newObjectEntryFolder.getLabel());
		Assert.assertEquals(
			existingObjectEntryFolder.getName(),
			newObjectEntryFolder.getName());
		Assert.assertEquals(
			existingObjectEntryFolder.getTreePath(),
			newObjectEntryFolder.getTreePath());
		Assert.assertEquals(
			existingObjectEntryFolder.getStatus(),
			newObjectEntryFolder.getStatus());
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
	public void testCountByERC_G_C() throws Exception {
		_persistence.countByERC_G_C(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_G_C("null", 0L, 0L);

		_persistence.countByERC_G_C((String)null, 0L, 0L);
	}

	@Test
	public void testCountByG_C_P() throws Exception {
		_persistence.countByG_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_LikeT() throws Exception {
		_persistence.countByG_C_LikeT(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_LikeT(0L, 0L, "null");

		_persistence.countByG_C_LikeT(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_P_N() throws Exception {
		_persistence.countByG_C_P_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_P_N(0L, 0L, 0L, "null");

		_persistence.countByG_C_P_N(0L, 0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		ObjectEntryFolder existingObjectEntryFolder =
			_persistence.findByPrimaryKey(newObjectEntryFolder.getPrimaryKey());

		Assert.assertEquals(existingObjectEntryFolder, newObjectEntryFolder);
	}

	@Test(expected = NoSuchObjectEntryFolderException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectEntryFolder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectEntryFolder", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectEntryFolderId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true,
			"parentObjectEntryFolderId", true, "description", true, "label",
			true, "name", true, "treePath", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		ObjectEntryFolder existingObjectEntryFolder =
			_persistence.fetchByPrimaryKey(
				newObjectEntryFolder.getPrimaryKey());

		Assert.assertEquals(existingObjectEntryFolder, newObjectEntryFolder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryFolder missingObjectEntryFolder =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectEntryFolder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectEntryFolder newObjectEntryFolder1 = addObjectEntryFolder();
		ObjectEntryFolder newObjectEntryFolder2 = addObjectEntryFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryFolder1.getPrimaryKey());
		primaryKeys.add(newObjectEntryFolder2.getPrimaryKey());

		Map<Serializable, ObjectEntryFolder> objectEntryFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectEntryFolders.size());
		Assert.assertEquals(
			newObjectEntryFolder1,
			objectEntryFolders.get(newObjectEntryFolder1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectEntryFolder2,
			objectEntryFolders.get(newObjectEntryFolder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectEntryFolder> objectEntryFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntryFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryFolder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectEntryFolder> objectEntryFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntryFolders.size());
		Assert.assertEquals(
			newObjectEntryFolder,
			objectEntryFolders.get(newObjectEntryFolder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectEntryFolder> objectEntryFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntryFolders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryFolder.getPrimaryKey());

		Map<Serializable, ObjectEntryFolder> objectEntryFolders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntryFolders.size());
		Assert.assertEquals(
			newObjectEntryFolder,
			objectEntryFolders.get(newObjectEntryFolder.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectEntryFolderLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ObjectEntryFolder>() {

				@Override
				public void performAction(ObjectEntryFolder objectEntryFolder) {
					Assert.assertNotNull(objectEntryFolder);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryFolder.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryFolderId",
				newObjectEntryFolder.getObjectEntryFolderId()));

		List<ObjectEntryFolder> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectEntryFolder existingObjectEntryFolder = result.get(0);

		Assert.assertEquals(existingObjectEntryFolder, newObjectEntryFolder);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryFolder.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryFolderId", RandomTestUtil.nextLong()));

		List<ObjectEntryFolder> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryFolder.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryFolderId"));

		Object newObjectEntryFolderId =
			newObjectEntryFolder.getObjectEntryFolderId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryFolderId", new Object[] {newObjectEntryFolderId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectEntryFolderId = result.get(0);

		Assert.assertEquals(
			existingObjectEntryFolderId, newObjectEntryFolderId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryFolder.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryFolderId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryFolderId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectEntryFolder.getPrimaryKey()));
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

		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryFolder.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryFolderId",
				newObjectEntryFolder.getObjectEntryFolderId()));

		List<ObjectEntryFolder> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectEntryFolder objectEntryFolder) {
		Assert.assertEquals(
			objectEntryFolder.getUuid(),
			ReflectionTestUtil.invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			objectEntryFolder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));

		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectEntryFolder.getParentObjectEntryFolderId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentObjectEntryFolderId"));
		Assert.assertEquals(
			objectEntryFolder.getName(),
			ReflectionTestUtil.invoke(
				objectEntryFolder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected ObjectEntryFolder addObjectEntryFolder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryFolder objectEntryFolder = _persistence.create(pk);

		objectEntryFolder.setMvccVersion(RandomTestUtil.nextLong());

		objectEntryFolder.setUuid(RandomTestUtil.randomString());

		objectEntryFolder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectEntryFolder.setGroupId(RandomTestUtil.nextLong());

		objectEntryFolder.setCompanyId(RandomTestUtil.nextLong());

		objectEntryFolder.setUserId(RandomTestUtil.nextLong());

		objectEntryFolder.setUserName(RandomTestUtil.randomString());

		objectEntryFolder.setCreateDate(RandomTestUtil.nextDate());

		objectEntryFolder.setModifiedDate(RandomTestUtil.nextDate());

		objectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.nextLong());

		objectEntryFolder.setDescription(RandomTestUtil.randomString());

		objectEntryFolder.setLabel(RandomTestUtil.randomString());

		objectEntryFolder.setName(RandomTestUtil.randomString());

		objectEntryFolder.setTreePath(RandomTestUtil.randomString());

		objectEntryFolder.setStatus(RandomTestUtil.nextInt());

		_objectEntryFolders.add(_persistence.update(objectEntryFolder));

		return objectEntryFolder;
	}

	private List<ObjectEntryFolder> _objectEntryFolders =
		new ArrayList<ObjectEntryFolder>();
	private ObjectEntryFolderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}