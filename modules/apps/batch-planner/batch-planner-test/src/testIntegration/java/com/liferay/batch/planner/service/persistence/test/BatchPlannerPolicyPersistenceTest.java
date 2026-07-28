/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.planner.exception.NoSuchPolicyException;
import com.liferay.batch.planner.model.BatchPlannerPolicy;
import com.liferay.batch.planner.service.BatchPlannerPolicyLocalServiceUtil;
import com.liferay.batch.planner.service.persistence.BatchPlannerPolicyPersistence;
import com.liferay.batch.planner.service.persistence.BatchPlannerPolicyUtil;
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
public class BatchPlannerPolicyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.batch.planner.service"));

	@Before
	public void setUp() {
		_persistence = BatchPlannerPolicyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BatchPlannerPolicy> iterator =
			_batchPlannerPolicies.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BatchPlannerPolicy batchPlannerPolicy = _persistence.create(pk);

		Assert.assertNotNull(batchPlannerPolicy);

		Assert.assertEquals(batchPlannerPolicy.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		_persistence.remove(newBatchPlannerPolicy);

		BatchPlannerPolicy existingBatchPlannerPolicy =
			_persistence.fetchByPrimaryKey(
				newBatchPlannerPolicy.getPrimaryKey());

		Assert.assertNull(existingBatchPlannerPolicy);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBatchPlannerPolicy();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		newBatchPlannerPolicy.setCompanyId(RandomTestUtil.nextLong());

		newBatchPlannerPolicy.setUserId(RandomTestUtil.nextLong());

		newBatchPlannerPolicy.setUserName(RandomTestUtil.randomString());

		newBatchPlannerPolicy.setCreateDate(RandomTestUtil.nextDate());

		newBatchPlannerPolicy.setModifiedDate(RandomTestUtil.nextDate());

		newBatchPlannerPolicy.setBatchPlannerPlanId(RandomTestUtil.nextLong());

		newBatchPlannerPolicy.setName(RandomTestUtil.randomString());

		newBatchPlannerPolicy.setValue(RandomTestUtil.randomString());

		newBatchPlannerPolicy = _persistence.update(newBatchPlannerPolicy);

		_batchPlannerPolicies.add(newBatchPlannerPolicy);

		BatchPlannerPolicy existingBatchPlannerPolicy =
			_persistence.findByPrimaryKey(
				newBatchPlannerPolicy.getPrimaryKey());

		Assert.assertEquals(
			existingBatchPlannerPolicy.getMvccVersion(),
			newBatchPlannerPolicy.getMvccVersion());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getBatchPlannerPolicyId(),
			newBatchPlannerPolicy.getBatchPlannerPolicyId());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getCompanyId(),
			newBatchPlannerPolicy.getCompanyId());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getUserId(),
			newBatchPlannerPolicy.getUserId());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getUserName(),
			newBatchPlannerPolicy.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBatchPlannerPolicy.getCreateDate()),
			Time.getShortTimestamp(newBatchPlannerPolicy.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingBatchPlannerPolicy.getModifiedDate()),
			Time.getShortTimestamp(newBatchPlannerPolicy.getModifiedDate()));
		Assert.assertEquals(
			existingBatchPlannerPolicy.getBatchPlannerPlanId(),
			newBatchPlannerPolicy.getBatchPlannerPlanId());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getName(),
			newBatchPlannerPolicy.getName());
		Assert.assertEquals(
			existingBatchPlannerPolicy.getValue(),
			newBatchPlannerPolicy.getValue());
	}

	@Test
	public void testCountByBatchPlannerPlanId() throws Exception {
		_persistence.countByBatchPlannerPlanId(RandomTestUtil.nextLong());

		_persistence.countByBatchPlannerPlanId(0L);
	}

	@Test
	public void testCountByBPPI_N() throws Exception {
		_persistence.countByBPPI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByBPPI_N(0L, "null");

		_persistence.countByBPPI_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		BatchPlannerPolicy existingBatchPlannerPolicy =
			_persistence.findByPrimaryKey(
				newBatchPlannerPolicy.getPrimaryKey());

		Assert.assertEquals(existingBatchPlannerPolicy, newBatchPlannerPolicy);
	}

	@Test(expected = NoSuchPolicyException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BatchPlannerPolicy> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BatchPlannerPolicy", "mvccVersion", true, "batchPlannerPolicyId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "batchPlannerPlanId",
			true, "name", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		BatchPlannerPolicy existingBatchPlannerPolicy =
			_persistence.fetchByPrimaryKey(
				newBatchPlannerPolicy.getPrimaryKey());

		Assert.assertEquals(existingBatchPlannerPolicy, newBatchPlannerPolicy);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BatchPlannerPolicy missingBatchPlannerPolicy =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingBatchPlannerPolicy);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BatchPlannerPolicy newBatchPlannerPolicy1 = addBatchPlannerPolicy();
		BatchPlannerPolicy newBatchPlannerPolicy2 = addBatchPlannerPolicy();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBatchPlannerPolicy1.getPrimaryKey());
		primaryKeys.add(newBatchPlannerPolicy2.getPrimaryKey());

		Map<Serializable, BatchPlannerPolicy> batchPlannerPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, batchPlannerPolicies.size());
		Assert.assertEquals(
			newBatchPlannerPolicy1,
			batchPlannerPolicies.get(newBatchPlannerPolicy1.getPrimaryKey()));
		Assert.assertEquals(
			newBatchPlannerPolicy2,
			batchPlannerPolicies.get(newBatchPlannerPolicy2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BatchPlannerPolicy> batchPlannerPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(batchPlannerPolicies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBatchPlannerPolicy.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BatchPlannerPolicy> batchPlannerPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, batchPlannerPolicies.size());
		Assert.assertEquals(
			newBatchPlannerPolicy,
			batchPlannerPolicies.get(newBatchPlannerPolicy.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BatchPlannerPolicy> batchPlannerPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(batchPlannerPolicies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBatchPlannerPolicy.getPrimaryKey());

		Map<Serializable, BatchPlannerPolicy> batchPlannerPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, batchPlannerPolicies.size());
		Assert.assertEquals(
			newBatchPlannerPolicy,
			batchPlannerPolicies.get(newBatchPlannerPolicy.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			BatchPlannerPolicyLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<BatchPlannerPolicy>() {

				@Override
				public void performAction(
					BatchPlannerPolicy batchPlannerPolicy) {

					Assert.assertNotNull(batchPlannerPolicy);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BatchPlannerPolicy.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"batchPlannerPolicyId",
				newBatchPlannerPolicy.getBatchPlannerPolicyId()));

		List<BatchPlannerPolicy> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		BatchPlannerPolicy existingBatchPlannerPolicy = result.get(0);

		Assert.assertEquals(existingBatchPlannerPolicy, newBatchPlannerPolicy);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BatchPlannerPolicy.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"batchPlannerPolicyId", RandomTestUtil.nextLong()));

		List<BatchPlannerPolicy> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BatchPlannerPolicy.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("batchPlannerPolicyId"));

		Object newBatchPlannerPolicyId =
			newBatchPlannerPolicy.getBatchPlannerPolicyId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"batchPlannerPolicyId",
				new Object[] {newBatchPlannerPolicyId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBatchPlannerPolicyId = result.get(0);

		Assert.assertEquals(
			existingBatchPlannerPolicyId, newBatchPlannerPolicyId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BatchPlannerPolicy.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("batchPlannerPolicyId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"batchPlannerPolicyId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newBatchPlannerPolicy.getPrimaryKey()));
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

		BatchPlannerPolicy newBatchPlannerPolicy = addBatchPlannerPolicy();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BatchPlannerPolicy.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"batchPlannerPolicyId",
				newBatchPlannerPolicy.getBatchPlannerPolicyId()));

		List<BatchPlannerPolicy> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(BatchPlannerPolicy batchPlannerPolicy) {
		Assert.assertEquals(
			Long.valueOf(batchPlannerPolicy.getBatchPlannerPlanId()),
			ReflectionTestUtil.<Long>invoke(
				batchPlannerPolicy, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "batchPlannerPlanId"));
		Assert.assertEquals(
			batchPlannerPolicy.getName(),
			ReflectionTestUtil.invoke(
				batchPlannerPolicy, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected BatchPlannerPolicy addBatchPlannerPolicy() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BatchPlannerPolicy batchPlannerPolicy = _persistence.create(pk);

		batchPlannerPolicy.setCompanyId(RandomTestUtil.nextLong());

		batchPlannerPolicy.setUserId(RandomTestUtil.nextLong());

		batchPlannerPolicy.setUserName(RandomTestUtil.randomString());

		batchPlannerPolicy.setCreateDate(RandomTestUtil.nextDate());

		batchPlannerPolicy.setModifiedDate(RandomTestUtil.nextDate());

		batchPlannerPolicy.setBatchPlannerPlanId(RandomTestUtil.nextLong());

		batchPlannerPolicy.setName(RandomTestUtil.randomString());

		batchPlannerPolicy.setValue(RandomTestUtil.randomString());

		_batchPlannerPolicies.add(_persistence.update(batchPlannerPolicy));

		return batchPlannerPolicy;
	}

	private List<BatchPlannerPolicy> _batchPlannerPolicies =
		new ArrayList<BatchPlannerPolicy>();
	private BatchPlannerPolicyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-769664114