/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectEntryVersionException;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.service.ObjectEntryVersionLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectEntryVersionPersistence;
import com.liferay.object.service.persistence.ObjectEntryVersionUtil;
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
public class ObjectEntryVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectEntryVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectEntryVersion> iterator = _objectEntryVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryVersion objectEntryVersion = _persistence.create(pk);

		Assert.assertNotNull(objectEntryVersion);

		Assert.assertEquals(objectEntryVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		_persistence.remove(newObjectEntryVersion);

		ObjectEntryVersion existingObjectEntryVersion =
			_persistence.fetchByPrimaryKey(
				newObjectEntryVersion.getPrimaryKey());

		Assert.assertNull(existingObjectEntryVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectEntryVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryVersion newObjectEntryVersion = _persistence.create(pk);

		newObjectEntryVersion.setMvccVersion(RandomTestUtil.nextLong());

		newObjectEntryVersion.setUuid(RandomTestUtil.randomString());

		newObjectEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		newObjectEntryVersion.setUserId(RandomTestUtil.nextLong());

		newObjectEntryVersion.setUserName(RandomTestUtil.randomString());

		newObjectEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		newObjectEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		newObjectEntryVersion.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectEntryVersion.setObjectEntryId(RandomTestUtil.nextLong());

		newObjectEntryVersion.setContent(RandomTestUtil.randomString());

		newObjectEntryVersion.setDisplayDate(RandomTestUtil.nextDate());

		newObjectEntryVersion.setExpirationDate(RandomTestUtil.nextDate());

		newObjectEntryVersion.setReviewDate(RandomTestUtil.nextDate());

		newObjectEntryVersion.setVersion(RandomTestUtil.nextInt());

		newObjectEntryVersion.setStatus(RandomTestUtil.nextInt());

		newObjectEntryVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newObjectEntryVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newObjectEntryVersion.setStatusDate(RandomTestUtil.nextDate());

		_objectEntryVersions.add(_persistence.update(newObjectEntryVersion));

		ObjectEntryVersion existingObjectEntryVersion =
			_persistence.findByPrimaryKey(
				newObjectEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingObjectEntryVersion.getMvccVersion(),
			newObjectEntryVersion.getMvccVersion());
		Assert.assertEquals(
			existingObjectEntryVersion.getUuid(),
			newObjectEntryVersion.getUuid());
		Assert.assertEquals(
			existingObjectEntryVersion.getObjectEntryVersionId(),
			newObjectEntryVersion.getObjectEntryVersionId());
		Assert.assertEquals(
			existingObjectEntryVersion.getCompanyId(),
			newObjectEntryVersion.getCompanyId());
		Assert.assertEquals(
			existingObjectEntryVersion.getUserId(),
			newObjectEntryVersion.getUserId());
		Assert.assertEquals(
			existingObjectEntryVersion.getUserName(),
			newObjectEntryVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryVersion.getCreateDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectEntryVersion.getModifiedDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getModifiedDate()));
		Assert.assertEquals(
			existingObjectEntryVersion.getObjectDefinitionId(),
			newObjectEntryVersion.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectEntryVersion.getObjectEntryId(),
			newObjectEntryVersion.getObjectEntryId());
		Assert.assertEquals(
			existingObjectEntryVersion.getContent(),
			newObjectEntryVersion.getContent());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryVersion.getDisplayDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectEntryVersion.getExpirationDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryVersion.getReviewDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getReviewDate()));
		Assert.assertEquals(
			existingObjectEntryVersion.getVersion(),
			newObjectEntryVersion.getVersion());
		Assert.assertEquals(
			existingObjectEntryVersion.getStatus(),
			newObjectEntryVersion.getStatus());
		Assert.assertEquals(
			existingObjectEntryVersion.getStatusByUserId(),
			newObjectEntryVersion.getStatusByUserId());
		Assert.assertEquals(
			existingObjectEntryVersion.getStatusByUserName(),
			newObjectEntryVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectEntryVersion.getStatusDate()),
			Time.getShortTimestamp(newObjectEntryVersion.getStatusDate()));
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
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByObjectEntryId() throws Exception {
		_persistence.countByObjectEntryId(RandomTestUtil.nextLong());

		_persistence.countByObjectEntryId(0L);
	}

	@Test
	public void testCountByC_CD() throws Exception {
		_persistence.countByC_CD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

		_persistence.countByC_CD(0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByOEI_V() throws Exception {
		_persistence.countByOEI_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByOEI_V(0L, 0);
	}

	@Test
	public void testCountByOEI_S() throws Exception {
		_persistence.countByOEI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByOEI_S(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		ObjectEntryVersion existingObjectEntryVersion =
			_persistence.findByPrimaryKey(
				newObjectEntryVersion.getPrimaryKey());

		Assert.assertEquals(existingObjectEntryVersion, newObjectEntryVersion);
	}

	@Test(expected = NoSuchObjectEntryVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectEntryVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectEntryVersion", "mvccVersion", true, "uuid", true,
			"objectEntryVersionId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectDefinitionId", true, "objectEntryId", true, "displayDate",
			true, "expirationDate", true, "reviewDate", true, "version", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		ObjectEntryVersion existingObjectEntryVersion =
			_persistence.fetchByPrimaryKey(
				newObjectEntryVersion.getPrimaryKey());

		Assert.assertEquals(existingObjectEntryVersion, newObjectEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryVersion missingObjectEntryVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectEntryVersion newObjectEntryVersion1 = addObjectEntryVersion();
		ObjectEntryVersion newObjectEntryVersion2 = addObjectEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryVersion1.getPrimaryKey());
		primaryKeys.add(newObjectEntryVersion2.getPrimaryKey());

		Map<Serializable, ObjectEntryVersion> objectEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectEntryVersions.size());
		Assert.assertEquals(
			newObjectEntryVersion1,
			objectEntryVersions.get(newObjectEntryVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectEntryVersion2,
			objectEntryVersions.get(newObjectEntryVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectEntryVersion> objectEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectEntryVersion> objectEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntryVersions.size());
		Assert.assertEquals(
			newObjectEntryVersion,
			objectEntryVersions.get(newObjectEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectEntryVersion> objectEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectEntryVersion.getPrimaryKey());

		Map<Serializable, ObjectEntryVersion> objectEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectEntryVersions.size());
		Assert.assertEquals(
			newObjectEntryVersion,
			objectEntryVersions.get(newObjectEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectEntryVersionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ObjectEntryVersion>() {

				@Override
				public void performAction(
					ObjectEntryVersion objectEntryVersion) {

					Assert.assertNotNull(objectEntryVersion);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryVersionId",
				newObjectEntryVersion.getObjectEntryVersionId()));

		List<ObjectEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectEntryVersion existingObjectEntryVersion = result.get(0);

		Assert.assertEquals(existingObjectEntryVersion, newObjectEntryVersion);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryVersionId", RandomTestUtil.nextLong()));

		List<ObjectEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryVersionId"));

		Object newObjectEntryVersionId =
			newObjectEntryVersion.getObjectEntryVersionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryVersionId",
				new Object[] {newObjectEntryVersionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectEntryVersionId = result.get(0);

		Assert.assertEquals(
			existingObjectEntryVersionId, newObjectEntryVersionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectEntryVersionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectEntryVersionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectEntryVersion.getPrimaryKey()));
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

		ObjectEntryVersion newObjectEntryVersion = addObjectEntryVersion();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectEntryVersionId",
				newObjectEntryVersion.getObjectEntryVersionId()));

		List<ObjectEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectEntryVersion objectEntryVersion) {
		Assert.assertEquals(
			Long.valueOf(objectEntryVersion.getObjectEntryId()),
			ReflectionTestUtil.<Long>invoke(
				objectEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectEntryId"));
		Assert.assertEquals(
			Integer.valueOf(objectEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				objectEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected ObjectEntryVersion addObjectEntryVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectEntryVersion objectEntryVersion = _persistence.create(pk);

		objectEntryVersion.setMvccVersion(RandomTestUtil.nextLong());

		objectEntryVersion.setUuid(RandomTestUtil.randomString());

		objectEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		objectEntryVersion.setUserId(RandomTestUtil.nextLong());

		objectEntryVersion.setUserName(RandomTestUtil.randomString());

		objectEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		objectEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		objectEntryVersion.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectEntryVersion.setObjectEntryId(RandomTestUtil.nextLong());

		objectEntryVersion.setContent(RandomTestUtil.randomString());

		objectEntryVersion.setDisplayDate(RandomTestUtil.nextDate());

		objectEntryVersion.setExpirationDate(RandomTestUtil.nextDate());

		objectEntryVersion.setReviewDate(RandomTestUtil.nextDate());

		objectEntryVersion.setVersion(RandomTestUtil.nextInt());

		objectEntryVersion.setStatus(RandomTestUtil.nextInt());

		objectEntryVersion.setStatusByUserId(RandomTestUtil.nextLong());

		objectEntryVersion.setStatusByUserName(RandomTestUtil.randomString());

		objectEntryVersion.setStatusDate(RandomTestUtil.nextDate());

		_objectEntryVersions.add(_persistence.update(objectEntryVersion));

		return objectEntryVersion;
	}

	private List<ObjectEntryVersion> _objectEntryVersions =
		new ArrayList<ObjectEntryVersion>();
	private ObjectEntryVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}