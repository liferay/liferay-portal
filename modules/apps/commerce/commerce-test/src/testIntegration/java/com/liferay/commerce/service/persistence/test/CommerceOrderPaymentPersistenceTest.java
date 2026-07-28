/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchOrderPaymentException;
import com.liferay.commerce.model.CommerceOrderPayment;
import com.liferay.commerce.service.CommerceOrderPaymentLocalServiceUtil;
import com.liferay.commerce.service.persistence.CommerceOrderPaymentPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderPaymentUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
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
public class CommerceOrderPaymentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceOrderPaymentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceOrderPayment> iterator =
			_commerceOrderPayments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderPayment commerceOrderPayment = _persistence.create(pk);

		Assert.assertNotNull(commerceOrderPayment);

		Assert.assertEquals(commerceOrderPayment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		_persistence.remove(newCommerceOrderPayment);

		CommerceOrderPayment existingCommerceOrderPayment =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderPayment.getPrimaryKey());

		Assert.assertNull(existingCommerceOrderPayment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceOrderPayment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		newCommerceOrderPayment.setGroupId(RandomTestUtil.nextLong());

		newCommerceOrderPayment.setCompanyId(RandomTestUtil.nextLong());

		newCommerceOrderPayment.setUserId(RandomTestUtil.nextLong());

		newCommerceOrderPayment.setUserName(RandomTestUtil.randomString());

		newCommerceOrderPayment.setCreateDate(RandomTestUtil.nextDate());

		newCommerceOrderPayment.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceOrderPayment.setCommerceOrderId(RandomTestUtil.nextLong());

		newCommerceOrderPayment.setCommercePaymentMethodKey(
			RandomTestUtil.randomString());

		newCommerceOrderPayment.setContent(RandomTestUtil.randomString());

		newCommerceOrderPayment.setStatus(RandomTestUtil.nextInt());

		newCommerceOrderPayment = _persistence.update(newCommerceOrderPayment);

		_commerceOrderPayments.add(newCommerceOrderPayment);

		CommerceOrderPayment existingCommerceOrderPayment =
			_persistence.findByPrimaryKey(
				newCommerceOrderPayment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderPayment.getMvccVersion(),
			newCommerceOrderPayment.getMvccVersion());
		Assert.assertEquals(
			existingCommerceOrderPayment.getCommerceOrderPaymentId(),
			newCommerceOrderPayment.getCommerceOrderPaymentId());
		Assert.assertEquals(
			existingCommerceOrderPayment.getGroupId(),
			newCommerceOrderPayment.getGroupId());
		Assert.assertEquals(
			existingCommerceOrderPayment.getCompanyId(),
			newCommerceOrderPayment.getCompanyId());
		Assert.assertEquals(
			existingCommerceOrderPayment.getUserId(),
			newCommerceOrderPayment.getUserId());
		Assert.assertEquals(
			existingCommerceOrderPayment.getUserName(),
			newCommerceOrderPayment.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderPayment.getCreateDate()),
			Time.getShortTimestamp(newCommerceOrderPayment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderPayment.getModifiedDate()),
			Time.getShortTimestamp(newCommerceOrderPayment.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceOrderPayment.getCommerceOrderId(),
			newCommerceOrderPayment.getCommerceOrderId());
		Assert.assertEquals(
			existingCommerceOrderPayment.getCommercePaymentMethodKey(),
			newCommerceOrderPayment.getCommercePaymentMethodKey());
		Assert.assertEquals(
			existingCommerceOrderPayment.getContent(),
			newCommerceOrderPayment.getContent());
		Assert.assertEquals(
			existingCommerceOrderPayment.getStatus(),
			newCommerceOrderPayment.getStatus());
	}

	@Test
	public void testCountByCommerceOrderId() throws Exception {
		_persistence.countByCommerceOrderId(RandomTestUtil.nextLong());

		_persistence.countByCommerceOrderId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		CommerceOrderPayment existingCommerceOrderPayment =
			_persistence.findByPrimaryKey(
				newCommerceOrderPayment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderPayment, newCommerceOrderPayment);
	}

	@Test(expected = NoSuchOrderPaymentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceOrderPayment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceOrderPayment", "mvccVersion", true,
			"commerceOrderPaymentId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "commerceOrderId", true,
			"commercePaymentMethodKey", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		CommerceOrderPayment existingCommerceOrderPayment =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderPayment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderPayment, newCommerceOrderPayment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderPayment missingCommerceOrderPayment =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceOrderPayment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceOrderPayment newCommerceOrderPayment1 =
			addCommerceOrderPayment();
		CommerceOrderPayment newCommerceOrderPayment2 =
			addCommerceOrderPayment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderPayment1.getPrimaryKey());
		primaryKeys.add(newCommerceOrderPayment2.getPrimaryKey());

		Map<Serializable, CommerceOrderPayment> commerceOrderPayments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceOrderPayments.size());
		Assert.assertEquals(
			newCommerceOrderPayment1,
			commerceOrderPayments.get(
				newCommerceOrderPayment1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceOrderPayment2,
			commerceOrderPayments.get(
				newCommerceOrderPayment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceOrderPayment> commerceOrderPayments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderPayments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderPayment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceOrderPayment> commerceOrderPayments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderPayments.size());
		Assert.assertEquals(
			newCommerceOrderPayment,
			commerceOrderPayments.get(newCommerceOrderPayment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceOrderPayment> commerceOrderPayments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderPayments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderPayment.getPrimaryKey());

		Map<Serializable, CommerceOrderPayment> commerceOrderPayments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderPayments.size());
		Assert.assertEquals(
			newCommerceOrderPayment,
			commerceOrderPayments.get(newCommerceOrderPayment.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceOrderPaymentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceOrderPayment>() {

				@Override
				public void performAction(
					CommerceOrderPayment commerceOrderPayment) {

					Assert.assertNotNull(commerceOrderPayment);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderPayment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceOrderPaymentId",
				newCommerceOrderPayment.getCommerceOrderPaymentId()));

		List<CommerceOrderPayment> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceOrderPayment existingCommerceOrderPayment = result.get(0);

		Assert.assertEquals(
			existingCommerceOrderPayment, newCommerceOrderPayment);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderPayment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceOrderPaymentId", RandomTestUtil.nextLong()));

		List<CommerceOrderPayment> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceOrderPayment newCommerceOrderPayment =
			addCommerceOrderPayment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderPayment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceOrderPaymentId"));

		Object newCommerceOrderPaymentId =
			newCommerceOrderPayment.getCommerceOrderPaymentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceOrderPaymentId",
				new Object[] {newCommerceOrderPaymentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceOrderPaymentId = result.get(0);

		Assert.assertEquals(
			existingCommerceOrderPaymentId, newCommerceOrderPaymentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderPayment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceOrderPaymentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceOrderPaymentId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CommerceOrderPayment addCommerceOrderPayment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderPayment commerceOrderPayment = _persistence.create(pk);

		commerceOrderPayment.setGroupId(RandomTestUtil.nextLong());

		commerceOrderPayment.setCompanyId(RandomTestUtil.nextLong());

		commerceOrderPayment.setUserId(RandomTestUtil.nextLong());

		commerceOrderPayment.setUserName(RandomTestUtil.randomString());

		commerceOrderPayment.setCreateDate(RandomTestUtil.nextDate());

		commerceOrderPayment.setModifiedDate(RandomTestUtil.nextDate());

		commerceOrderPayment.setCommerceOrderId(RandomTestUtil.nextLong());

		commerceOrderPayment.setCommercePaymentMethodKey(
			RandomTestUtil.randomString());

		commerceOrderPayment.setContent(RandomTestUtil.randomString());

		commerceOrderPayment.setStatus(RandomTestUtil.nextInt());

		_commerceOrderPayments.add(_persistence.update(commerceOrderPayment));

		return commerceOrderPayment;
	}

	private List<CommerceOrderPayment> _commerceOrderPayments =
		new ArrayList<CommerceOrderPayment>();
	private CommerceOrderPaymentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1826010483