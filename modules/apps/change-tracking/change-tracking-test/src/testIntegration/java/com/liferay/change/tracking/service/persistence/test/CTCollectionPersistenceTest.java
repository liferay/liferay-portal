/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.DuplicateCTCollectionExternalReferenceCodeException;
import com.liferay.change.tracking.exception.NoSuchCollectionException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalServiceUtil;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.change.tracking.service.persistence.CTCollectionUtil;
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
public class CTCollectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTCollectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTCollection> iterator = _ctCollections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollection ctCollection = _persistence.create(pk);

		Assert.assertNotNull(ctCollection);

		Assert.assertEquals(ctCollection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		_persistence.remove(newCTCollection);

		CTCollection existingCTCollection = _persistence.fetchByPrimaryKey(
			newCTCollection.getPrimaryKey());

		Assert.assertNull(existingCTCollection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTCollection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollection newCTCollection = _persistence.create(pk);

		newCTCollection.setMvccVersion(RandomTestUtil.nextLong());

		newCTCollection.setUuid(RandomTestUtil.randomString());

		newCTCollection.setExternalReferenceCode(RandomTestUtil.randomString());

		newCTCollection.setCompanyId(RandomTestUtil.nextLong());

		newCTCollection.setUserId(RandomTestUtil.nextLong());

		newCTCollection.setCreateDate(RandomTestUtil.nextDate());

		newCTCollection.setModifiedDate(RandomTestUtil.nextDate());

		newCTCollection.setCtRemoteId(RandomTestUtil.nextLong());

		newCTCollection.setSchemaVersionId(RandomTestUtil.nextLong());

		newCTCollection.setName(RandomTestUtil.randomString());

		newCTCollection.setDescription(RandomTestUtil.randomString());

		newCTCollection.setOnDemandUserId(RandomTestUtil.nextLong());

		newCTCollection.setShareable(RandomTestUtil.randomBoolean());

		newCTCollection.setStatus(RandomTestUtil.nextInt());

		newCTCollection.setStatusByUserId(RandomTestUtil.nextLong());

		newCTCollection.setStatusDate(RandomTestUtil.nextDate());

		_ctCollections.add(_persistence.update(newCTCollection));

		CTCollection existingCTCollection = _persistence.findByPrimaryKey(
			newCTCollection.getPrimaryKey());

		Assert.assertEquals(
			existingCTCollection.getMvccVersion(),
			newCTCollection.getMvccVersion());
		Assert.assertEquals(
			existingCTCollection.getUuid(), newCTCollection.getUuid());
		Assert.assertEquals(
			existingCTCollection.getExternalReferenceCode(),
			newCTCollection.getExternalReferenceCode());
		Assert.assertEquals(
			existingCTCollection.getCtCollectionId(),
			newCTCollection.getCtCollectionId());
		Assert.assertEquals(
			existingCTCollection.getCompanyId(),
			newCTCollection.getCompanyId());
		Assert.assertEquals(
			existingCTCollection.getUserId(), newCTCollection.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTCollection.getCreateDate()),
			Time.getShortTimestamp(newCTCollection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTCollection.getModifiedDate()),
			Time.getShortTimestamp(newCTCollection.getModifiedDate()));
		Assert.assertEquals(
			existingCTCollection.getCtRemoteId(),
			newCTCollection.getCtRemoteId());
		Assert.assertEquals(
			existingCTCollection.getSchemaVersionId(),
			newCTCollection.getSchemaVersionId());
		Assert.assertEquals(
			existingCTCollection.getName(), newCTCollection.getName());
		Assert.assertEquals(
			existingCTCollection.getDescription(),
			newCTCollection.getDescription());
		Assert.assertEquals(
			existingCTCollection.getOnDemandUserId(),
			newCTCollection.getOnDemandUserId());
		Assert.assertEquals(
			existingCTCollection.isShareable(), newCTCollection.isShareable());
		Assert.assertEquals(
			existingCTCollection.getStatus(), newCTCollection.getStatus());
		Assert.assertEquals(
			existingCTCollection.getStatusByUserId(),
			newCTCollection.getStatusByUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTCollection.getStatusDate()),
			Time.getShortTimestamp(newCTCollection.getStatusDate()));
	}

	@Test(expected = DuplicateCTCollectionExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CTCollection ctCollection = addCTCollection();

		CTCollection newCTCollection = addCTCollection();

		newCTCollection.setCompanyId(ctCollection.getCompanyId());

		newCTCollection = _persistence.update(newCTCollection);

		Session session = _persistence.getCurrentSession();

		session.evict(newCTCollection);

		newCTCollection.setExternalReferenceCode(
			ctCollection.getExternalReferenceCode());

		_persistence.update(newCTCollection);
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
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByC_SVI() throws Exception {
		_persistence.countByC_SVI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_SVI(0L, 0L);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByC_SArrayable() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		CTCollection existingCTCollection = _persistence.findByPrimaryKey(
			newCTCollection.getPrimaryKey());

		Assert.assertEquals(existingCTCollection, newCTCollection);
	}

	@Test(expected = NoSuchCollectionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTCollection> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTCollection", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "ctCollectionId", true, "companyId",
			true, "userId", true, "createDate", true, "modifiedDate", true,
			"ctRemoteId", true, "schemaVersionId", true, "name", true,
			"description", true, "onDemandUserId", true, "shareable", true,
			"status", true, "statusByUserId", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		CTCollection existingCTCollection = _persistence.fetchByPrimaryKey(
			newCTCollection.getPrimaryKey());

		Assert.assertEquals(existingCTCollection, newCTCollection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollection missingCTCollection = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTCollection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTCollection newCTCollection1 = addCTCollection();
		CTCollection newCTCollection2 = addCTCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollection1.getPrimaryKey());
		primaryKeys.add(newCTCollection2.getPrimaryKey());

		Map<Serializable, CTCollection> ctCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctCollections.size());
		Assert.assertEquals(
			newCTCollection1,
			ctCollections.get(newCTCollection1.getPrimaryKey()));
		Assert.assertEquals(
			newCTCollection2,
			ctCollections.get(newCTCollection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTCollection> ctCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTCollection newCTCollection = addCTCollection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTCollection> ctCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctCollections.size());
		Assert.assertEquals(
			newCTCollection,
			ctCollections.get(newCTCollection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTCollection> ctCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollection.getPrimaryKey());

		Map<Serializable, CTCollection> ctCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctCollections.size());
		Assert.assertEquals(
			newCTCollection,
			ctCollections.get(newCTCollection.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTCollectionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CTCollection>() {

				@Override
				public void performAction(CTCollection ctCollection) {
					Assert.assertNotNull(ctCollection);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctCollectionId", newCTCollection.getCtCollectionId()));

		List<CTCollection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTCollection existingCTCollection = result.get(0);

		Assert.assertEquals(existingCTCollection, newCTCollection);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctCollectionId", RandomTestUtil.nextLong()));

		List<CTCollection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctCollectionId"));

		Object newCtCollectionId = newCTCollection.getCtCollectionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctCollectionId", new Object[] {newCtCollectionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtCollectionId = result.get(0);

		Assert.assertEquals(existingCtCollectionId, newCtCollectionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctCollectionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctCollectionId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTCollection newCTCollection = addCTCollection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCTCollection.getPrimaryKey()));
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

		CTCollection newCTCollection = addCTCollection();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctCollectionId", newCTCollection.getCtCollectionId()));

		List<CTCollection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CTCollection ctCollection) {
		Assert.assertEquals(
			ctCollection.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ctCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ctCollection.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ctCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CTCollection addCTCollection() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollection ctCollection = _persistence.create(pk);

		ctCollection.setMvccVersion(RandomTestUtil.nextLong());

		ctCollection.setUuid(RandomTestUtil.randomString());

		ctCollection.setExternalReferenceCode(RandomTestUtil.randomString());

		ctCollection.setCompanyId(RandomTestUtil.nextLong());

		ctCollection.setUserId(RandomTestUtil.nextLong());

		ctCollection.setCreateDate(RandomTestUtil.nextDate());

		ctCollection.setModifiedDate(RandomTestUtil.nextDate());

		ctCollection.setCtRemoteId(RandomTestUtil.nextLong());

		ctCollection.setSchemaVersionId(RandomTestUtil.nextLong());

		ctCollection.setName(RandomTestUtil.randomString());

		ctCollection.setDescription(RandomTestUtil.randomString());

		ctCollection.setOnDemandUserId(RandomTestUtil.nextLong());

		ctCollection.setShareable(RandomTestUtil.randomBoolean());

		ctCollection.setStatus(RandomTestUtil.nextInt());

		ctCollection.setStatusByUserId(RandomTestUtil.nextLong());

		ctCollection.setStatusDate(RandomTestUtil.nextDate());

		_ctCollections.add(_persistence.update(ctCollection));

		return ctCollection;
	}

	private List<CTCollection> _ctCollections = new ArrayList<CTCollection>();
	private CTCollectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}