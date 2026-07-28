/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchUserNotificationDeliveryException;
import com.liferay.portal.kernel.model.UserNotificationDelivery;
import com.liferay.portal.kernel.service.UserNotificationDeliveryLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.UserNotificationDeliveryPersistence;
import com.liferay.portal.kernel.service.persistence.UserNotificationDeliveryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class UserNotificationDeliveryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserNotificationDeliveryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserNotificationDelivery> iterator =
			_userNotificationDeliveries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserNotificationDelivery userNotificationDelivery = _persistence.create(
			pk);

		Assert.assertNotNull(userNotificationDelivery);

		Assert.assertEquals(userNotificationDelivery.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		_persistence.remove(newUserNotificationDelivery);

		UserNotificationDelivery existingUserNotificationDelivery =
			_persistence.fetchByPrimaryKey(
				newUserNotificationDelivery.getPrimaryKey());

		Assert.assertNull(existingUserNotificationDelivery);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserNotificationDelivery();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		newUserNotificationDelivery.setCompanyId(RandomTestUtil.nextLong());

		newUserNotificationDelivery.setUserId(RandomTestUtil.nextLong());

		newUserNotificationDelivery.setPortletId(RandomTestUtil.randomString());

		newUserNotificationDelivery.setClassNameId(RandomTestUtil.nextLong());

		newUserNotificationDelivery.setNotificationType(
			RandomTestUtil.nextInt());

		newUserNotificationDelivery.setDeliveryType(RandomTestUtil.nextInt());

		newUserNotificationDelivery.setDeliver(RandomTestUtil.randomBoolean());

		newUserNotificationDelivery = _persistence.update(
			newUserNotificationDelivery);

		_userNotificationDeliveries.add(newUserNotificationDelivery);

		UserNotificationDelivery existingUserNotificationDelivery =
			_persistence.findByPrimaryKey(
				newUserNotificationDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingUserNotificationDelivery.getMvccVersion(),
			newUserNotificationDelivery.getMvccVersion());
		Assert.assertEquals(
			existingUserNotificationDelivery.getUserNotificationDeliveryId(),
			newUserNotificationDelivery.getUserNotificationDeliveryId());
		Assert.assertEquals(
			existingUserNotificationDelivery.getCompanyId(),
			newUserNotificationDelivery.getCompanyId());
		Assert.assertEquals(
			existingUserNotificationDelivery.getUserId(),
			newUserNotificationDelivery.getUserId());
		Assert.assertEquals(
			existingUserNotificationDelivery.getPortletId(),
			newUserNotificationDelivery.getPortletId());
		Assert.assertEquals(
			existingUserNotificationDelivery.getClassNameId(),
			newUserNotificationDelivery.getClassNameId());
		Assert.assertEquals(
			existingUserNotificationDelivery.getNotificationType(),
			newUserNotificationDelivery.getNotificationType());
		Assert.assertEquals(
			existingUserNotificationDelivery.getDeliveryType(),
			newUserNotificationDelivery.getDeliveryType());
		Assert.assertEquals(
			existingUserNotificationDelivery.isDeliver(),
			newUserNotificationDelivery.isDeliver());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByU_P_C_N_D() throws Exception {
		_persistence.countByU_P_C_N_D(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByU_P_C_N_D(0L, "null", 0L, 0, 0);

		_persistence.countByU_P_C_N_D(0L, (String)null, 0L, 0, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		UserNotificationDelivery existingUserNotificationDelivery =
			_persistence.findByPrimaryKey(
				newUserNotificationDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingUserNotificationDelivery, newUserNotificationDelivery);
	}

	@Test(expected = NoSuchUserNotificationDeliveryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserNotificationDelivery>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"UserNotificationDelivery", "mvccVersion", true,
			"userNotificationDeliveryId", true, "companyId", true, "userId",
			true, "portletId", true, "classNameId", true, "notificationType",
			true, "deliveryType", true, "deliver", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		UserNotificationDelivery existingUserNotificationDelivery =
			_persistence.fetchByPrimaryKey(
				newUserNotificationDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingUserNotificationDelivery, newUserNotificationDelivery);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserNotificationDelivery missingUserNotificationDelivery =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUserNotificationDelivery);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserNotificationDelivery newUserNotificationDelivery1 =
			addUserNotificationDelivery();
		UserNotificationDelivery newUserNotificationDelivery2 =
			addUserNotificationDelivery();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserNotificationDelivery1.getPrimaryKey());
		primaryKeys.add(newUserNotificationDelivery2.getPrimaryKey());

		Map<Serializable, UserNotificationDelivery> userNotificationDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userNotificationDeliveries.size());
		Assert.assertEquals(
			newUserNotificationDelivery1,
			userNotificationDeliveries.get(
				newUserNotificationDelivery1.getPrimaryKey()));
		Assert.assertEquals(
			newUserNotificationDelivery2,
			userNotificationDeliveries.get(
				newUserNotificationDelivery2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserNotificationDelivery> userNotificationDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userNotificationDeliveries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserNotificationDelivery.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserNotificationDelivery> userNotificationDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userNotificationDeliveries.size());
		Assert.assertEquals(
			newUserNotificationDelivery,
			userNotificationDeliveries.get(
				newUserNotificationDelivery.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserNotificationDelivery> userNotificationDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userNotificationDeliveries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserNotificationDelivery.getPrimaryKey());

		Map<Serializable, UserNotificationDelivery> userNotificationDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userNotificationDeliveries.size());
		Assert.assertEquals(
			newUserNotificationDelivery,
			userNotificationDeliveries.get(
				newUserNotificationDelivery.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			UserNotificationDeliveryLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<UserNotificationDelivery>() {

				@Override
				public void performAction(
					UserNotificationDelivery userNotificationDelivery) {

					Assert.assertNotNull(userNotificationDelivery);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationDelivery.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"userNotificationDeliveryId",
				newUserNotificationDelivery.getUserNotificationDeliveryId()));

		List<UserNotificationDelivery> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		UserNotificationDelivery existingUserNotificationDelivery = result.get(
			0);

		Assert.assertEquals(
			existingUserNotificationDelivery, newUserNotificationDelivery);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationDelivery.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"userNotificationDeliveryId", RandomTestUtil.nextLong()));

		List<UserNotificationDelivery> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationDelivery.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("userNotificationDeliveryId"));

		Object newUserNotificationDeliveryId =
			newUserNotificationDelivery.getUserNotificationDeliveryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"userNotificationDeliveryId",
				new Object[] {newUserNotificationDeliveryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingUserNotificationDeliveryId = result.get(0);

		Assert.assertEquals(
			existingUserNotificationDeliveryId, newUserNotificationDeliveryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationDelivery.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("userNotificationDeliveryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"userNotificationDeliveryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newUserNotificationDelivery.getPrimaryKey()));
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

		UserNotificationDelivery newUserNotificationDelivery =
			addUserNotificationDelivery();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationDelivery.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"userNotificationDeliveryId",
				newUserNotificationDelivery.getUserNotificationDeliveryId()));

		List<UserNotificationDelivery> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		UserNotificationDelivery userNotificationDelivery) {

		Assert.assertEquals(
			Long.valueOf(userNotificationDelivery.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				userNotificationDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			userNotificationDelivery.getPortletId(),
			ReflectionTestUtil.invoke(
				userNotificationDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));
		Assert.assertEquals(
			Long.valueOf(userNotificationDelivery.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				userNotificationDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Integer.valueOf(userNotificationDelivery.getNotificationType()),
			ReflectionTestUtil.<Integer>invoke(
				userNotificationDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "notificationType"));
		Assert.assertEquals(
			Integer.valueOf(userNotificationDelivery.getDeliveryType()),
			ReflectionTestUtil.<Integer>invoke(
				userNotificationDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "deliveryType"));
	}

	protected UserNotificationDelivery addUserNotificationDelivery()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		UserNotificationDelivery userNotificationDelivery = _persistence.create(
			pk);

		userNotificationDelivery.setCompanyId(RandomTestUtil.nextLong());

		userNotificationDelivery.setUserId(RandomTestUtil.nextLong());

		userNotificationDelivery.setPortletId(RandomTestUtil.randomString());

		userNotificationDelivery.setClassNameId(RandomTestUtil.nextLong());

		userNotificationDelivery.setNotificationType(RandomTestUtil.nextInt());

		userNotificationDelivery.setDeliveryType(RandomTestUtil.nextInt());

		userNotificationDelivery.setDeliver(RandomTestUtil.randomBoolean());

		_userNotificationDeliveries.add(
			_persistence.update(userNotificationDelivery));

		return userNotificationDelivery;
	}

	private List<UserNotificationDelivery> _userNotificationDeliveries =
		new ArrayList<UserNotificationDelivery>();
	private UserNotificationDeliveryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1822370161