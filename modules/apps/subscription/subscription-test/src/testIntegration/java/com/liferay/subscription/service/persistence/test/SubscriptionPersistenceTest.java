/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.subscription.exception.NoSuchSubscriptionException;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;
import com.liferay.subscription.service.persistence.SubscriptionPersistence;
import com.liferay.subscription.service.persistence.SubscriptionUtil;

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
public class SubscriptionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.subscription.service"));

	@Before
	public void setUp() {
		_persistence = SubscriptionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Subscription> iterator = _subscriptions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Subscription subscription = _persistence.create(pk);

		Assert.assertNotNull(subscription);

		Assert.assertEquals(subscription.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Subscription newSubscription = addSubscription();

		_persistence.remove(newSubscription);

		Subscription existingSubscription = _persistence.fetchByPrimaryKey(
			newSubscription.getPrimaryKey());

		Assert.assertNull(existingSubscription);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSubscription();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Subscription newSubscription = _persistence.create(pk);

		newSubscription.setMvccVersion(RandomTestUtil.nextLong());

		newSubscription.setCtCollectionId(RandomTestUtil.nextLong());

		newSubscription.setGroupId(RandomTestUtil.nextLong());

		newSubscription.setCompanyId(RandomTestUtil.nextLong());

		newSubscription.setUserId(RandomTestUtil.nextLong());

		newSubscription.setUserName(RandomTestUtil.randomString());

		newSubscription.setCreateDate(RandomTestUtil.nextDate());

		newSubscription.setModifiedDate(RandomTestUtil.nextDate());

		newSubscription.setClassNameId(RandomTestUtil.nextLong());

		newSubscription.setClassPK(RandomTestUtil.nextLong());

		newSubscription.setFrequency(RandomTestUtil.randomString());

		_subscriptions.add(_persistence.update(newSubscription));

		Subscription existingSubscription = _persistence.findByPrimaryKey(
			newSubscription.getPrimaryKey());

		Assert.assertEquals(
			existingSubscription.getMvccVersion(),
			newSubscription.getMvccVersion());
		Assert.assertEquals(
			existingSubscription.getCtCollectionId(),
			newSubscription.getCtCollectionId());
		Assert.assertEquals(
			existingSubscription.getSubscriptionId(),
			newSubscription.getSubscriptionId());
		Assert.assertEquals(
			existingSubscription.getGroupId(), newSubscription.getGroupId());
		Assert.assertEquals(
			existingSubscription.getCompanyId(),
			newSubscription.getCompanyId());
		Assert.assertEquals(
			existingSubscription.getUserId(), newSubscription.getUserId());
		Assert.assertEquals(
			existingSubscription.getUserName(), newSubscription.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSubscription.getCreateDate()),
			Time.getShortTimestamp(newSubscription.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSubscription.getModifiedDate()),
			Time.getShortTimestamp(newSubscription.getModifiedDate()));
		Assert.assertEquals(
			existingSubscription.getClassNameId(),
			newSubscription.getClassNameId());
		Assert.assertEquals(
			existingSubscription.getClassPK(), newSubscription.getClassPK());
		Assert.assertEquals(
			existingSubscription.getFrequency(),
			newSubscription.getFrequency());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByU_C() throws Exception {
		_persistence.countByU_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_U_C_C() throws Exception {
		_persistence.countByC_U_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U_C_C(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByC_U_C_CArrayable() throws Exception {
		_persistence.countByC_U_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Subscription newSubscription = addSubscription();

		Subscription existingSubscription = _persistence.findByPrimaryKey(
			newSubscription.getPrimaryKey());

		Assert.assertEquals(existingSubscription, newSubscription);
	}

	@Test(expected = NoSuchSubscriptionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Subscription> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Subscription", "mvccVersion", true, "ctCollectionId", true,
			"subscriptionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"frequency", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Subscription newSubscription = addSubscription();

		Subscription existingSubscription = _persistence.fetchByPrimaryKey(
			newSubscription.getPrimaryKey());

		Assert.assertEquals(existingSubscription, newSubscription);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Subscription missingSubscription = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSubscription);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Subscription newSubscription1 = addSubscription();
		Subscription newSubscription2 = addSubscription();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSubscription1.getPrimaryKey());
		primaryKeys.add(newSubscription2.getPrimaryKey());

		Map<Serializable, Subscription> subscriptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, subscriptions.size());
		Assert.assertEquals(
			newSubscription1,
			subscriptions.get(newSubscription1.getPrimaryKey()));
		Assert.assertEquals(
			newSubscription2,
			subscriptions.get(newSubscription2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Subscription> subscriptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(subscriptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Subscription newSubscription = addSubscription();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSubscription.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Subscription> subscriptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, subscriptions.size());
		Assert.assertEquals(
			newSubscription,
			subscriptions.get(newSubscription.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Subscription> subscriptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(subscriptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Subscription newSubscription = addSubscription();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSubscription.getPrimaryKey());

		Map<Serializable, Subscription> subscriptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, subscriptions.size());
		Assert.assertEquals(
			newSubscription,
			subscriptions.get(newSubscription.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SubscriptionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Subscription>() {

				@Override
				public void performAction(Subscription subscription) {
					Assert.assertNotNull(subscription);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Subscription newSubscription = addSubscription();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Subscription.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"subscriptionId", newSubscription.getSubscriptionId()));

		List<Subscription> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		Subscription existingSubscription = result.get(0);

		Assert.assertEquals(existingSubscription, newSubscription);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Subscription.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"subscriptionId", RandomTestUtil.nextLong()));

		List<Subscription> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Subscription newSubscription = addSubscription();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Subscription.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("subscriptionId"));

		Object newSubscriptionId = newSubscription.getSubscriptionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"subscriptionId", new Object[] {newSubscriptionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSubscriptionId = result.get(0);

		Assert.assertEquals(existingSubscriptionId, newSubscriptionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Subscription.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("subscriptionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"subscriptionId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Subscription newSubscription = addSubscription();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSubscription.getPrimaryKey()));
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

		Subscription newSubscription = addSubscription();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Subscription.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"subscriptionId", newSubscription.getSubscriptionId()));

		List<Subscription> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Subscription subscription) {
		Assert.assertEquals(
			Long.valueOf(subscription.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				subscription, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(subscription.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				subscription, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(subscription.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				subscription, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(subscription.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				subscription, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected Subscription addSubscription() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Subscription subscription = _persistence.create(pk);

		subscription.setMvccVersion(RandomTestUtil.nextLong());

		subscription.setCtCollectionId(RandomTestUtil.nextLong());

		subscription.setGroupId(RandomTestUtil.nextLong());

		subscription.setCompanyId(RandomTestUtil.nextLong());

		subscription.setUserId(RandomTestUtil.nextLong());

		subscription.setUserName(RandomTestUtil.randomString());

		subscription.setCreateDate(RandomTestUtil.nextDate());

		subscription.setModifiedDate(RandomTestUtil.nextDate());

		subscription.setClassNameId(RandomTestUtil.nextLong());

		subscription.setClassPK(RandomTestUtil.nextLong());

		subscription.setFrequency(RandomTestUtil.randomString());

		_subscriptions.add(_persistence.update(subscription));

		return subscription;
	}

	private List<Subscription> _subscriptions = new ArrayList<Subscription>();
	private SubscriptionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}