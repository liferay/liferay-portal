/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectEntryPersistence;
import com.liferay.object.service.persistence.ObjectEntryUtil;
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
public class ObjectEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectEntry> iterator = _objectEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntry objectEntry = _persistence.create(pk);

		Assert.assertNotNull(objectEntry);

		Assert.assertEquals(objectEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		_persistence.remove(newObjectEntry);

		ObjectEntry existingObjectEntry = _persistence.fetchByPrimaryKey(
			newObjectEntry.getPrimaryKey());

		Assert.assertNull(existingObjectEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntry newObjectEntry = _persistence.create(pk);

		newObjectEntry.setMvccVersion(RandomTestUtil.nextLong());

		newObjectEntry.setUuid(RandomTestUtil.randomString());

		newObjectEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		newObjectEntry.setGroupId(RandomTestUtil.nextLong());

		newObjectEntry.setCompanyId(RandomTestUtil.nextLong());

		newObjectEntry.setUserId(RandomTestUtil.nextLong());

		newObjectEntry.setUserName(RandomTestUtil.randomString());

		newObjectEntry.setCreateDate(RandomTestUtil.nextDate());

		newObjectEntry.setModifiedDate(RandomTestUtil.nextDate());

		newObjectEntry.setHeadObjectEntryId(RandomTestUtil.nextLong());

		newObjectEntry.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectEntry.setObjectEntryFolderId(RandomTestUtil.nextLong());

		newObjectEntry.setRootObjectEntryId(RandomTestUtil.nextLong());

		newObjectEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		newObjectEntry.setDisplayDate(RandomTestUtil.nextDate());

		newObjectEntry.setExpirationDate(RandomTestUtil.nextDate());

		newObjectEntry.setReviewDate(RandomTestUtil.nextDate());

		newObjectEntry.setTreePath(RandomTestUtil.randomString());

		newObjectEntry.setVersion(RandomTestUtil.nextInt());

		newObjectEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newObjectEntry.setStatus(RandomTestUtil.nextInt());

		newObjectEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newObjectEntry.setStatusByUserName(RandomTestUtil.randomString());

		newObjectEntry.setStatusDate(RandomTestUtil.nextDate());

		_objectEntries.add(_persistence.update(newObjectEntry));

		ObjectEntry existingObjectEntry = _persistence.findByPrimaryKey(
			newObjectEntry.getPrimaryKey());

		Assert.assertEquals(
			existingObjectEntry.getMvccVersion(),
			newObjectEntry.getMvccVersion());
		Assert.assertEquals(
			existingObjectEntry.getUuid(), newObjectEntry.getUuid());
		Assert.assertEquals(
			existingObjectEntry.getExternalReferenceCode(),
			newObjectEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectEntry.getObjectEntryId(),
			newObjectEntry.getObjectEntryId());
		Assert.assertEquals(
			existingObjectEntry.getGroupId(), newObjectEntry.getGroupId());
		Assert.assertEquals(
			existingObjectEntry.getCompanyId(), newObjectEntry.getCompanyId());
		Assert.assertEquals(
			existingObjectEntry.getUserId(), newObjectEntry.getUserId());
		Assert.assertEquals(
			existingObjectEntry.getUserName(), newObjectEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getCreateDate()),
			Time.getShortTimestamp(newObjectEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getModifiedDate()),
			Time.getShortTimestamp(newObjectEntry.getModifiedDate()));
		Assert.assertEquals(
			existingObjectEntry.getHeadObjectEntryId(),
			newObjectEntry.getHeadObjectEntryId());
		Assert.assertEquals(
			existingObjectEntry.getObjectDefinitionId(),
			newObjectEntry.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectEntry.getObjectEntryFolderId(),
			newObjectEntry.getObjectEntryFolderId());
		Assert.assertEquals(
			existingObjectEntry.getRootObjectEntryId(),
			newObjectEntry.getRootObjectEntryId());
		Assert.assertEquals(
			existingObjectEntry.getDefaultLanguageId(),
			newObjectEntry.getDefaultLanguageId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getDisplayDate()),
			Time.getShortTimestamp(newObjectEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getExpirationDate()),
			Time.getShortTimestamp(newObjectEntry.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getReviewDate()),
			Time.getShortTimestamp(newObjectEntry.getReviewDate()));
		Assert.assertEquals(
			existingObjectEntry.getTreePath(), newObjectEntry.getTreePath());
		Assert.assertEquals(
			existingObjectEntry.getVersion(), newObjectEntry.getVersion());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getLastPublishDate()),
			Time.getShortTimestamp(newObjectEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingObjectEntry.getStatus(), newObjectEntry.getStatus());
		Assert.assertEquals(
			existingObjectEntry.getStatusByUserId(),
			newObjectEntry.getStatusByUserId());
		Assert.assertEquals(
			existingObjectEntry.getStatusByUserName(),
			newObjectEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntry.getStatusDate()),
			Time.getShortTimestamp(newObjectEntry.getStatusDate()));
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
	public void testCountByHeadObjectEntryId() throws Exception {
		_persistence.countByHeadObjectEntryId(RandomTestUtil.nextLong());

		_persistence.countByHeadObjectEntryId(0L);
	}

	@Test
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByG_ODI() throws Exception {
		_persistence.countByG_ODI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_ODI(0L, 0L);
	}

	@Test
	public void testCountByG_OEFI() throws Exception {
		_persistence.countByG_OEFI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_OEFI(0L, 0L);
	}

	@Test
	public void testCountByU_ODI() throws Exception {
		_persistence.countByU_ODI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_ODI(0L, 0L);
	}

	@Test
	public void testCountByODI_NotS() throws Exception {
		_persistence.countByODI_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByODI_NotS(0L, 0);
	}

	@Test
	public void testCountByROEI_NotS() throws Exception {
		_persistence.countByROEI_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByROEI_NotS(0L, 0);
	}

	@Test
	public void testCountByG_C_OEFI() throws Exception {
		_persistence.countByG_C_OEFI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_OEFI(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_ODI_S() throws Exception {
		_persistence.countByG_ODI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_ODI_S(0L, 0L, 0);
	}

	@Test
	public void testCountByU_GtCD_ODI() throws Exception {
		_persistence.countByU_GtCD_ODI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextLong());

		_persistence.countByU_GtCD_ODI(0L, RandomTestUtil.nextDate(), 0L);
	}

	@Test
	public void testCountByERC_G_C_ODI() throws Exception {
		_persistence.countByERC_G_C_ODI(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByERC_G_C_ODI("null", 0L, 0L, 0L);

		_persistence.countByERC_G_C_ODI((String)null, 0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		ObjectEntry existingObjectEntry = _persistence.findByPrimaryKey(
			newObjectEntry.getPrimaryKey());

		Assert.assertEquals(existingObjectEntry, newObjectEntry);
	}

	@Test(expected = NoSuchObjectEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectEntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectEntryId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "headObjectEntryId", true,
			"objectDefinitionId", true, "objectEntryFolderId", true,
			"rootObjectEntryId", true, "defaultLanguageId", true, "displayDate",
			true, "expirationDate", true, "reviewDate", true, "treePath", true,
			"version", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		ObjectEntry existingObjectEntry = _persistence.fetchByPrimaryKey(
			newObjectEntry.getPrimaryKey());

		Assert.assertEquals(existingObjectEntry, newObjectEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntry missingObjectEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectEntry newObjectEntry1 = addObjectEntry();
		ObjectEntry newObjectEntry2 = addObjectEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntry1.getPrimaryKey());
		primaryKeys.add(newObjectEntry2.getPrimaryKey());

		Map<Serializable, ObjectEntry> objectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectEntries.size());
		Assert.assertEquals(
			newObjectEntry1,
			objectEntries.get(newObjectEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectEntry2,
			objectEntries.get(newObjectEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectEntry> objectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectEntry newObjectEntry = addObjectEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectEntry> objectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntries.size());
		Assert.assertEquals(
			newObjectEntry, objectEntries.get(newObjectEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectEntry> objectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntry.getPrimaryKey());

		Map<Serializable, ObjectEntry> objectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntries.size());
		Assert.assertEquals(
			newObjectEntry, objectEntries.get(newObjectEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectEntry>() {

				@Override
				public void performAction(ObjectEntry objectEntry) {
					Assert.assertNotNull(objectEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryId", newObjectEntry.getObjectEntryId()));

		List<ObjectEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectEntry existingObjectEntry = result.get(0);

		Assert.assertEquals(existingObjectEntry, newObjectEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryId", RandomTestUtil.nextLong()));

		List<ObjectEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryId"));

		Object newObjectEntryId = newObjectEntry.getObjectEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryId", new Object[] {newObjectEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectEntryId = result.get(0);

		Assert.assertEquals(existingObjectEntryId, newObjectEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectEntry newObjectEntry = addObjectEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectEntry.getPrimaryKey()));
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

		ObjectEntry newObjectEntry = addObjectEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryId", newObjectEntry.getObjectEntryId()));

		List<ObjectEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectEntry objectEntry) {
		Assert.assertEquals(
			objectEntry.getUuid(),
			ReflectionTestUtil.invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(objectEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(objectEntry.getHeadObjectEntryId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headObjectEntryId"));

		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(objectEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectEntry.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
	}

	protected ObjectEntry addObjectEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntry objectEntry = _persistence.create(pk);

		objectEntry.setMvccVersion(RandomTestUtil.nextLong());

		objectEntry.setUuid(RandomTestUtil.randomString());

		objectEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		objectEntry.setGroupId(RandomTestUtil.nextLong());

		objectEntry.setCompanyId(RandomTestUtil.nextLong());

		objectEntry.setUserId(RandomTestUtil.nextLong());

		objectEntry.setUserName(RandomTestUtil.randomString());

		objectEntry.setCreateDate(RandomTestUtil.nextDate());

		objectEntry.setModifiedDate(RandomTestUtil.nextDate());

		objectEntry.setHeadObjectEntryId(RandomTestUtil.nextLong());

		objectEntry.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectEntry.setObjectEntryFolderId(RandomTestUtil.nextLong());

		objectEntry.setRootObjectEntryId(RandomTestUtil.nextLong());

		objectEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		objectEntry.setDisplayDate(RandomTestUtil.nextDate());

		objectEntry.setExpirationDate(RandomTestUtil.nextDate());

		objectEntry.setReviewDate(RandomTestUtil.nextDate());

		objectEntry.setTreePath(RandomTestUtil.randomString());

		objectEntry.setVersion(RandomTestUtil.nextInt());

		objectEntry.setLastPublishDate(RandomTestUtil.nextDate());

		objectEntry.setStatus(RandomTestUtil.nextInt());

		objectEntry.setStatusByUserId(RandomTestUtil.nextLong());

		objectEntry.setStatusByUserName(RandomTestUtil.randomString());

		objectEntry.setStatusDate(RandomTestUtil.nextDate());

		_objectEntries.add(_persistence.update(objectEntry));

		return objectEntry;
	}

	private List<ObjectEntry> _objectEntries = new ArrayList<ObjectEntry>();
	private ObjectEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}